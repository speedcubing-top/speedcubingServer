package top.speedcubing.server.commands.staff;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayOutBed;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunkBulk;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import top.speedcubing.server.speedcubingServer;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class sendpacket implements CommandExecutor, Listener {
    private final static Set<Player> whoWasFucked = Collections.newSetFromMap(new WeakHashMap<>());

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (args.length != 2) {
            player.sendMessage("§cUsage: /sendpacket <player> <packet>");
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            player.sendMessage("§cPlayer not found");
            return true;
        }
        String function = args[1];
        switch (function) {
            case "sleep":
                sleep(target);
                break;
            case "wakeup":
                wakeUp(target);
                break;
            case "howtowin":
                howToWin(target);
                break;
            case "fuckpeople":
                fuckPeople(target);
        }

        return true;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        whoWasFucked.remove(e.getPlayer());
    }

    private void sleep(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        PacketPlayOutBed packetPlayOutBed = new PacketPlayOutBed(craftPlayer.getHandle(), new BlockPosition(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ()));
        craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutBed);
        craftPlayer.getHandle().u().getTracker().a(craftPlayer.getHandle(), packetPlayOutBed);
    }

    private void wakeUp(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        PacketPlayOutAnimation packetPlayOutBed = new PacketPlayOutAnimation(craftPlayer.getHandle(), 2);
        craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutBed);
        craftPlayer.getHandle().u().getTracker().a(craftPlayer.getHandle(), packetPlayOutBed);
    }

    private void howToWin(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        PacketPlayOutBed packetPlayOutBed = new PacketPlayOutBed(craftPlayer.getHandle(), new BlockPosition(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ()));
        craftPlayer.getHandle().u().getTracker().a(craftPlayer.getHandle(), packetPlayOutBed);
    }

    private void fuckPeople(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        whoWasFucked.add(player);
        ((WorldServer) craftPlayer.getHandle().world).getPlayerChunkMap().removePlayer(craftPlayer.getHandle());
        ((WorldServer) craftPlayer.getHandle().world).getPlayerChunkMap().addPlayer(craftPlayer.getHandle());
    }

    public static void initFuckPeople() {
        initSupported();
        speedcubingServer.instance.protocolManager.addPacketListener(new PacketAdapter(
                speedcubingServer.instance,
                ListenerPriority.NORMAL,
                PacketType.Play.Server.MAP_CHUNK_BULK
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (!whoWasFucked.contains(event.getPlayer())) return;
                try {
                    Field c = PacketPlayOutMapChunkBulk.class.getDeclaredField("c");
                    Field d = PacketPlayOutMapChunkBulk.class.getDeclaredField("d");
                    c.setAccessible(true);
                    d.setAccessible(true);
                    PacketPlayOutMapChunk.ChunkMap[] data = (PacketPlayOutMapChunk.ChunkMap[]) c.get(event.getPacket().getHandle());
                    boolean isOverworld = d.getBoolean(event.getPacket().getHandle());
                    for (PacketPlayOutMapChunk.ChunkMap datum : data) {
                        randomChunkData(isOverworld, true, datum);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        speedcubingServer.instance.protocolManager.addPacketListener(new PacketAdapter(
                speedcubingServer.instance,
                ListenerPriority.NORMAL,
                PacketType.Play.Server.MAP_CHUNK
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (!whoWasFucked.contains(event.getPlayer())) return;
                try {
                    Field c = PacketPlayOutMapChunk.class.getDeclaredField("c");
                    c.setAccessible(true);
                    Field d = PacketPlayOutMapChunk.class.getDeclaredField("d");
                    d.setAccessible(true);
                    boolean isInit = d.getBoolean(event.getPacket().getHandle());
                    boolean isOverworld = ((CraftPlayer) event.getPlayer()).getHandle().world.worldProvider.o();
                    PacketPlayOutMapChunk.ChunkMap data = (PacketPlayOutMapChunk.ChunkMap) c.get(event.getPacket().getHandle());
                    randomChunkData(isOverworld, isInit, data);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private static int reverseSectionsCount(int i, boolean flag, boolean flag1) {
        int j = 2 * 16 * 16 * 16;
        int k = 16 * 16 * 16 / 2;
        int l = flag ? 16 * 16 * 16 / 2 : 0;
        int i1 = flag1 ? 256 : 0;
        return (i - i1) / (j + k + l);
    }

    public static void randomChunkData(boolean sendBiome, boolean isInitChunk, PacketPlayOutMapChunk.ChunkMap input) {
        int sectionsCount = reverseSectionsCount(input.a.length, isInitChunk, sendBiome);

        int cur = 0;
        for (int i = 0; i < sectionsCount; i++) {
            for (int i1 = 0; i1 < 8192; i1 += 2) {
                int blockData = supported[ThreadLocalRandom.current().nextInt(supported.length)];
                input.a[cur++] = (byte) (blockData & 255);
                input.a[cur++] = (byte) (blockData >> 8 & 255);
            }
        }
    }

    private static int[] supported;

    private static void initSupported() {
        int supportedLen = 0;
        for (IBlockData iBlockData : Block.d) {
            supportedLen++;
        }
        supported = new int[supportedLen];
        int counter = 0;
        for (IBlockData iBlockData : Block.d) {
            supported[counter++] = Block.d.b(iBlockData);
        }
    }
}
