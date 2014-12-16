package server.web;

import java.util.ArrayList;
import java.util.List;

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
import database.PoemJDBCTemplate;
import database.domain.Poem;

@Controller
public class DeviceDetectionController {
    
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
    	System.out.println("Sending Title: " + poem.getTitle() + " And Author: " + poem.getAuthor());
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
    	System.out.println("Author: " + poem.getAuthor() + "\nTitle: " + poem.getTitle());
    	return "Success!";
    }
    

}