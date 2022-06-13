package speedcubing.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_8_R3.PacketPlayInChat;
import net.minecraft.server.v1_8_R3.PacketPlayInTabComplete;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import speedcubing.lib.event.LibEventManager;
import speedcubing.server.events.packet.PlayInChatEvent;
import speedcubing.server.events.packet.PlayInTabCompleteEvent;
import speedcubing.server.events.packet.PlayInUseEntityEvent;

import java.util.List;

public class PacketListener {
    public static void inject(Player player) {
        Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;

        if (channel.pipeline().get("PacketPlayOutInUseEntity") != null)
            return;
        channel.pipeline().addAfter("decoder", "PacketPlayInUseEntity", new MessageToMessageDecoder<PacketPlayInUseEntity>() {
            @Override
            protected void decode(ChannelHandlerContext channel, PacketPlayInUseEntity packet, List<Object> arg) {
                PlayInUseEntityEvent event = new PlayInUseEntityEvent(player, packet);
                LibEventManager.callEvent(event);
                if (!event.isCancelled)
                    arg.add(packet);
            }
        }).addAfter("decoder", "PacketPlayInChat", new MessageToMessageDecoder<PacketPlayInChat>() {
            @Override
            protected void decode(ChannelHandlerContext channel, PacketPlayInChat packet, List<Object> arg) {
                PlayInChatEvent event = new PlayInChatEvent(player, packet);
                LibEventManager.callEvent(event);
                if (!event.isCancelled)
                    arg.add(packet);
            }
        }).addAfter("decoder", "PacketPlayInTabComplete", new MessageToMessageDecoder<PacketPlayInTabComplete>() {
            @Override
            protected void decode(ChannelHandlerContext channel, PacketPlayInTabComplete packet, List<Object> arg) {
                PlayInTabCompleteEvent event = new PlayInTabCompleteEvent(player, packet);
                LibEventManager.callEvent(event);
                if (!event.isCancelled)
                    arg.add(packet);
            }
        });
    }
}
