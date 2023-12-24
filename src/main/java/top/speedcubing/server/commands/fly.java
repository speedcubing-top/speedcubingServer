package top.speedcubing.server.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import top.speedcubing.server.events.player.ToggleFlyEvent;
import top.speedcubing.server.lang.GlobalString;
import top.speedcubing.server.player.User;

public class fly implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            Player player = (Player) commandSender;
            if (!((ToggleFlyEvent) new ToggleFlyEvent(player).call()).isCancelled) {
                boolean allowFlight = player.getAllowFlight();
                player.setAllowFlight(!allowFlight);
                User.getUser(player).sendLangMessage(allowFlight ? GlobalString.FlyDisable : GlobalString.FlyEnable);
                commandSender.sendMessage("Fly Toggled.");
            }
        } else commandSender.sendMessage("/fly");
        return true;
    }
}
