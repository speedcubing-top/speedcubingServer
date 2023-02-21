package top.speedcubing.server.listeners;

import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import top.speedcubing.lib.bukkit.packetwrapper.OutScoreboardTeam;
import top.speedcubing.server.config;
import top.speedcubing.server.libs.PreLoginData;
import top.speedcubing.server.libs.User;
import top.speedcubing.server.speedcubingServer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Login implements Listener {
    @EventHandler
    public void PlayerLoginEvent(PlayerLoginEvent e) {
        Player player = e.getPlayer();
        String[] datas = speedcubingServer.connection.select("priority,nickpriority,perms,lang,id,name,opped,chatfilt").from("playersdata").where("uuid='" + player.getUniqueId() + "'").getStringArray();
        PreLoginData bungeeData = speedcubingServer.preLoginStorage.get(Integer.parseInt(datas[4]));
        if (bungeeData == null) {
            e.setKickMessage("§cServer Restarting... Please wait for a few seconds.");
            e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            return;
        }

        String displayName = player.getName();
        String realRank = speedcubingServer.getRank(datas[0], player.getUniqueId().toString());
        String displayRank = realRank;
        String nickedRealName = "";
        if (speedcubingServer.isBungeeOnlineMode) {
            if (!datas[5].equals(displayName)) {
                displayRank = datas[1];
                nickedRealName = datas[5];
            }
        }
        temp = new String[]{displayName, nickedRealName, realRank, datas[6]};
        Set<String> p = Sets.newHashSet(datas[2].split("\\|"));
        p.remove("");
        p.addAll(config.rankPermissions.get(realRank));
        Set<String> groups = new HashSet<>();
        for (String s : p) {
            if (User.group.matcher(s).matches() && config.grouppermissions.containsKey(s.substring(6)))
                groups.add(s.substring(6));
        }
        groups.forEach(a -> p.addAll(config.grouppermissions.get(a)));
        new User(player, displayRank, p, Integer.parseInt(datas[3]), Integer.parseInt(datas[4]), datas[6].equals("1"), bungeeData, datas[7].equals("1"), datas[5]);
    }

    String[] temp;

    @EventHandler(priority = EventPriority.LOWEST)
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        e.setJoinMessage("");
        Player player = e.getPlayer();
        player.setOp(temp[3].equals("1"));
        User user = User.getUser(player);

        //formatting
        for (User u : User.getUsers())
            user.sendPacket(u.leavePacket, u.joinPacket);
        String extracted = speedcubingServer.getCode(user.rank) + speedcubingServer.playerNameExtract(temp[0]);
        user.leavePacket = new OutScoreboardTeam().a(extracted).h(1).packet;
        user.joinPacket = new OutScoreboardTeam().a(extracted).c(speedcubingServer.getFormat(user.rank)[0]).g(Collections.singletonList(temp[0])).h(0).packet;
        for (User u : User.getUsers())
            u.sendPacket(user.leavePacket, user.joinPacket);

        //vanish
        if (user.vanished)
            for (Player p : Bukkit.getOnlinePlayers())
                p.hidePlayer(player);
        for (User u : User.getUsers())
            if (u.vanished) player.hidePlayer(u.player);

        //nick
        if (!temp[1].equals(""))
            user.sendPacket(new OutScoreboardTeam().a(speedcubingServer.getCode(temp[2]) + speedcubingServer.playerNameExtract(temp[1])).c(speedcubingServer.getFormat(temp[2])[0]).g(Collections.singletonList(temp[1])).h(0).packet);
    }
}
