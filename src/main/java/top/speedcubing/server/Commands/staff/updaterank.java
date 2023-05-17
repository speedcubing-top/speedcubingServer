package top.speedcubing.server.Commands.staff;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import top.speedcubing.server.Commands.nick;
import top.speedcubing.server.database.*;
import top.speedcubing.server.libs.User;

public class updaterank implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player target = Bukkit.getPlayerExact(strings[0]);
        if (target != null) {
            User user = User.getUser(target);
            if (!user.realName.equals(target.getName()))
                nick.nickPlayer(user.realName, Rank.getRank(user.realRank, User.getUser(commandSender).id), false, target);
            commandSender.sendMessage("updated");
        } else commandSender.sendMessage("offline or not exist");
        return true;
    }
}