package top.speedcubing.server;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunkBulk;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.RestartCommand;
import top.speedcubing.common.database.Database;
import top.speedcubing.lib.bukkit.TabCompleteUtils;
import top.speedcubing.lib.eventbus.CubingEventManager;
import top.speedcubing.lib.utils.SystemUtils;
import top.speedcubing.lib.utils.internet.HostAndPort;
import top.speedcubing.server.authenticator.AuthenticatorCommand;
import top.speedcubing.server.commandoverrider.OverrideCommandManager;
import top.speedcubing.server.commands.discord;
import top.speedcubing.server.commands.fly;
import top.speedcubing.server.commands.getitemtype;
import top.speedcubing.server.commands.hub;
import top.speedcubing.server.commands.image;
import top.speedcubing.server.commands.limbo;
import top.speedcubing.server.commands.nick.nick;
import top.speedcubing.server.commands.nick.unnick;
import top.speedcubing.server.commands.overrided.plugins;
import top.speedcubing.server.commands.skin;
import top.speedcubing.server.commands.staff.announce;
import top.speedcubing.server.commands.staff.deepfry;
import top.speedcubing.server.commands.staff.freeze;
import top.speedcubing.server.commands.staff.heal;
import top.speedcubing.server.commands.staff.history;
import top.speedcubing.server.commands.staff.kaboom;
import top.speedcubing.server.commands.staff.proxycommand;
import top.speedcubing.server.commands.staff.sendpacket;
import top.speedcubing.server.commands.staff.serverconfig;
import top.speedcubing.server.commands.staff.testkb;
import top.speedcubing.server.lang.LanguageSystem;
import top.speedcubing.server.listeners.PostListen;
import top.speedcubing.server.listeners.PreListen;
import top.speedcubing.server.listeners.SingleListen;
import top.speedcubing.server.login.PreLoginData;
import top.speedcubing.server.mulitproxy.SocketReader;
import top.speedcubing.server.player.User;
import top.speedcubing.server.utils.CubingTick;
import top.speedcubing.server.utils.LogListener;
import top.speedcubing.server.utils.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class speedcubingServer extends JavaPlugin {
    public static final Pattern nameRegex = Pattern.compile("^\\w{3,16}$");
    public static final Pattern legacyNameRegex = Pattern.compile("^\\w{1,16}$");
    public static Map<Integer, PreLoginData> preLoginStorage = new HashMap<>();

    public static boolean canRestart = true; //can Timer/Quit restart server?
    public static boolean restartable = false; //is it time to restart ?
    public static speedcubingServer instance;
    public static ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(10);
    public ProtocolManager protocolManager;

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
        registerListeners(
                new PreListen(),
                new PostListen(),
                new SingleListen());
        registerListeners(new history(), new sendpacket());
    }

    @Override
    public void onEnable() {
        instance = this;
        protocolManager = ProtocolLibrary.getProtocolManager();
        sendpacket.initFuckPeople();
        config.reload(true);
        Database.connect(config.DatabaseURL, config.DatabaseUser, config.DatabasePassword);
        config.reloadDBConfig();

        CubingTick.init();
        SocketReader.init();
        LanguageSystem.init();
        //temporarily fixed

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

        registerListeners();
        registerCommands();
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
//        new Timer("Cubing-Restart-Thread").schedule(new TimerTask() {
//            @Override
//            public void run() {
//                restartable = true;
//                if (Bukkit.getOnlinePlayers().isEmpty())
//                    restart();
//            }
//        }, 28800000);

        if (!config.removeLogs) {
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
        CubingTick.calcTimer.cancel();
        Database.systemConnection.update(
                "servers",
                "onlinecount=-1,ram_max=-1,ram_heap=-1,ram_used=-1,tps1=-1,tps2=-1,tps3=-1",
                "name='" + Bukkit.getServerName() + "'"
        );
    }

    public static HostAndPort getRandomBungee() {
        return (!User.usersByID.values().isEmpty() ? User.usersByID.values().iterator().next().proxy : new HostAndPort("host.docker.internal", 25565 + 1000));
    }

    public static String playerNameEncode(String name) {
        int buffer = 0;
        int nBitsIn = 0;
        StringBuilder builder = new StringBuilder();
        for (char c : name.toCharArray()) {
            //57('9') -> c - '0'                 c = c - 48    // 0~9 10
            //90('Z') -> c - ('A' - 10)          c = c - 55    // A~Z 26
            //95('_') -> (26 + 10)               c = 36        // _   1
            //other   -> c - ('a' - 10 - 26 - 1) c = c - 59    // a~z 26
            int b = (c <= 57 ? c - 48 : (c <= 90 ? c - 55 : (c == 95 ? 36 : c - 60)));
            nBitsIn += 6;
            buffer |= (b << (32 - nBitsIn));
            while (nBitsIn >= 16) {
                builder.append((char) (buffer >>> 16));
                nBitsIn -= 16;
                buffer <<= 16;
            }
        }
        if (nBitsIn != 0) {
            builder.append((char) (buffer >>> 16));
        }
        return builder.toString();
        /* old
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
         */
    }

    public static void restart() {
        if (canRestart)
            RestartCommand.restart();
    }

    public static void registerListeners(Listener... listeners) {
        for (Listener l : listeners) {
            Bukkit.getPluginManager().registerEvents(l, instance);
        }
    }
}