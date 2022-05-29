package cf.thdisstudio.ystwikiengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class YstWikiEngineApplication {

    public static Data data = new Data();

    public static void main(String[] args) throws ClassNotFoundException {
        data.init();
        SpringApplication.run(YstWikiEngineApplication.class, args);
    }

}
