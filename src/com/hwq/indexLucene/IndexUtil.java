package com.hwq.indexLucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

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
	private int[] attachs = {2,3,1,4,5,5};
	private String[] names = {"zhangsan","lisi","john","jetty","mike","jake"};
	private Directory directory = null;
	
	public IndexUtil(){
		try {
			directory = FSDirectory.open(new File("/Users/Macx/lucene/index02"));
		} catch (IOException e) {
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
	
	
	public void merae(){
		IndexWriter writer =null;
		try {
			 writer =
					new IndexWriter(directory,new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
			/*
			 * 会将索引合并为2段，这两段中的被删除的数据会被清空
			 * 特别注意：此处Lucene在3.5之后不建议使用，因为会消耗大量的开销
			 * Lucene会根据情况自动处理的
			 */
			 writer.forceMerge(2);
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
	
	
	//强制清空
	public void fourceDelete(){
		IndexWriter writer =null;
		try {
			 writer =
					new IndexWriter(directory,new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
			writer.forceMergeDeletes();
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
	
	//回复被删除的
	public void undelete(){
		//使用IndexReader进行恢复
		try {
			//恢复删除时，把IndexReader的只读(readyOnle)设置为fasle
			IndexReader reader = IndexReader.open(directory,false);
			reader.undeleteAll();
			reader.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//查询
	public void query(){
		try {
			IndexReader reader = IndexReader.open(directory);
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
