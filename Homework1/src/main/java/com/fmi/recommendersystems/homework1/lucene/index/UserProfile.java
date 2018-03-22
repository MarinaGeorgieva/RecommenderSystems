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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class UserProfile {
	
	private static final String USER_PROFILE_INDEX_DIR = "UserProfileIndex";
	private static final String USER_PROFILE_DOCS_DIR = "user_profile";
	
//	public static void main(String[] args) throws Exception {
//		
//		UserProfile.index();
//		UserProfile.get();
////		System.out.println("Total files indexed for user profile: " + numIndexed);
//		
//		
////		Map<String, Double> map = profile.extractKeywords();
//		
//		
////		Directory directory = FSDirectory.open(new File(userProfileIndexDirectory));
////		IndexReader indexReader = DirectoryReader.open(directory);
////		
////		DocFreqComparator cmp = new HighFreqTerms.DocFreqComparator();
////		TermStats[] highFreqTerms = HighFreqTerms.getHighFreqTerms(indexReader, 10, "content", cmp);
////
////		List<String> terms = new ArrayList<>(highFreqTerms.length);
////		for (TermStats ts : highFreqTerms) {
////		    terms.add(ts.termtext.utf8ToString());
////		    System.out.println("High freq terms: " + ts.termtext.utf8ToString());
////		}
//	}
	
//	public Map<String, Double> extractKeywords() throws IOException {
//		Map<String, Double> wordIdf = new HashMap<>();
//		
//		Directory directory = FSDirectory.open(indexDirectory);
//		IndexReader indexReader = DirectoryReader.open(directory);
//		
//		DefaultSimilarity similarity = new DefaultSimilarity();
//		int numDocs = indexReader.numDocs();
//		
//		Fields fields = MultiFields.getFields(indexReader);
//		Terms terms = fields.terms("content");
//		TermsEnum termsEnum = terms.iterator(null);
//		while (termsEnum.next() != null) {
//			double idf = similarity.idf(termsEnum.docFreq(), numDocs);
//			String term = termsEnum.term().utf8ToString();
////			System.out.println(term + " idf=" + idf);
//			wordIdf.put(termsEnum.term().utf8ToString(), idf);
//		}
//		
//		Map<String, Double> idfTop15 = wordIdf.entrySet().stream()
//				.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
//				.limit(15)
//				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
//
//		for (Map.Entry<String, Double> termFreq : idfTop15.entrySet()) {
//			System.out.println(termFreq.getKey() + ": " + termFreq.getValue());
//		}
//		
//		return idfTop15;
//	}
	
	public static String get() throws IOException {
		Path indexDirectory = Paths.get(USER_PROFILE_INDEX_DIR);
		Directory dir = FSDirectory.open(indexDirectory);
		
		IndexReader indexReader = DirectoryReader.open(dir);
		
//		Terms terms = indexReader.getTermVector(0, "content");
//		System.out.println(terms.size());
		
		IndexSearcher searcher = new IndexSearcher(indexReader);
		Analyzer analyzer = new StandardAnalyzer();
		
		
		TFIDFSimilarity similarity = new ClassicSimilarity();
		MoreLikeThis mlt = new MoreLikeThis(indexReader, similarity);
		mlt.setAnalyzer(analyzer);
		mlt.setFieldNames(new String[] { "content" });
		mlt.setBoost(true);
		mlt.setMinTermFreq(1);
		mlt.setMinDocFreq(1);
		
		Document doc = indexReader.document(0);
		IndexableField field = doc.getField("content");
		String userProfile = field.stringValue();
//		System.out.println(userProfile);
		
//		Reader reader = new StringReader(userProfile);
//		
//		System.out.println("------------------------------KEYWORDS-----------------------------------");
//		String[] keywords = mlt.retrieveInterestingTerms(reader, "content");
//		for (String keyword : keywords) {
//			System.out.println(keyword);
//		}
		
		return userProfile;
	}
	
	public static void index() throws IOException {
		
		Path docsDirectory = Paths.get(USER_PROFILE_DOCS_DIR);
		if (!Files.isReadable(docsDirectory)) {
			System.out.println("Document directory '" + docsDirectory.toAbsolutePath() + "' does not exist or is not readable, please check the path");
			System.exit(1);
		}
		
		try {
			System.out.println("Indexing to directory '" + USER_PROFILE_INDEX_DIR + "'...");
			
			Path indexDirectory = Paths.get(USER_PROFILE_INDEX_DIR);
			Directory dir = FSDirectory.open(indexDirectory);
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			
			IndexWriter writer = new IndexWriter(dir, config);
			
			File[] files = docsDirectory.toFile().listFiles();
			StringBuilder contentBuilder = new StringBuilder();
			for (File file : files) {
//				System.out.println("Indexing file " + file.getCanonicalPath());
				
//				Document doc = new Document();
				
//				Field pathField = new StringField("path", file.getPath(), Field.Store.YES);
//				doc.add(pathField);
				
				
			    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			 
			        String sCurrentLine;
			        while ((sCurrentLine = br.readLine()) != null)
			        {
			            contentBuilder.append(sCurrentLine).append("\n");
			        }
			    } catch (IOException e) {
			        e.printStackTrace();
			    }
				
//				doc.add(new TextField("content", contentBuilder.toString(), Store.NO));
				
//				System.out.println("adding " + file.getPath());
//				writer.addDocument(doc);
			}
			
			System.out.println("Adding user profile document...");
			Document doc = new Document();
			FieldType fieldType = new FieldType();
			fieldType.setTokenized(true);
			fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
			fieldType.setStored(true);
			fieldType.setStoreTermVectors(true);
			fieldType.setStoreTermVectorPositions(true);
			Field field = new Field("content", contentBuilder.toString(), fieldType);
			
//			doc.add(new TextField("content", contentBuilder.toString(), Store.YES));	
			doc.add(field);
			writer.addDocument(doc);
			
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
