package com.hwq.searchLucene;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

public class FileIndexUtils {
	private static Directory directory = null; 
	
	static{
		try {
			directory = FSDirectory.open(new File("/Users/Macx/lucene/files/"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Directory getDirectory(){
		return directory;
	}
	
	//建立索引
	public static void index(boolean hasNew){
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(directory,new IndexWriterConfig(Version.LUCENE_35,new StandardAnalyzer(Version.LUCENE_35)));
			if(hasNew){
				writer.deleteAll();
			}
			File f = new File("/Users/Macx/lucene/example");
			Document doc = null;
			for(File files:f.listFiles()){
				doc = new Document();
				doc.add(new Field("content",new FileReader(files)));
				doc.add(new Field("filename",files.getName(),Field.Store.YES,Field.Index.NOT_ANALYZED));
				doc.add(new Field("path",files.getAbsolutePath(),Field.Store.YES,Field.Index.NOT_ANALYZED));
				doc.add(new NumericField("date",Field.Store.YES,true).setLongValue(files.lastModified()));
				doc.add(new NumericField("size",Field.Store.YES,true).setIntValue((int)(files.length()/1024)));
				writer.addDocument(doc);
			}
			
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(writer!=null)
					writer.close();
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//分页查询
	public void searchPage(String query,int pageIndex,int pageSize){
		try {
			Directory directory = FileIndexUtils.getDirectory();
			IndexReader reader = IndexReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(reader);
			QueryParser parser = new QueryParser(Version.LUCENE_35,"content",new StandardAnalyzer(Version.LUCENE_35));
			Query q = parser.parse(query);
			int start = (pageIndex - 1)*pageSize;
			int end = pageIndex * pageSize ; 
			TopDocs tds = searcher.search(q,10);
			ScoreDoc[] sd = tds.scoreDocs;
			for(int i=start;i<end;i++){
				Document doc = searcher.doc(sd[i].doc);
				System.out.println(sd[i].doc+":"+doc.get("path")+"--->"+doc.get("filename"));
			}
			reader.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
	//无分页查询
	public void searchNoPage(String query){
		try {
			Directory directory = FileIndexUtils.getDirectory();
			IndexReader reader = IndexReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(reader);
			QueryParser parser = new QueryParser(Version.LUCENE_35,"content",new StandardAnalyzer(Version.LUCENE_35));
			Query q = parser.parse(query);
			TopDocs tds = searcher.search(q,5);
			ScoreDoc[] sd = tds.scoreDocs;
			for(int i=0;i<sd.length;i++){
				Document doc = searcher.doc(sd[i].doc);
				System.out.println(sd[i].doc+":"+doc.get("path")+"--->"+doc.get("filename"));
			}
			reader.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 根据页码和分页的大小获取上一次最后一个ScoreDoc
	 */
	private ScoreDoc getLastScoreDoc(Query query,IndexSearcher searcher,int pageIndex,int pageSize) throws IOException{
		if(pageIndex==1) return null;//如果是第一页则返回null
		int num = pageIndex*(pageSize-1);//获取上一页数量
		TopDocs tds = searcher.search(query,num);
		return tds.scoreDocs[num-1];
	}
	
	
	//searcherAfter
	public void searcherAfter(String query,int pageIndex,int pageSize){
		try {
			Directory directory = FileIndexUtils.getDirectory();
			IndexReader reader = IndexReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(reader);
			QueryParser parser = new QueryParser(Version.LUCENE_35,"content",new StandardAnalyzer(Version.LUCENE_35));
			Query q = parser.parse(query);
			//先获取上一页的最后一个元素
			ScoreDoc lastSd = getLastScoreDoc(q, searcher, pageIndex, pageSize);
			//通过最后一个元素搜索下页的pageSize个元素
			TopDocs tds = searcher.searchAfter(lastSd,q,pageSize);
			for(ScoreDoc sd : tds.scoreDocs){
				Document doc = searcher.doc(sd.doc);
				System.out.println(sd.doc+":"+doc.get("path")+"--->"+doc.get("filename"));
			}
			reader.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
}
