package top.speedcubing.server.commands.staff;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import top.speedcubing.lib.utils.Console;
import top.speedcubing.server.player.User;

import java.util.Collection;

public class kaboom implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            String senderName = commandSender.getName();
            displaySubtitleToAllPlayers("§l§cKABOOM!", senderName ,commandSender);
            applyVelocityToAllPlayers(new Vector(0, 4, 0), commandSender);
            issueLightningStrikesToAllPlayers(3, commandSender);
            commandSender.sendMessage("§l§cKABOOM!");
            Console.printlnColor("§7[§4STAFF§7] " + senderName + " caused a nuclear reaction!");
        } else {
            commandSender.sendMessage("/kaboom");
        }
        return true;
    }

    private void applyVelocityToAllPlayers(Vector velocity, CommandSender commandSender) {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            player.setVelocity(velocity);
          commandSender.sendMessage("§aLaunched " + User.getUser(player).realName + "!");
        }
        //commandSender.sendMessage("§bApplied velocity to all online players! " + velocityToString(velocity));
    }

    private void issueLightningStrikesToAllPlayers(int count, CommandSender commandSender) {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            for (int i = 0; i < count; i++) {
                player.getWorld().strikeLightning(player.getLocation());
            }
            //commandSender.sendMessage("§bIssued " + count + " lightning strikes to '" + User.getUser(player).realName + "'!");
        }
        //commandSender.sendMessage("§bIssued " + count + " lightning strikes to all online players!");
    }

    private void displaySubtitleToAllPlayers(String subtitle, String senderName, CommandSender commandSender) {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            player.sendTitle(subtitle,"§cBy "+ senderName); // Clear previous title, display subtitle for 2 seconds, fade in for 4 seconds, fade out for 1 second
            //commandSender.sendMessage("§bDisplayed subtitle to '" + User.getUser(player).realName + "'!");
        }
        //commandSender.sendMessage("§bDisplayed subtitle to all online players!");
    }

    private String velocityToString(Vector velocity) {
        return "(" + velocity.getX() + "," + velocity.getY() + "," + velocity.getZ() + ")";
    }
}
