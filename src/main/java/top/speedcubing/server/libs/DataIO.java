package top.speedcubing.server.libs;

import top.speedcubing.lib.utils.ByteArrayDataBuilder;
import top.speedcubing.server.events.InputEvent;
import top.speedcubing.server.speedcubingServer;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataIO {

    static Map<String, DataInputStream> waitData = new HashMap<>();

    public static DataInputStream sendOutPut(int port, byte[] data) {
        String uuid = UUID.randomUUID().toString();
        long b = System.currentTimeMillis();
        boolean a = true;
        try {
            speedcubingServer.tcpClient.sendUnsafe(port, new ByteArrayDataBuilder().writeUTF("in").writeInt(speedcubingServer.tcpServer.getLocalPort()).writeUTF(uuid).write(data).toByteArray());
        } catch (Exception e) {
            a = false;
        }
        while (a && !waitData.containsKey(uuid) && System.currentTimeMillis() - b < 100) {
        }
        DataInputStream c = waitData.get(uuid);
        waitData.remove(uuid);
        return c;
    }


    public static void handle(DataInputStream in, String header) throws IOException {
        switch (header) {
            case "out":
                DataIO.waitData.put(in.readUTF(), in);
                break;
            case "in":
                new Thread(() -> {
                    try {
                        int port = in.readInt();
                        InputEvent inputEvent = (InputEvent) new InputEvent(in, in.readUTF(), in.readUTF()).call();
                        speedcubingServer.tcpClient.send(port, inputEvent.respond.toByteArray());
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }).start();
                break;
        }
    }
}
