package top.speedcubing.server.authenticator.handlers;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import top.speedcubing.server.authenticator.events.AuthKeyChangeEvent;
import top.speedcubing.server.authenticator.events.AuthSessionChangeEvent;
import top.speedcubing.server.authenticator.events.AuthStatusChangeEvent;
import top.speedcubing.common.database.Database;
import top.speedcubing.server.player.User;

public class AuthHandler {
    public static boolean isEnable(UUID uuid) {
        return Database.connection.select("auth_enable").from("playersdata").where("uuid='" + uuid + "'").getBoolean();
    }

    //Died idk why
    public static void setEnable(UUID uuid, boolean bool) {
        AuthStatusChangeEvent authStatusChangeEvent = new AuthStatusChangeEvent(uuid, bool);
        Bukkit.getPluginManager().callEvent(authStatusChangeEvent);
        if (bool) {
            Database.connection.update("playersdata", "auth_enable='" + 1 + "'", "uuid='" + uuid + "'");
        } else {
            Database.connection.update("playersdata", "auth_enable='" + 0 + "'", "uuid='" + uuid + "'");
        }
    }

    public static boolean hasBypass(UUID uuid) {
        return Database.connection.select("auth_bypass").from("playersdata").where("uuid='" + uuid + "'").getBoolean();
    }

    public static boolean hasKey(UUID uuid) {
        String key = Database.connection.select("auth_key").from("playersdata").where("uuid='" + uuid + "'").getString();
        return key != null && !key.isEmpty();
    }

    public static String getKey(UUID uuid) {
        return Database.connection.select("auth_key").from("playersdata").where("uuid='" + uuid + "'").getString();
    }

    public static void setKey(UUID uuid, String key) {
        Database.connection.update("playersdata", "auth_key='" + key + "'", "uuid='" + uuid + "'");
        AuthKeyChangeEvent authKeyChangeEvent = new AuthKeyChangeEvent(uuid, key);
        Bukkit.getPluginManager().callEvent(authKeyChangeEvent);
    }

    public static void setTrustedSessions(Player player, boolean bool) {
        User user = User.getUser(player);
        AuthSessionChangeEvent authSessionChangeEvent = new AuthSessionChangeEvent(player, bool);
        Bukkit.getPluginManager().callEvent(authSessionChangeEvent);
        if (bool) {
            user.dbUpdate("auth_sessions=" + 1);
        } else {
            user.dbUpdate("auth_sessions=" + 0);
        }
    }

    public static void setTrustedSessions(User user, boolean bool) {
        AuthSessionChangeEvent authSessionChangeEvent = new AuthSessionChangeEvent(user, bool);
        Bukkit.getPluginManager().callEvent(authSessionChangeEvent);
        if (bool) {
            user.dbUpdate("auth_sessions=" + 1);
        } else {
            user.dbUpdate("auth_sessions=" + 0);
        }
    }

    public static boolean hasTrustedSessions(UUID uuid) {
        return Database.connection.select("auth_sessions").from("playersdata").where("uuid='" + uuid + "'").getBoolean();
    }

    public static void setIp(UUID uuid, String ip) {
        Database.connection.update("playersdata", "auth_ip='" + ip + "'", "uuid='" + uuid + "'");
    }

    public static String getIp(UUID uuid) {
        return Database.connection.select("auth_ip").from("playersdata").where("uuid='" + uuid + "'").getString();
    }

    public static void sendSetKeyMessage(Player player) {
        player.sendMessage("§cPlease set up 2FA with `/2fa setup <code>`");
    }

    public static void sendEnterCodeMessage(Player player) {
        player.sendMessage("§cPlease authenticate using `/2fa <code>`");
    }

    public static void sendErrorMessage(Player player) {
        player.sendMessage("§cAn error occurred");
    }
}
