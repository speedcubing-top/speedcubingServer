package top.speedcubing.server.commands.staff;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import top.speedcubing.server.utils.config;

public class serverconfig implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        config.reload();
        config.reloadDatabase();
        commandSender.sendMessage("reloading");
        return true;
    }
}
