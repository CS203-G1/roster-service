package csd.roster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@EnableResourceServer
@EnableScheduling
@SpringBootApplication
public class RosterServiceApplication {

    // comment for demo
    public static void main(String[] args) {
        SpringApplication.run(RosterServiceApplication.class, args);
    }
}