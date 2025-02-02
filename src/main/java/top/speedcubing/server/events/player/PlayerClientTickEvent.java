package top.speedcubing.server.events.player;

import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;

public class PlayerClientTickEvent extends PlayerEvent {
    private final PacketPlayInFlying flyPacket;

    public PlayerClientTickEvent(Player player, PacketPlayInFlying flyPacket) {
        super(player);
        this.flyPacket = flyPacket;
    }

    public PacketPlayInFlying getFlyPacket() {
        return flyPacket;
    }
}
