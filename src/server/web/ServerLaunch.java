package server.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import server.mallet.PipeLine;
import database.PoemJDBCTemplate;
import database.domain.Poem;

@ComponentScan
@EnableAutoConfiguration
public class ServerLaunch {

	public static final String testPoem =
			" Cammel watch the sun rise in the sky " +
			"Milk I ask God the question, Why? "+
			"Steve Why is there hatred in the world? "+
			"Rock Why are there starving boys and girls? "+
			"Why is there addiction everywhere? "+
			"Why are there people not willing to share? "+
			"Why is there prejudice and fear? "+
			"Why are there people not willing to be near? "+
			"I say, God please tell me why. "+
			"Then he whispers me a simple reply, "+
			"You must experience bad to know the good "+
			"You must do right just as you know you should "+
			"You must love me, yourself, and everyone "+
			"You must spread hope, faith, and love until your life is done "+
			"You must do unto others as you want them to do to you "+
			"You must persevere in all you do "+
			"All these things you must embrace "+
			"And God assures, everything else will fall into place.";
	
    public static void main(String[] args) {
    	
//    	ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
//    	PoemJDBCTemplate poemJDBCTemplate = (PoemJDBCTemplate) context.getBean("poemJDBCTemplate");
//    	
//    	Poem poem = new Poem();
//    	poem.setAuthor("Test");
//    	poem.setPoemText(testPoem);
//    	
//    	int id = poemJDBCTemplate.insertPoem(poem);
//    	System.out.println("ID IS:" + id);
//    	poem.setPoemID(id);
//    	PipeLine.addToPoemCollection(poem);
    	
        SpringApplication.run(ServerLaunch.class, args);
    }

}