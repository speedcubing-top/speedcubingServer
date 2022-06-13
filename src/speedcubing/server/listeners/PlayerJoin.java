package speedcubing.server.listeners;

import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import speedcubing.lib.bukkit.packetwrapper.OutScoreboardTeam;
import speedcubing.server.PacketListener;
import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerJoin implements Listener {
    public static Map<UUID, PacketPlayOutScoreboardTeam> RemovePackets = new HashMap<>();
    public static Map<UUID, PacketPlayOutScoreboardTeam> JoinPackets = new HashMap<>();
    public static String[] temp;

    @EventHandler(priority = EventPriority.LOWEST)
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        e.setJoinMessage("");
        Player player = e.getPlayer();
        PacketListener.inject(player);
        UUID uuid = player.getUniqueId();
        User user = User.getUser(uuid);
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        RemovePackets.values().forEach(connection::sendPacket);
        JoinPackets.values().forEach(connection::sendPacket);
        String extracted = speedcubingServer.getCode(user.rank) + speedcubingServer.playerNameExtract(temp[0]);
        PacketPlayOutScoreboardTeam leavePacket = OutScoreboardTeam.a(extracted, 1);
        PacketPlayOutScoreboardTeam joinPacket = OutScoreboardTeam.a(extracted, speedcubingServer.getFormat(user.rank)[0], Collections.singletonList(temp[0]), 0);
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerConnection c = ((CraftPlayer) p).getHandle().playerConnection;
            c.sendPacket(leavePacket);
            c.sendPacket(joinPacket);
        }
        if (!temp[1].equals(""))
            connection.sendPacket(OutScoreboardTeam.a(speedcubingServer.getCode(temp[2]) + speedcubingServer.playerNameExtract(temp[1]), speedcubingServer.getFormat(temp[2])[0], Collections.singletonList(temp[1]), 0));
        RemovePackets.put(uuid, leavePacket);
        JoinPackets.put(uuid, joinPacket);
    }
}