package top.speedcubing.server.commands.staff;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import top.speedcubing.common.io.SocketWriter;
import top.speedcubing.lib.utils.ByteArrayDataBuilder;
import top.speedcubing.server.speedcubingServer;

public class proxycommand implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0)
            commandSender.sendMessage("/proxycommand <command...>");
        else {
            StringBuilder comamnd = new StringBuilder();
            for (String string : strings) {
                comamnd.append(" ").append(string);
            }
            SocketWriter.write(speedcubingServer.getRandomBungee(), new ByteArrayDataBuilder().writeUTF("proxycmd").writeUTF(comamnd.substring(1)).toByteArray());
        }
        return true;
    }
}
