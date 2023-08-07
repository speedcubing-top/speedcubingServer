package top.speedcubing.server.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import top.speedcubing.lib.bukkit.pluginMessage.BungeePluginMessage;

public class limbo implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        BungeePluginMessage.switchServer((Player) commandSender, "limbo");
        return true;
    }
}
