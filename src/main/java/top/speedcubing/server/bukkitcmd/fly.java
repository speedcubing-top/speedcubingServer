package top.speedcubing.server.bukkitcmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.speedcubing.server.events.player.ToggleFlyEvent;
import top.speedcubing.server.player.User;

public class fly implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            Player player = (Player) commandSender;
            if (!((ToggleFlyEvent) new ToggleFlyEvent(player).call()).isCancelled()) {
                boolean allowFlight = player.getAllowFlight();
                player.setAllowFlight(!allowFlight);

                User user = User.getUser(player);
                user.sendMessage(allowFlight ? "%lang_cmd_fly_disable%" : "%lang_cmd_fly_enable%");

                user.dbUpdate("flying=" + (allowFlight ? 0 : 1));
            }
        } else commandSender.sendMessage("/fly");
        return true;
    }
}
