package top.speedcubing.server.cubingcmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.speedcubing.server.events.player.ToggleFlyEvent;
import top.speedcubing.server.lang.GlobalString;
import top.speedcubing.server.player.User;
import top.speedcubing.server.system.command.CubingCommand;

public class fly extends CubingCommand {
    public fly() {
        super("fly");
    }

    @Override
    public void execute(CommandSender commandSender, String command, String[] args) {
        if (args.length == 0) {
            Player player = (Player) commandSender;
            if (!((ToggleFlyEvent) new ToggleFlyEvent(player).call()).isCancelled()) {
                boolean allowFlight = player.getAllowFlight();
                player.setAllowFlight(!allowFlight);

                User user = User.getUser(player);
                user.sendLangMessage(allowFlight ? GlobalString.FlyDisable : GlobalString.FlyEnable);

                user.dbUpdate("flying=" + (allowFlight ? 0 : 1));
            }
        } else commandSender.sendMessage("/fly");
    }
}
