package top.speedcubing.server;

import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.RestartCommand;
import top.speedcubing.lib.bukkit.*;
import top.speedcubing.lib.eventbus.LibEventManager;
import top.speedcubing.lib.speedcubingLibBukkit;
import top.speedcubing.lib.utils.*;
import top.speedcubing.lib.utils.sockets.*;
import top.speedcubing.namedb.NameDb;
import top.speedcubing.server.Commands.*;
import top.speedcubing.server.Commands.overrided.plugins;
import top.speedcubing.server.ExploitFixer.ForceOp;
import top.speedcubing.server.commandoverrider.OverrideCommandManager;
import top.speedcubing.server.database.Database;
import top.speedcubing.server.events.SocketEvent;
import top.speedcubing.server.libs.*;
import top.speedcubing.server.listeners.*;

import java.io.*;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class speedcubingServer extends JavaPlugin {

    public static final Pattern nameRegex = Pattern.compile("^\\w{3,16}$");

    public static final Pattern legacyNameRegex = Pattern.compile("^\\w{1,16}$");
    public static ServerSocket tcpServer;
    public static TCPClient tcpClient;
    public static Map<Integer, PreLoginData> preLoginStorage = new HashMap<>();

    public static boolean canRestart = true; //can Timer/Quit restart server?
    public static boolean restartable = false; //is it time to restart ?

    public void onEnable() {
        NameDb.init();
        //conn
        new config().reload();
        Database.init();
        CubingTick.init();
        new config().reloadDatabase();
        try {
            tcpServer = new ServerSocket(Bukkit.getPort() + 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        tcpClient = new TCPClient("localhost", 100);

        //spigot
        try {
            Class.forName("top.speedcubing.server.CubingPaperConfig");
            CubingPaperConfig.restartArgument = new String[]{"screen", "-mdS", Bukkit.getServerName(), "sh", "../../../" + Bukkit.getServerName() + ".sh", "init"};
        } catch (Exception e) {
            e.printStackTrace();
        }
        //lib
        speedcubingLibBukkit.deletePlayerFile = true;
        new ForceOp().run();
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
                                    player.kickPlayer("Invalid Modification found.");
                                    punished = true;
                                    break;
                                }
                            }
                    } else name = string;
                    i = end;
                }
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
        Bukkit.getPluginCommand("heal").setExecutor(new heal());
        Bukkit.getPluginCommand("proxycommand").setExecutor(new proxycommand());
        Bukkit.getPluginCommand("announce").setExecutor(new announce());
        OverrideCommandManager.register(new plugins(), "pl", "plugins");
        speedcubingLibBukkit.deletePlayerFile = true;
        TabCompleteUtils.registerEmptyTabComplete("announce", "proxycommand", "heal", "fly", "hub", "skin", "discord", "nick", "unnick", "resetpassword", "premium");
        LibEventManager.registerListeners(new ServerEvent());
        new LogListener().reloadFilter();

        //socket receive
        Threads.newThread("Cubing-Socket-Thread", () -> {
            while (true) {
                try {
                    DataInputStream in = ByteUtils.inputStreamToDataInputStream(tcpServer.accept().getInputStream(), 1024);
                    String header;
                    try {
                        header = in.readUTF();
                    } catch (Exception e) {
                        continue;
                    }
                    DataIO.handle(in, header);
                    System.out.println("[Socket] received " + header + " " + in);
                    switch (header) {
                        case "bungee":
                            User.getUser(in.readInt()).tcpPort = in.readInt();
                            break;
                        case "cpsrequest":
                            int id = in.readInt();
                            User user = User.getUser(id);
                            if (user != null)
                                user.listened = in.readBoolean();
                            else preLoginStorage.get(id).cps = true;
                            break;
                        case "cfg":
                            new config().reload();
                            new config().reloadDatabase();
                            break;
                        case "demo":
                            PacketPlayOutGameStateChange packet = new PacketPlayOutGameStateChange(5, 0);
                            id = in.readInt();
                            if (id == 0)
                                User.getUsers().forEach(a -> a.sendPacket(packet));
                            else
                                User.getUser(id).sendPacket(packet);
                            break;
                        case "crash":
                            id = in.readInt();
                            if (id == 0)
                                Bukkit.getOnlinePlayers().forEach(PlayerUtils::explosionCrash);
                            else
                                PlayerUtils.explosionCrash(User.getUser(id).player);
                            break;
                        case "velo":
                            User.getUser(in.readInt()).velocities = in.readBoolean() ? new double[]{in.readDouble(), in.readDouble()} : null;
                            break;
                        case "vanish":
                            user = User.getUser(in.readInt());
                            if (in.readBoolean())
                                Bukkit.getScheduler().runTask(speedcubingServer.getPlugin(speedcubingServer.class), () -> {
                                    for (Player p : Bukkit.getOnlinePlayers())
                                        p.hidePlayer(user.player);
                                });
                            else
                                Bukkit.getScheduler().runTask(speedcubingServer.getPlugin(speedcubingServer.class), () -> {
                                    for (Player p : Bukkit.getOnlinePlayers())
                                        p.showPlayer(user.player);
                                });
                            break;
                        case "restart":
                            RestartCommand.restart();
                            break;
                        default:
                            new SocketEvent(in, header).call();
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Database.systemConnection.update("servers",
                "launchtime=" + SystemUtils.getCurrentSecond() +
                        ",ram_max=" + SystemUtils.getXmx() / 1048576
                , "name='" + Bukkit.getServerName() + "'");

        //restart
        new Timer("Cubing-Restart-Thread").schedule(new TimerTask() {
            @Override
            public void run() {
                restartable = true;
                if (Bukkit.getOnlinePlayers().size() == 0)
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
        return (User.usersByID.values().size() != 0 ? User.usersByID.values().iterator().next().tcpPort : 25566);
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