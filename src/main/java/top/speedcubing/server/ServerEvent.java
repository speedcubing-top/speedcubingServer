package top.speedcubing.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayInCustomPayload;
import net.minecraft.server.v1_8_R3.PacketPlayInKeepAlive;
import net.minecraft.server.v1_8_R3.PacketPlayInTabComplete;
import net.minecraft.server.v1_8_R3.PacketPlayInUpdateSign;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunkBulk;
import net.minecraft.server.v1_8_R3.PacketPlayOutStatistic;
import net.minecraft.server.v1_8_R3.PacketPlayOutTabComplete;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import top.speedcubing.lib.bukkit.events.packet.PlayInEvent;
import top.speedcubing.lib.bukkit.events.packet.PlayOutEvent;
import top.speedcubing.lib.eventbus.CubingEventHandler;
import top.speedcubing.lib.utils.ReflectionUtils;
import top.speedcubing.server.commands.nick.nick;
import top.speedcubing.server.commands.troll.sendpacket;
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
        } else if (e.getPacket() instanceof PacketPlayInUpdateSign) {
            PacketPlayInUpdateSign packet = (PacketPlayInUpdateSign) e.getPacket();
            IChatBaseComponent[] components = packet.b();
            String name = components[0].getText();

            if (nick.settingNick.containsKey(e.getPlayer().getUniqueId())) {
                Bukkit.getScheduler().runTask(speedcubingServer.instance, () -> {
                    try {
                        e.getPlayer().performCommand("nick " + name + " " + nick.nickRank.get(e.getPlayer().getUniqueId()) + " true");
                        nick.openNickBook(e.getPlayer(), nick.NickBook.RULE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        e.getPlayer().sendMessage("§cError executing command: " + ex.getMessage());
                    }
                });
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
            System.out.println("tab complete packet " + Arrays.toString(s));
        } else if (e.getPacket() instanceof PacketPlayOutMapChunk) {

            if (!sendpacket.whoWasFucked.contains(e.getPlayer()))
                return;

            try {
                PacketPlayOutMapChunk.ChunkMap data = (PacketPlayOutMapChunk.ChunkMap) ReflectionUtils.getField(e.getPacket(), "c");
                boolean isInit = (boolean) ReflectionUtils.getField(e.getPacket(), "d");
                boolean isOverworld = ((CraftPlayer) e.getPlayer()).getHandle().world.worldProvider.o();
                sendpacket.randomChunkData(isOverworld, isInit, data);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } else if (e.getPacket() instanceof PacketPlayOutMapChunkBulk) {

            if (!sendpacket.whoWasFucked.contains(e.getPlayer()))
                return;

            try {
                PacketPlayOutMapChunk.ChunkMap[] data = (PacketPlayOutMapChunk.ChunkMap[]) ReflectionUtils.getField(e.getPacket(), "c");
                boolean isOverworld = (boolean) ReflectionUtils.getField(e.getPacket(), "d");
                for (PacketPlayOutMapChunk.ChunkMap datum : data) {
                    sendpacket.randomChunkData(isOverworld, true, datum);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

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
}