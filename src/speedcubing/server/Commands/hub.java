package speedcubing.server.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import speedcubing.lib.bukkit.BungeePluginMessage;
import speedcubing.lib.eventbus.LibEventManager;
import speedcubing.server.events.player.HubEvent;
import speedcubing.server.libs.GlobalString;
import speedcubing.server.libs.User;

import java.util.ArrayList;
import java.util.List;

public class hub implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            Player player = (Player) commandSender;
            HubEvent event = new HubEvent(player);
            LibEventManager.callEvent(event);
            if (!event.isCancelled)
                BungeePluginMessage.switchServer(player, "lobby");
        } else commandSender.sendMessage("/hub, /l, /lobby");
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }
}
