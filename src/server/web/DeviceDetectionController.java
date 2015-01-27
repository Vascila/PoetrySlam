package server.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import server.mallet.PipeLine;
import server.mallet.TopicModelling;
import server.util.MapFunctions;
import database.PoemJDBCTemplate;
import database.domain.Poem;

@Controller
public class DeviceDetectionController implements InitializingBean {
	
	private ApplicationContext context;
	private PoemJDBCTemplate poemJDBCTemplate;
	private MapFunctions mapFunctions;
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
    	context = new ClassPathXmlApplicationContext("Beans.xml");
    	poemJDBCTemplate = (PoemJDBCTemplate) context.getBean("poemJDBCTemplate");
    	mapFunctions = new MapFunctions();
	}
    
    @RequestMapping(value = "/angularjs-http-service-ajax-post-json-data-code-example", method = RequestMethod.GET)
    public ModelAndView httpServicePostJSONDataExample( ModelMap model ) {
    	return new ModelAndView("httpservice_post_json");
    }
    
    @RequestMapping(value = "/getAllPoems", method = RequestMethod.POST)
    @ResponseBody
    public List<Poem> getAllPoems() {
    	List<Poem> poems = new ArrayList<Poem>();
    	poems.addAll(poemJDBCTemplate.getAllPoems());
    	return poems;
    }
    
    @RequestMapping(value = "/getNewestPoems", method = RequestMethod.POST)
    @ResponseBody
    public List<Poem> getNewestPoems() {
    	List<Poem> poems = new ArrayList<Poem>();
    	poems.addAll(poemJDBCTemplate.getNewestPoems());
    	System.out.println("Getting Newest Poems!");
    	return poems;
    }
    
    
    @RequestMapping(value = "/getPoemByID", method = RequestMethod.POST)
    @ResponseBody
    public Poem getPoemByID(@RequestBody int id) {
    	Poem poem = new Poem();
    	poem = poemJDBCTemplate.getPoemByID(id);
    	System.out.println("Sending Title: " + poem.getTitle() + " And Author: " + poem.getAuthor() + "\nPoem Text: \n" + poem.getText());
    	return poem;
    }

    @RequestMapping(value = "/savePoem_json", method = RequestMethod.POST)
    @ResponseBody
    public String savePoem_JSON( @RequestBody Poem poem )   {	
    		
    	int id = poemJDBCTemplate.insertPoem(poem);
    	poem.setPoemID(id);
    	PipeLine.addToPoemCollection(poem);
    	
    	TopicModelling.runMallet();
    	System.out.println("Author: " + poem.getAuthor() + "\nTitle: " + poem.getTitle() + "\nPoem Text: \n" + poem.getText());
    	return "Success!";
    }
    

    @RequestMapping(value = "/findSimilar", method = RequestMethod.POST)
    @ResponseBody
    public Poem findSimilar( @RequestBody List<Integer> history) throws IOException   {	
    	int sID = mapFunctions.getSimilarID(history);
    	Poem poem = poemJDBCTemplate.getPoemByID(sID);
    	return poem;
    }
    
    @RequestMapping(value = "/findNew", method = RequestMethod.POST)
    @ResponseBody
    public List<Poem> findNew( @RequestBody List<Integer> history) throws IOException   {
    	List<Integer> nID = mapFunctions.getNewID(history);
    	List<Poem> poems = poemJDBCTemplate.getPoemsByID(nID);
    	
    	return poems;
    }    
    
    @RequestMapping(value = "/getRandDist", method = RequestMethod.POST)
    @ResponseBody
    public List<Poem> getRandDist( @RequestBody List<Integer> history) throws IOException   {
    	
    	System.out.println("Getting Random Distribution");
    	List<Integer> nID = mapFunctions.getRandomDist(history);
    	List<Poem> poems = poemJDBCTemplate.getPoemsByID(nID);
    	
    	return poems;
    }
	
}