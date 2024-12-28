package top.speedcubing.server.bukkitcmd.staff;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.speedcubing.common.database.Database;
import top.speedcubing.lib.utils.SQL.SQLConnection;

public class heal implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (strings.length == 0) {
                player.setHealth(player.getMaxHealth());
                player.sendMessage("Healed!");
            } else if (strings.length == 1) {
                Player target = Bukkit.getPlayer(strings[0]);
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
        return true;
    }
}
