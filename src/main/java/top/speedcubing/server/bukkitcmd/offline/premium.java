package top.speedcubing.server.bukkitcmd.offline;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.speedcubing.server.player.User;

public class premium implements CommandExecutor {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            User user = User.getUser(commandSender);
            String[] datas = user.dbSelect("name,autologin").getStringArray();
            boolean disable = datas[0].equals("1");
            ((Player) commandSender).kickPlayer((disable ? "§cDisabled" : "§aEnabled") + "§6Premium Check for user \"§b" + datas[0] + "§6\".");
            user.dbUpdate("autologin=" + (disable ? 0 : 1));
        } else commandSender.sendMessage("/premium");
        return true;
    }
}