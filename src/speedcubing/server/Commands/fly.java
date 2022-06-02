package speedcubing.server.Commands;

import speedcubing.server.libs.GlobalString;
import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class fly implements CommandExecutor, TabCompleter {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (strings.length == 0) {
            int lang = User.getUser(player.getUniqueId()).lang;
            switch (Bukkit.getServerName()) {
                case "lobby":
                case "bedwars":
                case "mlgrush":
                case "practice":
                case "auth":
                    if (player.getWorld().getName().equals("world"))
                        if (player.getAllowFlight()) {
                            player.sendMessage(GlobalString.FlyDisable[lang]);
                            player.setAllowFlight(false);
                        } else {
                            player.sendMessage(GlobalString.FlyEnable[lang]);
                            player.setAllowFlight(true);
                        }
                    else
                        player.sendMessage(GlobalString.OnlyInHub[lang]);
                    break;
                case "knockbackffa":
                case "fastbuilder":
                case "clutch":
                case "reducebot":
                    player.sendMessage(GlobalString.OnlyInHub[lang]);
                    break;
            }
        } else player.sendMessage("/fly");
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }
}
