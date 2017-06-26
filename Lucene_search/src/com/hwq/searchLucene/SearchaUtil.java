package com.hwq.searchLucene;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class SearchaUtil {
	private Directory directory;
	private static  IndexReader reader;
	
	private String[] ids = {"1","2","3","4","5","6"};
	private String[] emails = {"aa@itat.org","bb@itat.org","cc@cc.org","dd@sina.org","ee@zttc.edu","ff@itat.org"};
	private String[] contents = {
			"welcome to visited the space,I like book",
			"hello boy, I like pingpeng ball",
			"my name is cc I like game",
			"I like football",
			"I like football and I like basketball too",
			"I like movie and swim"
	}; 
	private Date[] date = null;
	private int[] attachs = {2,3,1,4,5,5};
	private String[] names = {"zhangsan","lisi","john","jetty","mike","jake"};
	private Map<String,Float> map = new HashMap<String,Float>();
	
	//初始化加载
	public SearchaUtil(){
		directory = new RAMDirectory();//建立在内存中
		setDate();
		index();
	}	
	//建立索引
	public void index(){
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(directory, new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
			Document doc = null;
			writer.deleteAll();//建立索引之前全部删除
			for(int i=0;i<ids.length;i++){
				doc = new Document();
				doc.add(new Field("id", ids[i],Field.Store.YES,Field.Index.ANALYZED_NO_NORMS));
				doc.add(new Field("emails", emails[i], Field.Store.YES,Field.Index.NOT_ANALYZED));
				doc.add(new Field("contents", contents[i], Field.Store.NO,Field.Index.ANALYZED));
				doc.add(new Field("names", names[i], Field.Store.YES,Field.Index.NOT_ANALYZED_NO_NORMS));
				//设置整数的索引
				doc.add(new NumericField("attachs",Field.Store.YES,true).setIntValue(attachs[i]));
				//设置日期的索引
				doc.add(new NumericField("date",Field.Store.YES,true).setLongValue(date[i].getTime()));
				String etc = emails[i].substring(emails[i].lastIndexOf("@")+1);
				if(map.containsKey(etc)){
					doc.setBoost(map.get(etc));
				}else{
					doc.setBoost(0.5f);
				}
				writer.addDocument(doc);
			}
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(writer!=null)writer.close();
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void setDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		date = new Date[6];
		try {
			date[0] = sdf.parse("2011-09-11");
			date[1] = sdf.parse("2012-10-11");
			date[2] = sdf.parse("2023-12-20");
			date[3] = sdf.parse("2001-05-20");
			date[4] = sdf.parse("2002-09-10");
			date[5] = sdf.parse("2017-09-25");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
	
	public IndexSearcher getSearch(){
		try {
			if(reader==null){
				reader = IndexReader.open(directory);
			}else{
				IndexReader tr = IndexReader.openIfChanged(reader);
				if(tr!=null){
					reader.close();
					reader = tr ;
				}
			}
			return new IndexSearcher(reader);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	//精确查询
	public void searchByTerm(String field,String name,int num){
		try {
			IndexSearcher searcher = getSearch();
			Query query = new TermQuery(new Term(field,name));
			TopDocs toc = searcher.search(query, num);
			System.out.println("一共查询了:"+toc.totalHits);
			for(ScoreDoc sd : toc.scoreDocs){
				Document doc = searcher.doc(sd.doc);
				System.out.println(doc.get("id")+"---->"+
						doc.get("names")+"["+doc.get("emails")+"]-->"+doc.get("id")+","+
						doc.get("attachs")+","+doc.get("date"));
			}
			searcher.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//查询字符串以什么开头以什么结尾
	public void searchByTermRange(String field,String start,String end,int num){
		try {
			IndexSearcher searcher = getSearch();
			Query query = new TermRangeQuery(field, start, end, true, true);
			TopDocs toc = searcher.search(query, num);
			System.out.println("一共查询了:"+toc.totalHits);
			for(ScoreDoc sd : toc.scoreDocs){
				Document doc = searcher.doc(sd.doc);
				System.out.println(doc.get("id")+"---->"+
						doc.get("names")+"["+doc.get("emails")+"]-->"+doc.get("id")+","+
						doc.get("attachs")+","+doc.get("date"));
			}
			searcher.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//查询数字类型 因为TermRangeQuery无法查询
	public void searchByNumTermRange(String field,int start,int end,int num){
		try {
			IndexSearcher searcher = getSearch();
			Query query = NumericRangeQuery.newIntRange(field, start, end, true, true);
			TopDocs toc = searcher.search(query, num);
			System.out.println("一共查询了:"+toc.totalHits);
			for(ScoreDoc sd : toc.scoreDocs){
				Document doc = searcher.doc(sd.doc);
				System.out.println(doc.get("id")+"---->"+
						doc.get("names")+"["+doc.get("emails")+"]-->"+doc.get("id")+","+
						doc.get("attachs")+","+doc.get("date"));
			}
			searcher.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//前缀搜索
	public void searchByPrefix(String field,String value,int num){
		try {
			IndexSearcher searcher = getSearch();
			Query query = new PrefixQuery(new Term(field,value));
			TopDocs toc = searcher.search(query, num);
			System.out.println("一共查询了:"+toc.totalHits);
			for(ScoreDoc sd : toc.scoreDocs){
				Document doc = searcher.doc(sd.doc);
				System.out.println(doc.get("id")+"---->"+
						doc.get("names")+"["+doc.get("emails")+"]-->"+doc.get("id")+","+
						doc.get("attachs")+","+doc.get("date"));
			}
			searcher.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//通配符搜索
	public void searchByWildcard(String field,String value,int num){
		try {
			IndexSearcher searcher = getSearch();
			//在传入的value中可以使用通配符 ?和* ?表示可以匹配一个字符 *表示可以匹配任意多个字符
			Query query = new WildcardQuery(new Term(field,value));
			TopDocs toc = searcher.search(query, num);
			System.out.println("一共查询了:"+toc.totalHits);
			for(ScoreDoc sd : toc.scoreDocs){
				Document doc = searcher.doc(sd.doc);
				System.out.println(doc.get("id")+"---->"+
						doc.get("names")+"["+doc.get("emails")+"]-->"+doc.get("id")+","+
						doc.get("attachs")+","+doc.get("date"));
			}
			searcher.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//组合搜索
	public void searchByBoolean(int num){
		try {
			IndexSearcher searcher = getSearch();
			BooleanQuery query = new BooleanQuery();
			/**
			 * Occur.MUST表示必须出现
			 * Occur.SHOULD表示可以出现
			 * Occur.MUST_NOT表示不能出现
			 */
			query.add(new TermQuery(new Term("names","zhangsan")),Occur.MUST);
			query.add(new TermQuery(new Term("contents","game")),Occur.SHOULD);
			TopDocs toc = searcher.search(query, num);
			System.out.println("一共查询了:"+toc.totalHits);
			for(ScoreDoc sd : toc.scoreDocs){
				Document doc = searcher.doc(sd.doc);
				System.out.println(doc.get("id")+"---->"+
						doc.get("names")+"["+doc.get("emails")+"]-->"+doc.get("id")+","+
						doc.get("attachs")+","+doc.get("date"));
			}
			searcher.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//词语搜索 仅限应为
	public void searchByPhrase(int num){
		try {
			IndexSearcher searcher = getSearch();
			PhraseQuery query = new PhraseQuery();
			query.setSlop(1);
			//第一个Term
			query.add(new Term("contents","i"));
			//第二个Term
			query.add(new Term("contents","football"));
			TopDocs toc = searcher.search(query, num);
			System.out.println("一共查询了:"+toc.totalHits);
			for(ScoreDoc sd : toc.scoreDocs){
				Document doc = searcher.doc(sd.doc);
				System.out.println(doc.get("id")+"---->"+
						doc.get("names")+"["+doc.get("emails")+"]-->"+doc.get("id")+","+
						doc.get("attachs")+","+doc.get("date"));
			}
			searcher.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//模糊查询
	public void searchByFuzzy(String field,String name,int num){
		try {
			IndexSearcher searcher = getSearch();
			Query query = new FuzzyQuery(new Term("names","mike"));
			TopDocs toc = searcher.search(query, num);
			System.out.println("一共查询了:"+toc.totalHits);
			for(ScoreDoc sd : toc.scoreDocs){
				Document doc = searcher.doc(sd.doc);
				System.out.println(doc.get("id")+"---->"+
						doc.get("names")+"["+doc.get("emails")+"]-->"+doc.get("id")+","+
						doc.get("attachs")+","+doc.get("date"));
			}
			searcher.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
