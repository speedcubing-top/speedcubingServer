package top.speedcubing.server.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.speedcubing.server.database.Database;
import top.speedcubing.server.events.player.NickEvent;
import top.speedcubing.server.libs.GlobalString;
import top.speedcubing.server.libs.User;
import top.speedcubing.server.speedcubingServer;

public class unnick implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (!((NickEvent) new NickEvent(player).call()).isCancelled) {
            String[] datas = Database.connection.select("name,priority").from("playersdata").where("id=" + User.getUser(commandSender).id).getStringArray();
            if (datas[0].equals(player.getName()))
                User.getUser(commandSender).sendLangMessage(GlobalString.notNicked);
            else if (strings.length == 0)
                nick.nickPlayer(datas[0], speedcubingServer.getRank(datas[1], player.getUniqueId().toString()), false, player);
            else commandSender.sendMessage("/unnick");
        }
        return true;
    }
}