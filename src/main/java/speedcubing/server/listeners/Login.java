package speedcubing.server.listeners;

import com.google.common.collect.Sets;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import speedcubing.lib.bukkit.packetwrapper.OutScoreboardTeam;
import speedcubing.lib.utils.SQL.SQLUtils;
import speedcubing.server.Commands.end;
import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Login implements Listener {
    @EventHandler
    public void PlayerLoginEvent(PlayerLoginEvent e) {
        if (end.restarting) {
            e.setKickMessage("§cServer Restarting... Please wait for a few seconds.");
            e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            return;
        }
        Player player = e.getPlayer();
        String[] datas = SQLUtils.getStringArray(speedcubingServer.connection.select("playersdata", "priority,nickpriority,perms,lang,id,name,allow_op", "uuid='" + player.getUniqueId() + "'"));
        if (player.isOp() && datas[6].equals("0")) {
            e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            return;
        }
        String name = player.getName();
        String old = datas[0];
        String realname = "";
        if (speedcubingServer.isBungeeOnlineMode) {
            if (!datas[5].equalsIgnoreCase(name)) {
                datas[0] = datas[1];
                realname = datas[5];
            }
        }
        temp = new String[]{name, realname, old};
        Set<String> a = Sets.newHashSet(datas[2].split("\\|"));
        a.remove("");
        new User(player, datas[0], new HashSet<>(speedcubingServer.rankPermissions.get(old)) {{
            addAll(a);
        }}, Integer.parseInt(datas[3]), Integer.parseInt(datas[4]), datas[6].equals("1"));
    }

    String[] temp;

    @EventHandler(priority = EventPriority.LOWEST)
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        e.setJoinMessage("");
        Player player = e.getPlayer();
        User user = User.getUser(player);
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        for (User u : User.usersByID.values()) {
            connection.sendPacket(u.leavePacket);
            connection.sendPacket(u.joinPacket);
        }
        String extracted = speedcubingServer.getCode(user.rank) + speedcubingServer.playerNameExtract(temp[0]);
        user.leavePacket = new OutScoreboardTeam().a(extracted).h(1).packet;
        user.joinPacket = new OutScoreboardTeam().a(extracted).c(speedcubingServer.getFormat(user.rank)[0]).g(Collections.singletonList(temp[0])).h(0).packet;
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerConnection c = ((CraftPlayer) p).getHandle().playerConnection;
            c.sendPacket(user.leavePacket);
            c.sendPacket(user.joinPacket);
        }
        if (!temp[1].equals(""))
            connection.sendPacket(new OutScoreboardTeam().a(speedcubingServer.getCode(temp[2]) + speedcubingServer.playerNameExtract(temp[1])).c(speedcubingServer.getFormat(temp[2])[0]).g(Collections.singletonList(temp[1])).h(0).packet);
    }
}
