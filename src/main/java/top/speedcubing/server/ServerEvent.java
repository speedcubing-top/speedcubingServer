package top.speedcubing.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayInCustomPayload;
import net.minecraft.server.v1_8_R3.PacketPlayInKeepAlive;
import net.minecraft.server.v1_8_R3.PacketPlayInTabComplete;
import net.minecraft.server.v1_8_R3.PacketPlayOutStatistic;
import net.minecraft.server.v1_8_R3.PacketPlayOutTabComplete;
import org.bukkit.entity.Player;
import top.speedcubing.lib.api.events.SignUpdateEvent;
import top.speedcubing.lib.bukkit.events.packet.PlayInEvent;
import top.speedcubing.lib.bukkit.events.packet.PlayOutEvent;
import top.speedcubing.lib.eventbus.CubingEventHandler;
import top.speedcubing.lib.utils.ReflectionUtils;
import top.speedcubing.server.commands.nick.nick;
import top.speedcubing.server.events.InputEvent;
import top.speedcubing.server.login.PreLoginData;
import top.speedcubing.server.player.User;

public class ServerEvent {

    public static int get(String s) {
        String[] args = s.split(" ");
        if (args.length == 1)
            return !s.endsWith(" ") ? -1 : (s.endsWith("  ") ? 2 : 0);
        return s.endsWith(" ") ? args.length - 1 : args.length - 2;
    }

    @CubingEventHandler
    public void PlayInEvent(PlayInEvent e) {
        if (e.getPacket() instanceof PacketPlayInTabComplete) {
            String s = ((PacketPlayInTabComplete) e.getPacket()).a();
            String command = s.split(" ")[0].substring(1).toLowerCase();
            User user = User.getUser(e.getPlayer());
            user.lastTabbed = command;
            if (user.hasPermission("cmd." + command) || user.hasPermission("cmd.*"))
                return;
            e.setCancelled(true);
        } else if (e.getPacket() instanceof PacketPlayInKeepAlive) {
//            if(e.player.getName().equals("speedcubing")) {
//                System.out.println("cancel");
//                e.isCancelled = true;
//            }
        } else if (e.getPacket() instanceof PacketPlayInCustomPayload) {
            PacketPlayInCustomPayload packet = (PacketPlayInCustomPayload) e.getPacket();
            if (packet.a().equals("labymod3:main")) {
                PacketDataSerializer serializer = packet.b();

                String type = serializer.c(64);

                if (!type.equals("INFO")) {
                    return;
                }

                String json = serializer.c(10000);

                JsonObject parser = JsonParser.parseString(json).getAsJsonObject();
                System.out.println(parser);
            }
        }
    }

    @CubingEventHandler
    public void PlayOutEvent(PlayOutEvent e) {
        if (e.getPacket() instanceof PacketPlayOutStatistic) {
            Map<?, Integer> stats = (Map<?, Integer>) ReflectionUtils.getField(e.getPacket(), "a");
            stats.replaceAll((k, v) -> 0);
        } else if (e.getPacket() instanceof PacketPlayOutTabComplete) {
            String[] s = (String[]) ReflectionUtils.getField(e.getPacket(), "a");
            System.out.println(Arrays.toString(s));
        }
    }

    @CubingEventHandler
    public void InputEvent(InputEvent e) {
        try {
            switch (e.subHeader) {
                case "bungee":
                    int i = e.receive.readInt();
                    speedcubingServer.preLoginStorage.put(i, new PreLoginData(e.receive.readUTF(), e.receive.readInt(), e.receive.readUTF(), e.receive.readUTF(), e.receive.readBoolean()));
                    break;
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @CubingEventHandler
    public void SignUpdateEvent(SignUpdateEvent e) {
        Player player = e.getPlayer();
        List<String> lines = e.getLines();
        System.out.println("a");
        if (nick.settingNick.containsKey(player.getUniqueId())) {
            String name = lines.get(0);
            System.out.println("b");
            player.performCommand("nick " + name + " " + nick.nickRank.get(player.getUniqueId()) + " true");
        } else {
            System.out.println("c");
        }
    }
}