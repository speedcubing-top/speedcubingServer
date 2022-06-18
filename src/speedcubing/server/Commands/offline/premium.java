package speedcubing.server.Commands.offline;

import speedcubing.server.libs.GlobalString;
import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class premium implements CommandExecutor, TabCompleter {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            Player player = ((Player) commandSender);
            UUID uuid = player.getUniqueId();
            if (speedcubingServer.connection.selectBoolean("playersdata", "autologin", "uuid='" + uuid + "'")) {
                player.kickPlayer("§cDisabled §6Premium Check for user \"§b" + speedcubingServer.connection.selectString("playersdata", "name", "uuid='" + uuid + "'") + "§6\".");
                speedcubingServer.connection.update("playersdata", "autologin=0", "uuid='" + uuid + "'");
            } else {
                player.kickPlayer("§aEnabled §6Premium Check for the user \"§b" + speedcubingServer.connection.selectString("playersdata", "name", "uuid='" + uuid + "'") + "§6\".");
                speedcubingServer.connection.update("playersdata", "autologin=1", "uuid='" + uuid + "'");
            }
        } else commandSender.sendMessage("/premium");
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }
}