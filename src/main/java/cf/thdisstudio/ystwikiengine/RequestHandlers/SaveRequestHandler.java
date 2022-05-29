package cf.thdisstudio.ystwikiengine.RequestHandlers;

import cf.thdisstudio.ystwikiengine.Data;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.session.Session;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static cf.thdisstudio.ystwikiengine.Data.queryToMap;

@RequestMapping("/save")
@RestController
public class SaveRequestHandler {

    @RequestMapping(value = "/{document}", method = RequestMethod.POST)
    public String main(@PathVariable("document") String title, @RequestBody String request, HttpServletResponse response, HttpSession session) throws SQLException {
        List<String> infos = Data.getDocument(title);
        Map<String, String> parameters = queryToMap(request);
        if(!infos.get(1).equals(URLDecoder.decode(request, StandardCharsets.UTF_8))){
            int level = Data.getPermission(Data.getUserId(session.getAttribute("accessToken")), title);
            if(level == 0){
                response.setStatus(403);
                return "";
            }
            if((level == 2 || level == 3 || level > 5) && Data.getDocument(title) != null)
                Data.editDocument(title, parameters.get("contents"), parameters.get("sideContents"));
        }
        response.setHeader("Location", "/w/"+URLEncoder.encode(title, StandardCharsets.UTF_8));
        response.setStatus(302);
        return "";
    }
}
