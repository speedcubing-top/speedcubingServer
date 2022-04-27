package cubingserver;

import cubing.lib.api.SQLConnection;
import speedcubing.spigot.Event.ServerEventManager;
import cubing.lib.bukkit.PlayerUtils;
import cubing.lib.utils.sockets.TCP;
import cubing.lib.utils.sockets.UDP;
import cubingserver.Commands.*;
import cubingserver.Commands.offline.premium;
import cubingserver.Commands.offline.resetpassword;
import cubingserver.ExploitFixer.ForceOp;
import cubingserver.customEvents.NickEvent;
import cubingserver.customEvents.SocketEvent;
import cubingserver.customEvents.UDPEvent;
import cubingserver.libs.LogListener;
import cubingserver.things.CommandPermissions;
import cubingserver.things.Cps;
import cubingserver.things.events.*;
import cubingserver.things.froze;
import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class speedcubingServer extends JavaPlugin {
    public static int AllPlayers;

    public static Map<UUID, Set<String>> permissions = new HashMap<>();
    public static Map<UUID, Double[]> velocities = new HashMap<>();

    public static int BungeeTCP;
    public static int TCP;
    public static SQLConnection connection;
    public static TCP tcp;
    public static UDP udp;
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

        TCP = Bukkit.getPort() + 2;
        BungeeTCP = 25568 - Bukkit.getPort() % 2;
        new config().reload();
        tcp = new TCP("localhost", TCP, 100);
        udp = new UDP("localhost", Bukkit.getPort());
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
        new LogListener().reloadFilter();

        new Thread(() -> {
            while (true) {
                String receive = "";
                try {
                    receive = new BufferedReader(new InputStreamReader(tcp.socket.accept().getInputStream())).readLine();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String[] rs = receive.split("\\|");
                switch (rs[0]) {
                    case "c"://cps
                        switch (rs[1]) {
                            case "a":
                                Cps.CpsListening.add(UUID.fromString(rs[2]));
                                break;
                            case "r":
                                Cps.CpsListening.remove(UUID.fromString(rs[2]));
                                break;
                        }
                        break;
                    case "f"://froze
                        switch (rs[1]) {
                            case "a":
                                froze.frozed.add(Bukkit.getPlayerExact(rs[2]).getUniqueId());
                                break;
                            case "r":
                                froze.frozed.remove(Bukkit.getPlayerExact(rs[2]).getUniqueId());
                                break;
                        }
                        break;
                    case "g":
                        new config().reload();
                        break;
                    case "m"://demo
                        PacketPlayOutGameStateChange packet = new PacketPlayOutGameStateChange(5, 0);
                        if (rs[1].equals("%ALL%"))
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
                            }
                        else
                            ((CraftPlayer) Bukkit.getPlayerExact(rs[1])).getHandle().playerConnection.sendPacket(packet);
                        break;
                    case "r"://crash
                        if (rs[1].equals("%ALL%"))
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                PlayerUtils.explosionCrash(((CraftPlayer) p).getHandle().playerConnection);
                            }
                        else
                            PlayerUtils.explosionCrash(((CraftPlayer) Bukkit.getPlayerExact(rs[1])).getHandle().playerConnection);
                        break;
                    case "t"://run command
                        String re = receive.split("\\|", 2)[1];
                        Bukkit.getScheduler().runTask(speedcubingServer.getPlugin(speedcubingServer.class), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), re));
                        break;
                    case "l"://enable logger
                        LogListener.Listening = rs[1].equals("a");
                        break;
                    case "v"://velocity
                        switch (rs[1]) {
                            case "a":
                                speedcubingServer.velocities.put(UUID.fromString(rs[2]), new Double[]{Double.parseDouble(rs[3]), Double.parseDouble(rs[4])});
                                break;
                            case "r":
                                speedcubingServer.velocities.remove(UUID.fromString(rs[2]));
                                break;
                        }
                        break;
                    default:
                        ServerEventManager.callEvent(new SocketEvent(rs));
                        break;
                }
            }
        }).start();
        DatagramPacket datagramPacket = new DatagramPacket(new byte[1024], 1024);
        new Thread(() -> {
            while (true) {
                try {
                    udp.socket.receive(datagramPacket);
                    ServerEventManager.callEvent(new UDPEvent(new String(datagramPacket.getData(), 0, datagramPacket.getLength()).split("\\|")));
                } catch (Exception e) {
                }
            }
        }).start();
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

    public static void node(boolean add, UUID uuid) {
        speedcubingServer.tcp.send(speedcubingServer.BungeeTCP, "h|" + (add ? "a" : "r") + "|" + uuid);
    }
}
