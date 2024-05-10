package top.speedcubing.common.io;

import java.io.DataInputStream;
import top.speedcubing.lib.utils.bytes.ByteArrayBuffer;
import top.speedcubing.lib.utils.bytes.IOUtils;
import top.speedcubing.lib.utils.internet.HostAndPort;
import top.speedcubing.lib.utils.sockets.TCPClient;

public class SocketWriter {

    public static DataInputStream writeResponse(HostAndPort hostPort, byte[] data) {
        byte[] packet = new ByteArrayBuffer().writeUTF("in").write(data).toByteArray();
        byte[] response = TCPClient.writeAndReadAll(hostPort, packet);
        return IOUtils.toDataInputStream(response);
    }
}
