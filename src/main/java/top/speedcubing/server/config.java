package top.speedcubing.server;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import top.speedcubing.lib.utils.SQL.SQLUtils;

import java.io.FileReader;
import java.util.*;
import java.util.regex.Pattern;

public class config {
    public static String DatabaseURL;
    public static String DatabaseUser;
    public static String DatabasePassword;

    public static List<String> ranks = new ArrayList<>();
    public static Set<Pattern> blockedLog = new HashSet<>();
    public static Set<Pattern> blockedTab = new HashSet<>();
    public static Set<Pattern> blockedMod = new HashSet<>();

    public static Map<String, String[]> colors = new HashMap<>();
    public static Map<String, Set<String>> rankPermissions = new HashMap<>();

    public static Map<String, Set<String>> grouppermissions = new HashMap<>();

    public void reload() {
        try {
            JsonObject object = new JsonParser().parse(new FileReader("../../../server.json")).getAsJsonObject();
            DatabaseURL = object.getAsJsonObject("database").get("url").getAsString();
            DatabaseUser = object.getAsJsonObject("database").get("user").getAsString();
            DatabasePassword = object.getAsJsonObject("database").get("password").getAsString();
            LeftCpsLimit = object.get("leftcpslimit").getAsInt();
            RightCpsLimit = object.get("rightcpslimit").getAsInt();
            blockedLog.clear();
            blockedTab.clear();
            blockedMod.clear();
            object.get("spigotblockedlog").getAsJsonArray().forEach(a -> blockedLog.add(Pattern.compile(a.getAsString())));
            object.get("allowtabcomplete").getAsJsonArray().forEach(a -> blockedTab.add(Pattern.compile(a.getAsString())));
            object.get("blockedmod").getAsJsonArray().forEach(a -> blockedMod.add(Pattern.compile(a.getAsString())));
            colors.clear();
            rankPermissions.clear();
            ranks.clear();
            for (Map.Entry<String, JsonElement> c : object.getAsJsonObject("ranks").entrySet()) {
                String[] color = new Gson().fromJson(c.getValue().getAsJsonObject().get("texts").getAsJsonArray().toString(), new TypeToken<String[]>() {
                }.getType());
                colors.put(c.getKey(), new String[]{color[0], color[0].lastIndexOf('§') == -1 ? "" : ("§" + color[0].charAt(color[0].lastIndexOf('§') + 1)), color[1]});
                rankPermissions.put(c.getKey(), new Gson().fromJson(c.getValue().getAsJsonObject().get("permissions").getAsJsonArray().toString(), new TypeToken<Set<String>>() {
                }.getType()));
                ranks.add(c.getKey());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void reloadDatabase() {
        for (String s : SQLUtils.getStringArray(speedcubingServer.systemConnection.select("groups", "name", "1"))) {
            config.grouppermissions.put(s, Sets.newHashSet(SQLUtils.getString(speedcubingServer.systemConnection.select("groups", "perms", "name='" + s + "'")).split("\\|")));
        }
    }

    public static int LeftCpsLimit = Integer.MAX_VALUE;
    public static int RightCpsLimit = Integer.MAX_VALUE;
    public static String SERVERIP = Bukkit.getPort() % 2 == 0 ? "cracked.speedcubing.top" : "speedcubing.top";
}
