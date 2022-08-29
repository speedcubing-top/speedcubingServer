package speedcubing.server.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class announce implements CommandExecutor, TabCompleter {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length != 0) {
            StringBuilder result = new StringBuilder();
            for (String str : strings) {
                result.append(" ").append(str);
            }
            result = new StringBuilder(ChatColor.translateAlternateColorCodes('&', result.substring(1)));
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(result.toString());
            }
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }
}
