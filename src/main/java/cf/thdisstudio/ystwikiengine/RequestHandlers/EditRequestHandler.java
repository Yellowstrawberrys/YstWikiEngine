package cf.thdisstudio.ystwikiengine.RequestHandlers;

import cf.thdisstudio.ystwikiengine.Data;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.session.Session;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequestMapping("/edit")
@RestController
public class EditRequestHandler {

    String template = """
             <!DOCTYPE html>
             <html lang="en">
             <head>
                 <meta charset="UTF-8">
                 <title>문서 수정하기 - YST WIKI</title>
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
                                 <input id="search" maxlength="2048" name="q" type="text" aria-autocomplete="both" aria-haspopup="false" autocapitalize="none" autocomplete="off" autocorrect="off" autofocus="" role="combobox" spellcheck="false" title="검색" value="" aria-label="검색">
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
                     <form action="/save/%s" method="post">
                         <div id="contentpane">
                                 <p id="title">'%s' 수정하기</p>
                                 %s
                     </form>
                 </body>
             </html>
            """;

    String editing = """
            <button type="button" id="moreButton" onclick="docMenu()">
                                     <img src="/imgs/More.svg" style="width: 25px; height: 25px; margin: 0" alt="more..."/>
                                 </button>
                                     <div id="docMenu">
                                         <button class="docMenuButton" type="submit">
                                             <img src="/imgs/Save.svg" style="float: left; width: 25px; height: 25px; margin: 0" alt="save"/>
                                             저장
                                         </button><br/>
                                         <button type="button" class="docMenuButton" onclick="window.location = '/w/%s'">
                                             취소
                                         </button>
                                     </div>
                                     <hr id="splitter"/>
                                     <textarea class="contents" name="contents">%s</textarea>
                         </div>
                         <textarea id="sideContents" name="sideContents">%s</textarea>""";

    @RequestMapping("/{document}")
    public String main(@PathVariable("document") String title, HttpSession session, HttpServletResponse response) throws SQLException {
        List<String> infos = Data.getDocument(title);
        if(infos != null) {
            int level = Data.getPermission(Data.getUserId(session.getAttribute("accessToken")), title);
            if(level == 0){
                response.setStatus(403);
                return "";
            }
            if(level == 2 || level == 3 || level > 5) {
                return Data.formatLogin(template.formatted(title, title, editing.formatted(title, infos.get(1), infos.get(2))), session.getAttribute("accessToken"));
            }
            else return Data.formatLogin(template.formatted(title, title, "<blockquote style=\"border-color: red;\"><p style=\"color: red;\">권한이 부족합니다.</p></blockquote></div>"), session.getAttribute("accessToken"));
//            if(session.getAttribute("accessToken") != null) {
//                int level = Data.getPermission(Data.getUserId(session.getAttribute("accessToken").toString()), title);
//                if(level == 2 || level == 3 || level > 5)
//                    return Data.formatLogin(template.formatted(title, title, editing.formatted(title, infos.get(1), infos.get(2))), session.getAttribute("accessToken"));
//                else return Data.formatLogin(template.formatted(title, title, "권한이 부족합니다.</div>"), session.getAttribute("accessToken"));
//            }else{
//                int level = Data.getDocumentGroupPermission("unknown", title);
//                if(level == 2 || level == 3 || level > 5)
//                    return Data.formatLogin(template.formatted(title, title, editing.formatted(title, infos.get(1), infos.get(2))), session.getAttribute("accessToken"));
//                else
//                    return Data.formatLogin(template.formatted(title, title, "권한이 부족합니다.</div>"), session.getAttribute("accessToken"));
//            }
        }else
            return "N/A";
    }
}
