package server.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import maps.DocsInTopicMap;
import maps.TopicDistWrapper;
import maps.TopicsInDocsMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import database.PoemJDBCTemplate;
import database.domain.Poem;

@Controller
public class DeviceDetectionController implements InitializingBean {
	
	private Map<Integer, List<TopicDistWrapper>> topicsInDocsMap;
	private Map<Integer, List<Integer>> docsInTopicsMap;
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
    	topicsInDocsMap = TopicsInDocsMap.generateMap();
    	docsInTopicsMap = DocsInTopicMap.generateMap();
	}
    
    @RequestMapping(value = "/angularjs-http-service-ajax-post-json-data-code-example", method = RequestMethod.GET)
    public ModelAndView httpServicePostJSONDataExample( ModelMap model ) {
    	return new ModelAndView("httpservice_post_json");
    }
    
    @RequestMapping(value = "/getAllPoems", method = RequestMethod.POST)
    @ResponseBody
    public List<Poem> getAllPoems() {
    	ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
    	PoemJDBCTemplate poemJDBCTemplate = (PoemJDBCTemplate) context.getBean("poemJDBCTemplate");
    	
    	List<Poem> poems = new ArrayList<Poem>();
    	poems.addAll(poemJDBCTemplate.getAllPoems());
    	System.out.println("Hit The Endpoint!");
    	return poems;
    }
    
    
    @RequestMapping(value = "/getPoemByID", method = RequestMethod.POST)
    @ResponseBody
    public Poem getPoemByID(@RequestBody int id) {
    	System.out.println("ID IS: " + id);
    	ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
    	PoemJDBCTemplate poemJDBCTemplate = (PoemJDBCTemplate) context.getBean("poemJDBCTemplate");
    	
    	Poem poem = new Poem();
    	poem = poemJDBCTemplate.getPoemByID(id);
    	System.out.println("Sending Title: " + poem.getTitle() + " And Author: " + poem.getAuthor() + "\nPoem Text: \n" + poem.getText());
    	return poem;
    }

    @RequestMapping(value = "/savePoem_json", method = RequestMethod.POST)
    @ResponseBody
    public String savePoem_JSON( @RequestBody Poem poem )   {	
    	//ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
    	//PoemJDBCTemplate poemJDBCTemplate = (PoemJDBCTemplate) context.getBean("poemJDBCTemplate");
    		
    	//int id = poemJDBCTemplate.insertPoem(poem);
    	//poem.setPoemID(id);
    	//PipeLine.addToPoemCollection(poem);
    	
    	//TopicModelling.runMallet();
    	System.out.println("Author: " + poem.getAuthor() + "\nTitle: " + poem.getTitle() + "\nPoem Text: \n" + poem.getText());
    	return "Success!";
    }
    

    @RequestMapping(value = "/findSimilar", method = RequestMethod.POST)
    @ResponseBody
    public Poem findSimilar( @RequestBody List<Integer> history) throws IOException   {	
    	ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
    	PoemJDBCTemplate poemJDBCTemplate = (PoemJDBCTemplate) context.getBean("poemJDBCTemplate");
    	
    	int sID = getSimilarID(history);
    	
    	System.out.println(sID);
    	Poem poem = poemJDBCTemplate.getPoemByID(sID);
    	return poem;
    }
    
    
    public int getSimilarID(List<Integer> history) {
    	int current = history.get(history.size() - 1);
    	int topic = findTopTopic(current);
    	int mS = getMostSimilar(topic, history);
    	return mS;
    }
    
    public int findTopTopic(int poemID) {
    	return topicsInDocsMap.get(poemID).get(0).getTopicId();
    }
    
    public int getMostSimilar(int topicID, List<Integer> history) {
    	List<Integer> topList = docsInTopicsMap.get(topicID);
    	for(int id: topList) {
    		if (!history.contains(id))
    			return id;
    	}
    	return -1;
    }

	
}