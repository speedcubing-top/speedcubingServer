package cubingserver.Commands;

import cubing.bukkit.api.BungeePluginMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class end implements CommandExecutor, TabCompleter {
    public static boolean restarting = false;

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        switch (Bukkit.getServerName()) {
            case "mlgrush":
            case "practice":
            case "bedwars":
            case "knockbackffa":
            case "fastbuilder":
            case "clutch":
            case "reduce":
                for (Player p : Bukkit.getOnlinePlayers()) {
                    BungeePluginMessage.switchServer(p, "lobby");
                }
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
