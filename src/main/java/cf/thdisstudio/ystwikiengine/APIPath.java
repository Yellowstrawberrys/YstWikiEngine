package cf.thdisstudio.ystwikiengine;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Map;

import static cf.thdisstudio.ystwikiengine.Data.queryToMap;

@RequestMapping(path = "/api/v0", produces= MediaType.APPLICATION_JSON_VALUE)
@RestController
public class APIPath {

    @RequestMapping("/version")
    public String onVersion(){
        return "{\"releaseType\": \"beta\",\"version\": \"v0.0.0.1\",\"buildNumber\": 20220525}";
    }

    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    public String login(@RequestBody String body, HttpSession session, HttpServletResponse response) {
        try {
            Map<String, String> query = queryToMap(body);
            response.setStatus(302);
            if (query.containsKey("signup")) {
                String token = Data.signup(query.get("uid"), query.get("pw"));
                if (token.equals("signup_already"))
                    response.setHeader("Location", "/auth/login/?err=signup_already");
                else {
                    session.setAttribute("accessToken", token);
                    response.setHeader("Location", "/");
                }
            } else {
                String token = Data.login(query.get("uid"), query.get("pw"));
                if (token != null) {
                    session.setAttribute("accessToken", token);
                    response.setHeader("Location", "/");
                } else {
                    response.setHeader("Location", "/auth/login/?err=login_failed");
                }
            }
        }catch (Exception e){
            response.setHeader("Location", "/auth/login/?err=server_err");
        }
        return "";
    }

}
