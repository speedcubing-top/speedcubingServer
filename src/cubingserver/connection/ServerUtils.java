package cubingserver.connection;

import cubing.bukkit.PlayerUtils;
import cubingserver.libs.LogListener;
import cubingserver.speedcubingServer;
import cubingserver.things.Cps;
import cubingserver.things.froze;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import java.util.UUID;

public class ServerUtils {
    public static int AllPlayers;

    public static String[] SocketEvent(String receive) {
        String[] rs = receive.split("\\|");
        switch (rs[0]) {
            case "c":
                switch (rs[1]) {
                    case "a":
                        Cps.CpsListening.add(UUID.fromString(rs[2]));
                        break;
                    case "r":
                        Cps.CpsListening.remove(UUID.fromString(rs[2]));
                        break;
                }
                break;
            case "f":
                switch (rs[1]) {
                    case "a":
                        froze.frozed.add(Bukkit.getPlayerExact(rs[2]).getUniqueId());
                        break;
                    case "r":
                        froze.frozed.remove(Bukkit.getPlayerExact(rs[2]).getUniqueId());
                        break;
                }
                break;
            case "k":
                String text = "";
                String[] hex = rs[2].split("\\\\u");
                for (int i = 1; i < hex.length; i++) {
                    text += (char) Integer.parseInt(hex[i], 16);
                }
                String t = text;
                Bukkit.getScheduler().runTask(speedcubingServer.getPlugin(speedcubingServer.class), () -> Bukkit.getPlayerExact(rs[1]).kickPlayer(t));
                break;
            case "r":
                PlayerUtils.explosionCrash(((CraftPlayer) Bukkit.getPlayerExact(rs[1])).getHandle().playerConnection);
                break;
            case "t":
                String re = receive.split("\\|", 2)[1];
                Bukkit.getScheduler().runTask(speedcubingServer.getPlugin(speedcubingServer.class), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), re));
                break;
            case "l":
                LogListener.Listening = rs[1].equals("a");
                break;
            case "v":
                switch (rs[1]) {
                    case "a":
                        speedcubingServer.velocities.put(UUID.fromString(rs[2]), new Double[]{Double.parseDouble(rs[3]), Double.parseDouble(rs[4])});
                        break;
                    case "r":
                        speedcubingServer.velocities.remove(UUID.fromString(rs[2]));
                        break;
                }
                break;
            default:
                return rs;
        }
        return null;
    }
}
