package top.speedcubing.server.utils;

import com.google.common.collect.Sets;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import top.speedcubing.server.database.Database;
import top.speedcubing.server.events.ConfigReloadEvent;

import java.io.FileReader;
import java.util.*;
import java.util.regex.Pattern;

public class config {
    public static ConfigReloadEvent event = new ConfigReloadEvent();
    public static JsonObject config;
    public static String DatabaseURL;
    public static String DatabaseUser;
    public static String DatabasePassword;

    public static List<String> ranks = new ArrayList<>();
    public static Set<Pattern> filteredText = new HashSet<>();
    public static Set<String> onlineCrash = new HashSet<>();
    public static Set<Pattern> blockedLog = new HashSet<>();
    public static Set<Pattern> blockedMod = new HashSet<>();
    public static Set<Pattern> blacklistedMod = new HashSet<>();

    public static Map<String, String[]> colors = new HashMap<>();
    public static Map<String, Set<String>> rankPermissions = new HashMap<>();

    public static Map<String, Set<String>> grouppermissions = new HashMap<>();

    public static boolean debugMode;

    public static void reload() {
        try {
            config = new JsonParser().parse(new FileReader("../../server.json")).getAsJsonObject();
            DatabaseURL = config.getAsJsonObject("database").get("url").getAsString();
            DatabaseUser = config.getAsJsonObject("database").get("user").getAsString();
            DatabasePassword = config.getAsJsonObject("database").get("password").getAsString();
            LeftCpsLimit = config.get("leftcpslimit").getAsInt();
            RightCpsLimit = config.get("rightcpslimit").getAsInt();
            debugMode = config.get("debug").getAsBoolean();
            filteredText.clear();
            config.get("filteredtext").getAsJsonArray().forEach(a -> filteredText.add(Pattern.compile(a.getAsString())));
            blockedLog.clear();
            config.get("spigotblockedlog").getAsJsonArray().forEach(a -> blockedLog.add(Pattern.compile(a.getAsString())));
            blockedMod.clear();
            config.get("blockedmod").getAsJsonArray().forEach(a -> blockedMod.add(Pattern.compile(a.getAsString())));
            onlineCrash.clear();
            config.get("onlinecrash").getAsJsonArray().forEach(a -> onlineCrash.add(a.getAsString()));
            blacklistedMod.clear();
            config.get("blacklistedmod").getAsJsonArray().forEach(a -> blacklistedMod.add(Pattern.compile(a.getAsString())));
            colors.clear();
            rankPermissions.clear();
            ranks.clear();
            for (Map.Entry<String, JsonElement> c : config.getAsJsonObject("ranks").entrySet()) {
                String[] color = new Gson().fromJson(c.getValue().getAsJsonObject().get("texts").getAsJsonArray().toString(), new TypeToken<String[]>() {
                }.getType());
                colors.put(c.getKey(), new String[]{color[0], color[0].lastIndexOf('§') == -1 ? "" : ("§" + color[0].charAt(color[0].lastIndexOf('§') + 1)), color[1]});
                rankPermissions.put(c.getKey(), new Gson().fromJson(c.getValue().getAsJsonObject().get("permissions").getAsJsonArray().toString(), new TypeToken<Set<String>>() {
                }.getType()));
                ranks.add(c.getKey());
            }
            event.call();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void reloadDatabase() {
        for (String s : Database.systemConnection.select("name").from("groups").getStringArray())
            grouppermissions.put(s, Sets.newHashSet(Database.systemConnection.select("perms").from("groups").where("name='" + s + "'").getString().split("\\|")));
    }

    public static int LeftCpsLimit = Integer.MAX_VALUE;
    public static int RightCpsLimit = Integer.MAX_VALUE;
    public static String SERVERIP = "speedcubing.top";
}
