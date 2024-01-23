package top.speedcubing.server.commands.staff;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class heal implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (strings.length == 0) {
                player.setHealth(player.getMaxHealth());
            } else if (strings.length == 1) {
                Player target = Bukkit.getPlayer(strings[0]);
                if (target != null) {
                    target.setHealth(target.getMaxHealth());
                } else {
                    player.sendMessage("player not found!");
                }
            } else {
                player.sendMessage("/heal");
            }
        }
        return true;
    }
}
