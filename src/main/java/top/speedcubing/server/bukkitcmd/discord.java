package top.speedcubing.server.bukkitcmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import top.speedcubing.common.configuration.DataBaseConfig;
import top.speedcubing.server.player.User;


public class discord implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        User.getUser(commandSender).sendMessage("%lang_cmd_discord%", DataBaseConfig.get("discord_link"));
        return true;
    }
}
