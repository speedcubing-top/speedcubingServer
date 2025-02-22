package top.speedcubing.server.bukkitcmd.offline;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.speedcubing.common.database.Database;
import top.speedcubing.lib.utils.SQL.SQLConnection;
import top.speedcubing.server.player.User;

public class resetpassword implements CommandExecutor {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length != 2) {
            commandSender.sendMessage("/register <password> <confirm password>");
            return true;
        }

        int l = strings[0].length();
        if (l > 16) {
            commandSender.sendMessage("The password is too long.");
        } else if (l < 8) {
            commandSender.sendMessage("The password is too short. You need at least 8 characters.");
        } else if (strings[0].equals(strings[1])) {
            try (SQLConnection connection = Database.getCubing()) {
                connection.update("playersdata")
                        .set("password=?")
                        .where("id=" + User.getUser(commandSender).id)
                        .setString(0, strings[0])
                        .execute();
            }
            ((Player) commandSender).kickPlayer("§cReset §6password for user \"§b" + commandSender.getName() + "§6\".");
        } else {
            commandSender.sendMessage("The password and confirm password do not match.");
        }
        return true;
    }
}
