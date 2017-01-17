[README in English](https://github.com/knshen/JSearcher/blob/master/README.md)
# JSearcher
JSearcher是一个纯Java编写的，可扩展的分布式爬虫框架。JSearcher的主要特性包括：

* 常规爬虫应用的快速构建
* 多个爬虫任务并发执行
* 单机多线程爬取
* 多个节点分布式爬取
* 二进制数据的下载：如图片和pdf文档
* 数据存储：内置Mysql，MongoDB，ElasticSearch三种存储控制器，或者你也可以定制自己的存储方式
* 代理服务器设置

# 安装
目前本项目还不支持从maven安装
### 源码下载
`git clone https://github.com/knshen/JSearcher`

进入jsearcher目录，运行`mvn install`，下载相关依赖

### 添加Jar包
添加JSearcher.jar到相关项目的classPath

# 快速开始
以网站[http://quotes.toscrape.com/](http://quotes.toscrape.com/)为例（这是一个记录名人名言的网站），我们想要爬取该网站上的名言，爬取内容包括名言内容和作者。该例子的完整代码位于[代码](https://github.com/knshen/JSearcher-examples/tree/master/quotes)。

### 定义DTO对象
DTO结构化地定义了待爬取的数据。在DTO类定义中，必须为属性定义对应的setter和getter，方法名也必须与属性名对应。
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

### 定义数据抽取器
数据抽取器定义了从一个页面抽取目标数据的方法。你可以使用正在表达式，JSoup自带的css选择器或者xpath语法解析网页。下面的程序使用了JSoup的css选择器：
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
抽取器必须继承DataExtractor类，并且实现extract方法，该方法返回一个List，List中每个元素是一个DTO对象；参数doc是一个Document对象，表示当前网页对象。

### 定义输出器
输出器定义了保存目标数据的方法。输出器并不是必需的。
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
输出器必须继承Outputer类，并且实现output方法。在output方法中可以以任意方式保存数据，例如保存为json，xml，csv，数据库等。


### 爬虫参数配置
基础配置通过yaml文件定义。配置文件定义了单个爬取任务的一些参数，这些参数包括：
* 线程数量
* 最大访问页面数量
* 爬取任务名称
* DTO对象路径
* 持久化方式
* 集群信息
* 数据库或者ES的配置信息（可选）

具体的配置方法和示例请见[配置文件示例](https://github.com/knshen/JSearcher-examples/blob/master/quotes/quotes.yml)

### 入口程序
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
首先必须定义种子URL列表，这些URL将直接被添加到待访问URL队列中；随后，生成爬虫调度器，加载配置文件，使用SpiderConfig类配置数据抽取器和输出器；最后运行爬虫任务。

# 许可证
[Apache License 2.0](https://github.com/knshen/JSearcher/blob/master/LICENSE)

# 文档
待完成