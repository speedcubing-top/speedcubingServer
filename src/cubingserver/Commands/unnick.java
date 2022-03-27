package cubingserver.Commands;

import cubingserver.StringList.GlobalString;
import cubingserver.libs.User;
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
        if (speedcubingServer.isBungeeOnlineMode) {
            Player player = (Player) commandSender;
            switch (speedcubingServer.getServer(Bukkit.getPort())) {
                case "lobby":
                case "bedwars":
                case "mlgrush":
                case "practice":
                case "clutch":
                    UUID uuid = ((Player) commandSender).getUniqueId();
                    String[] datas = speedcubingServer.connection.selectStrings("playersdata", "name,priority", "uuid='" + uuid + "'");
                    if (datas[0].equals(player.getName())) {
                        commandSender.sendMessage("you are not nicked!");
                    } else if (player.getWorld().getName().equals("world")) {
                        if (strings.length == 0) {
                            nick.nickPlayer(datas[0], datas[1], uuid, false, player);
                        } else commandSender.sendMessage("/unnick");
                    } else
                        player.sendMessage(GlobalString.OnlyInHub[User.getLang(player.getUniqueId())]);
                    break;
                case "reduce":
                case "knockbackffa":
                case "fastbuilder":
                case "auth":
                    player.sendMessage(GlobalString.OnlyInHub[User.getLang(player.getUniqueId())]);
                    break;
            }
        } else
            commandSender.sendMessage(GlobalString.UnknownCommand[User.getLang(((Player) commandSender).getUniqueId())]);
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }
}