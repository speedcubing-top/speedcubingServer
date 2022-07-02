package speedcubing.server.Commands;

import net.minecraft.server.v1_8_R3.Packet;
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
import speedcubing.lib.eventbus.LibEventManager;
import speedcubing.server.events.player.SkinEvent;
import speedcubing.server.libs.GlobalString;
import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;

import java.util.ArrayList;
import java.util.List;

public class skin implements CommandExecutor, TabCompleter {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        SkinEvent event = new SkinEvent(player);
        LibEventManager.callEvent(event);
        if (!event.isCancelled)
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
                        player.sendMessage(GlobalString.invalidName[User.getUser(commandSender).lang]);
                    else {
                        String[] skin = SessionServer.getSkin(id);
                        List<Packet<?>>[] packets = PlayerUtils.changeSkin(player, skin);
                        packets[0].forEach(((CraftPlayer) player).getHandle().playerConnection::sendPacket);
                        String worldname = player.getWorld().getName();
                        player.updateInventory();
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (!p.getWorld().getName().equals(worldname))
                                packets[2].forEach(((CraftPlayer) p).getHandle().playerConnection::sendPacket);
                            else if (p != player)
                                packets[1].forEach(((CraftPlayer) p).getHandle().playerConnection::sendPacket);
                        }
                        User user = User.getUser(commandSender);
                        if (!target.equalsIgnoreCase(player.getName()))
                            speedcubingServer.connection.update("playersdata", "skinvalue='" + skin[0] + "',skinsignature='" + skin[1] + "'", "id=" + user.id);
                        else
                            speedcubingServer.connection.update("playersdata", "skinvalue=NULL,skinsignature=NULL", "id=" + user.id);
                        speedcubingServer.tcp.send(User.getUser(commandSender).tcpPort, "skin|" + user.id + "|" + skin[0] + "|" + skin[1]);
                    }
                }
            }).start();
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }
}