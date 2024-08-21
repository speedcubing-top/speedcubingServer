package top.speedcubing.server.cubingcmd.staff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.speedcubing.server.system.command.CubingCommand;

public class announce extends CubingCommand {
    public announce() {
        super("announce");
    }

    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        if (strings.length == 0) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (String str : strings) {
            builder.append(" ").append(str);
        }

        String result = builder.substring(1);
        result = ChatColor.translateAlternateColorCodes('&', result);

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(result);
        }
    }
}
