package top.speedcubing.server.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import top.speedcubing.lib.eventbus.LibEventManager;
import top.speedcubing.server.events.player.ToggleFlyEvent;
import top.speedcubing.server.libs.GlobalString;
import top.speedcubing.server.libs.User;

import java.util.*;

public class fly implements CommandExecutor, TabCompleter {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            Player player = (Player) commandSender;
            ToggleFlyEvent event = new ToggleFlyEvent(player);
            LibEventManager.callEvent(event);
            if (!event.isCancelled) {
                boolean allowFlight = player.getAllowFlight();
                player.setAllowFlight(!allowFlight);
                player.sendMessage(allowFlight ? GlobalString.FlyDisable[User.getUser(commandSender).lang] : GlobalString.FlyEnable[User.getUser(commandSender).lang]);
            }
        } else commandSender.sendMessage("/fly");
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }
}
