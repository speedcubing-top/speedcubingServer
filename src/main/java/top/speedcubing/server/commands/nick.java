package top.speedcubing.server.commands;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.speedcubing.lib.api.MojangAPI;
import top.speedcubing.lib.bukkit.packetwrapper.OutScoreboardTeam;
import top.speedcubing.lib.utils.ByteArrayDataBuilder;
import top.speedcubing.lib.utils.Reflections;
import top.speedcubing.server.database.Database;
import top.speedcubing.server.database.Rank;
import top.speedcubing.server.events.player.NickEvent;
import top.speedcubing.server.lang.GlobalString;
import top.speedcubing.server.player.User;
import top.speedcubing.server.speedcubingServer;
import top.speedcubing.server.utils.config;

import java.util.Collections;
import java.util.HashSet;

public class nick implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!((NickEvent) new NickEvent((Player) commandSender).call()).isCancelled) {
            if (strings.length == 1) {
                String name = strings[0];
                User user = User.getUser(commandSender);
                if (name.equals(commandSender.getName()))
                    user.sendLangMessage(GlobalString.nicksameusername);
                else if (name.equals(user.realName))
                    user.sendLangMessage(GlobalString.nickdefaultusername);
                else
                    nickCheck(user, name, user.player, user.dbSelect("nickpriority").getString());
            } else if (strings.length == 2) {
                User user = User.getUser(commandSender);
                if (user.hasPermission("perm.nick.nickrank")) {
                    String name = strings[0];
                    if (config.rankPermissions.containsKey(strings[1].toLowerCase())) {
                        nickCheck(user, name, user.player, strings[1].toLowerCase());
                        user.dbUpdate("nickpriority='" + strings[1].toLowerCase() + "'");
                    } else
                        user.sendLangMessage(GlobalString.unknownRank);
                } else commandSender.sendMessage("/nick <nickname>\n/nick (use the previous nick)");
            } else if (strings.length == 0) {
                String[] datas = Database.connection.select("nickname,nickpriority").from("playersdata").where("id=" + User.getUser(commandSender).id).getStringArray();
                if (datas[0].equals(""))
                    commandSender.sendMessage("You didn't nicked before! please use /nick <nickname>");
                else if (datas[0].equals(commandSender.getName()))
                    User.getUser(commandSender).sendLangMessage(GlobalString.alreadyNicked);
                else nick.nickPlayer(datas[0], datas[1], true, (Player) commandSender);
            } else commandSender.sendMessage("/nick <nickname>\n/nick (use the previous nick)");
        }
        return true;
    }

    private void nickCheck(User user, String name, Player player, String rank) {
        boolean allow = (user.hasPermission("perm.nick.legacyregex") ? speedcubingServer.legacyNameRegex : speedcubingServer.nameRegex).matcher(name).matches() && !Database.connection.exist("playersdata", "name='" + name + "' OR id!='" + user.id + "' AND nickname='" + name + "'");
        if (allow) {
            if (!user.hasPermission("perm.nick.anyname")) {
                try {
                    MojangAPI.getByName(name);
                    allow = false;
                } catch (Exception e) {
                }
            }
        }
        if (allow)
            nickPlayer(name, rank, true, player);
        else
            user.sendLangMessage(GlobalString.nicknotavaliable);
    }

    public static void nickPlayer(String name, String rank, boolean nick, Player player) {
        User user = User.getUser(player);
        EntityPlayer entityPlayer = user.toNMS();

        //guild
        String tag = Database.connection.select("tag").from("guild").where("name='" + user.getGuild() + "'").getString();
        tag = nick ? "" : (tag == null ? "" : " §6[" + tag + "]");

        String extracted2 = speedcubingServer.getCode(rank) + speedcubingServer.playerNameExtract(name);
        PacketPlayOutScoreboardTeam old = new OutScoreboardTeam().a(speedcubingServer.getCode(user.displayRank) + speedcubingServer.playerNameExtract(player.getName())).h(1).packet;
        user.leavePacket = new OutScoreboardTeam().a(extracted2).h(1).packet;
        user.joinPacket = new OutScoreboardTeam().a(extracted2).c(Rank.getFormat(rank, user.id)[0]).d(tag).g(Collections.singletonList(name)).h(0).packet;

        for (User u : User.getUsers())
            if (u != user)
                u.sendPacket(old);

        Reflections.setField(entityPlayer.getProfile(), "name", name);

        for (User u : User.getUsers()) {
            u.bHidePlayer(player);
            u.bShowPlayer(player);
            u.sendPacket(user.leavePacket, user.joinPacket);
        }
        Location l = player.getLocation();
        user.sendPacket(
                new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer),
                new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer),
                new PacketPlayOutRespawn(player.getWorld().getEnvironment().getId(), entityPlayer.world.getDifficulty(), entityPlayer.world.getWorldData().getType(), entityPlayer.playerInteractManager.getGameMode()),
                new PacketPlayOutPosition(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch(), new HashSet<>()));
        new PacketPlayOutHeldItemSlot(player.getInventory().getHeldItemSlot());
        player.updateInventory();
        user.dbUpdate("nicked=" + (nick ? 1 : 0) + (nick ? ",nickname='" + name + "'" : ""));
        Database.connection.update("onlineplayer", "displayname='" + rank + "',displayrank='" + name + "'", "id=" + user.id);
        speedcubingServer.tcpClient.send(user.tcpPort, new ByteArrayDataBuilder().writeUTF("nick").writeInt(user.id).writeUTF(rank).writeUTF(name).toByteArray());
        user.displayRank = rank;
    }
}