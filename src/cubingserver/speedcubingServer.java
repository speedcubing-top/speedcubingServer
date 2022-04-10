package cubingserver;

import cubing.lib.api.SQLConnection;
import cubing.lib.bukkit.Event.ServerEventManager;
import cubingserver.Commands.*;
import cubingserver.Commands.offline.premium;
import cubingserver.Commands.offline.resetpassword;
import cubingserver.ExploitFixer.ForceOp;
import cubingserver.connection.SocketUtils;
import cubingserver.connection.UDPSocketUtils;
import cubingserver.customEvents.NickEvent;
import cubingserver.customEvents.SocketEvent;
import cubingserver.customEvents.UDPEvent;
import cubingserver.libs.LogListener;
import cubingserver.things.CommandPermissions;
import cubingserver.things.Cps;
import cubingserver.things.events.*;
import cubingserver.things.froze;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class speedcubingServer extends JavaPlugin {
    public static int AllPlayers;

    public static Map<UUID, Set<String>> permissions = new HashMap<>();
    public static Map<UUID, String> lastmsg = new HashMap<>();
    public static Map<UUID, Long> spam = new HashMap<>();
    public static Map<UUID, Double[]> velocities = new HashMap<>();

    public static int BungeeTCPPort;
    public static SQLConnection connection;
    public static boolean isBungeeOnlineMode;

    public void onEnable() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        try {
            File file = new File("../../Proxies/WaterFall/config.yml");
            isBungeeOnlineMode = (Boolean) ((HashMap<?, ?>) new Yaml().load(new FileInputStream(file))).get("online_mode");
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection = new SQLConnection("jdbc:mysql://localhost:3306/" + (Bukkit.getPort() % 2 == 1 ? "speedcubing" : "offlinecubing") + "?useUnicode=true&characterEncoding=utf8", "cubing", "6688andy");

        BungeeTCPPort = 25568 - Bukkit.getPort() % 2;
        new config().reload();
        new SocketUtils().Load(2 + Bukkit.getPort());
        new UDPSocketUtils().Load(Bukkit.getPort());
        new Cps().Load();
        new ForceOp().run();
        Bukkit.getPluginManager().registerEvents(new PlayerKick(), this);
        Bukkit.getPluginManager().registerEvents(new froze(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
        Bukkit.getPluginManager().registerEvents(new CommandPermissions(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryOpen(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuit(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerVelocity(), this);
        Bukkit.getPluginManager().registerEvents(new Cps(), this);
        Bukkit.getPluginManager().registerEvents(new ForceOp(), this);
        Bukkit.getPluginManager().registerEvents(new CreatureSpawn(), this);
        Bukkit.getPluginCommand("demo").setExecutor(new demo());
        Bukkit.getPluginCommand("demo").setTabCompleter(new demo());
        Bukkit.getPluginCommand("discord").setExecutor(new discord());
        Bukkit.getPluginCommand("discord").setTabCompleter(new discord());
        Bukkit.getPluginCommand("skin").setExecutor(new skin());
        Bukkit.getPluginCommand("skin").setTabCompleter(new skin());
        Bukkit.getPluginCommand("hub").setExecutor(new hub());
        Bukkit.getPluginCommand("hub").setTabCompleter(new hub());
        Bukkit.getPluginCommand("fly").setExecutor(new fly());
        Bukkit.getPluginCommand("fly").setTabCompleter(new fly());
        Bukkit.getPluginCommand("heal").setExecutor(new heal());
        Bukkit.getPluginCommand("heal").setTabCompleter(new heal());
        Bukkit.getPluginCommand("end").setExecutor(new end());
        Bukkit.getPluginCommand("end").setTabCompleter(new end());
        Bukkit.getPluginCommand("proxycommand").setExecutor(new proxycommand());
        Bukkit.getPluginCommand("proxycommand").setTabCompleter(new proxycommand());
        Bukkit.getPluginManager().registerEvents(new WeatherChange(), this);
        Bukkit.getPluginCommand("premium").setExecutor(new premium());
        Bukkit.getPluginCommand("premium").setTabCompleter(new premium());
        Bukkit.getPluginCommand("resetpassword").setExecutor(new resetpassword());
        Bukkit.getPluginCommand("resetpassword").setTabCompleter(new resetpassword());
        Bukkit.getPluginCommand("nick").setExecutor(new nick());
        Bukkit.getPluginCommand("nick").setTabCompleter(new nick());
        Bukkit.getPluginCommand("unnick").setExecutor(new unnick());
        Bukkit.getPluginCommand("unnick").setTabCompleter(new unnick());
        ServerEventManager.createNewEvents(SocketEvent.class, NickEvent.class, UDPEvent.class);
        new LogListener().reloadFilter();
    }

    public void onDisable() {
        File index = new File(Bukkit.getWorlds().get(0).getWorldFolder() + "/playerdata");
        if (index.list() != null)
            for (String s : index.list())
                new File(index.getPath(), s).delete();
        index.delete();
    }

    public static Map<String, Integer> map = new HashMap<String, Integer>() {{
        put("auth", 25569);
        put("lobby", 25573);
        put("fastbuilder", 25577);
        put("mlgrush", 25581);
        put("knockbackffa", 25585);
        put("practice", 25589);
        put("bedwars", 25593);
        put("clutch", 25597);
        put("reduce", 25601);
    }};

    public static String getServer(int defaultport) {
        defaultport = defaultport % 2 + defaultport - 1;
        for (Map.Entry<String, Integer> x : map.entrySet()) {
            if (x.getValue() == defaultport)
                return x.getKey();
        }
        return "";
    }
}
