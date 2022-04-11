package cubingserver.things.events;

import com.google.common.collect.Sets;
import cubing.lib.bukkit.packetwrapper.OutPlayerListHeaderFooter;
import cubing.lib.bukkit.packetwrapper.OutScoreboardTeam;
import cubingserver.Commands.end;
import cubingserver.ExploitFixer.ForceOp;
import cubingserver.StringList.GlobalString;
import cubingserver.libs.User;
import cubingserver.speedcubingServer;
import cubingserver.things.Cps;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;

public class PlayerJoin implements Listener {
    public static Map<UUID, PacketPlayOutScoreboardTeam> RemovePackets = new HashMap<>();
    public static Map<UUID, PacketPlayOutScoreboardTeam> JoinPackets = new HashMap<>();
    public static Set<UUID> Canspam = new HashSet<>();

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
            if (speedcubingServer.connection.selectBoolean("playersdata", "spam_whitelist", "uuid='" + uuid + "'"))
                Canspam.add(uuid);
            Cps.Counter.put(uuid, new Integer[]{0, 0});


            String name = player.getName();
            String[] datas = speedcubingServer.connection.selectStrings("playersdata", "priority,nickpriority,perms,disabledperms", "uuid='" + uuid + "'");
            String old = datas[0];
            Set<String> perms = Sets.newHashSet(User.getPerms(old));
            if (datas[2] != null)
                perms.addAll(Sets.newHashSet(datas[2].split("\\|")));
            if (datas[3] != null)
                perms.removeAll(Sets.newHashSet(datas[3].split("\\|")));
            speedcubingServer.permissions.put(uuid, perms);
            String realname = "";
            if (speedcubingServer.isBungeeOnlineMode) {
                String res = speedcubingServer.connection.selectString("playersdata", "name", "uuid='" + uuid + "'");
                if (!res.equalsIgnoreCase(name)) {
                    datas[0] = datas[1];
                    realname = res;
                }
            }
            User.RankCache.put(uuid, datas[0]);

            int lang = User.getLang(uuid);
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            connection.sendPacket(OutPlayerListHeaderFooter.a(GlobalString.LobbyTabList[0][lang], GlobalString.LobbyTabList[1][lang].replace("%int%", Integer.toString(speedcubingServer.AllPlayers))));
            RemovePackets.values().forEach(connection::sendPacket);
            JoinPackets.values().forEach(connection::sendPacket);
            String extracted = User.getCode(datas[0]) + User.playerNameExtract(name);
            PacketPlayOutScoreboardTeam leavePacket = OutScoreboardTeam.a(extracted, 1);
            PacketPlayOutScoreboardTeam joinPacket = OutScoreboardTeam.a(extracted, User.getFormat(datas[0])[0], Collections.singletonList(name), 0);
            for (Player p : Bukkit.getOnlinePlayers()) {
                PlayerConnection c = ((CraftPlayer) p).getHandle().playerConnection;
                c.sendPacket(leavePacket);
                c.sendPacket(joinPacket);
            }
            if (!realname.equals(""))
                connection.sendPacket(OutScoreboardTeam.a(User.getCode(old) + User.playerNameExtract(realname), User.getFormat(old)[0], Collections.singletonList(realname), 0));
            RemovePackets.put(uuid, leavePacket);
            JoinPackets.put(uuid, joinPacket);
        }
    }
}
