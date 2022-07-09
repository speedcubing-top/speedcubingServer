package speedcubing.server.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import speedcubing.lib.eventbus.LibEventManager;
import speedcubing.lib.utils.SQL.SQLUtils;
import speedcubing.server.events.player.NickEvent;
import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;

import java.util.ArrayList;
import java.util.List;

public class unnick implements CommandExecutor, TabCompleter {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        NickEvent event = new NickEvent(player);
        LibEventManager.callEvent(event);
        if (!event.isCancelled) {
            String[] datas = SQLUtils.getStringArray(speedcubingServer.connection.select("playersdata", "name,priority", "id=" + User.getUser(commandSender).id));
            if (datas[0].equals(player.getName()))
                commandSender.sendMessage("you are not nicked!");
            else if (strings.length == 0)
                nick.nickPlayer(datas[0], datas[1], false, player);
            else commandSender.sendMessage("/unnick");
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }
}