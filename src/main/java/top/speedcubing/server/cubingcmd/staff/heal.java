package top.speedcubing.server.cubingcmd.staff;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.speedcubing.server.system.command.CubingCommand;

public class heal extends CubingCommand {
    public heal() {
        super("heal");
    }

    @Override
    public void execute(CommandSender commandSender, String s, String[] args) {
        if (commandSender instanceof Player player) {
            if (args.length == 0) {
                player.setHealth(player.getMaxHealth());
                player.sendMessage("Healed!");
            } else if (args.length == 1) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    target.setHealth(target.getMaxHealth());
                    player.sendMessage("Healed!");
                } else {
                    player.sendMessage("player not found!");
                }
            } else {
                player.sendMessage("/heal");
            }
        }
    }
}
