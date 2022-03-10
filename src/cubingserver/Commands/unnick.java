package cubingserver.Commands;

import cubingserver.StringList.GlobalString;
import cubingserver.libs.PlayerData;
import cubingserver.speedcubingServer;
import org.bukkit.Bukkit;
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
        Player player = (Player) commandSender;
        switch (Bukkit.getServerName()) {
            case "lobby":
            case "bedwars":
            case "mlgrush":
            case "practice":
            case "clutch":
                if (player.getWorld().getName().equals("world")) {
                    if (strings.length == 0) {
                        UUID uuid = ((Player) commandSender).getUniqueId();
                        String[] datas = speedcubingServer.connection.selectStrings("playersdata", "name,priority", "uuid='" + uuid + "'");
                        nick.nickPlayer(datas[0], Integer.parseInt(datas[1]), uuid, false, player);
                    } else commandSender.sendMessage("/unnick");
                } else
                    player.sendMessage(GlobalString.OnlyInHub[PlayerData.getLang(player.getUniqueId())]);
                break;
            case "reduce":
            case "knockbackffa":
            case "fastbuilder":
            case "auth":
                player.sendMessage(GlobalString.OnlyInHub[PlayerData.getLang(player.getUniqueId())]);
                break;
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }
}