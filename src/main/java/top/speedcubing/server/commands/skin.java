package top.speedcubing.server.commands;

import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.speedcubing.lib.api.MojangAPI;
import top.speedcubing.lib.api.mojang.ProfileSkin;
import top.speedcubing.lib.bukkit.PlayerUtils;
import top.speedcubing.lib.utils.ByteArrayDataBuilder;
import top.speedcubing.server.events.player.SkinEvent;
import top.speedcubing.server.lang.GlobalString;
import top.speedcubing.server.player.User;
import top.speedcubing.server.speedcubingServer;

import java.util.List;

public class skin implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (!((SkinEvent) new SkinEvent(player).call()).isCancelled) {
            User user = User.getUser(commandSender);
            String target = "";
            if (strings.length == 0)
                target = user.realName;
            else if (strings.length == 1)
                target = strings[0];
            else player.sendMessage("/skin , /skin <player>");
            if (!target.equals("")) {
                ProfileSkin skin;
                try {
                    skin = MojangAPI.getSkinByName(target);
                } catch (Exception e) {
                    user.sendLangMessage(GlobalString.invalidName);
                    return true;
                }
                List<Packet<?>>[] packets = PlayerUtils.changeSkin(player, new String[]{skin.getValue(), skin.getSignature()});
                String worldname = player.getWorld().getName();
                for (User p : User.getUsers()) {
                    if (!p.player.getWorld().getName().equals(worldname))
                        packets[1].forEach(p::sendPacket);
                    else if (p != user)
                        packets[0].forEach(p::sendPacket);
                }
                user.dbUpdate(target.equalsIgnoreCase(user.realName) ? "skinvalue='',skinsignature=''" : ("skinvalue='" + skin.getValue() + "',skinsignature='" + skin.getSignature() + "'"));
                speedcubingServer.tcpClient.send(user.tcpPort, new ByteArrayDataBuilder().writeUTF("skin").writeInt(user.id).writeUTF(skin.getValue()).writeUTF(skin.getSignature()).toByteArray());
            }
        }
        return true;
    }
}