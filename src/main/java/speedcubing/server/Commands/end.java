package speedcubing.server.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import speedcubing.lib.bukkit.BungeePluginMessage;

import java.util.*;

public class end implements CommandExecutor, TabCompleter {
    public static boolean restarting = false;

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        restarting = true;
        Bukkit.getOnlinePlayers().forEach(a -> BungeePluginMessage.switchServer(a, "lobby"));
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().size() == 0 || Bukkit.getPort() == 25573 || Bukkit.getPort() == 25574)
                    Bukkit.shutdown();
            }
        }, 1000, 1000);
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }
}
