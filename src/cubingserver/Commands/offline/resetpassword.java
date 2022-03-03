package cubingserver.Commands.offline;

import cubingserver.speedcubingServer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class resetpassword implements CommandExecutor, TabCompleter {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (Bukkit.getPort() % 2 == 0) {
            if (strings.length == 2) {
                int l = strings[0].length();
                if (l > 16)
                    commandSender.sendMessage("The password is too long.");
                else if (l < 8)
                    commandSender.sendMessage("The password is too short. You need atleast 8 characters");
                else if (strings[0].equals(strings[1])) {
                    String name = commandSender.getName();
                    speedcubingServer.connection.update("playersdata", "password='" + strings[0] + "'", "name='" + name + "'");
                    ((Player) commandSender).kickPlayer("§cResetted §6password for user \"§b" + name + "§6\".");
                } else commandSender.sendMessage("The password and confirm password do not match.");
            } else commandSender.sendMessage("/register <password> <confirm password>");
        } else commandSender.sendMessage("the command is not supported in offline server.");
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }
}
