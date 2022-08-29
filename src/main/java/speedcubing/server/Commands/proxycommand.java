package speedcubing.server.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class proxycommand implements CommandExecutor, TabCompleter {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0)
            commandSender.sendMessage("/proxycommand <command...>");
        else {
            StringBuilder comamnd = new StringBuilder();
            for (String string : strings) {
                comamnd.append(" ").append(string);
            }
            int port = commandSender instanceof ConsoleCommandSender ?
                    (User.usersByID.values().size() != 0 ? User.usersByID.values().iterator().next().tcpPort : 25568 - Bukkit.getPort() % 2)
                    : User.getUser(commandSender).tcpPort;
            speedcubingServer.tcp.send(port, "proxycmd|" + comamnd.substring(1));
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }
}
