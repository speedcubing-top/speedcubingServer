package speedcubing.server.Commands.offline;

import speedcubing.server.StringList.GlobalString;
import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class  premium implements CommandExecutor, TabCompleter {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!speedcubingServer.isBungeeOnlineMode) {
            if (strings.length == 0) {
                String name = commandSender.getName();
                if (speedcubingServer.connection.selectBoolean("playersdata", "autologin", "name='" + name + "'")) {
                    ((Player) commandSender).kickPlayer("§cDisabled §6Premium Check for user \"§b" + name + "§6\".");
                    speedcubingServer.connection.update("playersdata", "autologin=0", "name='" + name + "'");
                } else {
                    ((Player) commandSender).kickPlayer("§aEnabled §6Premium Check for the user \"§b" + name + "§6\".");
                    speedcubingServer.connection.update("playersdata", "autologin=1", "name='" + name + "'");
                }
            } else commandSender.sendMessage("/premium");
        } else
            commandSender.sendMessage(GlobalString.UnknownCommand[User.getLang(((Player) commandSender).getUniqueId())]);
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }
}