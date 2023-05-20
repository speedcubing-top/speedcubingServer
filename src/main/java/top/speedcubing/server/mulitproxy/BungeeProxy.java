package top.speedcubing.server.mulitproxy;

import top.speedcubing.lib.utils.ByteArrayDataBuilder;
import top.speedcubing.server.speedcubingServer;

public class BungeeProxy {
    public static void switchServer(int id, String server) {
        switchServer(id, server, speedcubingServer.getRandomBungeePort());
    }

    public static void switchServer(int id, String server, int port) {
        speedcubingServer.tcpClient.send(port, new ByteArrayDataBuilder().writeUTF("connectionreq").writeInt(id).writeUTF("").writeUTF(server).toByteArray());
    }
}
