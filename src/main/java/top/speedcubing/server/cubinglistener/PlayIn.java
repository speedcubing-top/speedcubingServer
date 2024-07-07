package top.speedcubing.server.cubinglistener;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayInCustomPayload;
import net.minecraft.server.v1_8_R3.PacketPlayInKeepAlive;
import net.minecraft.server.v1_8_R3.PacketPlayInTabComplete;
import net.minecraft.server.v1_8_R3.PacketPlayInUpdateSign;
import org.bukkit.Bukkit;
import top.speedcubing.lib.bukkit.events.packet.PlayInEvent;
import top.speedcubing.lib.eventbus.CubingEventHandler;
import top.speedcubing.server.bukkitcmd.nick.nick;
import top.speedcubing.server.player.User;
import top.speedcubing.server.speedcubingServer;

public class PlayIn {


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
}
