package top.speedcubing.server.mulitproxy;

import java.io.DataInputStream;
import java.io.IOException;
import top.speedcubing.lib.utils.ByteArrayDataBuilder;
import top.speedcubing.lib.utils.IOUtils;
import top.speedcubing.server.speedcubingServer;

public class DataIO {

    public static DataInputStream sendOutPut(int port, byte[] data) {
        try {
            byte[] b = new ByteArrayDataBuilder().writeUTF("in").write(data).toByteArray();
            byte[] result = speedcubingServer.tcpClient.sendAndRead(port, b, 2048);
            return IOUtils.toDataInputStream(result);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
