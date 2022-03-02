package cubingserver.connection;

import org.bukkit.Bukkit;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPSocketUtils {
    public static DatagramSocket socket;

    public void Load(int port) {
        try {
            socket = new DatagramSocket(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
