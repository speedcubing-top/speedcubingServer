package top.speedcubing.common.io;

import java.io.DataInputStream;
import java.io.IOException;
import top.speedcubing.lib.utils.ByteArrayDataBuilder;
import top.speedcubing.lib.utils.IOUtils;
import top.speedcubing.lib.utils.internet.HostAndPort;
import top.speedcubing.lib.utils.sockets.TCPClient;

public class SocketWriter {

    public static DataInputStream writeResponse(HostAndPort hostPort, byte[] data) {
        try {
            TCPClient tcpClient = new TCPClient(hostPort.getHost(), 100);
            byte[] b = new ByteArrayDataBuilder().writeUTF("in").write(data).toByteArray();
            byte[] result = tcpClient.sendAndRead(hostPort.getPort(), b, 2048);
            return IOUtils.toDataInputStream(result);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void write(HostAndPort hostPort, byte[] data) {
        TCPClient tcpClient = new TCPClient(hostPort.getHost(), 100);
        tcpClient.send(hostPort.getPort(), data);
    }
}
