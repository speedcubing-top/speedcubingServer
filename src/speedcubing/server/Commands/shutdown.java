package speedcubing.server.Commands;

import speedcubing.server.PluginMessage;
import speedcubing.server.speedcubingServer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class shutdown implements CommandExecutor, TabCompleter {
    public static boolean restarting = true;

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        switch (speedcubingServer.getServer(Bukkit.getPort())) {
            case "mlgrush":
            case "practice":
            case "bedwars":
            case "knockbackffa":
            case "fastbuilder":
            case "clutch":
            case "reduce":
                restarting = true;
                Bukkit.getOnlinePlayers().forEach(a -> PluginMessage.switchServer(a,"lobby"));
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
