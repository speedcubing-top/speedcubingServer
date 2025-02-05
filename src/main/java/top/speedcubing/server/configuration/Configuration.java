package top.speedcubing.server.configuration;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import top.speedcubing.common.configuration.ServerConfig;
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
    public static boolean removeLogs;

    public static void reload() {
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
    }

    @CubingEventHandler(priority = 20)
    public void reload(ConfigReloadEvent e) {
        reload();
    }
}
