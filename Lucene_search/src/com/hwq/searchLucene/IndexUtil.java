package com.hwq.searchLucene;

import java.io.File;
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
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
/**
 * 项目开发中IndexReader尽量是单例模式的
 * IndexWriter也可以是单例模式  这种情况下 finally代码块就不用writer.close
 * @author Fox
 *
 */
public class IndexUtil {
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
	private Directory directory = null;
	private Map<String,Float> map = new HashMap<String,Float>();
	private static  IndexReader reader = null ;
	
	public IndexUtil(){
		try {
			directory = FSDirectory.open(new File("/Users/Macx/lucene/index02"));
			setDate();//设置日期
			map.put("itat.org", 2.0f);
			map.put("zttc.edu", 1.5f);
			reader = IndexReader.open(directory);//单例模式下
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//IndexReader 单例模式下的创建
	public IndexSearcher getSearch(){
		try {
			if(reader == null){
				reader = IndexReader.open(directory);
			}else{
				IndexReader st = IndexReader.openIfChanged(reader);
				if(st != null)
				reader.close();//关闭之前的在赋值
				reader = st;
				return new IndexSearcher(reader);
			}
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return  null;
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

	public void update(){
		IndexWriter writer =null;
		try {
			 writer =
					new IndexWriter(directory,new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
			/*
			 * Lucene并没有提供更新，这里的更新操作其实是如下两个操作的合集
			 * 先删除之后再添加
			 */
			 Document doc = new Document();
			 	doc.add(new Field("id", "11",Field.Store.YES,Field.Index.ANALYZED_NO_NORMS));
				doc.add(new Field("emails", emails[0], Field.Store.YES,Field.Index.NOT_ANALYZED));
				doc.add(new Field("contents", contents[0], Field.Store.NO,Field.Index.ANALYZED));
				doc.add(new Field("names", names[0], Field.Store.YES,Field.Index.NOT_ANALYZED_NO_NORMS));
			 writer.updateDocument(new Term("id", "1"),doc);
			 //选择用commit提交后 finally块中则不用writer.close();
			 writer.commit();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			/**
			 * 如果writer也是单例模式 则可以不用关闭 而用commit提交就行
			 */
//			try {
//				if(writer!=null)writer.close();
//			} catch (CorruptIndexException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
	}
	
	
	
	//查询
	public void query(){
		try {
			//通过reader有效的查询文档的数量
			System.out.println("docNume:"+reader.numDocs());
			System.out.println("docMax:"+reader.maxDoc());
			System.out.println("deleteDocs:"+reader.numDeletedDocs());
			reader.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//删除
	public void delete(){
		IndexWriter writer =null;
		try {
			 writer =
					new IndexWriter(directory,new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
			 //参数是一个选项，可以是一个query,也可以是一个term term是一个精确查找的值
			writer.deleteDocuments(new Term("id","1"));
			writer.close();
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
				System.out.println(etc);
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
}
