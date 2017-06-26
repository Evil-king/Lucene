package com.hwq.helloLucene;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
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

public class HelloLucene {
	
	//建立索引
	public void index(){
		IndexWriter writer = null;
		try {
			//1、创建Directory对象
//			Directory directory = new RAMDirectory();//建立在内存中
			Directory directory = FSDirectory.open(new File("/Users/Macx/lucene/index01"));//创建在硬盘中
			//2、创建IndexWriter
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35));
			writer = new IndexWriter(directory, iwc);
			//3、创建Document对象
			Document document = null;
			//4、为Document添加Field
			File f = new File("/Users/Macx/Desktop/lucene/example");
			for(File file : f.listFiles()){
				String content = FileUtils.readFileToString(file);
				System.out.println(content);
				document = new Document();
				document.add(new Field("content",new FileReader(file)));
				document.add(new Field("name",file.getName(),Field.Store.YES,Field.Index.NOT_ANALYZED));
				document.add(new Field("path",file.getAbsolutePath(),Field.Store.YES,Field.Index.NOT_ANALYZED));
				//5、通过IndexWriter添加文档到索引中
				writer.addDocument(document);
			}
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
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
	
	//搜索
	public void search(){
		try {
			//1、创建Directory
			Directory directory = FSDirectory.open(new File("/Users/Macx/lucene/index01"));//创建在硬盘中
			//2、创建IndexReader
			IndexReader reader = IndexReader.open(directory);
			//3、根据IndexReader创建IndexSearch
			IndexSearcher searcher = new IndexSearcher(reader);
			//4、创建搜索的Query
			//创建parser来确定要搜索文件的内容，第二个参数表示搜索的域
			QueryParser parser = new QueryParser(Version.LUCENE_35, "content", new StandardAnalyzer(Version.LUCENE_35));
			//创建query，表示搜索域为content中包含java的文档
			Query query = parser.parse("java");
			//5、根据searcher搜索并且返回TopDocs
			TopDocs tdDocs = searcher.search(query, 100);
			//6、根据TopDocs获取ScoreDoc[] 对象
			ScoreDoc[] scoreDocs = tdDocs.scoreDocs;
			for(ScoreDoc sd:scoreDocs){
				//7、根据searcher和ScordDoc对象获取具体的Document
				Document d = searcher.doc(sd.doc);
				//8、根据Document对象获取需要的值
				System.out.println(d.get("name")+"["+d.get("path")+"]");
			}
			//9、关闭reader
			reader.clone();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
