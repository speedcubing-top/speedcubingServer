package top.speedcubing.server.Commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import top.speedcubing.server.database.*;
import top.speedcubing.server.events.player.NickEvent;
import top.speedcubing.server.libs.*;

public class unnick implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (!((NickEvent) new NickEvent(player).call()).isCancelled) {
            User user = User.getUser(player);
            if (user.realName.equals(player.getName()))
                User.getUser(commandSender).sendLangMessage(GlobalString.notNicked);
            else if (strings.length == 0)
                nick.nickPlayer(user.realName, Rank.getRank(user.realRank, User.getUser(commandSender).id), false, player);
            else commandSender.sendMessage("/unnick");
        }
        return true;
    }
}