package top.speedcubing.server.bukkitcmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.speedcubing.lib.api.MojangAPI;
import top.speedcubing.lib.api.mojang.ProfileSkin;
import top.speedcubing.lib.api.mojang.Skin;
import top.speedcubing.lib.bukkit.PlayerUtils;
import top.speedcubing.lib.utils.bytes.ByteArrayBuffer;
import top.speedcubing.lib.utils.sockets.TCPClient;
import top.speedcubing.server.events.player.SkinEvent;
import top.speedcubing.server.lang.GlobalString;
import top.speedcubing.server.player.User;

public class skin implements CommandExecutor {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (!((SkinEvent) new SkinEvent(player).call()).isCancelled()) {
            User user = User.getUser(commandSender);
            String target = "";
            if (strings.length == 0)
                target = user.realName;
            else if (strings.length == 1)
                target = strings[0];
            else player.sendMessage("/skin , /skin <player>");

            if (!target.isEmpty() && !target.equalsIgnoreCase(user.realName)) {
                ProfileSkin profileSkin;
                try {
                    profileSkin = MojangAPI.getSkinByName(target);
                    if (profileSkin == null) {
                        user.sendLangMessage(GlobalString.invalidName);
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }
                user.updateSkin(profileSkin.getSkin(), target);
            } else if (target.equalsIgnoreCase(user.realName)) {
                user.updateSkin(user.defaultSkin, target);
            } else {
                player.sendMessage("§cDefault skin not found");
            }
        }
        return true;
    }
}