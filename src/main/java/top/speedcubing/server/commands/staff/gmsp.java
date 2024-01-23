package top.speedcubing.server.commands.staff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.speedcubing.lib.utils.Console;

public class gmsp implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            if (commandSender instanceof Player) {
                ((Player) commandSender).setGameMode(GameMode.SPECTATOR);
                commandSender.sendMessage(ChatColor.WHITE + "Your game mode has been updated");
                return true;
            } else {
                Console.printlnColor("§cUsage: /gmsp <player>");
                return true;
            }
        } else if (strings.length == 1) {
            Player target = Bukkit.getPlayer(strings[0]);
            if (target != null) {
                target.setGameMode(GameMode.SPECTATOR);
                target.sendMessage(ChatColor.WHITE + "Your game mode has been updated");
                commandSender.sendMessage(ChatColor.WHITE + "Updated " + target.getName() + " game mode");
                return true;
            } else {
                commandSender.sendMessage("§cPlayer not found!");
                return true;
            }
        } else {
            commandSender.sendMessage("§cUsage: /gmsp <player>");
            return true;
        }
    }
}
