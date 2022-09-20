package top.speedcubing.server.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import top.speedcubing.server.libs.GlobalString;
import top.speedcubing.server.libs.User;


public class discord implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        User.getUser(commandSender).sendLangMessage(GlobalString.discord);
        return true;
    }
}
