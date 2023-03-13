package top.speedcubing.server.listeners;

import com.google.common.collect.Sets;
import net.minecraft.server.v1_8_R3.WorldData;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import top.speedcubing.lib.bukkit.packetwrapper.OutScoreboardTeam;
import top.speedcubing.lib.utils.Reflections;
import top.speedcubing.server.config;
import top.speedcubing.server.database.Database;
import top.speedcubing.server.libs.PreLoginData;
import top.speedcubing.server.libs.User;
import top.speedcubing.server.speedcubingServer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FrontListen implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void PlayerLoginEvent(PlayerLoginEvent e) {
        Player player = e.getPlayer();
        String[] datas = Database.connection.select("priority,nickpriority,perms,lang,id,name,opped,chatfilt").from("playersdata").where("uuid='" + player.getUniqueId() + "'").getStringArray();
        PreLoginData bungeeData = speedcubingServer.preLoginStorage.get(Integer.parseInt(datas[4]));
        if (bungeeData == null) {
            e.setKickMessage("§cServer Restarting... Please wait for a few seconds.");
            e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        } else {
            this.datas = datas;
            this.bungeeData = bungeeData;
        }
    }

    String[] datas;
    PreLoginData bungeeData;

    @EventHandler(priority = EventPriority.LOW)
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        e.setJoinMessage("");
        Player player = e.getPlayer();
        String displayName = player.getName();
        String realRank = speedcubingServer.getRank(datas[0], player.getUniqueId().toString());
        String displayRank = realRank;
        String nickedRealName = "";
        if (!datas[5].equals(displayName)) {
            displayRank = datas[1];
            nickedRealName = datas[5];
        }
        Set<String> perms = Sets.newHashSet(datas[2].split("\\|"));
        perms.remove("");
        perms.addAll(config.rankPermissions.get(realRank));
        Set<String> groups = new HashSet<>();
        for (String s : perms) {
            if (User.group.matcher(s).matches() && config.grouppermissions.containsKey(s.substring(6)))
                groups.add(s.substring(6));
        }
        groups.forEach(a -> perms.addAll(config.grouppermissions.get(a)));
        new User(player, displayRank, perms, Integer.parseInt(datas[3]), Integer.parseInt(datas[4]), datas[6].equals("1"), bungeeData, datas[7].equals("1"), datas[5]);

        player.setOp(datas[6].equals("1"));
        User user = User.getUser(player);

        String extracted = speedcubingServer.getCode(user.rank) + speedcubingServer.playerNameExtract(displayName);
        user.leavePacket = new OutScoreboardTeam().a(extracted).h(1).packet;
        user.joinPacket = new OutScoreboardTeam().a(extracted).c(speedcubingServer.getFormat(user.rank)[0]).g(Collections.singletonList(displayName)).h(0).packet;
        //formatting
        for (User u : User.getUsers())
            user.sendPacket(u.leavePacket, u.joinPacket);
        for (User u : User.getUsers())
            if (u != user)
                u.sendPacket(user.leavePacket, user.joinPacket);
        //vanish
        if (user.vanished)
            for (Player p : Bukkit.getOnlinePlayers())
                p.hidePlayer(player);
        for (User u : User.getUsers())
            if (u.vanished) player.hidePlayer(u.player);

        //nick
        if (!nickedRealName.equals(""))
            user.sendPacket(new OutScoreboardTeam().a(speedcubingServer.getCode(realRank) + speedcubingServer.playerNameExtract(nickedRealName)).c(speedcubingServer.getFormat(realRank)[0]).g(Collections.singletonList(nickedRealName)).h(0).packet);
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

    @EventHandler
    public void ServerCommandEvent(ServerCommandEvent e) {
        System.out.print("[CONSOLE] " + e.getCommand());
    }

    @EventHandler
    public void WeatherChangeEvent(WeatherChangeEvent e) {
        if (e.getWorld().hasStorm()) {
            WorldData worldData = ((CraftWorld) e.getWorld()).getHandle().worldData;
            worldData.setWeatherDuration(0);
            Reflections.setField(worldData, "q", false);
        } else e.setCancelled(true);
    }
}