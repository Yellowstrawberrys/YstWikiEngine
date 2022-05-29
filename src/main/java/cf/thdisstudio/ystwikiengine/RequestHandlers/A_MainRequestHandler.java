package cf.thdisstudio.ystwikiengine.RequestHandlers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
public class A_MainRequestHandler {

    @RequestMapping("/")
    public void main(HttpServletResponse response){
        response.setHeader("Location", "/w/"+URLEncoder.encode("YSTWIKI:대문", StandardCharsets.UTF_8));
        response.setStatus(302);
    }
}
