package top.speedcubing.server.Commands;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import top.speedcubing.lib.bukkit.packetwrapper.OutScoreboardTeam;
import top.speedcubing.lib.eventbus.LibEventManager;
import top.speedcubing.lib.utils.Reflections;
import top.speedcubing.lib.utils.SQL.SQLUtils;
import top.speedcubing.server.events.player.NickEvent;
import top.speedcubing.server.libs.GlobalString;
import top.speedcubing.server.libs.User;
import top.speedcubing.server.speedcubingServer;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class nick implements CommandExecutor, TabCompleter {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        NickEvent event = new NickEvent((Player) commandSender);
        LibEventManager.callEvent(event);
        if (!event.isCancelled) {
            if (strings.length == 1) {
                String name = strings[0];
                User user = User.getUser(commandSender);
                if (name.equals(commandSender.getName()))
                    commandSender.sendMessage(GlobalString.nicksameusername[user.lang]);
                else if (name.equals(SQLUtils.getString(speedcubingServer.connection.select("playersdata", "name", "id=" + user.id))))
                    commandSender.sendMessage(GlobalString.nickdefaultusername[user.lang]);
                else if (name.matches("^\\w{1,16}$") && !speedcubingServer.connection.isStringExist("playersdata", "name='" + name + "'") && !speedcubingServer.connection.isStringExist("playersdata", "id!='" + user.id + "' AND nickname='" + name + "'"))
                    nickPlayer(name, SQLUtils.getString(speedcubingServer.connection.select("playersdata", "nickpriority", "id=" + user.id)), true, (Player) commandSender);
                else
                    commandSender.sendMessage(GlobalString.nicknotavaliable[user.lang]);
            } else if (strings.length == 0) {
                String[] datas = SQLUtils.getStringArray(speedcubingServer.connection.select("playersdata", "nickname,nickpriority", "id=" + User.getUser(commandSender).id));
                if (datas[0].equals(""))
                    commandSender.sendMessage("/nick <nickname>");
                else if (datas[0].equals(commandSender.getName()))
                    commandSender.sendMessage("you are already nicked!");
                else nick.nickPlayer(datas[0], datas[1], true, (Player) commandSender);
            } else commandSender.sendMessage("/nick <nickname>, /nick (use the previous nick)");
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }

    public static void nickPlayer(String name, String rank, boolean nick, Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        PlayerConnection connection = entityPlayer.playerConnection;
        String extracted2 = speedcubingServer.getCode(rank) + speedcubingServer.playerNameExtract(name);
        User user = User.getUser(player);
        PacketPlayOutScoreboardTeam old = new OutScoreboardTeam().a(speedcubingServer.getCode(user.rank) + speedcubingServer.playerNameExtract(player.getName())).h(1).packet;
        PacketPlayOutScoreboardTeam leavePacket = new OutScoreboardTeam().a(extracted2).h(1).packet;
        PacketPlayOutScoreboardTeam joinPacket = new OutScoreboardTeam().a(extracted2).c(speedcubingServer.getFormat(rank)[0]).g(Collections.singletonList(name)).h(0).packet;
        PacketPlayOutPlayerInfo removePlayerPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer);
        PacketPlayOutPlayerInfo addPlayerPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p != player)
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(old);
        }
        Reflections.setField(entityPlayer.getProfile(), "name", name);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.hidePlayer(player);
            p.showPlayer(player);
        }
        Location l = player.getLocation();
        connection.sendPacket(removePlayerPacket);
        connection.sendPacket(addPlayerPacket);
        connection.sendPacket(new PacketPlayOutRespawn(entityPlayer.world.getWorld().getEnvironment().getId(), entityPlayer.world.getDifficulty(), entityPlayer.world.getWorldData().getType(), entityPlayer.playerInteractManager.getGameMode()));
        connection.sendPacket(new PacketPlayOutPosition(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch(), new HashSet<>()));
        connection.sendPacket(new PacketPlayOutHeldItemSlot(player.getInventory().getHeldItemSlot()));
        player.updateInventory();
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerConnection c = ((CraftPlayer) p).getHandle().playerConnection;
            c.sendPacket(leavePacket);
            c.sendPacket(joinPacket);
        }
        user.joinPacket = joinPacket;
        user.leavePacket = leavePacket;
        speedcubingServer.connection.update("playersdata", "nicked=" + (nick ? 1 : 0) + (nick ? ",nickname='" + name + "'" : ""), "id=" + user.id);
        speedcubingServer.tcp.send(user.tcpPort, "nick|" + user.id + "|" + rank + "|" + name);
        user.rank = rank;
    }
}