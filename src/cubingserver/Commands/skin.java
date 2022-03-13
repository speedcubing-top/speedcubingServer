package cubingserver.Commands;

import cubing.api.MojangAPI;
import cubing.api.SessionServer;
import cubing.api.exception.APIErrorException;
import cubing.bukkit.PlayerUtils;
import cubingserver.StringList.GlobalString;
import cubingserver.connection.SocketUtils;
import cubingserver.libs.User;
import cubingserver.speedcubingServer;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class skin implements CommandExecutor, TabCompleter {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        switch (Bukkit.getServerName()) {
            case "lobby":
            case "bedwars":
            case "mlgrush":
            case "practice":
                if (player.getWorld().getName().equals("world")) {
                    String name = player.getName();
                    new Thread(() -> {
                        String target = "";
                        if (strings.length == 0)
                            target = name;
                        else if (strings.length == 1)
                            target = strings[0];
                        else player.sendMessage("/skin , /skin <player>");
                        if (!target.equals("")) {
                            String id;
                            try {
                                id = MojangAPI.getUUID(target);
                            } catch (APIErrorException e) {
                                id = "invalidName";
                            }
                            if (id.equals("invalidName"))
                                player.sendMessage(GlobalString.invalidName[User.getLang(player.getUniqueId())]);
                            else {
                                String[] skin = SessionServer.getSkin(id);
                                List<Packet<?>>[] packets = PlayerUtils.changeSkin(((CraftPlayer) player).getHandle(), skin);
                                PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
                                for (Packet<?> p : packets[0]) {
                                    connection.sendPacket(p);
                                }
                                String worldname = player.getWorld().getName();
                                UUID uuid = player.getUniqueId();
                                player.updateInventory();
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    if (!p.getWorld().getName().equals(worldname)) {
                                        PlayerConnection c = ((CraftPlayer) p).getHandle().playerConnection;
                                        for (Packet<?> packet : packets[2]) {
                                            c.sendPacket(packet);
                                        }
                                    } else if (p.getUniqueId() != uuid) {
                                        PlayerConnection c = ((CraftPlayer) p).getHandle().playerConnection;
                                        for (Packet<?> packet : packets[1]) {
                                            c.sendPacket(packet);
                                        }
                                    }
                                }
                                SocketUtils.sendData(speedcubingServer.BungeeTCPPort, "s|" + uuid + "|" + skin[0] + "|" + skin[1] + (target.equals(name) ? "|null" : ""), 100);
                            }
                        }
                    }).start();
                } else
                    player.sendMessage(GlobalString.OnlyInHub[User.getLang(player.getUniqueId())]);
                break;
            case "clutch":
            case "reduce":
            case "knockbackffa":
            case "fastbuilder":
            case "auth":
                player.sendMessage(GlobalString.OnlyInHub[User.getLang(player.getUniqueId())]);
                break;
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }
}