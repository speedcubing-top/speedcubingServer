package top.speedcubing.server.bukkitcmd.staff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class announce implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            return true;
        }

        StringBuilder builder = new StringBuilder();
        for (String str : strings) {
            builder.append(" ").append(str);
        }

        String result = builder.substring(1);
        result = ChatColor.translateAlternateColorCodes('&', result.substring(1));

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(result);
        }

        return true;
    }
}
