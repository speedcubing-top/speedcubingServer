package top.speedcubing.server;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.*;
import top.speedcubing.lib.bukkit.TabCompleteUtils;
import top.speedcubing.lib.eventbus.CubingEventManager;
import top.speedcubing.lib.utils.*;
import top.speedcubing.lib.utils.sockets.TCPClient;
import top.speedcubing.namedb.NameDb;
import top.speedcubing.paper.CubingPaperConfig;
import top.speedcubing.server.commandoverrider.OverrideCommandManager;
import top.speedcubing.server.commands.*;
import top.speedcubing.server.commands.overrided.plugins;
import top.speedcubing.server.commands.staff.*;
import top.speedcubing.server.database.Database;
import top.speedcubing.server.listeners.*;
import top.speedcubing.server.mulitproxy.SocketReader;
import top.speedcubing.server.player.*;
import top.speedcubing.server.utils.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class speedcubingServer extends JavaPlugin {

    public static final Pattern nameRegex = Pattern.compile("^\\w{3,16}$");

    public static final Pattern legacyNameRegex = Pattern.compile("^\\w{1,16}$");
    public static TCPClient tcpClient;
    public static Map<Integer, PreLoginData> preLoginStorage = new HashMap<>();

    public static boolean canRestart = true; //can Timer/Quit restart server?
    public static boolean restartable = false; //is it time to restart ?

    public void onEnable() {
        NameDb.init();
        //conn
        config.reload();
        Database.init();
        CubingTick.init();
        config.reloadDatabase();
        tcpClient = new TCPClient("localhost", 100);
        SocketReader.init();
        //spigot.yml
        SpigotConfig.disableStatSaving = true;
        //spigot
        try {
            Class.forName("top.speedcubing.paper.CubingPaperConfig");
            CubingPaperConfig.commandOP = true;
            CubingPaperConfig.disableOpsJson = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //lib
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "FML|HS", (s, player, bytes) -> {
            if (bytes.length != 2) {
                boolean store = false, punished = false;
                String name = null, a2, string;
                Boolean bypass = Database.connection.select("modbypass").from("playersdata").where("id=" + User.getUser(player).id).getBoolean();
                for (int i = 2; i < bytes.length; store = !store) {
                    int end = i + bytes[i] + 1;
                    string = new String(Arrays.copyOfRange(bytes, i + 1, end));
                    a2 = name + " " + string;
                    if (store && !bypass) {
                        if (!punished)
                            for (Pattern p : config.blacklistedMod) {
                                if (p.matcher(a2).matches()) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "proxycommand ban " + player.getName() + " 0 Suspicious activities detected on your account.");
                                    punished = true;
                                    break;
                                }
                            }
                        if (!punished)
                            for (Pattern p : config.blockedMod) {
                                if (p.matcher(a2).matches()) {
                                    player.kickPlayer("Invalid Modification Found.");
                                    punished = true;
                                    break;
                                }
                            }
                    } else name = string;
                    i = end;
                }
                String mods = new String(bytes, StandardCharsets.UTF_8);
                System.out.println(mods);
                System.out.println(Arrays.toString(bytes));
                User.getUser(player).dbUpdate("forgemod='" + new String(bytes, StandardCharsets.UTF_8) + "'");
            }
        });
        Bukkit.getPluginManager().registerEvents(new CommandPermissions(), this);
        Bukkit.getPluginManager().registerEvents(new FrontListen(), this);
        Bukkit.getPluginManager().registerEvents(new BackListen(), this);
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
        OverrideCommandManager.register(new plugins());
        TabCompleteUtils.registerEmptyTabComplete("announce", "proxycommand", "heal", "fly", "hub", "skin", "discord", "nick", "unnick", "resetpassword", "premium");
        CubingEventManager.registerListeners(new ServerEvent());
        new LogListener().reloadFilter();

        //socket receive

        Database.systemConnection.update("servers",
                "launchtime=" + SystemUtils.getCurrentSecond() +
                        ",ram_max=" + SystemUtils.getXmx() / 1048576
                , "name='" + Bukkit.getServerName() + "'");

        //restart
        new Timer("Cubing-Restart-Thread").schedule(new TimerTask() {
            @Override
            public void run() {
                restartable = true;
                if (Bukkit.getOnlinePlayers().isEmpty())
                    restart();
            }
        }, 28800000);


        if (!config.debugMode) {
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

    public void onDisable() {
        CubingTick.calcTimer.cancel();
        Database.systemConnection.update(
                "servers",
                "onlinecount=0,ram_max=0,ram_heap=0,ram_used=0,tps1=0,tps2=0,tps3=0",
                "name='" + Bukkit.getServerName() + "'"
        );
    }

    public static void node(boolean add, int id, int port) {
        tcpClient.send(port, new ByteArrayDataBuilder().writeUTF("hasnode").writeInt(id).writeBoolean(add).toByteArray());
    }

    public static int getRandomBungeePort() {
        return (!User.usersByID.values().isEmpty() ? User.usersByID.values().iterator().next().tcpPort : 25565 + 1000);
    }

    public static int getCode(String rank) {
        return 10 + config.ranks.indexOf(rank);
    }

    public static String playerNameExtract(String name) {
        StringBuilder str = new StringBuilder();
        StringBuilder nameBuilder = new StringBuilder(name);
        while (nameBuilder.length() < 16) {
            nameBuilder.append(" ");
        }
        name = nameBuilder.toString();
        for (int i = 0; i < 16; i++) {
            int c = name.charAt(i);
            c = (c == 32 ? 0 : (c <= 57 ? c - 47 : (c <= 90 ? c - 54 : (c == 95 ? 37 : c - 59))));
            StringBuilder bin = new StringBuilder(Integer.toBinaryString(c));
            while (bin.length() < 6) {
                bin.insert(0, "0");
            }
            str.append(bin);
        }
        str.append("00");
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < 14; i++) {
            string.append((char) (Integer.parseInt(str.substring(i * 7, i * 7 + 6), 2) + 32));
        }
        return string.toString();
    }

    public static void restart() {
        if (canRestart)
            RestartCommand.restart();
    }
}