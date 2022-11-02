package cf.thdisstudio.ystwikiengine.RequestHandlers;

import cf.thdisstudio.ystwikiengine.Data;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

@RequestMapping("/search")
@RestController
public class SearchRequestHandler {

    String template = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>검색 - YST WIKI</title>
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
                            <button id="userIcon" onclick="userInfo()" style="position: absolute; right: 10px; top: 10px; background: none; border: none;">
                                <img src="/imgs/User.svg" style="width: 25px; height: 25px; margin: 0;" alt="user"/>
                            </button>
                            <div id="userInfo"></div>
                        </div>
                    </div>
                    <div id="contentpane">
                        <p id="title" style="margin-bottom: 20px">검색 결과</p>
                        <hr id="splitter"/>
                        <form id="bigSearchBar">
                            <input id="bigSearch" maxlength="2048" name="q" type="text" aria-autocomplete="both" aria-haspopup="false" autocapitalize="none" autocomplete="off" autocorrect="off" autofocus="" role="combobox" spellcheck="false" title="검색" value="%s" aria-label="검색">
                            <button type="submit" id="bigSearchButton" value="">
                                <img src="/imgs/Search.svg" style="width: 35px; height: 35px; margin: 0" alt="search"/>
                            </button>
                        </form>
                        <div class="contents">%s</div>
                    </div>
                </body>
            </html>""";

    String resultTemplate = """
            <div class="searchResult">
                <a href="/w/%s">
                    <h2>%s</h2>
                    <h4>%s</h4>
                </a>
            </div>""";

    @RequestMapping("/")
    public String search(@RequestParam("q") String query, HttpSession session, HttpServletResponse rep) throws SQLException {
        String results = "";
        for(List<String> infos : Data.getSearchResults(URLEncoder.encode(query, StandardCharsets.UTF_8), 20)){
            if(infos.get(0).equals(query)){
                rep.addHeader("Location", "/w/%s".formatted(URLEncoder.encode(query, StandardCharsets.UTF_8)));
                rep.setStatus(302);
                return "";
            }
            results += resultTemplate.formatted(infos.get(0), infos.get(0), infos.get(1));
        }
        return Data.formatLogin(template.formatted(query, (results.isEmpty() ? "검색 결과가 없습니다. <a href=\"/create/%s\">새로 만들까요?</a>".formatted(query) : results)), session.getAttribute("accessToken"));
    }
}
