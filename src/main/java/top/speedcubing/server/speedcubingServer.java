package top.speedcubing.server;

import com.google.common.collect.Sets;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.RestartCommand;
import top.speedcubing.lib.bukkit.PlayerUtils;
import top.speedcubing.lib.bukkit.TabCompleteUtils;
import top.speedcubing.lib.eventbus.LibEventManager;
import top.speedcubing.lib.speedcubingLibBukkit;
import top.speedcubing.lib.utils.ByteArrayDataBuilder;
import top.speedcubing.lib.utils.SQL.SQLConnection;
import top.speedcubing.lib.utils.SystemUtils;
import top.speedcubing.lib.utils.Threads;
import top.speedcubing.lib.utils.sockets.ByteUtils;
import top.speedcubing.lib.utils.sockets.TCPClient;
import top.speedcubing.server.Commands.*;
import top.speedcubing.server.Commands.offline.premium;
import top.speedcubing.server.Commands.offline.resetpassword;
import top.speedcubing.server.Commands.overrided.plugins;
import top.speedcubing.server.ExploitFixer.ForceOp;
import top.speedcubing.server.commandoverrider.OverrideCommandManager;
import top.speedcubing.server.events.CubingTickEvent;
import top.speedcubing.server.events.SocketEvent;
import top.speedcubing.server.libs.DataIO;
import top.speedcubing.server.libs.LogListener;
import top.speedcubing.server.libs.PreLoginData;
import top.speedcubing.server.libs.User;
import top.speedcubing.server.listeners.*;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class speedcubingServer extends JavaPlugin {

    public static final Pattern nameRegex = Pattern.compile("^\\w{1,16}$");
    public static SQLConnection connection;
    public static SQLConnection systemConnection;
    public static ServerSocket tcpServer;
    public static TCPClient tcpClient;
    public static boolean isBungeeOnlineMode;
    public static Map<Integer, PreLoginData> preLoginStorage = new HashMap<>();

    public static boolean canRestart = true; //can Timer/Quit restart server?
    public static boolean restartable = false; //is it time to restart ?
    private static final Timer calcTimer = new Timer("Cubing-Tick-Thread");
    public static String onlineOroFfline;

    public void onEnable() {
        //check proxy online mode
        try {
//            File file = new File("../../Proxies/WaterFall/config.yml");
//            isBungeeOnlineMode = (Boolean) ((HashMap<?, ?>) new Yaml().load(Files.newInputStream(file.toPath()))).get("online_mode");
            File file = new File("../../Proxies/Velocity1/velocity.toml");
            BufferedReader input = new BufferedReader(new FileReader(file));
            String line = "";
            boolean a = true;
            while (a) {
                line = input.readLine();
                if (line != null && line.startsWith("online-mode = "))
                    a = false;
            }
            isBungeeOnlineMode = line.equals("online-mode = true");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //conn
        new config().reload();
        onlineOroFfline = (Bukkit.getPort() % 2 == 1 ? "online" : "offline");
        connection = new SQLConnection(config.DatabaseURL.replace("%db%", Bukkit.getPort() % 2 == 1 ? "speedcubing" : "offlinecubing"), config.DatabaseUser, config.DatabasePassword);
        systemConnection = new SQLConnection(config.DatabaseURL.replace("%db%", "speedcubingsystem"), config.DatabaseUser, config.DatabasePassword);
        new config().reloadDatabase();
        try {
            tcpServer = new ServerSocket(Bukkit.getPort() + 2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        tcpClient = new TCPClient("localhost", 100);

        //spigot
        try {
            Class.forName("top.speedcubing.server.CubingPaperConfig");
            CubingPaperConfig.restartArgument = new String[]{"screen", "-mdS", onlineOroFfline + Bukkit.getServerName(), "sh", "../../../" + Bukkit.getServerName() + ".sh", onlineOroFfline, "init"};
        } catch (Exception e) {
            e.printStackTrace();
        }
        //lib
        speedcubingLibBukkit.deletePlayerFile = true;

        //self
        new Cps().Load();
        new ForceOp().run();
        if (!isBungeeOnlineMode) {
            Bukkit.getPluginCommand("premium").setExecutor(new premium());
            Bukkit.getPluginCommand("resetpassword").setExecutor(new resetpassword());
        } else {
            Bukkit.getPluginCommand("nick").setExecutor(new nick());
            Bukkit.getPluginCommand("unnick").setExecutor(new unnick());
        }
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "FML|HS", (s, player, bytes) -> {
            if (bytes.length != 2) {
                boolean store = false, punished = false;
                String name = null, a2, string;
                for (int i = 2; i < bytes.length; store = !store) {
                    int end = i + bytes[i] + 1;
                    string = new String(Arrays.copyOfRange(bytes, i + 1, end));
                    a2 = name + " " + string;
                    if (store) {
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
        Bukkit.getPluginManager().registerEvents(new PlayerKick(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
        Bukkit.getPluginManager().registerEvents(new CommandPermissions(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryOpen(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuit(), this);
        Bukkit.getPluginManager().registerEvents(new Login(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerVelocity(), this);
        Bukkit.getPluginManager().registerEvents(new Cps(), this);
        Bukkit.getPluginManager().registerEvents(new ForceOp(), this);
        Bukkit.getPluginManager().registerEvents(new ServerCommand(), this);
        Bukkit.getPluginManager().registerEvents(new CreatureSpawn(), this);
        Bukkit.getPluginCommand("discord").setExecutor(new discord());
        Bukkit.getPluginCommand("skin").setExecutor(new skin());
        Bukkit.getPluginCommand("hub").setExecutor(new hub());
        Bukkit.getPluginCommand("fly").setExecutor(new fly());
        Bukkit.getPluginCommand("heal").setExecutor(new heal());
        Bukkit.getPluginCommand("proxycommand").setExecutor(new proxycommand());
        Bukkit.getPluginManager().registerEvents(new WeatherChange(), this);
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
                    DataInputStream in = ByteUtils.inputStreamToDataInputStream(1024, tcpServer.accept().getInputStream());
                    String header = in.readUTF();
                    DataIO.handle(in, header);
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
                                Bukkit.getOnlinePlayers().forEach(a -> ((CraftPlayer) a).getHandle().playerConnection.sendPacket(packet));
                            else
                                ((CraftPlayer) User.getUser(id).player).getHandle().playerConnection.sendPacket(packet);
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
        systemConnection.update("servers",
                "launchtime=" + SystemUtils.getCurrentSecond() +
                        ",ram_max=" + SystemUtils.getXmx() / 1048576
                , "name='" + onlineOroFfline + Bukkit.getServerName() + "'");

        //restart
        new Timer("Cubing-Restart-Thread").schedule(new TimerTask() {
            @Override
            public void run() {
                restartable = true;
                if (Bukkit.getOnlinePlayers().size() == 0)
                    restart();
            }
        }, 28800000);

        calcTimer.schedule(new TimerTask() {
            double[] tps;
            final CubingTickEvent event = new CubingTickEvent();
            MemoryUsage usage;

            @Override
            public void run() {
                usage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
                tps = MinecraftServer.getServer().recentTps;
                systemConnection.update(
                        "servers",
                        "onlinecount=" + Bukkit.getOnlinePlayers().size() +
                                ",ram_heap=" + usage.getCommitted() / 1048576 +
                                ",ram_used=" + usage.getUsed() / 1048576 +
                                ",tps1=" + Math.round(tps[0] * 100.0) / 100.0 +
                                ",tps2=" + Math.round(tps[1] * 100.0) / 100.0 +
                                ",tps3=" + Math.round(tps[2] * 100.0) / 100.0,
                        "name='" + onlineOroFfline + Bukkit.getServerName() + "'"
                );
                event.call();
            }
        }, 0, 1000);

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
        calcTimer.cancel();
        systemConnection.update(
                "servers",
                "onlinecount=0,ram_max=0,ram_heap=0,ram_used=0,tps1=0,tps2=0,tps3=0",
                "name='" + onlineOroFfline + Bukkit.getServerName() + "'"
        );
    }

    public static int getOnlineCount() {
        return systemConnection.select("SUM(onlinecount)").from("proxies").where("`name` LIKE '%" + onlineOroFfline + "%'").getInt();
    }

    public static void node(boolean add, int id, int port) {
        tcpClient.send(port, new ByteArrayDataBuilder().writeUTF("hasnode").writeInt(id).writeBoolean(add).toByteArray());
    }

    public static int getRandomBungeePort(CommandSender sender) {
        return (sender == null || sender instanceof ConsoleCommandSender) ?
                (User.usersByID.values().size() != 0 ? User.usersByID.values().iterator().next().tcpPort : 25568 - Bukkit.getPort() % 2)
                : User.getUser(sender).tcpPort;
    }

    public static String[] getFormat(String rank) {
        return config.colors.get(rank);
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

    public static String getRank(String priority, String uuid) {
        return priority.equals("default") && connection.select("COUNT(*)").from("champ").where("uuid='" + uuid + "'").getInt() > 0 ? "champ" : priority;
    }

    public static String getRank(String priority, String uuid, Set<String> champs) {
        return priority.equals("default") && champs.contains(uuid) ? "champ" : priority;
    }

    public static Set<String> getChamps() {
        return Sets.newHashSet(connection.select("uuid").from("champ").getStringArray());
    }
}