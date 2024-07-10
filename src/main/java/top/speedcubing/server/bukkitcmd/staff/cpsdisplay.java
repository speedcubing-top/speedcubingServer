package top.speedcubing.server.bukkitcmd.staff;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import top.speedcubing.lib.bukkit.entity.Hologram;
import top.speedcubing.server.player.User;

public class cpsdisplay implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length != 1) {
            commandSender.sendMessage("/cpsdisplay <player>");
            return true;
        }
        String target = strings[0];
        Player player = Bukkit.getPlayerExact(target);
        if (player == null) {
            commandSender.sendMessage("DNE");
            return true;
        }
        User user = User.getUser(player);
        if (user.cpsHologram != null) {
            user.removeCPSHologram();
            return true;
        }
        Hologram h = new Hologram("", true, true).world("world");
        h.follow(Bukkit.getPlayerExact(target), new Vector(0, 2.5, 0));
        h.spawn();
        user.cpsHologram = h;
        return true;
    }
}
