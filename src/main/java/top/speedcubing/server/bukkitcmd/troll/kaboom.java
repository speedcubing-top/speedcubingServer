package top.speedcubing.server.bukkitcmd.troll;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;
import top.speedcubing.lib.minecraft.MinecraftConsole;
import top.speedcubing.server.player.User;

public class kaboom implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            String senderName = commandSender.getName();
            displaySubtitleToAllPlayers("§l§cKABOOM!", senderName, commandSender);
            applyVelocityToAllPlayers(new Vector(0, 4, 0), commandSender);
            issueLightningStrikesToAllPlayers(commandSender);
            commandSender.sendMessage("§l§cKABOOM!");
            MinecraftConsole.printlnColor("§7[§4STAFF§7] " + senderName + " caused a nuclear reaction!");
        } else {
            commandSender.sendMessage("/kaboom");
            commandSender.sendMessage("§4WARNING! §cTHIS COMMAND IS NOT FOR PRODUCTION AND MIGHT CAUSE FALSE BANS ON KNOCKBACKFFA");
        }
        return true;
    }

    private void applyVelocityToAllPlayers(Vector velocity, CommandSender commandSender) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setVelocity(velocity);
            commandSender.sendMessage("§aLaunched " + User.getUser(player).realName + "!");
        });
    }

    private void issueLightningStrikesToAllPlayers(CommandSender commandSender) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.getWorld().strikeLightning(player.getLocation());
            player.setHealth(20);
        });
    }

    private void displaySubtitleToAllPlayers(String subtitle, String senderName, CommandSender commandSender) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendTitle(subtitle, "§cBy " + User.getUser(commandSender).getPrefixName(true));
        });
    }

    private String velocityToString(Vector velocity) {
        return "(" + velocity.getX() + "," + velocity.getY() + "," + velocity.getZ() + ")";
    }
}
