package top.speedcubing.server.Commands.override;

import org.bukkit.command.CommandSender;
import top.speedcubing.server.commandoverrider.OverrideCommandManager;

public class restart implements OverrideCommandManager.OverridedCommand {

    public void execute(CommandSender sender, String message) {
        System.out.println(message);
    }
}
