package speedcubing.server.Commands;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import speedcubing.lib.bukkit.packetwrapper.OutScoreboardTeam;
import speedcubing.lib.event.LibEventManager;
import speedcubing.lib.utils.Reflections;
import speedcubing.server.events.NickEvent;
import speedcubing.server.libs.GlobalString;
import speedcubing.server.libs.User;
import speedcubing.server.listeners.PlayerJoin;
import speedcubing.server.speedcubingServer;

import java.util.*;

public class nick implements CommandExecutor, TabCompleter {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (speedcubingServer.isBungeeOnlineMode) {
            Player player = (Player) commandSender;
            switch (Bukkit.getServerName()) {
                case "lobby":
                case "mlgrush":
                case "practice":
                case "bedwars":
                case "clutch":
                    if (player.getWorld().getName().equals("world")) {
                        if (strings.length == 1) {
                            String name = strings[0];
                            UUID uuid = ((Player) commandSender).getUniqueId();
                            if (name.equals(commandSender.getName()))
                                commandSender.sendMessage(GlobalString.nicksameusername[User.getUser(player.getUniqueId()).lang]);
                            else if (name.equals(speedcubingServer.connection.selectString("playersdata", "name", "uuid='" + uuid + "'")))
                                commandSender.sendMessage(GlobalString.nickdefaultusername[User.getUser(player.getUniqueId()).lang]);
                            else if (name.matches("^\\w{3,16}$") && !speedcubingServer.connection.isStringExist("playersdata", "name='" + name + "'") && !speedcubingServer.connection.isStringExist("playersdata", "uuid!='" + uuid + "' AND nickname='" + name + "'"))
                                nickPlayer(name, speedcubingServer.connection.selectString("playersdata", "nickpriority", "uuid='" + uuid + "'"), true, player);
                            else
                                commandSender.sendMessage(GlobalString.nicknotavaliable[User.getUser(player.getUniqueId()).lang]);
                        } else if (strings.length == 0) {
                            UUID uuid = ((Player) commandSender).getUniqueId();
                            String[] datas = speedcubingServer.connection.selectStrings("playersdata", "nickname,nickpriority", "uuid='" + uuid + "'");
                            if (datas[0].equals(""))
                                commandSender.sendMessage("/nick <nickname>");
                            else if (datas[0].equals(player.getName()))
                                commandSender.sendMessage("you are already nicked!");
                            else nick.nickPlayer(datas[0], datas[1], true, player);
                        } else commandSender.sendMessage("/nick <nickname>, /nick (use the previous nick)");
                    } else
                        player.sendMessage(GlobalString.OnlyInHub[User.getUser(player.getUniqueId()).lang]);
                    break;
                case "auth":
                case "fastbuilder":
                case "knockbackffa":
                case "reducebot":
                    player.sendMessage(GlobalString.OnlyInHub[User.getUser(player.getUniqueId()).lang]);
                    break;
            }
        } else
            commandSender.sendMessage(GlobalString.UnknownCommand[User.getUser(((Player) commandSender).getUniqueId()).lang]);
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }

    public static void nickPlayer(String name, String rank, boolean nick, Player player) {
        UUID uuid = player.getUniqueId();
        LibEventManager.callEvent(new NickEvent(name, rank, uuid, nick));
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        PlayerConnection connection = entityPlayer.playerConnection;
        String extracted2 = speedcubingServer.getCode(rank) + speedcubingServer.playerNameExtract(name);
        User user = User.getUser(uuid);
        PacketPlayOutScoreboardTeam old = OutScoreboardTeam.a(speedcubingServer.getCode(user.rank) + speedcubingServer.playerNameExtract(player.getName()), 1);
        PacketPlayOutScoreboardTeam leavePacket = OutScoreboardTeam.a(extracted2, 1);
        PacketPlayOutScoreboardTeam joinPacket = OutScoreboardTeam.a(extracted2, speedcubingServer.getFormat(rank)[0], Collections.singletonList(name), 0);
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
        PlayerJoin.RemovePackets.put(uuid, leavePacket);
        PlayerJoin.JoinPackets.put(uuid, joinPacket);
        speedcubingServer.connection.update("playersdata", "nicked=" + (nick ? 1 : 0), "uuid='" + uuid + "'");
        speedcubingServer.tcp.send(user.tcpPort, "nick|" + uuid + "|" + rank + "|" + name);
        user.rank = rank;
    }
}