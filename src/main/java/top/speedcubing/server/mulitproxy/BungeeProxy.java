package top.speedcubing.server.mulitproxy;

import top.speedcubing.lib.utils.bytes.ByteArrayBuffer;
import top.speedcubing.lib.utils.internet.HostAndPort;
import top.speedcubing.lib.utils.sockets.TCPClient;
import top.speedcubing.server.speedcubingServer;

public class BungeeProxy {
    public static void switchServer(int id, String server) {
        speedcubingServer.writeToRandomProxy(new ByteArrayBuffer().writeUTF("connectionreq").writeInt(id).writeUTF("").writeUTF(server).toByteArray());
    }
}
