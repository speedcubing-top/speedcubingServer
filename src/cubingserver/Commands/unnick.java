package cubingserver.Commands;

import cubingserver.speedcubingServer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class unnick implements CommandExecutor, TabCompleter {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            UUID uuid = ((Player) commandSender).getUniqueId();
            String[] datas = speedcubingServer.connection.selectStrings("playersdata", "name,priority", "uuid='" + uuid + "'");
            nick.nickPlayer(datas[0], Integer.parseInt(datas[1]), uuid,false);
        } else commandSender.sendMessage("/unnick");
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }
}