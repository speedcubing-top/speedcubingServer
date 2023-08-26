package top.speedcubing.server.mulitproxy;

import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.spigotmc.RestartCommand;
import top.speedcubing.lib.bukkit.PlayerUtils;
import top.speedcubing.lib.utils.ByteArrayDataBuilder;
import top.speedcubing.lib.utils.Threads;
import top.speedcubing.server.events.InputEvent;
import top.speedcubing.server.events.SocketEvent;
import top.speedcubing.server.player.User;
import top.speedcubing.server.speedcubingServer;
import top.speedcubing.server.utils.config;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketReader {
    public static ServerSocket tcpServer;

    private static void initServer() {
        try {
            tcpServer = new ServerSocket(Bukkit.getPort() + 1);
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
                    DataInputStream dataInputStream = new DataInputStream(s.getInputStream());
                    DataOutputStream dataOutputStream = new DataOutputStream(s.getOutputStream());
                    byte[] buffer = new byte[2048];
                    dataInputStream.read(buffer);
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(buffer));
                    try {
                        header = in.readUTF();
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                    switch (header) {
                        case "in":
                            try {
                                byte[] resend = new ByteArrayDataBuilder().write(((InputEvent) new InputEvent(in, in.readUTF()).call()).respond.toByteArray()).toByteArray();
                                dataOutputStream.write(resend);
                            } catch (IOException exception) {
                                exception.printStackTrace();
                            }
                            break;
                        case "bungee":
                            User.getUser(in.readInt()).tcpPort = in.readInt();
                            break;
                        case "cpsrequest":
                            int id = in.readInt();
                            User user = User.getUser(id);
                            if (user != null)
                                user.listened = in.readBoolean();
                            else speedcubingServer.preLoginStorage.get(id).cps = true;
                            break;
                        case "cfg":
                            config.reload();
                            config.reloadDatabase();
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
                            boolean vanish = in.readBoolean();
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
                            new SocketEvent(in, header).call();
                            break;
                    }
                    dataInputStream.close();
                    dataOutputStream.close();
                    in.close();
                    s.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
