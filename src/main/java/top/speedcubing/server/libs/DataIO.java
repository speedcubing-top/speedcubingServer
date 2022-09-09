package top.speedcubing.server.libs;

import top.speedcubing.lib.eventbus.LibEventManager;
import top.speedcubing.lib.utils.StringUtils;
import top.speedcubing.server.events.InputEvent;
import top.speedcubing.server.speedcubingServer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataIO {

    static Map<String, String> waitData = new HashMap<>();

    public static String sendOutPut(int port, String data) {
        String uuid = UUID.randomUUID().toString();
        long b = System.currentTimeMillis();
        boolean a = true;
        try {
            speedcubingServer.tcp.sendUnsafe(port, "in|" + speedcubingServer.tcp.socket.getLocalPort() + "|" + uuid + "|" + data);
        } catch (Exception e) {
            a = false;
        }
        while (a && !waitData.containsKey(uuid) && System.currentTimeMillis() - b < 100) {
        }
        String c = waitData.get(uuid);
        waitData.remove(uuid);
        return c;
    }


    public static void handle(String receive, String[] rs) {
        switch (rs[0]) {
            case "out":
                waitData.put(rs[1], rs.length == 2 ? null : receive.substring(StringUtils.indexOf(receive, "|", 2) + 1));
                break;
            case "in":
                LibEventManager.callEvent(new InputEvent(receive));
                break;
        }
    }
}
