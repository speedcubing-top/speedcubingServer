package top.speedcubing.server.cubinglistener;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunkBulk;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutStatistic;
import net.minecraft.server.v1_8_R3.PacketPlayOutTabComplete;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import top.speedcubing.lib.bukkit.events.packet.PlayOutEvent;
import top.speedcubing.lib.eventbus.CubingEventHandler;
import top.speedcubing.lib.utils.ReflectionUtils;
import top.speedcubing.server.bukkitcmd.troll.sendpacket;
import top.speedcubing.server.player.User;

public class PlayOut {

    public static UUID rrr = UUID.randomUUID();

    @CubingEventHandler
    public void PlayOutEvent(PlayOutEvent e) {
        if (e.getPacket() instanceof PacketPlayOutStatistic packet) {
            Map<?, Integer> stats = (Map<?, Integer>) ReflectionUtils.getField(packet, "a");
            stats.replaceAll((k, v) -> 0);
        } else if (e.getPacket() instanceof PacketPlayOutTabComplete packet) {
            String[] s = (String[]) ReflectionUtils.getField(packet, "a");
            System.out.println("tab complete packet " + Arrays.toString(s));
        } else if (e.getPacket() instanceof PacketPlayOutMapChunk packet) {

            if (!sendpacket.whoWasFucked.contains(e.getPlayer()))
                return;

            try {
                PacketPlayOutMapChunk.ChunkMap data = (PacketPlayOutMapChunk.ChunkMap) ReflectionUtils.getField(packet, "c");
                boolean isInit = (boolean) ReflectionUtils.getField(packet, "d");
                boolean isOverworld = ((CraftPlayer) e.getPlayer()).getHandle().world.worldProvider.o();
                sendpacket.randomChunkData(isOverworld, isInit, data);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } else if (e.getPacket() instanceof PacketPlayOutMapChunkBulk packet) {

            if (!sendpacket.whoWasFucked.contains(e.getPlayer()))
                return;

            try {
                PacketPlayOutMapChunk.ChunkMap[] data = (PacketPlayOutMapChunk.ChunkMap[]) ReflectionUtils.getField(packet, "c");
                boolean isOverworld = (boolean) ReflectionUtils.getField(packet, "d");
                for (PacketPlayOutMapChunk.ChunkMap datum : data) {
                    sendpacket.randomChunkData(isOverworld, true, datum);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (e.getPacket() instanceof PacketPlayOutNamedEntitySpawn packet) {
            UUID uuid = (UUID) ReflectionUtils.getField(packet, "b");
            User user = User.usersByUUID.get(uuid);
            if (user != null && user.nicked()) {
                ReflectionUtils.setField(packet, "b", user.calculateNickHashUUID());
            }
        } else if (e.getPacket() instanceof PacketPlayOutPlayerInfo packet) {
            PacketPlayOutPlayerInfo.EnumPlayerInfoAction action = (PacketPlayOutPlayerInfo.EnumPlayerInfoAction) ReflectionUtils.getField(packet, "a");
            List<PacketPlayOutPlayerInfo.PlayerInfoData> datas = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) ReflectionUtils.getField(packet, "b");

            if (action == PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER) {
                for (int i = 0; i < datas.size(); i++) {
                    PacketPlayOutPlayerInfo.PlayerInfoData data = datas.get(i);
                    UUID receivedUUID = data.a().getId();
                    if (receivedUUID.equals(e.getPlayer().getUniqueId())) {
                        continue;
                    }

                    User user = User.usersByUUID.get(receivedUUID);
                    if (user == null) {
                        continue;
                    }

                    if (user.nicked()) {
                        Property oldPF = user.toNMS().getProfile().getProperties().get("textures").iterator().next();
                        GameProfile profile = new GameProfile(user.calculateNickHashUUID(), user.bGetName());
                        profile.getProperties().put("textures", oldPF);
                        datas.set(i, packet.new PlayerInfoData(profile, data.b(), data.c(), data.d()));
                    }
                }
            }
        }
    }
}
