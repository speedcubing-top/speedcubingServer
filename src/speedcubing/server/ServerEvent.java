package speedcubing.server;

import speedcubing.lib.bukkit.event.PlayInTabCompleteEvent;
import speedcubing.lib.eventbus.LibEventHandler;
import speedcubing.server.events.InputEvent;

import java.util.UUID;

public class ServerEvent {
    @LibEventHandler
    public void PlayInTabCompleteEvent(PlayInTabCompleteEvent e) {
        if (speedcubingServer.blockedTab.contains(e.packet.a()))
            e.isCancelled = true;
    }

    @LibEventHandler(priority = -500)
    public void InputEvent(InputEvent e) {
        String[] rs = e.receive.split("\\|");
        switch (rs[3]) {
            case "bungee":
                speedcubingServer.tcpStorage.put(UUID.fromString(rs[4]), Integer.parseInt(rs[5]));
                break;
            case "bungeevelo":
                if (rs[4].equals("a"))
                    speedcubingServer.veloStorage.put(UUID.fromString(rs[5]), new Double[]{Double.parseDouble(rs[6]), Double.parseDouble(rs[7])});
                break;
        }
    }

    @LibEventHandler(priority = 1000)
    public void InputEvent2(InputEvent e) {
        String[] rs = e.receive.split("\\|");
        speedcubingServer.tcp.send(Integer.parseInt(rs[1]), "out|" + rs[2] + "|" + rs[3]);
    }
}