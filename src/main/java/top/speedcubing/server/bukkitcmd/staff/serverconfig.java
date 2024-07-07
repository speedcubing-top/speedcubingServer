package top.speedcubing.server.bukkitcmd.staff;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import top.speedcubing.common.configuration.ServerConfig;

public class serverconfig implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        ServerConfig.reload(false);
        commandSender.sendMessage("reloading");
        return true;
    }
}
