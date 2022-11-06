package top.speedcubing.server.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import top.speedcubing.lib.utils.ByteArrayDataBuilder;
import top.speedcubing.server.speedcubingServer;

import java.util.Collections;
import java.util.List;

public class proxycommand implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0)
            commandSender.sendMessage("/proxycommand <command...>");
        else {
            StringBuilder comamnd = new StringBuilder();
            for (String string : strings) {
                comamnd.append(" ").append(string);
            }
            speedcubingServer.tcpClient.send(speedcubingServer.getRandomBungeePort(commandSender), new ByteArrayDataBuilder().writeUTF("proxycmd").writeUTF(comamnd.substring(1)).toByteArray());
        }
        return true;
    }
}
