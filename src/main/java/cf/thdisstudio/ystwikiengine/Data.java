package cf.thdisstudio.ystwikiengine;

import netscape.javascript.JSObject;
import org.json.JSONObject;

import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Data {

    static Connection conn;
    static HashMap<String, List<String>> accessTokens = new HashMap<>();

    static String notLogin = ("<div id=\"userInfo\">\n" +
            "                                <button class=\"docMenuButton\" onclick=\"window.location = '/auth/login/'\">\n" +
            "                                    로그인\n" +
            "                                </button>\n" +
            "                            </div>");

    static String Login = ("<div id=\"userInfo\">\n" +
            "                                환영합니다, %s님! <br/>"+
            "                                <button class=\"docMenuButton\" onclick=\"window.location = '/user/@me/settings'\">\n" +
            "                                    설정\n" +
            "                                </button><br/>\n" +
            "                                <button class=\"docMenuButton\" onclick=\"window.location = '/api/v0/logout/'\">\n" +
            "                                    로그아웃\n" +
            "                                </button>\n" +
            "                            </div>");

    public void init(){
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:mariadb://localhost:3306/ystwiki?useUnicode=true&passwordCharacterEncoding=utf-8", "root", "root");
            conn.setAutoCommit(true);
            conn.createStatement().execute("CREATE TABLE IF NOT EXISTS `documents` (\n" +
                    "\t`id` BIGINT(20) NOT NULL,\n" +
                    "\t`title` LONGTEXT NOT NULL COLLATE 'utf8_general_ci',\n" +
                    "\t`contents` LONGTEXT NOT NULL COLLATE 'utf8_general_ci',\n" +
                    "\t`sidecontents` LONGTEXT NOT NULL COLLATE 'utf8_general_ci',\n" +
                    "\t`created_date` LONGTEXT NOT NULL COLLATE 'utf8_general_ci',\n" +
                    "\t`lastedit` LONGTEXT NOT NULL COLLATE 'utf8_general_ci',\n" +
                    "\t`history` LONGTEXT NOT NULL COLLATE 'utf8_general_ci',\n" +
                    "\t`permission` LONGTEXT NOT NULL DEFAULT '{   \"GroupPermissions\": {     \"users\": {       \"permissionSet\": \"rw-\",       \"permissionValue\": 6     }   },   \"UserPermissions\": {        } }' COLLATE 'utf8mb4_bin'\n" +
                    ")\n" +
                    "COLLATE='utf8_general_ci'\n" +
                    "ENGINE=InnoDB\n" +
                    ";\n");
            conn.createStatement().execute("CREATE TABLE IF NOT EXISTS `groups` (\n" +
                    "\t`id` BIGINT(20) NOT NULL,\n" +
                    "\t`name` LONGTEXT NOT NULL COLLATE 'utf8mb4_bin',\n" +
                    "\t`permission` INT(11) NOT NULL DEFAULT -1\n" +
                    ")\n" +
                    "COLLATE='utf8_general_ci'\n" +
                    "ENGINE=InnoDB\n" +
                    ";\n");
            conn.createStatement().execute("CREATE TABLE IF NOT EXISTS `accounts` (\n" +
                    "\t`userid` LONGTEXT NOT NULL COLLATE 'utf8_general_ci',\n" +
                    "\t`username` LONGTEXT NOT NULL COLLATE 'utf8_general_ci',\n" +
                    "\t`password` LONGTEXT NOT NULL COLLATE 'utf8_general_ci',\n" +
                    "\t`groups` LONGTEXT NOT NULL DEFAULT 'users' COLLATE 'utf8_general_ci'\n" +
                    ")\n" +
                    "COLLATE='utf8_general_ci'\n" +
                    "ENGINE=InnoDB\n" +
                    ";\n");
            if(!conn.createStatement().executeQuery("SELECT 1 FROM `ystwiki`.`documents` WHERE id=0;").first()){
                conn.createStatement().executeQuery("INSERT INTO `ystwiki`.`documents` (`id`, `title`, `contents`, `sidecontents`, `created_date`, `lastedit`, `history`)\n" +
                        "                       \n" +
                        "                          VALUES (\n" +
                        "                              0,\n" +
                        "                              'YSTWIKI%3A%EB%8C%80%EB%AC%B8',\n" +
                        "                              '%23%23%23+YSTWIKI%EB%A5%BC+%EC%82%AC%EC%9A%A9%ED%95%B4%EC%A3%BC%EC%85%94%EC%84%9C+%EA%B0%90%EC%82%AC%ED%95%A9%EB%8B%88%EB%8B%A4%21+%E2%99%A5%EF%B8%8F%0D%0A%0D%0A%23+1.+YST+WIKI+%EC%8B%9C%EC%9E%91%ED%95%98%EA%B8%B0%0D%0A%0D%0A%23%23+YST+WIKI+%EB%AC%B8%EB%B2%95%0D%0A%0D%0AYST+WIKI%EC%9D%98+%EB%AC%B8%EB%B2%95%EC%9D%80+%27Markdown%27%EC%99%80+%EB%8F%99%EC%9D%BC%ED%95%98%EA%B2%8C+%EC%82%AC%EC%9A%A9%ED%95%98%EA%B3%A0+%EC%9E%88%EC%8A%B5%EB%8B%88%EB%8B%A4.%0D%0A%3Cbr%2F%3E%EC%9E%90%EC%84%B8%ED%95%9C+Markdown+%EB%AC%B8%EB%B2%95%EC%9D%84+%EC%95%8C%EC%95%84%EB%B3%B4%EC%8B%9C%EB%A0%A4%EB%A9%B4+%5B%EC%9D%B4%EA%B3%B3%5D%28https%3A%2F%2Fwww.markdownguide.org%2Fbasic-syntax%2F%29%EC%9D%84+%ED%81%B4%EB%A6%AD%ED%95%B4%EC%A3%BC%EC%84%B8%EC%9A%94%21',\n" +
                        "                              '%7CYST+WIKI%7C%0D%0A%7C----------%7C%0D%0A%7C%3Cimg+src%3D%22%2Fimgs%2Flogo.png%22+alt%3D%22YST+WIKI%22+width%3D%22200%22%2F%3E%7C%0D%0A%0D%0A%7C%EC%A2%85%EB%A5%98%7C%EB%B2%84%EC%A0%84%7C%0D%0A%7C----%7C----%7C%0D%0A%7C%EC%9C%84%ED%82%A4%EC%97%94%EC%A7%84%7CBETA%7C',\n" +
                        "                              '0',\n" +
                        "                              '0',\n" +
                        "                              '0'\n" +
                        "                          );");
            }

            if(!conn.createStatement().executeQuery("SELECT 1 FROM `ystwiki`.`groups` WHERE id=2;").first())
                conn.createStatement().executeQuery("INSERT INTO `ystwiki`.`groups` (`id`, `name`, `permission`) VALUES (2, 'unknown', 4)");
            if(!conn.createStatement().executeQuery("SELECT 1 FROM `ystwiki`.`groups` WHERE id=1;").first())
                conn.createStatement().executeQuery("INSERT INTO `ystwiki`.`groups` (`id`, `name`, `permission`) VALUES (1, 'user', 6)");
            if(!conn.createStatement().executeQuery("SELECT 1 FROM `ystwiki`.`groups` WHERE id=0;").first())
                conn.createStatement().executeQuery("INSERT INTO `ystwiki`.`groups` (`id`, `name`, `permission`) VALUES (0, 'root', 7)");
        } catch (Exception se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }//Handle errors for Class.forName
    }

    public static List<String> getDocument(String title) throws SQLException {
        ResultSet result = conn.createStatement().executeQuery("SELECT contents,sidecontents FROM `ystwiki`.`documents` WHERE title='"+URLEncoder.encode(title, StandardCharsets.UTF_8)+"';");
        if(result.first()) {
            return Arrays.asList(
                    title,
                    URLDecoder.decode(result.getString(1), StandardCharsets.UTF_8),
                    URLDecoder.decode(result.getString(2), StandardCharsets.UTF_8)
            );
        }else
            return null;
    }

    public static String login(String uid, String pw) throws NoSuchAlgorithmException, SQLException {
        ResultSet result = conn.createStatement().executeQuery("SELECT userid,password FROM `ystwiki`.`accounts` WHERE username='"+URLEncoder.encode(uid, StandardCharsets.UTF_8)+"';");
        if(!result.first())
            return null;
        MessageDigest digest = MessageDigest.getInstance("sha256");
        String hash = new BigInteger(1, digest.digest(pw.getBytes(StandardCharsets.UTF_8))).toString(16);
        if(result.getString(2).equals(hash)){
            String token = String.valueOf(UUID.randomUUID());
            accessTokens.put(token, Arrays.asList(result.getString(1), String.valueOf(System.currentTimeMillis()+86400000)));
            return token;
        }else return null;
    }

    public static String signup(String uid, String pw) throws SQLException, NoSuchAlgorithmException {
        ResultSet result = conn.createStatement().executeQuery("SELECT 1 FROM `ystwiki`.`accounts` WHERE username='"+URLEncoder.encode(uid, StandardCharsets.UTF_8)+"';");
        if(!result.first()){
            String hash = sha256(pw);
            String uhash = String.valueOf(UUID.randomUUID());
            String token = String.valueOf(UUID.randomUUID());
            conn.createStatement().executeQuery("INSERT INTO `ystwiki`.`accounts` (`userid`, `username`, `password`) VALUES ('"+uhash+"', '"+URLEncoder.encode(uid, StandardCharsets.UTF_8)+"', '"+hash+"');");
            accessTokens.put(token, Arrays.asList(uhash, String.valueOf(System.currentTimeMillis()+86400000)));
            return token;
        }else return "signup_already";
    }

    public static void logout(Object token){
        if(token != null)
            accessTokens.remove(token.toString());
    }

    private static String sha256(String pw) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("sha256");
        return new BigInteger(1, digest.digest(pw.getBytes(StandardCharsets.UTF_8))).toString(16);
    }

    /*
    * -1 => Depends on document permission(if document doesn't have any rules then permission level will be rw- | Level 6)
    * 0 => No permission (---)
    * 1 => Permission Edit Only (--x)
    * 2 => Edit Only (-w-)
    * 3 => Permission Edit & Document Edit Only (-wx)
    * 4 => Read Only (r--)
    * 5 => Read & Permission Edit Only (r-x)
    * 6 => Read & Write Only (rw-)
    * 7 => Read & Write & Permission Edit (rwx)
    *
    * Permission Overrides
    * User Document Permission > Group Permission > Document Permission
    * */
    public static int getPermission(String uid, String document) throws SQLException {
        int userPermission = getDocumentUserPermission(uid, document);
        if(userPermission == -1) {
            ResultSet groupNameResult = conn.createStatement().executeQuery("SELECT groups FROM `ystwiki`.`accounts` WHERE userid='" + uid + "';");
            if(!groupNameResult.first())
                return getGroupPermission("unknown");

            String groupName = groupNameResult.getString(1);

            ResultSet groupPermissionResult = conn.createStatement().executeQuery("SELECT permission FROM `ystwiki`.`groups` WHERE name='" + URLEncoder.encode(groupName, StandardCharsets.UTF_8) + "';");
            if(!groupPermissionResult.first())
                return getDocumentGroupPermission(groupName, document);

            int groupPermission = groupPermissionResult.getInt(1);
            if (groupPermission == -1) {
                return getDocumentGroupPermission(groupName, document);
            } else
                return groupPermission;
        }
        return getDocumentGroupPermission("unknown", document);
    }

    public static int getPermission(String uid) throws SQLException {
        ResultSet groupNameResult = conn.createStatement().executeQuery("SELECT groups FROM `ystwiki`.`accounts` WHERE userid='" + uid + "';");
        if(!groupNameResult.first())
            return getGroupPermission("unknown");

        String groupName = groupNameResult.getString(1);

        return getGroupPermission(groupName);
    }

    public static int getGroupPermission(String groupName) throws SQLException {
        ResultSet groupPermissionResult = conn.createStatement().executeQuery("SELECT permission FROM `ystwiki`.`groups` WHERE name='" + URLEncoder.encode(groupName, StandardCharsets.UTF_8) + "';");
        if(!groupPermissionResult.first())
            return getGroupPermission("unknown");
        return groupPermissionResult.getInt(1);
    }

    public static int getDocumentGroupPermission(String group, String documentName) throws SQLException {
        ResultSet resultSet = conn.createStatement().executeQuery("SELECT permission FROM `ystwiki`.`documents` WHERE title='"+URLEncoder.encode(documentName, StandardCharsets.UTF_8)+"';");
        if(!resultSet.first())
            return -1;

        JSONObject jsonObject = new JSONObject(resultSet.getString(1));
        if(jsonObject.getJSONObject("GroupPermissions").has(group)){
            return jsonObject.getJSONObject("GroupPermissions").getJSONObject(group).getInt("permissionValue");
        }else
            return -1;
    }

    public static int getDocumentUserPermission(String user, String documentName) throws SQLException {
        ResultSet resultSet = conn.createStatement().executeQuery("SELECT permission FROM `ystwiki`.`documents` WHERE title='"+URLEncoder.encode(documentName, StandardCharsets.UTF_8)+"';");
        if(!resultSet.first())
            return -1;

        JSONObject jsonObject = new JSONObject(resultSet.getString(1));
        if(jsonObject.getJSONObject("UserPermissions").has(user)){
            return jsonObject.getJSONObject("UserPermissions").getJSONObject(user).getInt("permissionValue");
        }else
            return -1;
    }

    public static boolean isValidToken(String token){
        if(accessTokens.containsKey(token)){
            return Long.parseLong(accessTokens.get(token).get(1)) > System.currentTimeMillis();
        }else
            return false;
    }

    public static String getUsername(String uid) throws SQLException {
        ResultSet result = conn.createStatement().executeQuery("SELECT username FROM `ystwiki`.`accounts` WHERE userid='"+uid+"';");
        result.first();
        return result.getString(1);
    }

    public static String formatLogin(String toFormat, Object token) throws SQLException {
        if(token != null && isValidToken(token.toString())){
            return toFormat.replace("<div id=\"userInfo\"></div>", Login.formatted(getUsername(accessTokens.get(token).get(0))));
        }else
            return toFormat.replace("<div id=\"userInfo\"></div>", notLogin);
    }

    public static String getUserId(Object token){
        if(token != null && isValidToken(token.toString()))
            return accessTokens.get(token).get(0);
        else
            return null;
    }

    public static void editDocument(String title, String content, String sideContents) throws SQLException {
        conn.createStatement().executeQuery("UPDATE `ystwiki`.`documents` SET `contents`='"+content+"', `sidecontents`='"+sideContents+"' WHERE `title`='"+ URLEncoder.encode(title, StandardCharsets.UTF_8) +"';");
    }


    public static void createDocument(String title, String content, String sideContents) throws SQLException {
        ResultSet result = conn.createStatement().executeQuery("SELECT id FROM `ystwiki`.`documents` ORDER BY id DESC LIMIT 1;");
        result.first();
        conn.createStatement().executeQuery("INSERT INTO `ystwiki`.`documents` (`id`, `title`, `contents`, `sidecontents`, `created_date`, `lastedit`, `history`) VALUES ("+(result.getInt(1)+1)+", '"+URLEncoder.encode(title, StandardCharsets.UTF_8)+"', '"+content+"', '"+sideContents+"', '"+System.currentTimeMillis()+"', '"+System.currentTimeMillis()+"', 'N/A');");
    }

    public static List<List<String>> getSearchResults(String q, int max) throws SQLException {
        return getSearchResults(q,0, max);
    }

    public static List<List<String>> getSearchResults(String q, int offset, int max) throws SQLException {
        ResultSet result = conn.createStatement().executeQuery("SELECT title,contents FROM `ystwiki`.`documents` WHERE title LIKE '%"+q+"%';");
        List<List<String>> results = new ArrayList<>();
        for(int i=offset; i < max; i++){
            if(result.next()) {
                String description = URLDecoder.decode(result.getString(2), StandardCharsets.UTF_8);
                results.add(Arrays.asList(URLDecoder.decode(result.getString(1), StandardCharsets.UTF_8), description.substring(0, (Math.min(description.length(), 16))) + "..."));
            }else
                return results;
        }
        return results;
    }

    public static boolean uploadFile(String name, String filePath, String info) throws SQLException {
        ResultSet result = conn.createStatement().executeQuery("SELECT 1 FROM `ystwiki`.`files` WHERE name='"+URLEncoder.encode(name, StandardCharsets.UTF_8)+"';");
        if(result.next())
            return false;
        else{
            conn.createStatement().execute("INSERT INTO `ystwiki`.`files` (`name`, `path`, `information`) VALUES ('"+URLEncoder.encode(name, StandardCharsets.UTF_8)+"', '"+filePath+"', '"+info+"')");
            return true;
        }
    }

    public static List<String> getFile(String name) throws SQLException {
        ResultSet result = conn.createStatement().executeQuery("SELECT path,information FROM `ystwiki`.`files` WHERE name='"+URLEncoder.encode(name, StandardCharsets.UTF_8)+"';");
        if(!result.next())
            return null;
        return Arrays.asList(result.getString(1), result.getString(2));
    }

    public static Map<String, String> queryToMap(String query) {
        if(query == null) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], param.replaceFirst(entry[0]+"=", ""));
            }else{
                result.put(entry[0], "");
            }
        }
        return result;
    }
}
