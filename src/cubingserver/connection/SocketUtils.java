package cubingserver.connection;

import cubing.bukkit.Event.ServerEventManager;
import cubing.bukkit.PlayerUtils;
import cubingserver.customEvents.SocketEvent;
import cubingserver.libs.LogListener;
import cubingserver.speedcubingServer;
import cubingserver.things.Cps;
import cubingserver.things.froze;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

public class SocketUtils {
    public static ServerSocket socket;

    public static void UnHandledSendData(int port, String data, int timeout) throws Exception {
        Socket clientSocket = new Socket();
        clientSocket.connect(new InetSocketAddress("localhost", port), timeout);
        new DataOutputStream(clientSocket.getOutputStream()).writeBytes(data);
        clientSocket.close();
    }

    public static void sendData(int port, String data, int timeout) {
        try {
            Socket clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress("localhost", port), timeout);
            new DataOutputStream(clientSocket.getOutputStream()).writeBytes(data);
            clientSocket.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void Load(int port) {
        try {
            socket = new ServerSocket(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            while (true) {
                String receive = "";
                try {
                    receive = new BufferedReader(new InputStreamReader(SocketUtils.socket.accept().getInputStream())).readLine();
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
                    case "f"://frosze
                        switch (rs[1]) {
                            case "a":
                                froze.frozed.add(Bukkit.getPlayerExact(rs[2]).getUniqueId());
                                break;
                            case "r":
                                froze.frozed.remove(Bukkit.getPlayerExact(rs[2]).getUniqueId());
                                break;
                        }
                        break;
                    case "k"://kick
                        String text = "";
                        String[] hex = rs[2].split("\\\\u");
                        for (int i = 1; i < hex.length; i++) {
                            text += (char) Integer.parseInt(hex[i], 16);
                        }
                        String t = text;
                        Bukkit.getScheduler().runTask(speedcubingServer.getPlugin(speedcubingServer.class), () -> Bukkit.getPlayerExact(rs[1]).kickPlayer(t));
                        break;
                    case "r"://crash
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
    }
}