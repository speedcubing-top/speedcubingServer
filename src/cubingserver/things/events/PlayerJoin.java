package cubingserver.things.events;

import com.google.common.collect.Sets;
import cubing.bukkit.PacketWrapper;
import cubing.bukkit.PlayerUtils;
import cubingserver.Commands.end;
import cubingserver.ExploitFixer.ForceOp;
import cubingserver.StringList.GlobalString;
import cubingserver.libs.PlayerData;
import cubingserver.libs.Rank;
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
            String[] datas = speedcubingServer.connection.selectStrings("playersdata", "priority,nickpriority,permissions", "uuid='" + uuid + "'");
            int old = Integer.parseInt(datas[0]);
            Set<String> perms = Rank.values()[Rank.rankToIndex(Integer.parseInt(datas[0]))].getPerms();
            if (datas[2] != null)
                perms.addAll(Sets.newHashSet(datas[2].split("\\|")));
            speedcubingServer.permissions.put(uuid, new HashSet<>());
            speedcubingServer.permissions.get(uuid).addAll(perms);
            PlayerData.AbilityCache.put(uuid, Integer.parseInt(datas[0]));
            String realname = "";
            if (Bukkit.getPort() % 2 == 1) {
                String res = speedcubingServer.connection.selectString("playersdata", "name", "uuid='" + uuid + "'");
                if (!res.equalsIgnoreCase(name)) {
                    datas[0] = datas[1];
                    realname = res;
                }
            }
            PlayerData.RankCache.put(uuid, Integer.parseInt(datas[0]));

            int lang = PlayerData.getLang(uuid);
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            PlayerUtils.sendTabHeaderFooter(connection, GlobalString.LobbyTabList[0][lang], GlobalString.LobbyTabList[1][lang].replace("%int%", Integer.toString(speedcubingServer.AllPlayers)));

            for (PacketPlayOutScoreboardTeam p : RemovePackets.values()) {
                connection.sendPacket(p);
            }
            for (PacketPlayOutScoreboardTeam p : JoinPackets.values()) {
                connection.sendPacket(p);
            }
            String extracted = datas[0] + Rank.playerNameExtract(name);
            String[] format = Rank.format(uuid);
            PacketPlayOutScoreboardTeam leavePacket = PacketWrapper.packetPlayOutScreboardTeam(extracted, null, null, null, null, 1);
            PacketPlayOutScoreboardTeam joinPacket = PacketWrapper.packetPlayOutScreboardTeam(extracted, format[0] + format[1], "", ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS.e, Collections.singletonList(name), 0);

            for (Player p : Bukkit.getOnlinePlayers()) {
                PlayerConnection c = ((CraftPlayer) p).getHandle().playerConnection;
                c.sendPacket(leavePacket);
                c.sendPacket(joinPacket);
            }
            if (!realname.equals("")) {
                String extracted2 = old + Rank.playerNameExtract(realname);
                String[] newformat = Rank.values()[Rank.rankToIndex(old)].getFormat();
                connection.sendPacket(PacketWrapper.packetPlayOutScreboardTeam(extracted2, newformat[0] + newformat[1], "", ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS.e, Collections.singletonList(realname), 0));
            }
            RemovePackets.put(uuid, leavePacket);
            JoinPackets.put(uuid, joinPacket);
            speedcubingServer.lastmsg.put(uuid, "");
            speedcubingServer.spam.put(uuid, 0L);
        }
    }
}
