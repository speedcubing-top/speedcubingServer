package speedcubing.server.Commands.offline;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import speedcubing.lib.utils.SQL.SQLUtils;
import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;

import java.util.ArrayList;
import java.util.List;

public class premium implements CommandExecutor, TabCompleter {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            User user = User.getUser(commandSender);
            if (SQLUtils.getBoolean(speedcubingServer.connection.select("playersdata", "autologin", "id=" + user.id))) {
                ((Player) commandSender).kickPlayer("§cDisabled §6Premium Check for user \"§b" + SQLUtils.getString(speedcubingServer.connection.select("playersdata", "name", "id=" + user.id)) + "§6\".");
                speedcubingServer.connection.update("playersdata", "autologin=0", "id=" + user.id);
            } else {
                ((Player) commandSender).kickPlayer("§aEnabled §6Premium Check for the user \"§b" + SQLUtils.getString(speedcubingServer.connection.select("playersdata", "name", "id=" + user.id)) + "§6\".");
                speedcubingServer.connection.update("playersdata", "autologin=1", "id=" + user.id);
            }
        } else commandSender.sendMessage("/premium");
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }
}