package net.thumbtack.forums;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
//Uncomment to remove debug API
//@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "net.thumbtack.forums.debug..*"))
public class ForumsServer {

    public static void main(String[] args) {
        SpringApplication.run(ForumsServer.class, args);
    }
}
