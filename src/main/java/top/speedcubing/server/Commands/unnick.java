package top.speedcubing.server.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.speedcubing.lib.eventbus.LibEventManager;
import top.speedcubing.lib.utils.SQL.SQLUtils;
import top.speedcubing.server.events.player.NickEvent;
import top.speedcubing.server.libs.GlobalString;
import top.speedcubing.server.libs.User;
import top.speedcubing.server.speedcubingServer;

public class unnick implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (!((NickEvent) new NickEvent(player).call()).isCancelled) {
            String[] datas = SQLUtils.getStringArray(speedcubingServer.connection.select("playersdata", "name,priority", "id=" + User.getUser(commandSender).id));
            if (datas[0].equals(player.getName()))
                User.getUser(commandSender).sendLangMessage(GlobalString.notNicked);
            else if (strings.length == 0)
                nick.nickPlayer(datas[0], datas[1], false, player);
            else commandSender.sendMessage("/unnick");
        }
        return true;
    }
}