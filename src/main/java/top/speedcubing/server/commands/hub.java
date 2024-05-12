package top.speedcubing.server.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.speedcubing.lib.bukkit.pluginMessage.BungeePluginMessage;
import top.speedcubing.server.events.player.HubEvent;

public class hub implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            Player player = (Player) commandSender;
            if (!((HubEvent) new HubEvent(player).call()).isCancelled()) {
                if (Bukkit.getServerName().equalsIgnoreCase("bedwars") && !player.getWorld().getName().equals("world")) {
                    player.performCommand("leave");
                    return true;
                }
                BungeePluginMessage.switchServer(player, "lobby");
            }
        } else commandSender.sendMessage("/hub, /l, /lobby");
        return true;
    }
}
