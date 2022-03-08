package cubingserver.connection;

import cubing.bukkit.Event.ServerEventManager;
import cubingserver.customEvents.UDPEvent;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPSocketUtils {
    public static DatagramSocket socket;

    public void Load(int port) {
        try {
            socket = new DatagramSocket(port);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatagramPacket datagramPacket = new DatagramPacket(new byte[1024], 1024);
        new Thread(() -> {
            while (true) {
                try {
                    UDPSocketUtils.socket.receive(datagramPacket);
                    ServerEventManager.callEvent(new UDPEvent(new String(datagramPacket.getData(), 0, datagramPacket.getLength()).split("\\|")));
                } catch (Exception e) {
                }
            }
        }).start();
    }
}
