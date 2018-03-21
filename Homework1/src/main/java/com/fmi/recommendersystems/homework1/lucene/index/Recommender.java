package com.fmi.recommendersystems.homework1.lucene.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Recommender {
	
	private static final String INDEX_DIR = "Index";
	private static final String DOCS_DIR = "20_newsgroups";
	
//	public static void main(String[] args) throws Exception {
//		
////		UserProfile.index();
//		String userProfile = UserProfile.get();
//		
////		Recommender.createIndex();
//		
//		Recommender.getKeywords(userProfile);
//	}
	
	private static void getKeywords(String userProfile) throws IOException {
		Path indexDirectory = Paths.get(INDEX_DIR);
		Directory dir = FSDirectory.open(indexDirectory);
		
		IndexReader indexReader = DirectoryReader.open(dir);
//		IndexSearcher searcher = new IndexSearcher(indexReader);
		Analyzer analyzer = new StandardAnalyzer();
		
		TFIDFSimilarity similarity = new ClassicSimilarity();
		MoreLikeThis mlt = new MoreLikeThis(indexReader, similarity);
		mlt.setAnalyzer(analyzer);
		mlt.setBoost(true);
		mlt.setFieldNames(new String[] { "content" });
		mlt.setMinTermFreq(1);
		mlt.setMinDocFreq(1);
		
		Reader reader = new StringReader(userProfile);
		
		System.out.println("------------------------------KEYWORDS-----------------------------------");
		String[] keywords = mlt.retrieveInterestingTerms(reader, "content");
		for (String keyword : keywords) {
			System.out.println(keyword);
		}
	}

	private static void createIndex() throws IOException {
		Path docsDirectory = Paths.get(DOCS_DIR);
		if (!Files.isReadable(docsDirectory)) {
			System.out.println("Document directory '" + docsDirectory.toAbsolutePath() + "' does not exist or is not readable, please check the path");
			System.exit(1);
		}
		
		Date start = new Date();
		try {
			System.out.println("Indexing to directory '" + INDEX_DIR + "'...");
			
			Path indexDirectory = Paths.get(INDEX_DIR);
			Directory dir = FSDirectory.open(indexDirectory);
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			
			IndexWriter writer = new IndexWriter(dir, config);
			indexDocs(writer, docsDirectory);
			
			int numIndexed = writer.numDocs();
			System.out.println("Total files indexed: " + numIndexed);
			
			writer.close();
			
			Date end = new Date();
			System.out.println(end.getTime() - start.getTime() + " total milliseconds");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void indexDocs(IndexWriter writer, Path path) throws IOException {
		File[] dirs = path.toFile().listFiles();
		for (File dir : dirs) {
			if (dir.isDirectory()) {
				File[] files = dir.listFiles();
				for (File file : files) {
					StringBuilder contentBuilder = new StringBuilder();
					try (BufferedReader br = new BufferedReader(new FileReader(file))) {
						 
				        String sCurrentLine;
				        while ((sCurrentLine = br.readLine()) != null)
				        {
				            contentBuilder.append(sCurrentLine).append("\n");
				        }
				    } catch (IOException e) {
				        e.printStackTrace();
				    }
					
					Document doc = new Document();
					doc.add(new StoredField("content", contentBuilder.toString()));
					
					writer.addDocument(doc);
				}
			}
		}
	}
}
