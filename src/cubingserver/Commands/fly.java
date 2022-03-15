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

public class fly implements CommandExecutor, TabCompleter {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (strings.length == 0) {
            int lang = User.getLang(player.getUniqueId());
            switch (speedcubingServer.getServer(Bukkit.getPort())) {
                case "lobby":
                case "bedwars":
                case "mlgrush":
                case "practice":
                case "auth":
                    if (player.getWorld().getName().equals("world"))
                        if (player.getAllowFlight()) {
                            player.sendMessage(GlobalString.FlyDisable[User.getLang(player.getUniqueId())]);
                            player.setAllowFlight(false);
                        } else {
                            player.sendMessage(GlobalString.FlyEnable[User.getLang(player.getUniqueId())]);
                            player.setAllowFlight(true);
                        }
                    else
                        player.sendMessage(GlobalString.OnlyInHub[lang]);
                    break;
                case "knockbackffa":
                case "fastbuilder":
                case "clutch":
                case "reduce":
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
