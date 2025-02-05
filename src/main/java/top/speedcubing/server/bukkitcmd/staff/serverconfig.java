package top.speedcubing.server.bukkitcmd.staff;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import top.speedcubing.common.configuration.ServerConfig;
import top.speedcubing.common.events.ConfigReloadEvent;
import top.speedcubing.server.configuration.Configuration;

public class serverconfig implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        new ConfigReloadEvent().call();
        commandSender.sendMessage("reloading");
        return true;
    }
}
