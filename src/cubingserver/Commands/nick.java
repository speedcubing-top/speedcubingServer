package cubingserver.Commands;

import cubing.bukkit.Event.ServerEventManager;
import cubing.bukkit.PacketWrapper;
import cubing.utils.Reflections;
import cubingserver.StringList.GlobalString;
import cubingserver.connection.SocketUtils;
import cubingserver.customEvents.NickEvent;
import cubingserver.libs.PlayerData;
import cubingserver.libs.Rank;
import cubingserver.speedcubingServer;
import cubingserver.things.events.PlayerJoin;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class nick implements CommandExecutor, TabCompleter {
    public static Map<UUID, Integer> nicktimes = new HashMap<>();

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        switch (Bukkit.getServerName()) {
            case "lobby":
            case "bedwars":
            case "mlgrush":
            case "practice":
            case "clutch":
                if (player.getWorld().getName().equals("world")) {
                    if (strings.length == 1) {
                        String name = strings[0];
                        if (name.equals(commandSender.getName()))
                            commandSender.sendMessage(GlobalString.nicksameusername[PlayerData.getLang(((Player) commandSender).getUniqueId())]);
                        else {
                            UUID uuid = ((Player) commandSender).getUniqueId();
                            if (name.matches("^\\w{3,16}$")
                                    && !speedcubingServer.connection.isStringExist("playersdata", "name='" + name + "'")
                                    && !speedcubingServer.connection.isStringExist("playersdata", "uuid!='" + uuid + "'AND nickname='" + name + "'")) {
                                int rank = 95;
                                nickPlayer(name, rank, uuid, true, player);
                                speedcubingServer.connection.update("playersdata", "nickpriority='" + rank + "',nickname='" + name + "'", "uuid='" + uuid + "'");
                            } else commandSender.sendMessage(GlobalString.nicknotavaliable[PlayerData.getLang(uuid)]);
                        }
                    } else if (strings.length == 0) {
                        UUID uuid = ((Player) commandSender).getUniqueId();
                        String[] datas = speedcubingServer.connection.selectStrings("playersdata", "nickname,nickpriority", "uuid='" + uuid + "'");
                        if (datas[0].equals(""))
                            commandSender.sendMessage("/nick <nickname>");
                        else
                            nick.nickPlayer(datas[0], Integer.parseInt(datas[1]), uuid, true,player);
                    } else commandSender.sendMessage("/nick <nickname>, /nick (use the previous nick)");
                } else
                    player.sendMessage(GlobalString.OnlyInHub[PlayerData.getLang(player.getUniqueId())]);
                break;
            case "reduce":
            case "knockbackffa":
            case "fastbuilder":
            case "auth":
                player.sendMessage(GlobalString.OnlyInHub[PlayerData.getLang(player.getUniqueId())]);
                break;
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }

    public static void nickPlayer(String name, int rank, UUID uuid, boolean nick,Player player) {
        NickEvent nickEvent = (NickEvent) ServerEventManager.callEvent(new NickEvent(name, rank, uuid, nick));
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        PlayerConnection connection = entityPlayer.playerConnection;
        String extracted2 = rank + Rank.playerNameExtract(name);
        String[] format = Rank.values()[Rank.rankToIndex(rank)].getFormat();
        PacketPlayOutScoreboardTeam old = PacketWrapper.packetPlayOutScreboardTeam(PlayerData.getRank(uuid) + Rank.playerNameExtract(player.getName()), null, null, null, null, 1);
        PacketPlayOutScoreboardTeam leavePacket = PacketWrapper.packetPlayOutScreboardTeam(extracted2, null, null, null, null, 1);
        PacketPlayOutScoreboardTeam joinPacket = PacketWrapper.packetPlayOutScreboardTeam(extracted2, format[0] + format[1], "", ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS.e, Collections.singletonList(name), 0);
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
        SocketUtils.sendData(speedcubingServer.BungeeTCPPort, "n|" + uuid + (nick ? "|" + name : ""), 100);
        PlayerData.RankCache.put(uuid, rank);
    }
}