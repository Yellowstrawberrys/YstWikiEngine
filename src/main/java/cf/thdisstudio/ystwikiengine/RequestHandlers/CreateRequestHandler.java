package cf.thdisstudio.ystwikiengine.RequestHandlers;

import cf.thdisstudio.ystwikiengine.Data;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cf.thdisstudio.ystwikiengine.Data.queryToMap;

@RequestMapping("/create")
@RestController
public class CreateRequestHandler {
    String template = """
             <!DOCTYPE html>
             <html lang="en">
             <head>
                 <meta charset="UTF-8">
                 <title>Create a new document - YST WIKI</title>
                 <link rel="stylesheet" href="/css/main.css">
                 <link rel="stylesheet" href="/css/md.css">
                 <script src="/main.js"></script>
             </head>
                 <body>
                     <div id="sidebar">
                         <a href="/">
                             <div id="logo">
                                 <img id="logoImg" src="/imgs/logo.png"/>
                                 <p id="logoTitle">YST WIKI</p>
                             </div>
                        </a>
                     </div>
                     <div id="infoBar">
                         <div id="searchBarSection">
                             <form id="searchBar" action="/search/">
                                 <input id="search" maxlength="2048" name="q" type="text" aria-autocomplete="both" aria-haspopup="false" autocapitalize="none" autocomplete="off" autocorrect="off" autofocus="" role="combobox" spellcheck="false" title="Search" value="" aria-label="Search">
                                 <button type="submit" id="searchButton" value="">
                                     <img src="/imgs/Search.svg" style="width: 25px; height: 25px; margin: 0" alt="search"/>
                                 </button>
                             </form>
                             <button id="userIcon" onclick="userInfo()" style="position: absolute; right: 10px; top: 10px; background: none; border: none;">
                                 <img src="/imgs/User.svg" style="width: 25px; height: 25px; margin: 0;" alt="user"/>
                             </button>
                             <div id="userInfo"></div>
                         </div>
                     </div>
                     <div id="contentpane">
                         <p id="title">Create document '%s'</p>
                         %s
                 </body>
             </html>
            """;

    String save = """
            <button type="button" id="moreButton" onclick="docMenu()">
                                     <img src="/imgs/More.svg" style="width: 25px; height: 25px; margin: 0" alt="more..."/>
                                 </button>
                                     <div id="docMenu">
                                         <button class="docMenuButton" type="submit">
                                             <img src="/imgs/Save.svg" style="float: left; width: 25px; height: 25px; margin: 0" alt="save"/>
                                             Save
                                         </button><br/>
                                         <button type="button" class="docMenuButton" onclick="window.location = '/'">
                                             Cancel
                                         </button>
                                     </div>
                                     <hr id="splitter"/>
                                     <textarea class="contents" name="contents"></textarea>
                         </div>
                         <textarea id="sideContents" name="sideContents"></textarea>""";

    @RequestMapping("/{document}")
    public String main(@PathVariable("document") String title, HttpSession session, HttpServletResponse response) throws SQLException {
        List<String> infos = Data.getDocument(title);
        if(infos == null) {
            int level = Data.getPermission(Data.getUserId(session.getAttribute("accessToken")), title);
            if(level == 0){
                response.setStatus(403);
                return "";
            }
            if(level == 2 || level == 3 || level > 5)
                return Data.formatLogin(template.formatted(title, save), session.getAttribute("accessToken"));
            else
                return Data.formatLogin(template.formatted(title, "<blockquote style=\"border-color: red;\"><p style=\"color: red;\">권한이 부족합니다.</p></blockquote></div>"), session.getAttribute("accessToken"));
        }else
            return "N/A";
    }

    @RequestMapping(value = "/save/{document}", method = RequestMethod.POST)
    public String main(@PathVariable("document") String title, @RequestBody String request, HttpServletResponse response, HttpSession session) throws SQLException {
        int level = Data.getPermission(Data.getUserId(session.getAttribute("accessToken")), title);
        if(level == 0){
            response.setStatus(403);
            return "";
        }
        if((level == 2 || level == 3 || level > 5) && Data.getDocument(title) == null)
            Data.createDocument(title, queryToMap(request).get("contents"), "");
        response.setHeader("Location", "/w/"+ URLEncoder.encode(title, StandardCharsets.UTF_8));
        response.setStatus(302);
        return "";
    }
}
