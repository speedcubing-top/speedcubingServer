package speedcubing.server.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import speedcubing.server.libs.GlobalString;
import speedcubing.server.libs.User;

import java.util.ArrayList;
import java.util.List;
import java.util.*;


public class discord implements CommandExecutor, TabCompleter {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage(GlobalString.discord[User.getUser(commandSender).lang]);
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }
}
