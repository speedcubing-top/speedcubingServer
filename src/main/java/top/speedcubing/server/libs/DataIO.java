package top.speedcubing.server.libs;

import top.speedcubing.lib.utils.ByteArrayDataBuilder;
import top.speedcubing.lib.utils.sockets.ByteUtils;
import top.speedcubing.server.events.InputEvent;
import top.speedcubing.server.speedcubingServer;

import java.io.*;

public class DataIO {

    public static DataInputStream sendOutPut(int port, byte[] data) {
        try {
            byte[] result = speedcubingServer.tcpClient.sendAndRead(port, data, 2048);
            return ByteUtils.byteToDataInputStream(result);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
