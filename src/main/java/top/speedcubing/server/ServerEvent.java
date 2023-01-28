package top.speedcubing.server;

import net.minecraft.server.v1_8_R3.PacketPlayInTabComplete;
import top.speedcubing.lib.api.event.ProfileRespondEvent;
import top.speedcubing.lib.bukkit.event.PlayInEvent;
import top.speedcubing.lib.eventbus.LibEventHandler;
import top.speedcubing.server.events.InputEvent;
import top.speedcubing.server.libs.PreLoginData;

import java.io.IOException;
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
        try {
            switch (e.header) {
                case "bungee":
                    int i = e.receive.readInt();
                    speedcubingServer.preLoginStorage.put(i, new PreLoginData(e.receive.readInt(), e.receive.readUTF(), e.receive.readUTF(), e.receive.readBoolean()));
                    break;
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @LibEventHandler
    public void ProfileRespondEvent(ProfileRespondEvent e) {
        if (speedcubingServer.isBungeeOnlineMode)
            speedcubingServer.connection.update("playersdata", "name='" + e.profile.getName() + "'", "uuid='" + e.profile.getUUIDString() + "'");
    }
}