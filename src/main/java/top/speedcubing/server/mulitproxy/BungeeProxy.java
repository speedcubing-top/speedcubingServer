package top.speedcubing.server.mulitproxy;

import top.speedcubing.lib.utils.ByteArrayDataBuilder;
import top.speedcubing.server.speedcubingServer;

public class BungeeProxy {
    public static void switchServer(int id, String server) {
        speedcubingServer.tcpClient.send(speedcubingServer.getRandomBungeePort(), new ByteArrayDataBuilder().writeUTF("switchserver").writeInt(id).writeUTF(server).toByteArray());
    }
}
