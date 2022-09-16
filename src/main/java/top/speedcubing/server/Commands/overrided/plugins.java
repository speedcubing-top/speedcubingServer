package top.speedcubing.server.Commands.overrided;

import org.bukkit.command.CommandSender;
import top.speedcubing.server.commandoverrider.OverrideCommandManager;

public class plugins implements OverrideCommandManager.OverridedCommand {
    public void execute(CommandSender sender, String[] strings) {
        sender.sendMessage("Plugins (3): §aspeedcubing§f, §aspeedcubingLib§f, §aspeedcubingServer");
    }
}
