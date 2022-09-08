package top.speedcubing.server.Commands;

import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import top.speedcubing.lib.api.MojangAPI;
import top.speedcubing.lib.api.SessionServer;
import top.speedcubing.lib.api.exception.APIErrorException;
import top.speedcubing.lib.bukkit.PlayerUtils;
import top.speedcubing.lib.eventbus.LibEventManager;
import top.speedcubing.server.events.player.SkinEvent;
import top.speedcubing.server.libs.GlobalString;
import top.speedcubing.server.libs.User;
import top.speedcubing.server.speedcubingServer;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
                    UUID id = null;
                    try {
                        id = MojangAPI.getUUID(target);
                    } catch (APIErrorException e) {
                    }
                    if (id == null)
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
                            speedcubingServer.connection.update("playersdata", "skinvalue='',skinsignature=''", "id=" + user.id);
                        speedcubingServer.tcp.send(User.getUser(commandSender).tcpPort, "skin|" + user.id + "|" + skin[0] + "|" + skin[1]);
                    }
                }
            }).start();
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }
}