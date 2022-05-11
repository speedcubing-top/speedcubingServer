package speedcubing.server.listeners;

import com.google.common.collect.Sets;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import speedcubing.lib.bukkit.packetwrapper.OutPlayerListHeaderFooter;
import speedcubing.lib.bukkit.packetwrapper.OutScoreboardTeam;
import speedcubing.server.Commands.end;
import speedcubing.server.ExploitFixer.ForceOp;
import speedcubing.server.libs.GlobalString;
import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;

import java.util.*;

public class PlayerJoin implements Listener {
    public static Map<UUID, PacketPlayOutScoreboardTeam> RemovePackets = new HashMap<>();
    public static Map<UUID, PacketPlayOutScoreboardTeam> JoinPackets = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        e.setJoinMessage("");
        if (end.restarting)
            e.getPlayer().kickPlayer("");
        else {
            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();
            if (player.isOp()) {
                if (!ForceOp.AllowOP(uuid)) {
                    player.kickPlayer("");
                    return;
                }
            }
            Cps.Counter.put(uuid, new Integer[]{0, 0});
            String name = player.getName();
            String[] datas = speedcubingServer.connection.selectStrings("playersdata", "priority,nickpriority,perms,disabledperms", "uuid='" + uuid + "'");
            String old = datas[0];
            Set<String> perms = Sets.newHashSet(speedcubingServer.rankPermissions.get(old));
            if (datas[2] != null)
                perms.addAll(Sets.newHashSet(datas[2].split("\\|")));
            if (datas[3] != null)
                perms.removeAll(Sets.newHashSet(datas[3].split("\\|")));
            String realname = "";
            if (speedcubingServer.isBungeeOnlineMode) {
                String res = speedcubingServer.connection.selectString("playersdata", "name", "uuid='" + uuid + "'");
                if (!res.equalsIgnoreCase(name)) {
                    datas[0] = datas[1];
                    realname = res;
                }
            }
            User user = new User(uuid,datas[0],perms);
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            connection.sendPacket(OutPlayerListHeaderFooter.a(GlobalString.LobbyTabList[0][user.lang], GlobalString.LobbyTabList[1][user.lang].replace("%int%", Integer.toString(speedcubingServer.AllPlayers))));
            RemovePackets.values().forEach(connection::sendPacket);
            JoinPackets.values().forEach(connection::sendPacket);
            String extracted = speedcubingServer.getCode(datas[0]) + speedcubingServer.playerNameExtract(name);
            PacketPlayOutScoreboardTeam leavePacket = OutScoreboardTeam.a(extracted, 1);
            PacketPlayOutScoreboardTeam joinPacket = OutScoreboardTeam.a(extracted, speedcubingServer.getFormat(datas[0])[0], Collections.singletonList(name), 0);
            for (Player p : Bukkit.getOnlinePlayers()) {
                PlayerConnection c = ((CraftPlayer) p).getHandle().playerConnection;
                c.sendPacket(leavePacket);
                c.sendPacket(joinPacket);
            }
            if (!realname.equals(""))
                connection.sendPacket(OutScoreboardTeam.a(speedcubingServer.getCode(old) + speedcubingServer.playerNameExtract(realname), speedcubingServer.getFormat(old)[0], Collections.singletonList(realname), 0));
            RemovePackets.put(uuid, leavePacket);
            JoinPackets.put(uuid, joinPacket);
        }
    }
}
