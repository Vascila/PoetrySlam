package crawler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import server.mallet.PipeLine;
import database.PoemJDBCTemplate;
import database.domain.Poem;


public class PageParser implements InitializingBean, Runnable {
	
	private static final String TEMP_URL = "http://www.bartleby.com/101/118.html";
	private static final String collectionDir = "OtherText/urls1.txt";

	public void afterPropertiesSet() throws Exception {

	}
	@Override
	public void run() {
		List<URL> urls;
		try {
			urls = readUrls();
			List<Poem> poems = parseUrls(urls);
			savePoems(poems);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
	}
	
    public static void main(String[] args) throws IOException, SQLException {
    	//(new Thread(new PageParser())).start();
    	//updateAuthorNames();
    	removeBrokenPoems();
    }

    
    public List<Poem> parseUrls(List<URL> urls) throws IOException, InterruptedException {
        

        List<Poem> parsedPoems = new ArrayList<Poem>();
        List<URL> failedHits = new ArrayList<URL>();
        
        String authorName, poemTitle, poemLine = "";
        Document doc = null;
        Element author = null;
        Elements poem = null;
        
        for(URL page: urls) {
        	
        	try {
	        	Poem tempPoem = new Poem();
	        	print("Fetching %s...", page);
		        doc = Jsoup.parse(page, 3000);
		        
		        author = doc.select("table").get(6);
		        poem = doc.select("table").get(8).getElementsByTag("tr");
		        
		        poemTitle = author.select("tr").get(2).select("font").text();
		        authorName = author.select("tr").get(3).select("font").get(1).text();
		        
		        tempPoem.setAuthor(fixAuthorName(authorName));
		        tempPoem.setTitle(poemTitle);
		        
	//	        System.out.println(fixAuthorName(authorName));
	//	        System.out.println(poemTitle);
		        
		        for(Element e: poem) {
		        	poemLine += fixHtmlTags(e.select("td").get(0).text()) + "\n";
		    		//System.out.println(poemLine);
		        }	
		        tempPoem.setText(poemLine);
		        parsedPoems.add(tempPoem);
		        poemLine = "";
		        Thread.sleep(1500);
		        
        	} catch (SocketTimeoutException ste) {
        		failedHits.add(page);
        		System.out.println("Could not fetch URL: " + page);
        	} catch (Exception e) {
        		System.out.println("Page broke: " + page);
        	}
        }

    	System.out.println("\n###############################\n");
    	System.out.println("FAILED HITS\n");
        for (URL furl: failedHits) {
        	System.out.println(furl);
        }
    	System.out.println("\n###############################\n");
        return parsedPoems;
    }
    
    public List<URL> readUrls() throws IOException {
    	
    	List<URL> urls = new ArrayList<URL>();
    	
        try(BufferedReader br = new BufferedReader(new FileReader(collectionDir))) {
        	
            for(String line; (line = br.readLine()) != null; ) {
            	
            	try {
					urls.add(new URL(line));
				} catch (MalformedURLException e) {
					System.out.println("Is not a valid URL: " + line);
				}
            }
        }
        return urls;
    }
    
    public void savePoems(List<Poem> poems)   {	
    		
    	ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
    	PoemJDBCTemplate poemJDBCTemplate = (PoemJDBCTemplate) context.getBean("poemJDBCTemplate");
    	int id;
    	
    	for(Poem poem: poems) {
    		print("Storing:\t%s", poem.getTitle()); 
	    	id = poemJDBCTemplate.insertPoem(poem);
	    	poem.setPoemID(id);
	    	PipeLine.addToPoemCollection(poem);
    	}
    }
    
    public static void updateAuthorNames() throws SQLException {
    	
    	ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
    	PoemJDBCTemplate poemJDBCTemplate = (PoemJDBCTemplate) context.getBean("poemJDBCTemplate");
    	
    	poemJDBCTemplate.updateAuthorNames();
    }
    
    public static void removeBrokenPoems() throws SQLException {
    	ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
    	PoemJDBCTemplate poemJDBCTemplate = (PoemJDBCTemplate) context.getBean("poemJDBCTemplate");
    	
    	//poemJDBCTemplate.removeBrokenPoems();
    	//poemJDBCTemplate.checkDuplicateTitle();
    	poemJDBCTemplate.fixDoubleTitles();
    }
     
    private String fixHtmlTags(String line) {

    	Pattern p0 = Pattern.compile("\\u0097");
    	Pattern p1 = Pattern.compile("\\u0092");
    	Matcher m = p0.matcher(line);
    	if (m.find()) {
    	    // replace first number with "number" and second number with the first
    	    line = m.replaceAll("—");
    	}
    	m = p1.matcher(line);
    	if (m.find()) {
    		line = m.replaceAll("'");
    	}
    	return line;
    }
    
    private void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private String fixAuthorName(String name) {
    	if (name.endsWith("."))
    		return name.substring(0, name.length() - 1);
    	return name;
    }
    
    private String fixAtuthorName1(String name) {
    	String temp = "";
    	if (name.startsWith("By "))
    		temp = name.substring(3, name.length());
    	return temp.substring(0, temp.indexOf('('));
    }
    
}
