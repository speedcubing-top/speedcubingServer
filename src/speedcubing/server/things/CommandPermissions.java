package speedcubing.server.things;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import speedcubing.server.StringList.GlobalString;
import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CommandPermissions implements Listener {
    List<String> op = Arrays.asList("clear", "clone", "fill", "effect", "gamemode", "give", "kill", "pardon", "say", "setblock", "tell", "tellraw", "title", "tp");

    @EventHandler
    public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        String message = e.getMessage().toLowerCase();
        if (message.startsWith("/pl ") || message.equals("/pl") || message.startsWith("/plugins ") || message.equals("/plugins")) {
            e.setCancelled(true);
            player.sendMessage("Plugins (3): §aspeedcubing§f, §aspeedcubingLib§f, §aspeedcubingServer");
        } else Unknown(player, e, message);
    }

    private void Unknown(Player player, PlayerCommandPreprocessEvent e, String message) {
        String command;
        if (message.contains(" ")) {
            String[] b = message.substring(1).split(" ");
            command = b.length == 0 ? "" : b[0];
        } else command = message.substring(1);
        command = command.toLowerCase();
        if (op.contains(command)) {
            if (!player.isOp()) {
                player.sendMessage(GlobalString.UnknownCommand[User.getLang(player.getUniqueId())]);
                e.setCancelled(true);
            }
        } else {
            Set<String> perms = speedcubingServer.permissions.get(player.getUniqueId());
            if (!(perms.contains("cmd." + command) || perms.contains("cmd.*"))) {
                if (perms.contains("view." + command) || perms.contains("view.*"))
                    player.sendMessage(GlobalString.NoPermCommand[User.getLang(player.getUniqueId())]);
                else
                    player.sendMessage(GlobalString.UnknownCommand[User.getLang(player.getUniqueId())]);
                e.setCancelled(true);
            }
        }
    }
}

