package speedcubing.server.listeners;

import com.google.common.collect.Sets;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import speedcubing.server.Commands.end;
import speedcubing.server.ExploitFixer.ForceOp;
import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;

import java.util.Set;
import java.util.UUID;

public class PlayerLogin implements Listener {
    @EventHandler
    public void PlayerLoginEvent(PlayerLoginEvent e) {
        Player player = e.getPlayer();
        if (end.restarting) {
            e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            return;
        } else if (player.isOp() && !ForceOp.AllowOP(player.getUniqueId())) {
            e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            return;
        }
        UUID uuid = player.getUniqueId();
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
        PlayerJoin.temp = new String[]{name, realname, old};
        new User(uuid, datas[0], perms);
    }
}
