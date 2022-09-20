package top.speedcubing.server.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.speedcubing.lib.bukkit.BungeePluginMessage;
import top.speedcubing.server.events.player.HubEvent;

public class hub implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            Player player = (Player) commandSender;
            if (!((HubEvent) new HubEvent(player).call()).isCancelled)
                BungeePluginMessage.switchServer(player, "lobby");
        } else commandSender.sendMessage("/hub, /l, /lobby");
        return true;
    }
}
