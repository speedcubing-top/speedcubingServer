package speedcubing.server;

import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import speedcubing.lib.api.SQLConnection;
import speedcubing.lib.bukkit.PlayerUtils;
import speedcubing.lib.utils.sockets.TCP;
import speedcubing.server.Commands.*;
import speedcubing.server.Commands.offline.premium;
import speedcubing.server.Commands.offline.resetpassword;
import speedcubing.server.ExploitFixer.ForceOp;
import speedcubing.server.events.SocketEvent;
import speedcubing.server.libs.LogListener;
import speedcubing.server.libs.User;
import speedcubing.server.listeners.*;
import speedcubing.spigot.Event.ServerEventManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;

public class speedcubingServer extends JavaPlugin {
    public static int AllPlayers;
    public static int TCP;
    public static SQLConnection connection;
    public static TCP tcp;
    public static boolean isBungeeOnlineMode;
    public static Set<Pattern> blockedLog = new HashSet<>();
    public static Set<String> blockedTab = new HashSet<>();
    public static Map<UUID, Integer> tcpStorage = new HashMap<>();
    public static Map<UUID, Double[]> veloStorage = new HashMap<>();

    public void onEnable() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        try {
//            File file = new File("../../Proxies/WaterFall/config.yml");
//            isBungeeOnlineMode = (Boolean) ((HashMap<?, ?>) new Yaml().load(Files.newInputStream(file.toPath()))).get("online_mode");
            File file = new File("../../Proxies/Velocity/velocity.toml");
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
        connection = new SQLConnection("jdbc:mysql://localhost:3306/" + (Bukkit.getPort() % 2 == 1 ? "speedcubing" : "offlinecubing") + "?useUnicode=true&characterEncoding=utf8", "cubing", "6688andy");

        TCP = Bukkit.getPort() + 2;
        new config().reload();
        tcp = new TCP("localhost", TCP, 100);
        new Cps().Load();
        new ForceOp().run();
        Bukkit.getPluginManager().registerEvents(new PlayerKick(), this);
        Bukkit.getPluginManager().registerEvents(new froze(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
        Bukkit.getPluginManager().registerEvents(new CommandPermissions(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryOpen(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuit(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerLogin(), this);
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
        Bukkit.getPluginCommand("shutdown").setExecutor(new shutdown());
        Bukkit.getPluginCommand("shutdown").setTabCompleter(new shutdown());
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
        Bukkit.getPluginCommand("announce").setExecutor(new announce());
        Bukkit.getPluginCommand("announce").setTabCompleter(new announce());
        ServerEventManager.registerListeners(new ServerEvent());
        new LogListener().reloadFilter();

        new Thread(() -> {
            try {
                String receive;
                while (true) {
                    receive = new BufferedReader(new InputStreamReader(tcp.socket.accept().getInputStream())).readLine();
                    String[] rs = receive.split("\\|");
                    switch (rs[0]) {
                        case "bungee":
                            User.getUser(UUID.fromString(rs[1])).tcpPort = Integer.parseInt(rs[2]);
                            break;
                        case "cps":
                            switch (rs[1]) {
                                case "a":
                                    Cps.CpsListening.add(UUID.fromString(rs[2]));
                                    break;
                                case "r":
                                    Cps.CpsListening.remove(UUID.fromString(rs[2]));
                                    break;
                            }
                            break;
                        case "froze":
                            switch (rs[1]) {
                                case "a":
                                    froze.frozed.add(Bukkit.getPlayerExact(rs[2]).getUniqueId());
                                    break;
                                case "r":
                                    froze.frozed.remove(Bukkit.getPlayerExact(rs[2]).getUniqueId());
                                    break;
                            }
                            break;
                        case "cfg":
                            new config().reload();
                            break;
                        case "demo":
                            PacketPlayOutGameStateChange packet = new PacketPlayOutGameStateChange(5, 0);
                            if (rs[1].equals("%ALL%"))
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
                                }
                            else
                                ((CraftPlayer) Bukkit.getPlayerExact(rs[1])).getHandle().playerConnection.sendPacket(packet);
                            break;
                        case "crash":
                            if (rs[1].equals("%ALL%"))
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    PlayerUtils.explosionCrash(((CraftPlayer) p).getHandle().playerConnection);
                                }
                            else
                                PlayerUtils.explosionCrash(((CraftPlayer) Bukkit.getPlayerExact(rs[1])).getHandle().playerConnection);
                            break;
                        case "in":
                            switch (rs[3]) {
                                case "bungee":
                                    tcpStorage.put(UUID.fromString(rs[4]), Integer.parseInt(rs[5]));
                                    tcp.send(Integer.parseInt(rs[1]), "out|" + rs[2] + "| ");
                                    break;
                                case "bungeevelo":
                                    if (rs[4].equals("a"))
                                        veloStorage.put(UUID.fromString(rs[5]), new Double[]{Double.parseDouble(rs[6]), Double.parseDouble(rs[7])});
                                    tcp.send(Integer.parseInt(rs[1]), "out|" + rs[2] + "| ");
                                    break;
                            }
                            break;
//                    case "l"://enable logger
//                        LogListener.Listening = rs[1].equals("a");
//                        break;
                        case "velo":
                            User.getUser(UUID.fromString(rs[2])).velocities = rs[1].equals("a") ? new double[]{Double.parseDouble(rs[3]), Double.parseDouble(rs[4])} : null;
                            break;
                        default:
                            ServerEventManager.callEvent(new SocketEvent(rs));
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getScheduler().runTask(this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "end"));
            }
        }).start();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                shutdown.restarting = false;
            }
        }, 2000);
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

    public static void node(boolean add, UUID uuid, int port) {
        speedcubingServer.tcp.send(port, "hasnode|" + (add ? "a" : "r") + "|" + uuid);
    }

    public static Map<String, String[]> colors = new HashMap<>();
    public static Map<String, Set<String>> rankPermissions = new HashMap<>();

    public static String[] getFormat(String rank) {
        return colors.get(rank);
    }

    public static int getCode(String rank) {
        return 10 + ranks.indexOf(rank);
    }

    public static List<String> ranks = new ArrayList<>();

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
}
