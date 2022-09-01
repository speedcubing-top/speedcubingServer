package top.speedcubing.server;

import net.minecraft.server.v1_8_R3.PacketPlayInTabComplete;
import top.speedcubing.lib.bukkit.event.PlayInEvent;
import top.speedcubing.lib.eventbus.LibEventHandler;
import top.speedcubing.server.events.InputEvent;

public class ServerEvent {
    @LibEventHandler
    public void PlayInEvent(PlayInEvent e) {
        if (e.packet instanceof PacketPlayInTabComplete && speedcubingServer.blockedTab.contains(((PacketPlayInTabComplete) e.packet).a()))
            e.isCancelled = true;
    }

    @LibEventHandler
    public void InputEvent(InputEvent e) {
        String[] rs = e.receive.split("\\|");
        switch (rs[3]) {
            case "bungee":
                speedcubingServer.tcpStorage.put(Integer.parseInt(rs[4]), Integer.parseInt(rs[5]));
                break;
            case "bungeevelo":
                if (rs[4].equals("a"))
                    speedcubingServer.veloStorage.put(Integer.parseInt(rs[5]), new Double[]{Double.parseDouble(rs[6]), Double.parseDouble(rs[7])});
                break;
        }
    }

    @LibEventHandler(priority = 1000)
    public void InputEvent2(InputEvent e) {
        String[] rs = e.receive.split("\\|");
        speedcubingServer.tcp.send(Integer.parseInt(rs[1]), "out|" + rs[2] + "|" + rs[3]);
    }
}