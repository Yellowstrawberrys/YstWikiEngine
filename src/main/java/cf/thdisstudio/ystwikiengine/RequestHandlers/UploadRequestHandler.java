package cf.thdisstudio.ystwikiengine.RequestHandlers;

import cf.thdisstudio.ystwikiengine.Data;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

@RequestMapping("/upload")
@RestController
public class UploadRequestHandler {

    String template = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>File upload - YST WIKI</title>
                <link rel="stylesheet" href="/css/main.css">
                <link rel="stylesheet" href="/css/md.css">
                <link rel="stylesheet" href="/css/form.css">
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
                            <form id="searchBar">
                                <input id="search" maxlength="2048" name="q" type="text" aria-autocomplete="both" aria-haspopup="false" autocapitalize="none" autocomplete="off" autocorrect="off" autofocus="" role="combobox" spellcheck="false" title="Search" value="" aria-label="Search">
                                <button type="submit" id="searchButton" value="">
                                    <img src="/imgs/Search.svg" style="width: 25px; height: 25px; margin: 0" alt="search"/>
                                </button>
                            </form>
                            <button id="userIcon" onclick="userInfo()" style="position: absolute; right: 10px; top: 10px; background: none; border: none;">
                                <img src="/imgs/User.svg" style="width: 25px; height: 25px; margin: 0;" alt="user"/>
                            </button>
                            <div id="userInfo">
                                <button class="docMenuButton" onclick="window.location = '/user/@me/settings'">
                                    Settings
                                </button><br/>
                                <button class="docMenuButton" onclick="window.location = '/auth/login/'">
                                    Login
                                </button>
                            </div>
                        </div>
                    </div>
                    <div id="contentpane">
                        <p id="title">File Upload</p>
                        <hr id="splitter"/>
                        <div id="contents" style="margin-left: 50px">
                            %s
                            <form action="/upload/files/" method="post" enctype="multipart/form-data">
                                <label class="label_bigletter">Select File</label><br/>
                                <input type="file" accept="image/*" name="file" required>
                                <br/><br/>
                                <label for="fileName" class="label_bigletter">File name</label><br/>
                                <input type="text" id="fileName" style="width: 19vw; height: 25px" name="fileName" required>
                                <br/>
                                <label for="fileSource" class="label_bigletter">File source</label><br/>
                                <input type="text" id="fileSource" style="width: 19vw; height: 25px" name="fileSource" required>
                                <br/><br/>
                                <label for="copyrighter" class="label_bigletter">Copyrighted by</label><br/>
                                <input type="text" id="copyrighter" style="width: 19vw; height: 25px" name="copyrighter" required><br/>
                                <label for="copyright" class="label_bigletter">Copyright</label><br/>
                                <input type="text" id="copyright" style="width: 19vw; height: 25px" name="copyright" required>
                                <br/><br/>
                                <button class="confirmButton" type="submit" style="margin-left: 18vw">Upload</button>
                            </form>
                        </div>
                    </div>
                </body>
            </html>""";

    String err = """
                    <div class="alert">
                      <span class="closebtn" onclick="this.parentElement.style.display='none';">&times;</span>\s
                      <strong>오류</strong> 해당 이름의 파일은 이미 존재합니다.
                    </div>
            """;
    String success = """
                    <div class="alert success">
                      <span class="closebtn" onclick="this.parentElement.style.display='none';">&times;</span>\s
                      <strong>성공</strong> 해당 파일을 업로드하는데 성공하였습니다!
                    </div>
            """;

    @RequestMapping("/")
    public String main(HttpServletResponse response, HttpSession session, @RequestParam(value = "code", required = false) String code) throws SQLException {
        int level = Data.getPermission(Data.getUserId(session.getAttribute("accessToken")));
        if(level == 2 || level == 3 || level > 5){
            if(code != null){
                if(code.equals("success"))
                    return template.formatted(success);
                else if(code.equals("error"))
                    return template.formatted(err);
                else return template.formatted("");
            }else
                return template.formatted("");
        }
        else {
            response.setStatus(403);
            return null;
        }
    }

    String fileInfoJsonTemplate = """
            {
                "name": "%s",
                "version": "%s",
                "source":"%s",
                "copyrightedBy": "%s",
                "copyright": "%s"
            }""";

    @PostMapping("/files/")
    public void createFile(HttpServletResponse response, HttpSession session,
                           @RequestPart("file") MultipartFile file,
                           @RequestParam("fileName") String name,
                           @RequestParam("fileSource") String source,
                           @RequestParam("copyrighter") String copyrighter,
                           @RequestParam("copyright") String copyright) throws SQLException, IOException {
        response.setStatus(302);
        int level = Data.getPermission(Data.getUserId(session.getAttribute("accessToken")));
        if(level == 2 || level == 3 || level > 5){
            String path = "./%s/%s/%s/%s/%s/".formatted(
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                    Calendar.getInstance().get(Calendar.HOUR),
                    Calendar.getInstance().get(Calendar.MINUTE)
            )+file.getOriginalFilename();
            File f = new File(path);
            if(!f.canWrite()){
                response.setStatus(403);
                return;
            }
            f.mkdirs();
            file.transferTo(f);
            if(Data.uploadFile(name, path, fileInfoJsonTemplate.formatted(name, "v1", source, copyrighter, copyright)))
                response.setHeader("Location", "/?code=success");
            else
                response.setHeader("Location", "/?code=failed");
        }else {
            response.setStatus(403);
        }
    }

}
