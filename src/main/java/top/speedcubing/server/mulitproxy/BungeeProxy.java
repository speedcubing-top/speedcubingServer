package top.speedcubing.server.mulitproxy;

import top.speedcubing.common.io.SocketWriter;
import top.speedcubing.lib.utils.ByteArrayDataBuilder;
import top.speedcubing.lib.utils.internet.HostAndPort;
import top.speedcubing.server.speedcubingServer;

public class BungeeProxy {
    public static void switchServer(int id, String server) {
        switchServer(id, server, speedcubingServer.getRandomBungee());
    }

    public static void switchServer(int id, String server, HostAndPort target) {
        SocketWriter.write(target, new ByteArrayDataBuilder().writeUTF("connectionreq").writeInt(id).writeUTF("").writeUTF(server).toByteArray());
    }
}
