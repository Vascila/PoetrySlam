package server.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import database.domain.Poem;

@Controller
public class DeviceDetectionController {
    
    @RequestMapping(value = "/angularjs-http-service-ajax-post-json-data-code-example", method = RequestMethod.GET)
    public ModelAndView httpServicePostJSONDataExample( ModelMap model ) {
    	return new ModelAndView("httpservice_post_json");
    }

    @RequestMapping(value = "/savePoem_json", method = RequestMethod.POST)
    @ResponseBody
    public void savePoem_JSON( @RequestBody Poem poem )   {	
    	//ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
    	//PoemJDBCTemplate poemJDBCTemplate = (PoemJDBCTemplate) context.getBean("poemJDBCTemplate");
    		
    	//int id = poemJDBCTemplate.insertPoem(poem);
    	//poem.setPoemID(id);
    	//PipeLine.addToPoemCollection(poem);
    	
    	//TopicModelling.runMallet();
    	
    	System.out.println("Author: " + poem.getAuthor() + "\nTitle: " + poem.getTitle() + "\nText: " + poem.getText());
    }
    

}