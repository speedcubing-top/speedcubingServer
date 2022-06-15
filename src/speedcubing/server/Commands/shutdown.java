package speedcubing.server.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import speedcubing.lib.bukkit.BungeePluginMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class shutdown implements CommandExecutor, TabCompleter {
    public static boolean restarting = true;

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        switch (Bukkit.getServerName()) {
            case "mlgrush":
            case "practice":
            case "bedwars":
            case "knockbackffa":
            case "fastbuilder":
            case "clutch":
            case "reducebot":
                restarting = true;
                Bukkit.getOnlinePlayers().forEach(a -> BungeePluginMessage.switchServer(a,"lobby"));
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (Bukkit.getOnlinePlayers().size() == 0)
                            Bukkit.shutdown();
                    }
                }, 1000, 1000);
                break;
            case "lobby":
            case "auth":
                Bukkit.getServer().shutdown();
                break;
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }
}
