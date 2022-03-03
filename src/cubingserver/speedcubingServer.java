package cubingserver;

import cubing.api.SQLConnection;
import cubingserver.Commands.*;
import cubingserver.Commands.offline.premium;
import cubingserver.Commands.offline.resetpassword;
import cubingserver.ExploitFixer.ForceOp;
import cubingserver.connection.SocketUtils;
import cubingserver.connection.UDPSocketUtils;
import cubingserver.libs.LogListener;
import cubingserver.things.CommandPermissions;
import cubingserver.things.Cps;
import cubingserver.things.events.*;
import cubingserver.things.froze;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class speedcubingServer extends JavaPlugin {


    public static Map<UUID, String> lastmsg = new HashMap<>();
    public static Map<UUID, Long> spam = new HashMap<>();
    public static Map<UUID, Double[]> velocities = new HashMap<>();

    public static int BungeeTCPPort;
    public static SQLConnection connection;

    public void onEnable() {
        connection = new SQLConnection("jdbc:mysql://localhost:3306/" + (Bukkit.getPort() % 2 == 1 ? "speedcubing" : "offlinecubing") + "?useUnicode=true&characterEncoding=utf8", "cubing", "6688andy");
        BungeeTCPPort = 25568 - Bukkit.getPort() % 2;
        new configcache().reload();
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
        Bukkit.getPluginCommand("reloadcoreplugin").setExecutor(new configcache());
        Bukkit.getPluginCommand("reloadcoreplugin").setTabCompleter(new configcache());
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
        switch (Bukkit.getServerName()) {
            case "mlgrush":
            case "practice":
            case "bedwars":
            case "knockbackffa":
            case "fastbuilder":
            case "clutch":
            case "reduce":
            case "lobby":
            case "auth":
                new LogListener().reloadFilter();
                break;
        }
    }

    public void onDisable() {
        File index = new File(Bukkit.getWorlds().get(0).getWorldFolder() + "/playerdata");
        if (index.list() != null)
            for (String s : index.list())
                new File(index.getPath(), s).delete();
        index.delete();
    }
}
