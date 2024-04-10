package top.speedcubing.server.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import top.speedcubing.common.database.Database;
import top.speedcubing.common.rank.Rank;
import top.speedcubing.common.server.MinecraftServer;
import top.speedcubing.server.events.ConfigReloadEvent;

public class config {

    public static int LeftCpsLimit = Integer.MAX_VALUE;
    public static int RightCpsLimit = Integer.MAX_VALUE;
    public static ConfigReloadEvent event = new ConfigReloadEvent();
    public static JsonObject config;
    public static String DatabaseURL;
    public static String DatabaseUser;
    public static String DatabasePassword;

    public static Set<Pattern> filteredText = new HashSet<>();
    public static Set<String> onlineCrash = new HashSet<>();
    public static Set<Pattern> blockedLog = new HashSet<>();
    public static Set<Pattern> blockedMod = new HashSet<>();
    public static Set<Pattern> blacklistedMod = new HashSet<>();

    public static String discordWebook;
    public static boolean removeLogs;

    public static void reload(boolean init) {
        try {
            config = new JsonParser().parse(new FileReader("/storage/server.json")).getAsJsonObject();
            DatabaseURL = config.getAsJsonObject("database").get("url").getAsString();
            DatabaseUser = config.getAsJsonObject("database").get("user").getAsString();
            DatabasePassword = config.getAsJsonObject("database").get("password").getAsString();
            LeftCpsLimit = config.get("leftcpslimit").getAsInt();
            RightCpsLimit = config.get("rightcpslimit").getAsInt();
            removeLogs = config.get("removeLogs").getAsBoolean();
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
            event.call();

            if (!init) {
                reloadDBConfig();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void reloadDBConfig() {
        Rank.loadRanks();
        MinecraftServer.loadServers();
        discordWebook = Database.configConnection.select("discord_webhook").from("mc_servers").where("name=\"" + Bukkit.getServer().getServerName() + "\"").getString();
    }
}
