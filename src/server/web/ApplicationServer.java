package server.web;

import java.util.ArrayList;
import java.util.List;

import mallet.PipeLine;
import mallet.TopicModelling;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import server.util.MapFunctions;
import wrappers.DocDistWrapper;
import database.PoemJDBCTemplate;
import database.domain.Poem;
import database.domain.PoemLikeWrapper;

@Controller
public class ApplicationServer implements InitializingBean, WebServiceInterface {
	
	private ApplicationContext context;
	private PoemJDBCTemplate poemJDBCTemplate;
	private MapFunctions mapFunctions;
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
    	context = new ClassPathXmlApplicationContext("Beans.xml");
    	poemJDBCTemplate = (PoemJDBCTemplate) context.getBean("poemJDBCTemplate");
    	mapFunctions = new MapFunctions();
	}
    
    public List<Poem> getAllPoems() {
    	List<Poem> poems = new ArrayList<Poem>();
    	poems.addAll(poemJDBCTemplate.getAllPoems());
    	return poems;
    }
   
    public Poem getPoemByID(@RequestBody int id) {
    	Poem poem = poemJDBCTemplate.getPoemByID(id);
    	return poem;
    }

    public boolean savePoem_JSON(@RequestBody Poem poem) {	
    	int id = poemJDBCTemplate.insertPoem(poem);
    	poem.setPoemID(id);
    	PipeLine.addToPoemCollection(poem);
    	TopicModelling.runMallet();
    	return true;
    }
    
    public Poem findSimilar(@RequestBody List<Integer> history) {
    	DocDistWrapper sID = mapFunctions.getSimilarID(history);
    	Poem poem = poemJDBCTemplate.getPoemByID(sID.getDocName());
    	poem.setDistribution(sID.getDistribution());
    	return poem;
    }
    
    @Deprecated
    public List<Poem> getRandDistTopics(@RequestBody List<Integer> history) {
    	List<Integer> nID = mapFunctions.getRandomDistTopics(history);
    	List<Poem> poems = poemJDBCTemplate.getPoemsByID(nID);
    	return poems;
    }
    
    public List<Poem> getRandDistPoems(@RequestBody List<Integer> history) {
    	List<DocDistWrapper> nID = mapFunctions.getRandomDistPoems(history);
    	List<Poem> poems = poemJDBCTemplate.getPoemsFromWrappers(nID);
    	return poems;
    }
    
    public void likePoem(@RequestBody PoemLikeWrapper poem) {
    	poemJDBCTemplate.likePoem(poem);
    }
	
}