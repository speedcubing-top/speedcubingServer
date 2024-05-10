package top.speedcubing.server.authenticator;

import org.bukkit.entity.Player;

public class AuthMessenger {

    public static void sendSetKeyMessage(Player player) {
        player.sendMessage("§cPlease set up 2FA with `/2fa setup <code>`");
    }

    public static void sendEnterCodeMessage(Player player) {
        player.sendMessage("§cPlease authenticate using `/2fa <code>`");
    }
}
