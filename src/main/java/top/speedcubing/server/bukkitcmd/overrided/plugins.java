package top.speedcubing.server.bukkitcmd.overrided;

import org.bukkit.command.CommandSender;
import top.speedcubing.server.commandoverrider.OverrideCommandManager;

public class plugins extends OverrideCommandManager {
    public plugins() {
        super("plugins", "pl");
    }

    public void execute(CommandSender sender, String[] strings) {
        sender.sendMessage("Plugins (3): §aspeedcubing§f, §aspeedcubingLib§f, §aspeedcubingServer");
    }
}
