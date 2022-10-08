package top.speedcubing.server;

import net.minecraft.server.v1_8_R3.PacketPlayInTabComplete;
import top.speedcubing.lib.api.event.ProfileRespondEvent;
import top.speedcubing.lib.bukkit.event.PlayInEvent;
import top.speedcubing.lib.eventbus.LibEventHandler;
import top.speedcubing.server.events.InputEvent;

import java.util.regex.Pattern;

public class ServerEvent {
    @LibEventHandler
    public void PlayInEvent(PlayInEvent e) {
        if (e.packet instanceof PacketPlayInTabComplete) {
            String s = ((PacketPlayInTabComplete) e.packet).a();
            for (Pattern p : config.blockedTab) {
                if (p.matcher(s).matches())
                    return;
            }
            e.isCancelled = true;
        }
    }

    @LibEventHandler
    public void InputEvent(InputEvent e) {
        String[] rs = e.receive.split("\\|");
        switch (rs[3]) {
            case "bungee":
                speedcubingServer.preLoginStorage.put(Integer.parseInt(rs[4]), rs);
                break;
        }
    }

    @LibEventHandler(priority = 1000)
    public void InputEvent2(InputEvent e) {
        String[] rs = e.receive.split("\\|");
        speedcubingServer.tcp.send(Integer.parseInt(rs[1]), "out|" + rs[2] + "|" + rs[3]);
    }

    @LibEventHandler
    public void ProfileRespondEvent(ProfileRespondEvent e) {
        if (speedcubingServer.isBungeeOnlineMode)
            speedcubingServer.connection.update("playersdata", "name='" + e.profile.getName() + "'", "uuid='" + e.profile.getUUIDString() + "'");
    }
}