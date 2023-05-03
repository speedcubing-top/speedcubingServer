package top.speedcubing.server;

import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInTabComplete;
import net.minecraft.server.v1_8_R3.PacketPlayOutStatistic;
import net.minecraft.server.v1_8_R3.Statistic;
import top.speedcubing.lib.bukkit.event.PlayInEvent;
import top.speedcubing.lib.bukkit.event.PlayOutEvent;
import top.speedcubing.lib.eventbus.LibEventHandler;
import top.speedcubing.lib.utils.Reflections;
import top.speedcubing.server.events.InputEvent;
import top.speedcubing.server.libs.PreLoginData;

import java.io.IOException;
import java.util.Map;
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
        } else if (e.packet instanceof PacketPlayInClientCommand) {
            System.out.println(Reflections.getField(e.packet, "a"));
        }
    }

    @LibEventHandler
    public void PlayOutEvent(PlayOutEvent e) {
        if (e.packet instanceof PacketPlayOutStatistic) {
            ((Map<Statistic, Integer>) Reflections.getField(e.packet, "a")).replaceAll((k, v) -> 0);
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
}