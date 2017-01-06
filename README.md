[中文版README](https://github.com/knshen/JSearcher/blob/master/README-ch.md)
# JSearcher
JSearcher is a extensible, distributed crawler framework written in Java. The main features include:

* Quick construct for common spiders
* Execute multiple crawler tasks concurrently
* Crawl with multiple threads in a single node
* Crawl with multiple nodes
* Download binary data like images and pdf files
* Data persistency��3 built-in approaches in total: Mysql��MongoDB��ElasticSearch. You can customize your own data persistency approach
* Configure proxy servers

# Installation
### Download Source Code 
`git clone https://github.com/knshen/JSearcher`

### Add Jar File
Add JSearcher.jar to your classPath

### Use Maven
Not implemented yet

# Quick Start
Take this website [http://quotes.toscrape.com/](http://quotes.toscrape.com/) as an example.(This is a website recording quotes), we now want to crawl the quotes on this website, the crawled data includes quote content and its author. The complete code is available at [code](https://github.com/knshen/JSearcher-examples/tree/master/quotes).

### Define DTO Object
**DTO** structurally defines the crawled data. Within the definition of a **DTO**, there must contain setters and getters for attributes, and their names must be consistent with attributes names.
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

### Define Data Extractor
**Data Extractor** defines how to extract data from a web page. You can use regular expression, css selector or xpath parser in JSoup to parse a web page. The following code leverages css selector to extract data:
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
**Data Extractor** must extend the class **DataExtractor** and implements the method **extract**. Method **extract** returns a list of DTOs; the parameter **doc** is a **Document** object, which denotes current web page.

### Define Outputer
**Outputer** defines how to save crawled data. **Outputer** is not necessarily needed.
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
**Outputer** must extends class **Outputer** and implements method **output**. You can save data in this method in your way like saving to a json/xml/csv file or saving to database. 


### Spider Parameters Configuration
Common configurations of a crawl task are done by YAML. YAML file defines several parameters of a single crawl task like:
* Number of threads
* Maximum web pages allowed to visit
* Crawl task name
* DTO path
* Persistent style
* Cluster information
* Database or ES configurations(optional)

To see how to configure and the example, please visit [here](https://github.com/knshen/JSearcher-examples/blob/master/quotes/quotes.yml).

### Spider Entrance
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
First of first, you must define URL seeds, which will be added to "to visit" URL queue later; then, create a spider scheduler, load configuration file, use **SpiderConfig** to configure **Data Extractor** and **Outputer**; finally run the crawl task.

# License
[Apache License 2.0](https://github.com/knshen/JSearcher/blob/master/LICENSE)

# Documentation
To do...