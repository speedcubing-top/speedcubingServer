package top.speedcubing.server.Commands.offline;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.speedcubing.server.libs.User;
import top.speedcubing.server.speedcubingServer;

public class premium implements CommandExecutor {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            User user = User.getUser(commandSender);
            String[] datas = speedcubingServer.connection.select("name,autologin").from("playersdata").where("id=" + user.id).getStringArray();
            boolean disable = datas[0].equals("1");
            ((Player) commandSender).kickPlayer((disable ? "§cDisabled" : "§aEnabled") + "§6Premium Check for user \"§b" + datas[0] + "§6\".");
            speedcubingServer.connection.update("playersdata", "autologin=" + (disable ? 0 : 1), "id=" + user.id);
        } else commandSender.sendMessage("/premium");
        return true;
    }
}