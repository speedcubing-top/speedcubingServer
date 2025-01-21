package top.speedcubing.server.cubingcmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.speedcubing.lib.bukkit.pluginMessage.BungeePluginMessage;
import top.speedcubing.server.system.command.CubingCommand;

public class limbo extends CubingCommand {
    public limbo() {
        super("limbo");
    }

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        BungeePluginMessage.switchServer((Player) sender, "limbo");
    }
}
