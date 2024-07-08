package top.speedcubing.server;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.RestartCommand;
import top.speedcubing.common.CommonLib;
import top.speedcubing.common.database.Database;
import top.speedcubing.common.io.SocketReader;
import top.speedcubing.lib.bukkit.TabCompleteUtils;
import top.speedcubing.lib.eventbus.CubingEventManager;
import top.speedcubing.lib.utils.SystemUtils;
import top.speedcubing.lib.utils.internet.HostAndPort;
import top.speedcubing.server.authenticator.AuthenticatorCommand;
import top.speedcubing.server.bukkitcmd.discord;
import top.speedcubing.server.bukkitcmd.fly;
import top.speedcubing.server.bukkitcmd.getitemtype;
import top.speedcubing.server.bukkitcmd.hub;
import top.speedcubing.server.bukkitcmd.image;
import top.speedcubing.server.bukkitcmd.limbo;
import top.speedcubing.server.bukkitcmd.nick.nick;
import top.speedcubing.server.bukkitcmd.nick.unnick;
import top.speedcubing.server.bukkitcmd.overrided.plugins;
import top.speedcubing.server.bukkitcmd.skin;
import top.speedcubing.server.bukkitcmd.staff.announce;
import top.speedcubing.server.bukkitcmd.staff.freeze;
import top.speedcubing.server.bukkitcmd.staff.heal;
import top.speedcubing.server.bukkitcmd.staff.history;
import top.speedcubing.server.bukkitcmd.staff.proxycommand;
import top.speedcubing.server.bukkitcmd.staff.serverconfig;
import top.speedcubing.server.bukkitcmd.staff.testkb;
import top.speedcubing.server.bukkitcmd.troll.deepfry;
import top.speedcubing.server.bukkitcmd.troll.kaboom;
import top.speedcubing.server.bukkitcmd.troll.sendpacket;
import top.speedcubing.server.bukkitlistener.PostListen;
import top.speedcubing.server.bukkitlistener.PreListen;
import top.speedcubing.server.bukkitlistener.SingleListen;
import top.speedcubing.server.bukkitlistener.pluginchannel.FMLHSListener;
import top.speedcubing.server.commandoverrider.OverrideCommandManager;
import top.speedcubing.server.cubinglistener.CubingTick;
import top.speedcubing.server.cubinglistener.PlayIn;
import top.speedcubing.server.cubinglistener.PlayOut;
import top.speedcubing.server.cubinglistener.SocketInput;
import top.speedcubing.server.cubinglistener.SocketRead;
import top.speedcubing.server.lang.LanguageSystem;
import top.speedcubing.server.login.PreLoginData;
import top.speedcubing.server.player.User;
import top.speedcubing.server.utils.Configuration;
import top.speedcubing.server.utils.LogListener;

public class speedcubingServer extends JavaPlugin {
    public static final Pattern nameRegex = Pattern.compile("^\\w{3,16}$");
    public static final Pattern legacyNameRegex = Pattern.compile("^\\w{1,16}$");
    public static Map<Integer, PreLoginData> preLoginStorage = new HashMap<>();

    public static boolean canRestart = true; //can Timer/Quit restart server?
    public static boolean restartable = false; //is it time to restart ?
    public static speedcubingServer instance;
    public static ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(10);

    @Override
    public void onEnable() {
        instance = this;
        CubingEventManager.registerListeners(
                new CubingTick(),
                new PlayIn(),
                new PlayOut(),
                new SocketInput(),
                new SocketRead(),
                new Configuration());
        registerCommands();
        registerListeners();

        CommonLib.init();

        SocketReader.init(new HostAndPort("127.0.0.1", Bukkit.getPort() + 1000));

        LanguageSystem.init();

        //temporarily fixed

        sendpacket.initFuckPeople();

        Bukkit.getMessenger().registerIncomingPluginChannel(this, "FML|HS", new FMLHSListener());

        OverrideCommandManager.register(new plugins());
        TabCompleteUtils.registerEmptyTabComplete("announce", "proxycommand", "heal", "fly", "hub", "skin", "discord", "nick", "unnick", "resetpassword", "premium");
        new LogListener().reloadFilter();

        Database.systemConnection.update("servers",
                "launchtime=" + SystemUtils.getCurrentSecond() +
                        ",ram_max=" + SystemUtils.getXmx() / 1048576
                , "name='" + Bukkit.getServerName() + "'");

        //restart
//        new Timer("Cubing-Restart-Thread").schedule(new TimerTask() {
//            @Override
//            public void run() {
//                restartable = true;
//                if (Bukkit.getOnlinePlayers().isEmpty())
//                    restart();
//            }
//        }, 28800000);

        if (!Configuration.removeLogs) {
            //delete logs
            for (File f : new File("logs").listFiles()) {
                if (!f.getName().equals("latest.log"))
                    f.delete();
            }

            //delete hs_err
            for (File f : new File("./").listFiles()) {
                if (f.getName().startsWith("hs_err_pid"))
                    f.delete();
            }
        }
    }

    @Override
    public void onDisable() {
        CommonLib.shutdown();
        Database.systemConnection.update(
                "servers",
                "onlinecount=-1,ram_max=-1,ram_heap=-1,ram_used=-1,tps1=-1,tps2=-1,tps3=-1",
                "name='" + Bukkit.getServerName() + "'"
        );
    }

    private void registerCommands() {
        Bukkit.getPluginCommand("nick").setExecutor(new nick());
        Bukkit.getPluginCommand("unnick").setExecutor(new unnick());
        Bukkit.getPluginCommand("discord").setExecutor(new discord());
        Bukkit.getPluginCommand("skin").setExecutor(new skin());
        Bukkit.getPluginCommand("hub").setExecutor(new hub());
        Bukkit.getPluginCommand("fly").setExecutor(new fly());
        Bukkit.getPluginCommand("testkb").setExecutor(new testkb());
        Bukkit.getPluginCommand("limbo").setExecutor(new limbo());
        Bukkit.getPluginCommand("heal").setExecutor(new heal());
        Bukkit.getPluginCommand("proxycommand").setExecutor(new proxycommand());
        Bukkit.getPluginCommand("announce").setExecutor(new announce());
        Bukkit.getPluginCommand("kaboom").setExecutor(new kaboom());
        Bukkit.getPluginCommand("deepfry").setExecutor(new deepfry());
        Bukkit.getPluginCommand("freeze").setExecutor(new freeze());
        Bukkit.getPluginCommand("2fa").setExecutor(new AuthenticatorCommand());
        Bukkit.getPluginCommand("image").setExecutor(new image());
        Bukkit.getPluginCommand("serverconfig").setExecutor(new serverconfig());
        Bukkit.getPluginCommand("history").setExecutor(new history());
        Bukkit.getPluginCommand("getitemtype").setExecutor(new getitemtype());
        Bukkit.getPluginCommand("sendpacket").setExecutor(new sendpacket());
    }

    private void registerListeners() {
        registerListeners(new PreListen(), new PostListen(), new SingleListen(), new history(), new sendpacket());
    }

    public static Plugin getInstance() {
        return instance;
    }

    public static void registerListeners(Listener... listeners) {
        for (Listener l : listeners) {
            Bukkit.getPluginManager().registerEvents(l, instance);
        }
    }

    public static HostAndPort getRandomBungee() {
        return (!User.usersByID.values().isEmpty() ? User.usersByID.values().iterator().next().proxy : new HostAndPort("host.docker.internal", 25565 + 1000));
    }

    public static void restart() {
        if (canRestart)
            RestartCommand.restart();
    }
}