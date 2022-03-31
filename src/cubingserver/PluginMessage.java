package cubingserver;

import cubing.lib.bukkit.BungeePluginMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PluginMessage {
    public static void switchServer(Player player, String server) {
        BungeePluginMessage.switchServer(player, server, speedcubingServer.getPlugin(speedcubingServer.class));
    }

    public static void sendRawMessage(Player player, String target, String text) {
        BungeePluginMessage.sendRawMessage(player, target, text, speedcubingServer.getPlugin(speedcubingServer.class));
    }

    public static void msgPlayerCount(Player player, String server, Plugin plugin) {
        BungeePluginMessage.msgPlayerCount(player, server, speedcubingServer.getPlugin(speedcubingServer.class));
    }

    public static void msgPlayerList(Player player, String server, Plugin plugin) {
        BungeePluginMessage.msgPlayerList(player, server, speedcubingServer.getPlugin(speedcubingServer.class));
    }
}
