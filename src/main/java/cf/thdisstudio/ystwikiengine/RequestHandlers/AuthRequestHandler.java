package cf.thdisstudio.ystwikiengine.RequestHandlers;

import cf.thdisstudio.ystwikiengine.Data;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.session.Session;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthRequestHandler {

    String loginTemplate = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>YST WIKI</title>
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
                    <div id="contentpane">
                        <p id="title">로그인/회원가입</p>
                        <hr id="splitter"/>
                        <div class="contents">
                            %s
                            <form action="/api/v0/auth/" method="post">
                                <label>
                                    <h4 style="margin-bottom: 0;">유저이름</h4>
                                    <input id="uid" name="uid" style="width: 20vw; height: 30px;" type="text" aria-placeholder="유저이름을 입력하시오" placeholder="유저이름을 입력하시오"/>
                                </label>
                                <br/>
                                <label>
                                    <h4 style="margin-bottom: 0;">비밀번호</h4>
                                    <input id="pw" name="pw" style="width: 20vw; height: 30px;" type="password" aria-placeholder="비밀번호를 입력하시오" placeholder="비밀번호를 입력하시오"/>
                                </label>
                                <br/><br/>
                                <button class="confirmButton" style="margin-left: 11vw" type="submit" name="signup">회원가입</button>
                                <button class="confirmButton" type="submit" style="margin-left: 10px" name="login">로그인</button>
                            </form>
                        </div>
                    </div>
                </body>
            </html>""";

    String err = """
                    <div class="alert">
                      <span class="closebtn" onclick="this.parentElement.style.display='none';">&times;</span>\s
                      <strong>오류</strong> %s
                    </div>
            """;

    @RequestMapping("/login/")
    public String login(@RequestParam(value = "err", required = false) String errCode){
        if(errCode == null || errCode.isEmpty() && errCode.isBlank())
            return loginTemplate.formatted("");
        else{
            return switch (errCode) {
                case "signup_already" -> loginTemplate.formatted(err.formatted("이미 해당 이름을 가진 계정이 존재합니다."));
                case "server_err" -> loginTemplate.formatted(err.formatted("서버에서 처리하는중 오류가 발생하였습니다."));
                case "login_failed" ->
                        loginTemplate.formatted(err.formatted("로그인에 실패하였습니다. 비밀번호 또는 유저아이디가 맞는지 확인해주시길 바랍니다."));
                default -> loginTemplate.formatted("");
            };
        }
    }

    @RequestMapping("/logout/")
    public void logout(HttpSession session, HttpServletResponse response){
        Data.logout(session.getAttribute("accessToken"));
        session.removeAttribute("accessToken");
        response.setStatus(302);
        response.setHeader("location", "/");
    }

}
