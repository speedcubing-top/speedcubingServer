package top.speedcubing.server.bukkitcmd.staff;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import top.speedcubing.server.player.User;

public class testkb implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        switch (strings.length) {
            case 1:
                Player player = Bukkit.getPlayer(strings[0]);
                if (player == null)
                    commandSender.sendMessage("player not found in this server");
                else
                    test(0.1, 0.1, 0.1, player, commandSender);
                break;
            case 4:
                player = Bukkit.getPlayer(strings[0]);
                if (player == null)
                    commandSender.sendMessage("player not found in this server");
                else
                    try {
                        test(Double.parseDouble(strings[1]), Double.parseDouble(strings[2]), Double.parseDouble(strings[3]), player, commandSender);
                    } catch (Exception e) {
                        commandSender.sendMessage("invalid number");
                    }
                break;
            default:
                commandSender.sendMessage("/testkb <player> <x> <y> <z>\n/testkb <player>");
                break;
        }
        return true;
    }

    private void test(double x, double y, double z, Player player, CommandSender commandSender) {
        player.setVelocity(new Vector(x, y, z));
        commandSender.sendMessage("§bTested '" + User.getUser(player).realName + "' for anti kb! (" + x + "," + y + "," + z + ")");
    }
}
