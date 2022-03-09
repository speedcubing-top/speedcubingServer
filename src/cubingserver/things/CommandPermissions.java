package cubingserver.things;

import cubingserver.StringList.GlobalString;
import cubingserver.libs.PlayerData;
import cubingserver.libs.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;

public class CommandPermissions implements Listener {
    @EventHandler
    public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        String message = e.getMessage().toLowerCase();
        if (message.startsWith("/end ") || message.equals("/end"))
            Unknown(player, 0, Rank.OWNER.getId(), Rank.OWNER.getId(), e);
        else if (message.startsWith("/cps ") || message.equals("/cps"))
            Unknown(player, 0, Rank.ADMIN.getId(), Rank.ADMIN.getId(), e);
        else if (message.startsWith("/heal ") || message.equals("/heal"))
            Unknown(player, 0, Rank.OWNER.getId(), Rank.OWNER.getId(), e);
        else if (message.startsWith("/map ") || message.equals("/map"))
            Unknown(player, 0, Rank.OWNER.getId(), Rank.OWNER.getId(), e);
        else if (message.startsWith("/proxycommand ") || message.equals("/proxycommand"))
            Unknown(player, 0, Rank.ADMIN.getId(), Rank.ADMIN.getId(), e);
        else if (message.startsWith("/reset ") || message.equals("/reset"))
            Unknown(player, 0, Rank.YT.getId(), 10000, e);
        else if (message.startsWith("/fly ") || message.equals("/fly"))
            Unknown(player, 0, Rank.YT.getId(), 10000, e);
        else if (message.startsWith("/nick ") || message.equals("/nick"))
            if (Bukkit.getPort() % 2 == 0)
                Unknown(player, 0, 0, 0, e);
            else
                Unknown(player, 0, Rank.OWNER.getId(), 10000, e);
        else if (message.startsWith("/unnick ") || message.equals("/unnick"))
            if (Bukkit.getPort() % 2 == 0)
                Unknown(player, 0, 0, 0, e);
            else
                Unknown(player, 0, Rank.OWNER.getId(), 10000, e);
        else if (message.startsWith("/stats ") || message.equals("/stats"))
            Unknown(player, 0, 10000, 10000, e);
        else if (message.startsWith("/skin ") || message.equals("/skin"))
            Unknown(player, 0, 10000, 10000, e);
        else if (message.startsWith("/lang ") || message.equals("/lang"))
            Unknown(player, 0, 10000, 10000, e);
        else if (message.startsWith("/hub ") || message.equals("/hub"))
            Unknown(player, 0, 10000, 10000, e);
        else if (message.startsWith("/l ") || message.equals("/l"))
            Unknown(player, 0, 10000, 10000, e);
        else if (message.startsWith("/lobby ") || message.equals("/lobby"))
            Unknown(player, 0, 10000, 10000, e);
        else if (message.startsWith("/leave ") || message.equals("/leave"))
            Unknown(player, 0, 10000, 10000, e);
        else if (message.startsWith("/register ") || message.equals("/register")) {
            if (Bukkit.getPort() % 2 == 1)
                Unknown(player, 0, 0, 0, e);
            else Unknown(player, 0, 10000, 10000, e);
        } else if (message.startsWith("/login ") || message.equals("/login")) {
            if (Bukkit.getPort() % 2 == 1)
                Unknown(player, 0, 0, 0, e);
            else Unknown(player, 0, 10000, 10000, e);
        } else if (message.startsWith("/premium ") || message.equals("/premium")) {
            if (Bukkit.getPort() % 2 == 1)
                Unknown(player, 0, 0, 0, e);
            else Unknown(player, 0, 10000, 10000, e);
        } else if (message.startsWith("/resetpassword ") || message.equals("/resetpassword")) {
            if (Bukkit.getPort() % 2 == 1)
                Unknown(player, 0, 0, 0, e);
            else Unknown(player, 0, 10000, 10000, e);
        } else if (message.startsWith("/pl ") || message.equals("/pl") || message.startsWith("/plugins ") || message.equals("/plugins")) {
            e.setCancelled(true);
            player.sendMessage("Plugins (3): §aspeedcubing§f, §aspeedcubingLib§f, §aspeedcubingServer");
        } else if (PlayerData.getAbilityRank(player.getUniqueId()) > Rank.OWNER.getId()) {
            if (player.isOp()) {
                if (!IsEnabledforOP(message))
                    Unknown(player, 0, 0, 0, e);
            } else Unknown(player, 0, 0, 0, e);
        }
    }

    private boolean IsEnabledforOP(String message) {
        for (String str : Arrays.asList("clear", "clone", "fill", "effect", "gamemode", "give", "kill", "pardon", "say", "setblock", "tell", "tellraw", "title", "tp")) {
            if (message.startsWith("/" + str + " ") || message.equals("/" + str))
                return true;
        }
        return false;
    }

    private void Unknown(Player player, int LowRange, int HighRange, int visibleLimit, PlayerCommandPreprocessEvent e) {
        int r = PlayerData.getAbilityRank(player.getUniqueId());
        if (r > HighRange || r < LowRange) {
            if (r <= visibleLimit)
                player.sendMessage(GlobalString.NoPermCommand[PlayerData.getLang(player.getUniqueId())]);
            else
                player.sendMessage(GlobalString.UnknownCommand[PlayerData.getLang(player.getUniqueId())]);
            e.setCancelled(true);
        }
    }
}

