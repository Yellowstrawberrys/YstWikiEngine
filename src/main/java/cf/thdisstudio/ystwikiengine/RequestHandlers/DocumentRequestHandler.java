package cf.thdisstudio.ystwikiengine.RequestHandlers;

import cf.thdisstudio.ystwikiengine.Data;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.ins.InsExtension;
import org.commonmark.ext.task.list.items.TaskListItemsExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequestMapping("/w")
@RestController
public class DocumentRequestHandler {
    String template = """
             <!DOCTYPE html>
             <html lang="en">
             <head>
                 <meta charset="UTF-8">
                 <title>%s - YST WIKI</title>
                 %s
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
                         <div class="contents" style="margin: 0; width: 16.6vw">%s</div>
                     </div>
                     <div id="contentpane">
                         <p id="title">%s</p>
                         <button id="moreButton" onclick="docMenu()">
                             <img src="/imgs/More.svg" style="width: 25px; height: 25px; margin: 0" alt="more..."/>
                         </button>
                         <div id="docMenu">
                             <button class="docMenuButton" onclick="window.location = '/edit/%s'">
                                 <img src="/imgs/Edit.svg" style="float: left; width: 25px; height: 25px; margin: 0" alt="edit"/>
                                 Edit
                             </button><br/>
                             <button class="docMenuButton" onclick="window.location = '/history/%s'">
                                 History
                             </button>
                         </div>
                         <hr id="splitter"/>
                         <div class="contents">%s</div>
                     </div>
                 </body>
             </html>
            """;

    String ogTemplate = """ 
            <!-- HTML Meta Tags -->
            <meta name="description" content="%s">
            <meta property="og:url" content="https://wiki.iss.yellowstrawberry.me/">
            <meta property="og:type" content="website">
            <meta property="og:title" content="%s - YST WIKI">
            <meta property="og:description" content="%s">
            <meta property="og:image" content="%s">
            <meta name="twitter:card" content="summary_large_image">
            <meta property="twitter:domain" content="">
            <meta property="twitter:url" content="https://wiki.iss.yellowstrawberry.me/">
            <meta name="twitter:title" content="%s - YST WIKI">
            <meta name="twitter:description" content="%s">
            <meta name="twitter:image" content="%s">
            """;

    Parser parser = Parser
            .builder()
            .extensions(Arrays.asList(AutolinkExtension.create(), StrikethroughExtension.create(), TablesExtension.create(), InsExtension.create(), TaskListItemsExtension.create(), YamlFrontMatterExtension.create()))
            .build();
    HtmlRenderer renderer = HtmlRenderer
            .builder()
            .extensions(Arrays.asList(AutolinkExtension.create(), StrikethroughExtension.create(), TablesExtension.create(), InsExtension.create(), TaskListItemsExtension.create(), YamlFrontMatterExtension.create()))
            .build();


    @RequestMapping("/{document}")
    public String main(@PathVariable("document") String title, HttpSession session, HttpServletResponse response) throws SQLException {
        List<String> infos = Data.getDocument(title);
        List<String> content = toFilePath(infos.get(1));
        List<String> subContent = toFilePath(infos.get(2));
        if(infos != null) {
            if(Data.getPermission(Data.getUserId(session.getAttribute("accessToken")), title) > 3) {
                String s = (content.get(0).length() > 64 ? content.get(0).substring(0, 64) + "..." : content.get(0));
                String img = (subContent.size() > 1 ? subContent.get(1) : (content.size() > 1 ? subContent.get(1) : "/imgs/logo.png"));
                return Data.formatLogin(template.formatted(
                                title,
                                ogTemplate.formatted(
                                        s,
                                        title,
                                        s,
                                        img,
                                        title,
                                        s,
                                        img
                                ),
                                renderer.render(parser.parse(subContent.get(0))),
                                title,
                                title,
                                title,
                                renderer.render(parser.parse(content.get(0)))),
                        session.getAttribute("accessToken")
                );
            }
            else {
                response.setStatus(403);
                return "";
            }
        }else
            return "N/A";
    }

    public List<String> toFilePath(String content) throws SQLException {
        List<String> sts = new ArrayList<>();
        Matcher matcher = Pattern.compile("!\\[.*]\\(.*\\)").matcher(content);
        while (matcher.find()) {
            String st = "![" + matcher.group(1) + "](%s)";
            content = content.replaceAll(st.formatted(matcher.group(2)), st.formatted(Data.getFile(matcher.group(2)).get(0)));
            sts.add(Data.getFile(matcher.group(2)).get(0));
        }
        sts.add(0, content);
        return sts;
    }
}
