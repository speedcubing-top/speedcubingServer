package cubingserver.Commands;

import cubingserver.connection.SocketUtils;
import cubingserver.speedcubingServer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class proxycommand implements CommandExecutor , TabCompleter {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0)
            commandSender.sendMessage("/proxycommand <command...>");
        else {
            String comamnd = "";
            for (int i = 0; i < strings.length; i++) {
                comamnd += " " + strings[i];
            }
            SocketUtils.sendData(speedcubingServer.BungeeTCPPort, "p|" + comamnd.substring(1), 100);
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }
}
