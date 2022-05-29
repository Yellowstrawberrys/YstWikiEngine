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
                                 <p id="logoTitle">Sign in / Sign up - YST WIKI</p>
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
                            <div id="userInfo"></div>
                        </div>
                    </div>
                    <div id="contentpane">
                        <p id="title">Sign in / Sign up</p>
                        <hr id="splitter"/>
                        <div class="contents">
                            %s
                            <form action="/api/v0/auth/" method="post">
                                <label>
                                    <h4 style="margin-bottom: 0;">Username</h4>
                                    <input id="uid" name="uid" style="width: 20vw; height: 30px;" type="text" aria-placeholder="Username" placeholder="Username"/>
                                </label>
                                <br/>
                                <label>
                                    <h4 style="margin-bottom: 0;">Password</h4>
                                    <input id="pw" name="pw" style="width: 20vw; height: 30px;" type="password" aria-placeholder="Username" placeholder="Username"/>
                                </label>
                                <br/><br/>
                                <button class="confirmButton" style="margin-left: 11vw" type="submit" name="signup">Sign up</button>
                                <button class="confirmButton" type="submit" style="margin-left: 10px" name="login">Sign in</button>
                            </form>
                        </div>
                    </div>
                </body>
            </html>""";

    String err = """
                    <div class="alert">
                      <span class="closebtn" onclick="this.parentElement.style.display='none';">&times;</span>\s
                      <strong>Error</strong> %s
                    </div>
            """;

    @RequestMapping("/login/")
    public String login(@RequestParam(value = "err", required = false) String errCode){
        if(errCode == null || errCode.isEmpty() && errCode.isBlank())
            return loginTemplate.formatted("");
        else{
            return switch (errCode) {
                case "signup_already" -> loginTemplate.formatted(err.formatted("The username already exists."));
                case "server_err" -> loginTemplate.formatted(err.formatted("Error occurred during processing your request in our server :("));
                case "login_failed" ->
                        loginTemplate.formatted(err.formatted("Login Failed. Please check your username or password."));
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
