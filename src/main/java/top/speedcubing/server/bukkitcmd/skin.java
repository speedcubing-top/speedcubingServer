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
                updateSkin(user, profileSkin.getSkin(), target);
            } else if (target.equalsIgnoreCase(user.realName)) {
                updateSkin(user, user.defaultSkin, target);
            } else {
                player.sendMessage("§cDefault skin not found");
            }
        }
        return true;
    }

    public static void updateSkin(User user, Skin skin, String target) {
        PlayerUtils.changeSkin(user.player, skin.getValue(), skin.getSignature());

        for (User u : User.getUsers()) {
            if (u.player.canSee(user.player)) {
                u.bHidePlayer(user.player);
                u.bShowPlayer(user.player);
            }
        }

        if (target != null && target.equalsIgnoreCase(user.realName)) {
            user.uploadSkin(new Skin("", ""));
        } else user.uploadSkin(skin);

        TCPClient.write(user.proxy, new ByteArrayBuffer().writeUTF("skin").writeInt(user.id).writeUTF(skin.getValue()).writeUTF(skin.getSignature()).toByteArray());
    }
}