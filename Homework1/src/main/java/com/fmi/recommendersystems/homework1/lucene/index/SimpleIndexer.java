package com.fmi.recommendersystems.homework1.lucene.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

//public class SimpleIndexer {
//
//	private static final String indexDirectory = "Index";
//
//	private static final String dirToBeIndexed = "user_profile";
//	
//	private List<Integer> scannedDocs = new ArrayList<>();
//	private Map<String, Integer> termsCount = new HashMap<>();
//	private Set<String> terms = new HashSet<>();
//
//	public static void main(String[] args) throws Exception {
//
//		File indexDir = new File(indexDirectory);
//
//		File dataDir = new File(dirToBeIndexed);
//
//		SimpleIndexer indexer = new SimpleIndexer();
//
////		int numIndexed = indexer.index(indexDir, dataDir);
////
////		System.out.println("Total files indexed " + numIndexed);
//		
//		Directory directory = FSDirectory.open(indexDir);
//		IndexReader indexReader = DirectoryReader.open(directory);
//		
//		// Find most frequent term
//		Map<String, Long> termFrequencies = new HashMap<>();
//        final Fields fields = MultiFields.getFields(indexReader);
//        final Iterator<String> iterator = fields.iterator();
//
//        long maxFreq = Long.MIN_VALUE;
//        String freqTerm = "";
//        while(iterator.hasNext()) {
//            final String field = iterator.next();
//            final Terms terms = MultiFields.getTerms(indexReader, field);
//            final TermsEnum it = terms.iterator(null);
//            BytesRef term = it.next();
//            while (term != null) {
//                final long freq = it.totalTermFreq();
//                termFrequencies.put(term.utf8ToString(), freq);
//                term = it.next();
//            }
//        }
//        
//        Map<String,Long> topTen = termFrequencies.entrySet().stream()
//        		.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
//        		.limit(20)
//        		.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
//        
//        for (Map.Entry<String, Long> termFreq : topTen.entrySet()) {
//        	System.out.println(termFreq.getKey() + ": " + termFreq.getValue());
//		}
//
////        System.out.println(freqTerm + " " + maxFreq);
//		
////		Map<String, Double> termFrequencies = indexer.getTermFrequencies(indexReader, 5);
////		for (Map.Entry<String, Double> termFreq : termFrequencies.entrySet()) {
////			System.out.println(termFreq.getKey() + ": " + termFreq.getValue());
////		}
//
////		TotalTermFreqComparator cmp = new HighFreqTerms.TotalTermFreqComparator();
////		TermStats[] highFreqTerms = HighFreqTerms.getHighFreqTerms(indexReader, 5, "content", cmp);
////
////		List<String> terms = new ArrayList<>(highFreqTerms.length);
////		for (TermStats ts : highFreqTerms) {
////		    terms.add(ts.termtext.utf8ToString());
////		}
////		
//		
//	}
//	
////	public HashMap<Integer, HashMap> tfIdfScore(int numberOfDocs) throws CorruptIndexException, ParseException {
////
////	    int noOfDocs = 12;
////
////	    HashMap<Integer, HashMap> scoreMap = new HashMap<Integer, HashMap>();
////	    //HashMap<Integer, float[]> scoreMap = new HashMap<Integer, float[]>();
////
////
////	    try {
////	    	File indexDir = new File(indexDirectory);
////	    	Directory directory = FSDirectory.open(indexDir);
////			IndexReader re = DirectoryReader.open(directory);
////	       // IndexReader re = IndexReader.open(ramMemDir);
////
////	        int i = 0;
////	        for (int k = 0; k < numberOfDocs; k++) {
////	            int freq[];
////	            TermFreqVector termsFreq;
////	            TermFreqVector termsFreqDocId;
////	            //TermFreqVector termsFreq3[];
////	            HashMap<String, Float> wordMap = new HashMap<String, Float>();
////	            String terms[];
////	            float score[] = null;
////
////	            //termsFreq3=re.getTermFreqVectors(currentDocID);
////	            termsFreq = re.getTermFreqVector(k, "doccontent");
////	            termsFreqDocId = re.getTermFreqVector(k, "docid");
////
////	            int aInt = Integer.parseInt(termsFreqDocId.getTerms()[0]);
////	            freq = termsFreq.getTermFrequencies();
////
////	            terms = termsFreq.getTerms();
////
////	            int noOfTerms = terms.length;
////	            score = new float[noOfTerms];
////	            DefaultSimilarity simi = new DefaultSimilarity();
////	            for (i = 0; i < noOfTerms; i++) {
////	                int noofDocsContainTerm = re.docFreq(new Term("doccontent", terms[i]));
////	                // System.out.println(terms[i]+"\t"+freq[i]);
////	                //int noofDocsContainTerm = docsContainTerm(terms[i], "docnames");
////	                float tf = simi.tf(freq[i]);
////	                float idf = simi.idf(noofDocsContainTerm, noOfDocs);
////	                wordMap.put(terms[i], (tf * idf));
////
////	            }
////	            scoreMap.put(aInt, wordMap);
////	        }
////
////
////	    } catch (IOException e) {
////	        // score = null;
////	        e.printStackTrace();
////	    }
////
////
////
////	    //Map<Integer,Float[]> scoreMap=new Map<Integer, Float[]>(); 
////
////
////	    return scoreMap;
////	}
//
//	private int index(File indexDir, File dataDir) throws IOException {
//
//		Directory dir = FSDirectory.open(indexDir);
//		
//		Analyzer analyzer = new StandardAnalyzer();
//
//		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_4, analyzer);
//
//		IndexWriter indexWriter = new IndexWriter(dir, config);
//
//		File[] files = dataDir.listFiles();
//		
//		for (File f : files) {
//			System.out.println("Indexing file " + f.getCanonicalPath());
//
//			Document doc = new Document();
//			String text = "";
//			
//			FileReader fr = new FileReader(f);
//			BufferedReader br = new BufferedReader(fr);
//            String line;
//            while ((line = br.readLine()) != null) {
//                   text += line;
//            }
//            
//            br.close();
//			
////			doc.add(new StoredField("content", text));
////			doc.add(new StoredField("fileName", f.getCanonicalPath()));
//            FieldType fieldType = new FieldType();
//            fieldType.setIndexed(true);
//            fieldType.setTokenized(true);
//            fieldType.setStored(true);
//            fieldType.setStoreTermVectors(true);
//            fieldType.setStoreTermVectorPositions(true);
//            fieldType.freeze();
//            Field field = new Field("content", text, fieldType);
//            
//            doc.add(field);
//			
//			indexWriter.addDocument(doc);
//		}
//
//		int numIndexed = indexWriter.maxDoc();
//
//		indexWriter.close();
//
//		return numIndexed;
//
//	}
//	
//	private Map<String, Double> getTermFrequencies(IndexReader reader, int docId) {
//		
//        try {
//          Terms vector = reader.getTermVector(docId, "content");
//           TermsEnum termsEnum = null;
//           termsEnum = vector.iterator(termsEnum);
//           Map<String, Double> frequencies = new HashMap<>();
//           BytesRef text = null;
//           TFIDFSimilarity tfidfSim = new DefaultSimilarity();
//           boolean scannedDoc = scannedDocs.contains(docId);
//          
//           while ((text = termsEnum.next()) != null) {
//               String term = text.utf8ToString();
//               Term termInstance = new Term("content", term);
//               long indexDf = reader.docFreq(termInstance);
//               int docCount = reader.numDocs();
//                                
//               //increment the term count in the terms count lookup if doc not scanned before
//               if(!scannedDoc) {
//                 if(termsCount.containsKey(termInstance.toString())) {
//                        Integer cnt = termsCount.get(termInstance.toString());
//                        cnt++;
//                        termsCount.replace(termInstance.toString(), cnt);
//                      } else {
//                        termsCount.put(termInstance.toString(), 1);
//                      }
//               }
//               
//               DocsEnum docs = termsEnum.docs(MultiFields.getLiveDocs(reader),null,0);
//              
//               //calculate the TF-IDF of the term, as compared to all documents in the corpus (the Apache Lucene Index)
//               double tfidf = 0.0;
//               while(docs.nextDoc() != DocsEnum.NO_MORE_DOCS)  {
//                 tfidf = tfidfSim.tf(docs.freq()) * tfidfSim.idf(docCount, indexDf);
//               }
//            
//               frequencies.put(term, tfidf);
//               scannedDocs.add(docId);
//               terms.add(term);
//           }
//           return frequencies;
//        } catch (Exception e) {
//           e.printStackTrace();
//        }
//           return null;
//       }
//}
