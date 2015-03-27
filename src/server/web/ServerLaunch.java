package server.web;

import mallet.TopicModelling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableAutoConfiguration
public class ServerLaunch {

	/**
	 * Main method for launching the server
	 */
    public static void main(String[] args) {
    	TopicModelling.runMallet();
        SpringApplication.run(ServerLaunch.class, args);
    }

}
