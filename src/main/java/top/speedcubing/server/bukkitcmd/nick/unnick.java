package top.speedcubing.server.bukkitcmd.nick;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.speedcubing.common.rank.Rank;
import top.speedcubing.server.events.player.NickEvent;
import top.speedcubing.server.lang.GlobalString;
import top.speedcubing.server.player.User;

public class unnick implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;

        if (((NickEvent) new NickEvent(player).call()).isCancelled()) {
            return true;
        }

        User user = User.getUser(player);
        if (!user.nickState()) {
            User.getUser(commandSender).sendLangMessage(GlobalString.notNicked);
            return true;
        }

        if (strings.length != 0) {
            commandSender.sendMessage("/unnick");
            return true;
        }

        nick.nickPlayer(user.realName, Rank.getRank(user.realRank, User.getUser(commandSender).id), false, player, false);
        user.updateSkin(user.defaultSkin, user.realName);
        return true;
    }
}