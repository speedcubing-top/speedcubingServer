package top.speedcubing.server.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.util.Java15Compat;
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
        CommandElement element = new CommandElement(e.getMessage());
        if (op.contains(element.command)) {
            if (!player.isOp()) {
                player.sendMessage(GlobalString.UnknownCommand[User.getUser(player).lang]);
                e.setCancelled(true);
            }
        } else {
            User user = User.getUser(player);
            Set<String> perms = user.permissions;
            if (!(perms.contains("cmd." + element.command) || perms.contains("cmd.*"))) {
                player.sendMessage(perms.contains("view." + element.command) || perms.contains("view.*") ?
                        GlobalString.NoPermCommand[user.lang] : GlobalString.UnknownCommand[user.lang]);
                e.setCancelled(true);
            }
            if (!e.isCancelled()) {
                e.setCancelled(OverrideCommandManager.dispatchOverride(player, element.command, element.strings));
            }
        }
    }

    @EventHandler
    public void ServerCommandEvent(ServerCommandEvent e) {
        CommandElement element = new CommandElement(e.getCommand());
        e.setCancelled(OverrideCommandManager.dispatchOverride(e.getSender(), element.command, element.strings));
    }

    class CommandElement {
        public final String command;
        public final String[] strings;

        public CommandElement(String message) {
            String[] args = message.substring(1).split(" ");
            this.command = args[0].toLowerCase();
            this.strings = Java15Compat.Arrays_copyOfRange(args, 1, args.length);
        }
    }
}

