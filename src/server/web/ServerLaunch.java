package server.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableAutoConfiguration
public class ServerLaunch {

    public static void main(String[] args) {
        SpringApplication.run(ServerLaunch.class, args);
    }

}