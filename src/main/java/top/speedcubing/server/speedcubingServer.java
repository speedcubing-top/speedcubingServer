package top.speedcubing.server;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.spigotmc.AsyncCatcher;
import org.spigotmc.SpigotConfig;
import org.spigotmc.WatchdogThread;
import top.speedcubing.lib.api.MojangAPI;
import top.speedcubing.lib.api.SessionServer;
import top.speedcubing.lib.bukkit.PlayerUtils;
import top.speedcubing.lib.eventbus.LibEventManager;
import top.speedcubing.lib.utils.SQL.SQLConnection;
import top.speedcubing.lib.utils.StringUtils;
import top.speedcubing.lib.utils.sockets.TCP;
import top.speedcubing.server.Commands.*;
import top.speedcubing.server.Commands.offline.premium;
import top.speedcubing.server.Commands.offline.resetpassword;
import top.speedcubing.server.Commands.overrided.restart;
import top.speedcubing.server.Commands.overrided.tps;
import top.speedcubing.server.ExploitFixer.ForceOp;
import top.speedcubing.server.commandoverrider.OverrideCommandManager;
import top.speedcubing.server.events.SocketEvent;
import top.speedcubing.server.libs.DataIO;
import top.speedcubing.server.libs.LogListener;
import top.speedcubing.server.libs.User;
import top.speedcubing.server.listeners.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
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

    public static boolean restartable = false;

    public static boolean restarting = false;

    public void onEnable() {
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
        new config().reload();
        connection = new SQLConnection(config.DatabaseURL.replace("%db%", Bukkit.getPort() % 2 == 1 ? "speedcubing" : "offlinecubing"), config.DatabaseUser, config.DatabasePassword);
        systemConnection = new SQLConnection(config.DatabaseURL.replace("%db%", "speedcubingsystem"), config.DatabaseUser, config.DatabasePassword);
        new config().reloadDatabase();
        tcp = new TCP("localhost", Bukkit.getPort() + 2, 100);
        new Cps().Load();
        new ForceOp().run();
        if (!isBungeeOnlineMode) {
            Bukkit.getPluginCommand("premium").setExecutor(new premium());
            Bukkit.getPluginCommand("premium").setTabCompleter(new premium());
            Bukkit.getPluginCommand("resetpassword").setExecutor(new resetpassword());
            Bukkit.getPluginCommand("resetpassword").setTabCompleter(new resetpassword());
        } else {
            Bukkit.getPluginCommand("nick").setExecutor(new nick());
            Bukkit.getPluginCommand("nick").setTabCompleter(new nick());
            Bukkit.getPluginCommand("unnick").setExecutor(new unnick());
            Bukkit.getPluginCommand("unnick").setTabCompleter(new unnick());
        }
        Messenger messenger = Bukkit.getMessenger();
        messenger.registerIncomingPluginChannel(this, "FML|HS", (s, player, bytes) -> {
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
                    if (config.blockedMod.contains(m.toLowerCase())) {
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
        Bukkit.getPluginCommand("discord").setTabCompleter(new discord());
        Bukkit.getPluginCommand("skin").setExecutor(new skin());
        Bukkit.getPluginCommand("skin").setTabCompleter(new skin());
        Bukkit.getPluginCommand("hub").setExecutor(new hub());
        Bukkit.getPluginCommand("hub").setTabCompleter(new hub());
        Bukkit.getPluginCommand("fly").setExecutor(new fly());
        Bukkit.getPluginCommand("fly").setTabCompleter(new fly());
        Bukkit.getPluginCommand("heal").setExecutor(new heal());
        Bukkit.getPluginCommand("heal").setTabCompleter(new heal());
        Bukkit.getPluginCommand("proxycommand").setExecutor(new proxycommand());
        Bukkit.getPluginCommand("proxycommand").setTabCompleter(new proxycommand());
        Bukkit.getPluginManager().registerEvents(new WeatherChange(), this);
        Bukkit.getPluginCommand("announce").setExecutor(new announce());
        Bukkit.getPluginCommand("announce").setTabCompleter(new announce());
        OverrideCommandManager.register("tps", new tps());
        OverrideCommandManager.register("restart", new restart());
        LibEventManager.registerListeners(new ServerEvent());
        new LogListener().reloadFilter();

        new Thread(() -> {
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
                                speedcubingServer.restart();
                                break;
                            default:
                                LibEventManager.callEvent(new SocketEvent(receive));
                                break;
                        }
                    } else System.out.print("[Server] received null line of socket");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getScheduler().runTask(this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "end"));
            }
        }).start();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                restarting = false;
            }
        }, 2000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                restartable = true;
                if (Bukkit.getOnlinePlayers().size() == 0)
                    restart();
            }
        }, 43200000);
    }

    public void onDisable() {
        File index = new File(Bukkit.getWorlds().get(0).getWorldFolder() + "/playerdata");
        if (index.list() != null)
            for (String s : index.list())
                new File(index.getPath(), s).delete();
        index.delete();
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

    public static String[] getSkin(String name) {
        try {
            String data = DataIO.sendOutPut(getRandomBungeePort(null), "getskin|" + name);
            return data != null ? data.split("\\|") : SessionServer.getSkin(MojangAPI.getUUID(name));
        } catch (Exception e) {
            return null;
        }
    }

    public static void restart() {
        AsyncCatcher.enabled = false;
        try {
            WatchdogThread.doStop();
            for (EntityPlayer p : MinecraftServer.getServer().getPlayerList().players) {
                p.playerConnection.disconnect(SpigotConfig.restartMessage);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
            MinecraftServer.getServer().getServerConnection().b();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
            try {
                MinecraftServer.getServer().stop();
            } catch (Throwable t) {
            }
            Thread shutdownHook = new Thread(() -> {
                try {
                    Runtime.getRuntime().exec(new String[]{"screen", "-mdS", (Bukkit.getPort() % 2 == 1 ? "online" : "offline") + Bukkit.getServerName(), "sh", "../../../" + Bukkit.getServerName() + ".sh", Bukkit.getPort() % 2 == 1 ? "online" : "offline", "init"});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            shutdownHook.setDaemon(true);
            Runtime.getRuntime().addShutdownHook(shutdownHook);
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}