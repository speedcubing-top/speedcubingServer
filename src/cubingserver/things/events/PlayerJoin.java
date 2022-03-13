package cubingserver.things.events;

import com.google.common.collect.Sets;
import cubing.bukkit.PacketWrapper;
import cubing.bukkit.PlayerUtils;
import cubingserver.Commands.end;
import cubingserver.ExploitFixer.ForceOp;
import cubingserver.StringList.GlobalString;
import cubingserver.libs.User;
import cubingserver.speedcubingServer;
import cubingserver.things.Cps;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.ScoreboardTeamBase;
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
            if (Bukkit.getPort() % 2 == 1) {
                String res = speedcubingServer.connection.selectString("playersdata", "name", "uuid='" + uuid + "'");
                if (!res.equalsIgnoreCase(name)) {
                    datas[0] = datas[1];
                    realname = res;
                }
            }
            User.RankCache.put(uuid, datas[0]);

            int lang = User.getLang(uuid);
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            PlayerUtils.sendTabHeaderFooter(connection, GlobalString.LobbyTabList[0][lang], GlobalString.LobbyTabList[1][lang].replace("%int%", Integer.toString(speedcubingServer.AllPlayers)));

            for (PacketPlayOutScoreboardTeam p : RemovePackets.values()) {
                connection.sendPacket(p);
            }
            for (PacketPlayOutScoreboardTeam p : JoinPackets.values()) {
                connection.sendPacket(p);
            }
            String extracted = User.getCode(datas[0]) + User.playerNameExtract(name);
            String[] format = User.getFormat(datas[0]);
            PacketPlayOutScoreboardTeam leavePacket = PacketWrapper.packetPlayOutScreboardTeam(extracted, null, null, null, null, 1);
            PacketPlayOutScoreboardTeam joinPacket = PacketWrapper.packetPlayOutScreboardTeam(extracted, format[0] + format[1], "", ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS.e, Collections.singletonList(name), 0);

            for (Player p : Bukkit.getOnlinePlayers()) {
                PlayerConnection c = ((CraftPlayer) p).getHandle().playerConnection;
                c.sendPacket(leavePacket);
                c.sendPacket(joinPacket);
            }
            if (!realname.equals("")) {
                String[] newformat = User.getFormat(old);
                connection.sendPacket(PacketWrapper.packetPlayOutScreboardTeam(User.getCode(old) + User.playerNameExtract(realname), newformat[0] + newformat[1], "", ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS.e, Collections.singletonList(realname), 0));
            }
            RemovePackets.put(uuid, leavePacket);
            JoinPackets.put(uuid, joinPacket);
            speedcubingServer.lastmsg.put(uuid, "");
            speedcubingServer.spam.put(uuid, 0L);
        }
    }
}
