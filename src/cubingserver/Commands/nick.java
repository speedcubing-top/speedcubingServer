package cubingserver.Commands;

import cubing.bukkit.PacketWrapper;
import cubing.utils.Reflections;
import cubingserver.connection.SocketUtils;
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

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (Bukkit.getPort() % 2 == 0) {
            if (strings.length == 1) {
                String name = strings[0];
                if (name.matches("^\\w{3,16}$")
                        && !speedcubingServer.connection.isStringExist("playersdata", "name='" + name + "'")
                &&!speedcubingServer.connection.isStringExist("playersdata", "nickname='" + name + "'")) {
                    UUID uuid = ((Player) commandSender).getUniqueId();
                    int rank = 95;
                    Player player = Bukkit.getPlayer(uuid);
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
                    for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
                        p.hidePlayer(player);
                        p.showPlayer(player);
                    }
                    Location l = player.getLocation();
                    connection.sendPacket(removePlayerPacket);
                    connection.sendPacket(addPlayerPacket);
                    connection.sendPacket(new PacketPlayOutRespawn(entityPlayer.world.getWorld().getEnvironment().getId(), entityPlayer.world.getDifficulty(), entityPlayer.world.getWorldData().getType(), entityPlayer.playerInteractManager.getGameMode()));
                    connection.sendPacket(new PacketPlayOutPosition(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch(), new HashSet<>()));
                    connection.sendPacket(new PacketPlayOutHeldItemSlot(player.getInventory().getHeldItemSlot()));
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        PlayerConnection c = ((CraftPlayer) p).getHandle().playerConnection;
                        c.sendPacket(leavePacket);
                        c.sendPacket(joinPacket);
                    }
                    PlayerJoin.RemovePackets.put(uuid, leavePacket);
                    PlayerJoin.JoinPackets.put(uuid, joinPacket);
                    SocketUtils.sendData(speedcubingServer.BungeeTCPPort, "n|" + uuid + "|" + name, 100);
                    PlayerData.RankCache.put(uuid, rank);
                    speedcubingServer.connection.update("playersdata", "nickpriority='" + rank + "'", "uuid='" + uuid + "'");
                } else commandSender.sendMessage("this nickname is not avaliable.");
            } else commandSender.sendMessage("/nick <nickname>");
        } else commandSender.sendMessage("the command is not supported in offline server.");
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }
}