package speedcubing.server.events.packet;

import net.minecraft.server.v1_8_R3.PacketPlayInTabComplete;
import org.bukkit.entity.Player;

public class PlayInTabCompleteEvent {
    public PacketPlayInTabComplete packet;
    public Player player;
    public boolean isCancelled = false;

    public PlayInTabCompleteEvent(Player player, PacketPlayInTabComplete packet) {
        this.player = player;
        this.packet = packet;
    }

    public void setCancelled(boolean b) {
        isCancelled = b;
    }
}