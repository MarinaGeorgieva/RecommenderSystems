package fmi.recommendersystems.hw1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.queries.mlt.MoreLikeThisQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class Recommender {

	private static final String NEWS_DIR = "20_newsgroups";

	private static final String ILLEGAL_CHARS = ".,\\/=!?\"'`@#$%^&*()[]{}_-+<>;:";

	private static final int RECOMMENDATIONS_COUNT = 10;
	private static final int TOP_HITS_COUNT = 5;

	public static void main(String[] args) throws IOException {
		System.out.println("Extracting all news files ...");
		List<List<News>> allNews = extractAllNews();

		System.out.println("Creating user profile ...");
		List<News> userNews = UserProfileGenerator.generate(allNews);
		System.out.println("User profile created");

		System.out.println("\nUser read the following news: ");
		for (int i = 0; i < userNews.size(); i++) {
			System.out.println(
					"Category: " + userNews.get(i).getCategory() + ", Filename: " + userNews.get(i).getFileName());
		}

		Directory indexDir = new RAMDirectory();
		createIndex(allNews, indexDir);

		List<News> recommendedNews = getRecommendations(allNews, userNews, indexDir);
		System.out.println("\nRecommended news:");
		for (News news : recommendedNews) {
			System.out.println("Category: " + news.getCategory() + ", Filename: " + news.getFileName());
		}
	}

	private static List<News> getRecommendations(List<List<News>> allNews, List<News> userNews, Directory indexDir)
			throws IOException {
		IndexReader indexReader = DirectoryReader.open(indexDir);
		IndexSearcher searcher = new IndexSearcher(indexReader);
		Analyzer analyzer = new StandardAnalyzer();

		TFIDFSimilarity similarity = new ClassicSimilarity();
		MoreLikeThis mlt = new MoreLikeThis(indexReader, similarity);
		mlt.setAnalyzer(analyzer);
		mlt.setBoost(true);
		mlt.setFieldNames(new String[] { "content" });
		mlt.setMinTermFreq(1);
		mlt.setMinDocFreq(1);

		List<TopDocs> topDocsForUserNews = retrieveTopDocsForUserNews(userNews, searcher, analyzer, mlt);
		List<ScoreDoc> allRecommendedDocs = retrieveAllRecommendedDocs(topDocsForUserNews);
		List<News> recommendedNews = getRecommendedNews(allRecommendedDocs, searcher);
		
		return recommendedNews;
	}

	private static List<News> getRecommendedNews(List<ScoreDoc> allRecommendedDocs, IndexSearcher searcher)
			throws IOException {
		List<News> recommendedNews = new ArrayList<News>();

		Set<Integer> usedDocs = new HashSet<Integer>();
		for (ScoreDoc scoreDoc : allRecommendedDocs) {
			if (recommendedNews.size() >= RECOMMENDATIONS_COUNT) {
				break;
			}

			int docID = scoreDoc.doc;
			if (!usedDocs.contains(docID)) {
				Document document = searcher.doc(docID);
				recommendedNews.add(new News("", document.get("category"), document.get("fileName"), false));
				usedDocs.add(docID);
			}
		}

		return recommendedNews;
	}

	private static List<ScoreDoc> retrieveAllRecommendedDocs(List<TopDocs> topDocsForUserNews) {
		List<ScoreDoc> allRecommendedDocs = new ArrayList<ScoreDoc>();
		for (TopDocs topDocs : topDocsForUserNews) {
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {					
				allRecommendedDocs.add(scoreDoc);
			}
		}

		Collections.sort(allRecommendedDocs, new Comparator<ScoreDoc>() {
			public int compare(ScoreDoc s1, ScoreDoc s2) {
				if (s1.score > s2.score) {
					return -1;
				} else if (s1.score == s2.score) {
					return 0;
				} else {
					return 1;
				}
			}
		});

		return allRecommendedDocs;
	}

	private static List<TopDocs> retrieveTopDocsForUserNews(List<News> userNews, IndexSearcher searcher,
			Analyzer analyzer, MoreLikeThis mlt) throws IOException {
		List<TopDocs> topDocsForUserNews = new ArrayList<TopDocs>();

		System.out.println("\nRetrieving top docs for each user news...");
		for (News singleUserNews : userNews) {
//			System.out.println("\nFor news with category: " + singleUserNews.getCategory() + " and filename: "
//					+ singleUserNews.getFileName());
			topDocsForUserNews.add(retrieveTopDocsForSingleUserNews(singleUserNews, searcher, analyzer, mlt));
		}

		return topDocsForUserNews;
	}

	private static TopDocs retrieveTopDocsForSingleUserNews(News singleUserNews, IndexSearcher searcher,
			Analyzer analyzer, MoreLikeThis mlt) throws IOException {
		MoreLikeThisQuery query = new MoreLikeThisQuery(singleUserNews.getContent(), new String[] { "content" },
				analyzer, "content");

		// Reader reader = new StringReader(singleUserNews.getContent());
		// Query query = mlt.like("content", reader);

		TopDocs topDocs = searcher.search(query, TOP_HITS_COUNT);
//		System.out.println("Total hits: " + topDocs.totalHits);

		return topDocs;
	}

	private static void createIndex(List<List<News>> allNews, Directory indexDir) {
		Date start = new Date();
		try {
			System.out.println("Indexing all news ...");

			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(analyzer);

			IndexWriter writer = new IndexWriter(indexDir, config);
			indexDocs(writer, allNews);

			int numIndexed = writer.numDocs();
			System.out.println("Total files indexed: " + numIndexed);

			writer.close();

			Date end = new Date();
			System.out.println("Indexing took " + (end.getTime() - start.getTime()) + " total milliseconds");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void indexDocs(IndexWriter writer, List<List<News>> allNews) throws IOException {
		for (List<News> newsByCategory : allNews) {
			for (News news : newsByCategory) {
				if (!news.isRead()) {
					FieldType fieldType = new FieldType();
					fieldType.setTokenized(true);
					fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
					fieldType.setStored(true);
					fieldType.setStoreTermVectors(true);
					// fieldType.setStoreTermVectorPositions(true);

					Document doc = new Document();
					doc.add(new Field("content", news.getContent(), fieldType));
					doc.add(new StringField("category", news.getCategory(), Field.Store.YES));
					doc.add(new StringField("fileName", news.getFileName(), Field.Store.YES));

					writer.addDocument(doc);
				}
			}
		}
	}

	private static List<List<News>> extractAllNews() throws IOException {
		List<List<News>> news = new ArrayList<List<News>>();

		File allNewsDir = new File(NEWS_DIR);
		if (allNewsDir.exists() && allNewsDir.isDirectory()) {
			File[] categoryDirs = allNewsDir.listFiles();
			for (int i = 0; i < categoryDirs.length; i++) {
				if (categoryDirs[i].exists() && categoryDirs[i].isDirectory()) {
					System.out.println("Extracting news from category: " + categoryDirs[i].getName());

					File[] categoryNewsFiles = categoryDirs[i].listFiles();
					List<News> newsByCategory = new ArrayList<News>();
					for (int j = 0; j < categoryNewsFiles.length; j++) {
						if (categoryNewsFiles[j].isFile()) {
							newsByCategory.add(extractNewsByCategory(categoryNewsFiles[j], categoryDirs[i].getName()));
						}
					}

					news.add(newsByCategory);
				}
			}
		}

		return news;
	}

	private static News extractNewsByCategory(File newsFile, String category) throws IOException {
		StringBuilder contentBuilder = new StringBuilder();

		try (BufferedReader br = new BufferedReader(new FileReader(newsFile))) {
			String currentLine;
			while ((currentLine = br.readLine()) != null) {
				contentBuilder.append(currentLine).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < contentBuilder.length(); i++) {
			if (ILLEGAL_CHARS.indexOf(contentBuilder.charAt(i)) != -1) {
				contentBuilder.setCharAt(i, ' ');
			}
		}

		return new News(contentBuilder.toString(), category, newsFile.getName(), false);
	}
}
