package top.speedcubing.server.bukkitcmd.offline;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.speedcubing.lib.utils.SQL.SQLRow;
import top.speedcubing.server.player.User;

public class premium implements CommandExecutor {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            User user = User.getUser(commandSender);
            SQLRow r = user.dbSelect("name,autologin");
            boolean disable = r.getBoolean(1);
            ((Player) commandSender).kickPlayer((disable ? "§cDisabled" : "§aEnabled") + "§6Premium Check for user \"§b" + r.getString(0) + "§6\".");
            user.dbUpdate("autologin=" + (disable ? 0 : 1));
        } else commandSender.sendMessage("/premium");
        return true;
    }
}