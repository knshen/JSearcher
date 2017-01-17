[README in English](https://github.com/knshen/JSearcher/blob/master/README.md)
# JSearcher
JSearcher��һ����Java��д�ģ�����չ�ķֲ�ʽ�����ܡ�JSearcher����Ҫ���԰�����

* ��������Ӧ�õĿ��ٹ���
* ����������񲢷�ִ��
* �������߳���ȡ
* ����ڵ�ֲ�ʽ��ȡ
* ���������ݵ����أ���ͼƬ��pdf�ĵ�
* ���ݴ洢������Mysql��MongoDB��ElasticSearch���ִ洢��������������Ҳ���Զ����Լ��Ĵ洢��ʽ
* �������������

# ��װ
Ŀǰ����Ŀ����֧�ִ�maven��װ
### Դ������
`git clone https://github.com/knshen/JSearcher`

����jsearcherĿ¼������`mvn install`�������������

### ���Jar��
���JSearcher.jar�������Ŀ��classPath

# ���ٿ�ʼ
����վ[http://quotes.toscrape.com/](http://quotes.toscrape.com/)Ϊ��������һ����¼�������Ե���վ����������Ҫ��ȡ����վ�ϵ����ԣ���ȡ���ݰ����������ݺ����ߡ������ӵ���������λ��[����](https://github.com/knshen/JSearcher-examples/tree/master/quotes)��

### ����DTO����
DTO�ṹ���ض����˴���ȡ�����ݡ���DTO�ඨ���У�����Ϊ���Զ����Ӧ��setter��getter��������Ҳ��������������Ӧ��
```java
public class QuoteDTO {
    String content;
    String author;
	
    public String getContent() {
        return content;
    }
	
    public void setContent(String content) {
        this.content = content;
    }
	
    public String getAuthor() {
        return author;
    }
	
    public void setAuthor(String author) {
        this.author = author;
    }
}
```

### �������ݳ�ȡ��
���ݳ�ȡ�������˴�һ��ҳ���ȡĿ�����ݵķ����������ʹ�����ڱ��ʽ��JSoup�Դ���cssѡ��������xpath�﷨������ҳ������ĳ���ʹ����JSoup��cssѡ������
```java
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import sjtu.sk.parser.DataExtractor;

public class QuotesExtractor extends DataExtractor {
    @Override
    public List<Object> extract(Document doc, String url) {
        List<Object> data = new ArrayList<Object>();
        List<Element> contents = doc.select("span.text");
        List<Element> authors = doc.select("small.author");
		
        assert contents.size() == authors.size();
		
        for(int i=0; i<contents.size(); i++) {
            QuoteDTO quote = new QuoteDTO();
            quote.setContent(contents.get(i).text());
            quote.setAuthor(authors.get(i).text());
            data.add(quote);
        }
        return data;
    }
}
```
��ȡ������̳�DataExtractor�࣬����ʵ��extract�������÷�������һ��List��List��ÿ��Ԫ����һ��DTO���󣻲���doc��һ��Document���󣬱�ʾ��ǰ��ҳ����

### ���������
����������˱���Ŀ�����ݵķ���������������Ǳ���ġ�
```java
import java.util.List;
import sjtu.sk.outputer.Outputer;

public class QuotesOutputer extends Outputer {
    @Override
    public boolean output(String task_name, List<Object> data) {
        for(Object obj : data) {
            QuoteDTO quote = (QuoteDTO)obj;
            System.out.println(quote.getContent() + "\n--- " + quote.getAuthor() + "\n");
        }
        return true;    
    }
}
```
���������̳�Outputer�࣬����ʵ��output��������output�����п��������ⷽʽ�������ݣ����籣��Ϊjson��xml��csv�����ݿ�ȡ�


### �����������
��������ͨ��yaml�ļ����塣�����ļ������˵�����ȡ�����һЩ��������Щ����������
* �߳�����
* ������ҳ������
* ��ȡ��������
* DTO����·��
* �־û���ʽ
* ��Ⱥ��Ϣ
* ���ݿ����ES��������Ϣ����ѡ��

��������÷�����ʾ�����[�����ļ�ʾ��](https://github.com/knshen/JSearcher-examples/blob/master/quotes/quotes.yml)

### ��ڳ���
```java
import java.util.ArrayList;
import java.util.List;
import sjtu.sk.scheduler.DefaultScheduler;
import sjtu.sk.scheduler.SpiderConfig;
import sjtu.sk.url.manager.URL;

public class QuotesSpider {
    public static void main(String[] args) {
        //define URL seeds
        List<URL> seeds = new ArrayList<URL>();
        for(int i=1; i<=10; i++)
            seeds.add(new URL("http://quotes.toscrape.com/page/" + i));
		
        //create a single scheduler, load configuration file
        DefaultScheduler ds = DefaultScheduler.createDefaultScheduler("quotes.yml");
        // add extractor and outputer
        SpiderConfig.setDataExtractor(ds, new QuotesExtractor());
        SpiderConfig.setOutputer(ds, new QuotesOutputer());
        // run tasks
        ds.runTask(seeds);
    }
}
```
���ȱ��붨������URL�б���ЩURL��ֱ�ӱ���ӵ�������URL�����У����������������������������ļ���ʹ��SpiderConfig���������ݳ�ȡ��������������������������

# ���֤
[Apache License 2.0](https://github.com/knshen/JSearcher/blob/master/LICENSE)

# �ĵ�
�����