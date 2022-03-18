package cubingserver.Commands;

import cubing.spigot.lib.bukkit.api.BungeePluginMessage;
import cubingserver.PluginMessage;
import cubingserver.StringList.GlobalString;
import cubingserver.libs.User;
import cubingserver.speedcubingServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class hub implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (strings.length == 0) {
            switch (speedcubingServer.getServer(Bukkit.getPort())) {
                case "lobby":
                case "auth":
                    player.sendMessage(GlobalString.AlreadyInHub[User.getLang(player.getUniqueId())]);
                    break;
                case "bedwars":
                case "mlgrush":
                case "practice":
                    if (player.getWorld().getName().equals("world"))
                        PluginMessage.switchServer(player, "Lobby");
                    else
                        player.teleport(new Location(Bukkit.getWorld("world"), 0.5D, 100D, 0.5D, 0F, 0F));
                    break;
                case "reduce":
                case "clutch":
                case "knockbackffa":
                case "fastbuilder":
                    PluginMessage.switchServer(player, "lobby");
                    break;
            }
        } else player.sendMessage("/hub, /l, /lobby");
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }
}
