package top.speedcubing.common.io;

import java.io.DataInputStream;
import java.io.IOException;
import top.speedcubing.lib.utils.ByteArrayDataBuilder;
import top.speedcubing.lib.utils.IOUtils;
import top.speedcubing.lib.utils.sockets.TCPClient;

public class SocketWriter {

    private static TCPClient tcpClient;

    public static void init() {
        tcpClient = new TCPClient("localhost", 100);
    }

    public static DataInputStream writeResponse(int port, byte[] data) {
        try {
            byte[] b = new ByteArrayDataBuilder().writeUTF("in").write(data).toByteArray();
            byte[] result = tcpClient.sendAndRead(port, b, 2048);
            return IOUtils.toDataInputStream(result);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void write(int port, byte[] data) {
        tcpClient.send(port, data);
    }
}
