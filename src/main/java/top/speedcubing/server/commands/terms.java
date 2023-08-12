package top.speedcubing.server.commands;

import org.bukkit.command.*;
import top.speedcubing.server.lang.GlobalString;
import top.speedcubing.server.player.User;

public class terms implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        User.getUser(commandSender).sendLangMessage(GlobalString.terms);
        return true;
    }
}