package top.speedcubing.server.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import top.speedcubing.server.commandoverrider.OverrideCommandManager;
import top.speedcubing.server.libs.GlobalString;
import top.speedcubing.server.libs.User;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CommandPermissions implements Listener {
    List<String> op = Arrays.asList("clear", "clone", "fill", "effect", "gamemode", "give", "kill", "pardon", "say", "setblock", "tell", "tellraw", "title", "tp");

    @EventHandler
    public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        String message = e.getMessage().toLowerCase().substring(1);
        if (message.startsWith("pl ") || message.equals("pl") || message.startsWith("plugins ") || message.equals("plugins")) {
            e.setCancelled(true);
            player.sendMessage("Plugins (3): §aspeedcubing§f, §aspeedcubingLib§f, §aspeedcubingServer");
        } else {
            String command = getCmd(message);
            if (op.contains(command)) {
                if (!player.isOp()) {
                    player.sendMessage(GlobalString.UnknownCommand[User.getUser(player).lang]);
                    e.setCancelled(true);
                }
            } else {
                User user = User.getUser(player);
                Set<String> perms = user.permissions;
                if (!(perms.contains("cmd." + command) || perms.contains("cmd.*"))) {
                    if (perms.contains("view." + command) || perms.contains("view.*"))
                        player.sendMessage(GlobalString.NoPermCommand[user.lang]);
                    else
                        player.sendMessage(GlobalString.UnknownCommand[user.lang]);
                    e.setCancelled(true);
                }

                if (!e.isCancelled()) {
                    e.setCancelled(OverrideCommandManager.dispatchOverride(player, command, message));
                }
            }
        }
    }

    @EventHandler
    public void ServerCommandEvent(ServerCommandEvent e) {
        String message = e.getCommand();
        e.setCancelled(OverrideCommandManager.dispatchOverride(e.getSender(), getCmd(message), message));
    }

    String getCmd(String message) {
        String command;
        if (message.contains(" ")) {
            String[] b = message.split(" ");
            command = b.length == 0 ? "" : b[0];
        } else command = message;
        command = command.toLowerCase();
        return command.toLowerCase();
    }
}

