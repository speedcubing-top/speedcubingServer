package speedcubing.server;

import org.bukkit.entity.Player;
import speedcubing.lib.bukkit.BungeePluginMessage;

public class PluginMessage {
    public static void switchServer(Player player, String server) {
        BungeePluginMessage.switchServer(player, server, speedcubingServer.getPlugin(speedcubingServer.class));
    }
}
