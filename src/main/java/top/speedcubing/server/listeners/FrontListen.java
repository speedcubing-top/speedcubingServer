package top.speedcubing.server.listeners;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import net.minecraft.server.v1_8_R3.WorldData;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import top.speedcubing.common.database.Database;
import top.speedcubing.common.rank.Rank;
import top.speedcubing.lib.bukkit.PlayerUtils;
import top.speedcubing.lib.bukkit.packetwrapper.OutScoreboardTeam;
import top.speedcubing.lib.utils.Reflections;
import top.speedcubing.server.player.PreLoginData;
import top.speedcubing.server.player.User;
import top.speedcubing.server.speedcubingServer;
import top.speedcubing.server.utils.config;

public class FrontListen implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void PlayerLoginEvent(PlayerLoginEvent e) {
        Player player = e.getPlayer();
        String[] datas = Database.connection.select("priority,nickpriority,perms,lang,id,name,chatfilt,guild,serverwhitelist,agreement").from("playersdata").where("uuid='" + player.getUniqueId() + "'").getStringArray();
        int id = Integer.parseInt(datas[4]);
        realRank = Rank.getRank(datas[0], id);
        if (!Rank.isStaff(realRank) && Bukkit.hasWhitelist() && (datas[8].equals("0"))) {
            e.setKickMessage("§cThis server is currently under maintenance.");
            e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            speedcubingServer.preLoginStorage.remove(id);
            return;
        }
        PreLoginData bungeeData = speedcubingServer.preLoginStorage.get(id);
        if (bungeeData == null) {
            e.setKickMessage("§cError occurred.");
            e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        } else {
            this.datas = datas;
            this.bungeeData = bungeeData;
            speedcubingServer.preLoginStorage.remove(id);
        }
    }

    String realRank;
    String[] datas;
    PreLoginData bungeeData;

    @EventHandler(priority = EventPriority.LOW)
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        e.setJoinMessage("");
        Player player = e.getPlayer();
        //Check Nick
        String displayName = player.getName();

        String displayRank = realRank;
        boolean nicked = !datas[5].equals(displayName);
        if (nicked)
            displayRank = datas[1];
        //Perms
        Set<String> perms = Sets.newHashSet(datas[2].split("\\|"));
        perms.remove("");
        perms.addAll(Rank.rankByName.get(realRank).getPerms());
        Set<String> groups = perms.stream().filter(s -> User.group.matcher(s).matches() && Rank.grouppermissions.containsKey(s.substring(6))).map(s -> s.substring(6)).collect(Collectors.toSet());
        groups.forEach(a -> perms.addAll(Rank.grouppermissions.get(a)));
        //User
        User user = new User(player, displayRank, realRank, perms, Integer.parseInt(datas[3]), Integer.parseInt(datas[4]), datas[6].equals("1"), bungeeData, datas[6].equals("1"), datas[5]);
        //OP
        player.setOp(user.hasPermission("perm.op"));
        //Guild
        String tag = Database.connection.select("tag").from("guild").where("name='" + datas[7] + "'").getString();
        tag = nicked ? "" : (tag == null ? "" : " §6[" + tag + "]");
        //Packets
        String extracted = Rank.getCode(user.displayRank) + speedcubingServer.playerNameExtract(displayName);
        user.leavePacket = new OutScoreboardTeam().a(extracted).h(1).packet;
        user.joinPacket = new OutScoreboardTeam().a(extracted).c(user.getFormat(false).getPrefix()).d(tag).g(Collections.singletonList(displayName)).h(0).packet;
        //formatting
        for (User u : User.getUsers()) {
            user.sendPacket(u.leavePacket, u.joinPacket);
            if (u != user)
                u.sendPacket(user.leavePacket, user.joinPacket);
        }
        //vanish
        if (user.vanished)
            for (Player p : Bukkit.getOnlinePlayers())
                p.hidePlayer(player);
        for (User u : User.getUsers())
            if (u.vanished) player.hidePlayer(u.player);
        //nick
        if (nicked)
            user.sendPacket(new OutScoreboardTeam().a(Rank.getCode(realRank) + speedcubingServer.playerNameExtract(datas[5])).c(Rank.getFormat(realRank, user.id).getPrefix()).d(tag).g(Collections.singletonList(datas[5])).h(0).packet);

        if (config.onlineCrash.contains(player.getUniqueId().toString()) || config.onlineCrash.contains(player.getAddress().getAddress().getHostAddress())) {
            speedcubingServer.scheduledPool.schedule(() -> PlayerUtils.crashAll(player), 50, TimeUnit.MILLISECONDS);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void PlayerVelocityEvent(PlayerVelocityEvent e) {
        Player player = e.getPlayer();
        player.setVelocity(User.getUser(player).applyKnockback(player.getVelocity()));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void InventoryOpenEvent(InventoryOpenEvent e) {
        InventoryType type = e.getInventory().getType();
        if (type == InventoryType.BEACON || type == InventoryType.HOPPER || type == InventoryType.ANVIL)
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void InventoryClickEvent(InventoryClickEvent e) {
        User user = User.getUser(e.getWhoClicked());
        long l = System.currentTimeMillis();
        if (l - user.lastInvClick < 100)
            e.setCancelled(true);
        else user.lastInvClick = l;
    }


    @EventHandler(priority = EventPriority.LOW)
    public void PlayerInteractEvent(PlayerInteractEvent e) {
        switch (e.getAction()) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                User.getUser(e.getPlayer()).leftClick += 1;
                break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                User.getUser(e.getPlayer()).rightClick += 1;
                break;
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void CreatureSpawnEvent(CreatureSpawnEvent e) {
        if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER && e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void ServerCommandEvent(ServerCommandEvent e) {
        System.out.print("[CONSOLE] " + e.getCommand());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void WeatherChangeEvent(WeatherChangeEvent e) {
        if (e.getWorld().hasStorm()) {
            WorldData worldData = ((CraftWorld) e.getWorld()).getHandle().worldData;
            worldData.setWeatherDuration(0);
            Reflections.setField(worldData, "q", false);
        } else e.setCancelled(true);
    }
}