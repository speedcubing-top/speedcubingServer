package top.speedcubing.server;

import net.minecraft.server.v1_8_R3.*;
import top.speedcubing.lib.bukkit.event.*;
import top.speedcubing.lib.eventbus.LibEventHandler;
import top.speedcubing.lib.utils.Reflections;
import top.speedcubing.server.events.InputEvent;
import top.speedcubing.server.libs.*;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class ServerEvent {

    public static int get(String s) {
        String[] args = s.split(" ");
        if (args.length == 1)
            return !s.endsWith(" ") ? -1 : (s.endsWith("  ") ? 2 : 0);
        return s.endsWith(" ") ? args.length - 1 : args.length - 2;
    }

    @LibEventHandler
    public void PlayInEvent(PlayInEvent e) {
        if (e.packet instanceof PacketPlayInTabComplete) {
            String s = ((PacketPlayInTabComplete) e.packet).a();
            String command = s.split(" ")[0].substring(1).toLowerCase();
            User user = User.getUser(e.player);
            user.lastTabbed = command;
            if (user.hasPermission("cmd." + command) || user.hasPermission("cmd.*"))
                return;
            e.isCancelled = true;
        }
    }

    @LibEventHandler
    public void PlayOutEvent(PlayOutEvent e) {
        if (e.packet instanceof PacketPlayOutStatistic) {
            ((Map<Statistic, Integer>) Reflections.getField(e.packet, "a")).replaceAll((k, v) -> 0);
        } else if(e.packet instanceof PacketPlayOutTabComplete){
            String[] s = (String[]) Reflections.getField(e.packet,"a");
            System.out.println(Arrays.toString(s));
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