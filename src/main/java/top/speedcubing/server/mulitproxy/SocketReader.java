package top.speedcubing.server.mulitproxy;

import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.spigotmc.RestartCommand;
import top.speedcubing.lib.bukkit.PlayerUtils;
import top.speedcubing.lib.utils.ByteArrayDataBuilder;
import top.speedcubing.lib.utils.IOUtils;
import top.speedcubing.lib.utils.Threads;
import top.speedcubing.server.events.InputEvent;
import top.speedcubing.server.events.SocketEvent;
import top.speedcubing.server.player.User;
import top.speedcubing.server.speedcubingServer;
import top.speedcubing.server.utils.config;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketReader {
    public static ServerSocket tcpServer;

    private static void initServer() {
        try {
            tcpServer = new ServerSocket(Bukkit.getPort() + 1000);
            tcpServer.setSoTimeout(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void init() {
        initServer();
        Threads.newThread("Cubing-Socket-Thread", () -> {
            while (true) {
                try {
                    Socket s = tcpServer.accept();
                    String header;
                    InputStream in = s.getInputStream();
                    OutputStream out = s.getOutputStream();
                    byte[] d = IOUtils.readOnce(in, 2048);
                    DataInputStream data = IOUtils.toDataInputStream(d);
                    try {
                        header = data.readUTF();
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                    switch (header) {
                        case "in":
                            try {
                                byte[] resend = new ByteArrayDataBuilder().write(((InputEvent) new InputEvent(data, data.readUTF()).call()).respond.toByteArray()).toByteArray();
                                out.write(resend);
                            } catch (IOException exception) {
                                exception.printStackTrace();
                            }
                            break;
                        case "bungee":
                            User.getUser(data.readInt()).tcpPort = data.readInt();
                            break;
                        case "cpsrequest":
                            int id = data.readInt();
                            User user = User.getUser(id);
                            if (user != null)
                                user.listened = data.readBoolean();
                            else speedcubingServer.preLoginStorage.get(id).cps = true;
                            break;
                        case "cfg":
                            config.reload();
                            config.reloadDatabase();
                            break;
                        case "demo":
                            PacketPlayOutGameStateChange packet = new PacketPlayOutGameStateChange(5, 0);
                            id = data.readInt();
                            if (id == 0)
                                User.getUsers().forEach(a -> a.sendPacket(packet));
                            else
                                User.getUser(id).sendPacket(packet);
                            break;
                        case "crash":
                            id = data.readInt();
                            if (id == 0)
                                Bukkit.getOnlinePlayers().forEach(PlayerUtils::crashAll);
                            else
                                PlayerUtils.crashAll(User.getUser(id).player);
                            break;
                        case "velo":
                            User.getUser(data.readInt()).velocities = data.readBoolean() ? new double[]{data.readDouble(), data.readDouble()} : null;
                            break;
                        case "vanish":
                            user = User.getUser(data.readInt());
                            boolean vanish = data.readBoolean();
                            user.vanished = vanish;
                            if (vanish)
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
                            new SocketEvent(data, header).call();
                            break;
                    }
                    IOUtils.closeQuietly(in, out, data, s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
