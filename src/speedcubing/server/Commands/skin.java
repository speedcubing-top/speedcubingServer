package speedcubing.server.Commands;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import speedcubing.lib.api.MojangAPI;
import speedcubing.lib.api.SessionServer;
import speedcubing.lib.api.exception.APIErrorException;
import speedcubing.lib.bukkit.PlayerUtils;
import speedcubing.server.libs.GlobalString;
import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;

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
                    new Thread(() -> {
                        String target = "";
                        if (strings.length == 0)
                            target = player.getName();
                        else if (strings.length == 1)
                            target = strings[0];
                        else player.sendMessage("/skin , /skin <player>");
                        if (!target.equals("")) {
                            String id = "";
                            try {
                                id = MojangAPI.getUUID(target);
                            } catch (APIErrorException e) {
                            }
                            if (id.equals(""))
                                player.sendMessage(GlobalString.invalidName[User.getUser(player.getUniqueId()).lang]);
                            else {
                                String[] skin = SessionServer.getSkin(id);
                                List<Packet<?>>[] packets = PlayerUtils.changeSkin(player, skin);
                                PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
                                packets[0].forEach(connection::sendPacket);
                                String worldname = player.getWorld().getName();
                                UUID uuid = player.getUniqueId();
                                player.updateInventory();
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    if (!p.getWorld().getName().equals(worldname))
                                        packets[2].forEach(((CraftPlayer) p).getHandle().playerConnection::sendPacket);
                                    else if (p.getUniqueId() != uuid)
                                        packets[1].forEach(((CraftPlayer) p).getHandle().playerConnection::sendPacket);
                                }
                                if (!target.equalsIgnoreCase(player.getName()))
                                    speedcubingServer.connection.update("playersdata", "skinvalue='" + skin[0] + "',skinsignature='" + skin[1] + "'", "uuid='" + uuid + "'");
                                else
                                    speedcubingServer.connection.update("playersdata", "skinvalue=NULL,skinsignature=NULL", "uuid='" + uuid + "'");
                                speedcubingServer.tcp.send(User.getUser(uuid).tcpPort, "skin|" + uuid + "|" + skin[0] + "|" + skin[1]);
                            }
                        }
                    }).start();
                } else
                    player.sendMessage(GlobalString.OnlyInHub[User.getUser(player.getUniqueId()).lang]);
                break;
            case "clutch":
            case "reducebot":
            case "knockbackffa":
            case "fastbuilder":
            case "auth":
                player.sendMessage(GlobalString.OnlyInHub[User.getUser(player.getUniqueId()).lang]);
                break;
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }
}