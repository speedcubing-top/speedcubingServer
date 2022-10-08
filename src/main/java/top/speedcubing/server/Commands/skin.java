package top.speedcubing.server.Commands;

import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import top.speedcubing.lib.api.MojangAPI;
import top.speedcubing.lib.api.mojang.ProfileSkin;
import top.speedcubing.lib.bukkit.PlayerUtils;
import top.speedcubing.server.events.player.SkinEvent;
import top.speedcubing.server.libs.GlobalString;
import top.speedcubing.server.libs.User;
import top.speedcubing.server.speedcubingServer;

import java.util.List;

public class skin implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (!((SkinEvent) new SkinEvent(player).call()).isCancelled)
            new Thread(() -> {
                User user = User.getUser(commandSender);
                String target = "";
                if (strings.length == 0)
                    target = speedcubingServer.connection.select("name").from("playersdata").where("id=" + user.id).getString();
                else if (strings.length == 1)
                    target = strings[0];
                else player.sendMessage("/skin , /skin <player>");
                if (!target.equals("")) {
                    try {
                        String finalTarget = target;
                        new Thread(() -> {
                            ProfileSkin skin;
                            try {
                                skin = MojangAPI.getSkinByName(finalTarget);
                            } catch (Exception e) {
                                user.sendLangMessage(GlobalString.invalidName);
                                return;
                            }
                            List<Packet<?>>[] packets = PlayerUtils.changeSkin(player, new String[]{skin.getValue(), skin.getSignature()});
                            packets[0].forEach(((CraftPlayer) player).getHandle().playerConnection::sendPacket);
                            String worldname = player.getWorld().getName();
                            player.updateInventory();
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (!p.getWorld().getName().equals(worldname))
                                    packets[2].forEach(((CraftPlayer) p).getHandle().playerConnection::sendPacket);
                                else if (p != player)
                                    packets[1].forEach(((CraftPlayer) p).getHandle().playerConnection::sendPacket);
                            }
                            if (!finalTarget.equalsIgnoreCase(player.getName()))
                                speedcubingServer.connection.update("playersdata", "skinvalue='" + skin.getValue() + "',skinsignature='" + skin.getSignature() + "'", "id=" + user.id);
                            else
                                speedcubingServer.connection.update("playersdata", "skinvalue='',skinsignature=''", "id=" + user.id);
                            speedcubingServer.tcp.send(user.tcpPort, "skin|" + user.id + "|" + skin.getValue() + "|" + skin.getSignature());
                        }).start();
                    } catch (Exception e) {
                        user.sendLangMessage(GlobalString.invalidName);
                    }
                }
            }).start();
        return true;
    }
}