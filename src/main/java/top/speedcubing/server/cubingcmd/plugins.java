package top.speedcubing.server.cubingcmd;

import org.bukkit.command.CommandSender;
import top.speedcubing.server.system.command.CubingCommand;

public class plugins extends CubingCommand {
    public plugins() {
        super("plugins", "pl");
    }

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        sender.sendMessage("Plugins (3): §aspeedcubing§f, §aspeedcubingLib§f, §aspeedcubingServer");
    }
}
