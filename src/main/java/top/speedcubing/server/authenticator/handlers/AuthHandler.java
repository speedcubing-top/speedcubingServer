package top.speedcubing.server.authenticator.handlers;

import org.bukkit.entity.Player;
import top.speedcubing.server.database.Database;
import top.speedcubing.server.player.User;

import java.util.UUID;

public class AuthHandler {
    public static boolean isEnable(UUID uuid) {
        return Database.connection.select("auth_enable").from("playersdata").where("uuid='" + uuid + "'").getBoolean();
    }

    public static void setEnable(UUID uuid, boolean bool) {
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
        if (key != null && !key.isEmpty()) {
            return true;
        }
        return false;
    }

    public static String getKey(UUID uuid) {
        return Database.connection.select("auth_key").from("playersdata").where("uuid='" + uuid + "'").getString();
    }

    public static void setKey(UUID uuid, String key) {
        Database.connection.update("playersdata", "auth_key='" + key + "'", "uuid='" + uuid + "'");
    }

    public static void setTrustedSessions(Player player, boolean bool) {
        User user = User.getUser(player);
        if (bool) {
            user.dbUpdate("auth_sessions=" + 1);
        } else {
            user.dbUpdate("auth_sessions=" + 0);
        }
    }
    public static void setTrustedSessions(User user, boolean bool) {
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
        //TODO: modify this message
        player.sendMessage("set key pls");
    }

    public static void sendEnterCodeMessage(Player player) {
        //TODO: modify this message
        player.sendMessage("enter code pls");
    }
}
