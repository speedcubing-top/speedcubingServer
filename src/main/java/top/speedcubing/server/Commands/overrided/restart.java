package top.speedcubing.server.Commands.overrided;

import org.bukkit.command.CommandSender;
import top.speedcubing.server.commandoverrider.OverrideCommandManager;
import top.speedcubing.server.speedcubingServer;

public class restart implements OverrideCommandManager.OverridedCommand {
    public void execute(CommandSender commandSender, String message) {
        speedcubingServer.restart();
    }
}
