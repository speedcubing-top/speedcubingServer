package top.speedcubing.server;

import com.google.common.collect.Sets;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.RestartCommand;
import top.speedcubing.CubingPaperConfig;
import top.speedcubing.lib.bukkit.PlayerUtils;
import top.speedcubing.lib.bukkit.TabCompleteUtils;
import top.speedcubing.lib.eventbus.LibEventManager;
import top.speedcubing.lib.speedcubingLibBukkit;
import top.speedcubing.lib.utils.SQL.SQLConnection;
import top.speedcubing.lib.utils.StringUtils;
import top.speedcubing.lib.utils.SystemUtils;
import top.speedcubing.lib.utils.sockets.TCP;
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
import top.speedcubing.server.libs.User;
import top.speedcubing.server.listeners.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class speedcubingServer extends JavaPlugin {

    public static final Pattern nameRegex = Pattern.compile("^\\w{1,16}$");
    public static SQLConnection connection;
    public static SQLConnection systemConnection;
    public static TCP tcp;
    public static boolean isBungeeOnlineMode;
    public static Map<Integer, String[]> preLoginStorage = new HashMap<>();

    public static boolean canRestart = true; //can Timer/Quit restart server?
    public static boolean restartable = false; //is it time to restart ?
    private static Timer calcTimer = new Timer("Cubing-Tick-Thread");
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
        tcp = new TCP("localhost", Bukkit.getPort() + 2, 100);

        //spigot
        try {
            Class.forName("top.speedcubing.CubingPaperConfig");
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
            String brand = (new String(bytes, StandardCharsets.UTF_8)).substring(1);
            if (brand.length() != 1) {
                Map<String, String> mods = new HashMap<>();
                boolean store = false;
                String tempName = null;
                for (int i = 2; i < bytes.length; store = !store) {
                    int end = i + bytes[i] + 1;
                    byte[] range = Arrays.copyOfRange(bytes, i + 1, end);
                    String string = new String(range);
                    if (store) {
                        mods.put(tempName, string);
                    } else {
                        tempName = string;
                    }
                    i = end;
                }
                for (String m : mods.keySet()) {
                    for (Pattern p : config.blockedMod) {
                        if (p.matcher(m).matches())
                            player.kickPlayer("Invalid Modification found.");
                    }
                }
                connection.update("playersdata", "forgemod='" + mods + "'", "uuid='" + player.getUniqueId() + "'");
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
        Thread thread = new Thread(() -> {
            try {
                String receive;
                while (true) {
                    receive = new BufferedReader(new InputStreamReader(tcp.socket.accept().getInputStream())).readLine();
                    if (receive != null) {
                        String[] rs = receive.split("\\|");
                        DataIO.handle(receive, rs);
                        switch (rs[0]) {
                            case "bungee":
                                User.getUser(Integer.parseInt(rs[1])).tcpPort = Integer.parseInt(rs[2]);
                                break;
                            case "cpsrequest":
                                User.getUser(Integer.parseInt(rs[2])).listened = rs[1].equals("a");
                                break;
                            case "cfg":
                                new config().reload();
                                new config().reloadDatabase();
                                break;
                            case "demo":
                                PacketPlayOutGameStateChange packet = new PacketPlayOutGameStateChange(5, 0);
                                if (rs[1].equals("-1"))
                                    Bukkit.getOnlinePlayers().forEach(a -> ((CraftPlayer) a).getHandle().playerConnection.sendPacket(packet));
                                else
                                    ((CraftPlayer) User.getUser(Integer.parseInt(rs[1])).player).getHandle().playerConnection.sendPacket(packet);
                                break;
                            case "crash":
                                if (rs[1].equals("-1"))
                                    Bukkit.getOnlinePlayers().forEach(PlayerUtils::explosionCrash);
                                else
                                    PlayerUtils.explosionCrash(User.getUser(Integer.parseInt(rs[1])).player);
                                break;
                            case "cmd":
                                String finalStr = receive;
                                Bukkit.getScheduler().runTask(this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalStr.substring(StringUtils.indexOf(finalStr, "|", 1) + 1)));
                                break;
                            case "velo":
                                User.getUser(Integer.parseInt(rs[2])).velocities = rs[1].equals("a") ? new double[]{Double.parseDouble(rs[3]), Double.parseDouble(rs[4])} : null;
                                break;
                            case "restart":
                                RestartCommand.restart();
                                break;
                            default:
                                new SocketEvent(receive).call();
                                break;
                        }
                    } else System.out.print("[Server] received null line of socket");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getScheduler().runTask(this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "end"));
            }
        });
        thread.setName("Cubing-Socket-Thread");
        thread.start();
        systemConnection.update("servers",
                "launchtime=" + System.currentTimeMillis() / 1000 +
                        ",ram_max=" + SystemUtils.getXmx() / 1048576
                , "name='" + onlineOroFfline + Bukkit.getServerName() + "'");

        //restart
        Calendar tomorrow = new GregorianCalendar();
        tomorrow.add(Calendar.DATE, 1);
        new Timer("Cubing-Restart-Thread").schedule(new TimerTask() {
            @Override
            public void run() {
                restartable = true;
                if (Bukkit.getOnlinePlayers().size() == 0)
                    restart();
            }
        }, new GregorianCalendar(tomorrow.get(Calendar.YEAR), tomorrow.get(Calendar.MONTH), tomorrow.get(Calendar.DATE), 0, 0).getTime().getTime() - System.currentTimeMillis());

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
        speedcubingServer.tcp.send(port, "hasnode|" + (add ? "a" : "r") + "|" + id);
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

    public static void globalChat(Collection<? extends Player> players, Player sender, TextComponent[] msg) {
        String[] ignores = connection.select("uuid").from("ignorelist").where("target='" + sender.getUniqueId() + "'").getStringArray();
        User user;
        for (Player p : players) {
            user = User.getUser(p);
            boolean ignored = false;
            for (String s : ignores) {
                if (user.player.getUniqueId().toString().equals(s)) {
                    ignored = true;
                }
            }
            if (!ignored)
                user.player.spigot().sendMessage(msg[user.lang]);
        }
    }

    public static void globalChat(Collection<? extends Player> players, Player sender, String[] msg) {
        String[] ignores = connection.select("uuid").from("ignorelist").where("target='" + sender.getUniqueId() + "'").getStringArray();
        User user;
        for (Player p : players) {
            user = User.getUser(p);
            boolean ignored = false;
            for (String s : ignores) {
                if (user.player.getUniqueId().toString().equals(s)) {
                    ignored = true;
                }
            }
            if (!ignored)
                user.sendLangMessage(msg);
        }
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