package top.speedcubing.server.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import top.speedcubing.common.configuration.ServerConfig;
import top.speedcubing.common.database.Database;
import top.speedcubing.common.events.ConfigReloadEvent;
import top.speedcubing.lib.eventbus.CubingEventHandler;

public class Configuration {
    public static int LeftCpsLimit = Integer.MAX_VALUE;
    public static int RightCpsLimit = Integer.MAX_VALUE;
    public static Set<Pattern> filteredText = new HashSet<>();
    public static Set<String> onlineCrash = new HashSet<>();
    public static Set<Pattern> blockedLog = new HashSet<>();
    public static Set<Pattern> blockedMod = new HashSet<>();
    public static Set<Pattern> blacklistedMod = new HashSet<>();
    public static String discordWebook;
    public static boolean removeLogs;

    @CubingEventHandler
    public void reload(ConfigReloadEvent e) {
        Configuration.LeftCpsLimit = ServerConfig.config.get("leftcpslimit").getAsInt();
        Configuration.RightCpsLimit = ServerConfig.config.get("rightcpslimit").getAsInt();
        Configuration.removeLogs = ServerConfig.config.get("removeLogs").getAsBoolean();
        Configuration.filteredText.clear();
        ServerConfig.config.get("filteredtext").getAsJsonArray().forEach(a -> Configuration.filteredText.add(Pattern.compile(a.getAsString())));
        Configuration.blockedLog.clear();
        ServerConfig.config.get("spigotblockedlog").getAsJsonArray().forEach(a -> Configuration.blockedLog.add(Pattern.compile(a.getAsString())));
        Configuration.blockedMod.clear();
        ServerConfig.config.get("blockedmod").getAsJsonArray().forEach(a -> Configuration.blockedMod.add(Pattern.compile(a.getAsString())));
        Configuration.onlineCrash.clear();
        ServerConfig.config.get("onlinecrash").getAsJsonArray().forEach(a -> Configuration.onlineCrash.add(a.getAsString()));
        Configuration.blacklistedMod.clear();
        ServerConfig.config.get("blacklistedmod").getAsJsonArray().forEach(a -> Configuration.blacklistedMod.add(Pattern.compile(a.getAsString())));
        Configuration.discordWebook = Database.systemConnection.select("discord_webhook").from("servers").where("name=\"" + Bukkit.getServer().getServerName() + "\"").getString();
    }
}
