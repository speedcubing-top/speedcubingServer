package top.speedcubing.server.listeners;

import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.util.Java15Compat;
import top.speedcubing.server.commandoverrider.OverrideCommandManager;
import top.speedcubing.server.lang.GlobalString;
import top.speedcubing.server.player.User;

public class CommandPermissions implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        CommandElement element = new CommandElement(e.getMessage(), false);
        User user = User.getUser(player);
        Set<String> perms = user.permissions;
        if (!(perms.contains("cmd." + element.command) || perms.contains("cmd.*"))) {
            user.sendLangMessage(perms.contains("view." + element.command) || perms.contains("view.*") ?
                    GlobalString.NoPermCommand : GlobalString.UnknownCommand);
            e.setCancelled(true);
        }
        if (!e.isCancelled()) {
            e.setCancelled(OverrideCommandManager.dispatchOverride(player, element.command, element.strings));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void ServerCommandEvent(ServerCommandEvent e) {
        CommandElement element = new CommandElement(e.getCommand(), true);
        e.setCancelled(OverrideCommandManager.dispatchOverride(e.getSender(), element.command, element.strings));
    }

    static class CommandElement {
        public final String command;
        public final String[] strings;

        public CommandElement(String message, boolean console) {
            String[] args = (console ? message : message.substring(1)).split(" ");
            this.command = args[0].toLowerCase();
            this.strings = Java15Compat.Arrays_copyOfRange(args, 1, args.length);
        }
    }
}

