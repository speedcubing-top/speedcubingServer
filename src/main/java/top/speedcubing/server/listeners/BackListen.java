package top.speedcubing.server.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import top.speedcubing.lib.events.SignUpdateEvent;
import top.speedcubing.server.commands.nick;
import top.speedcubing.server.player.User;
import top.speedcubing.server.speedcubingServer;

import java.util.List;

public class BackListen implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void FoodLevelChangeEvent(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void InventoryOpenEvent(InventoryOpenEvent e) {
        InventoryType type = e.getInventory().getType();
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE)
            if (type == InventoryType.BEACON || type == InventoryType.HOPPER || type == InventoryType.ANVIL)
                e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerDeathEvent(PlayerDeathEvent e) {
        e.setDeathMessage("");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerKickEvent(PlayerKickEvent e) {
        if (e.getReason().equals("disconnect.spam"))
            e.setCancelled(true);
        else if(e.getReason().equals("Timed out")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("cancelled timed out");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerQuitEvent(PlayerQuitEvent e) {
        e.setQuitMessage("");
        Player player = e.getPlayer();
        User.usersByID.remove(User.getUser(player).id);
        User.usersByUUID.remove(player.getUniqueId());
        nick.settingNick.remove(e.getPlayer().getUniqueId());
        nick.nickName.remove(e.getPlayer().getUniqueId());
        nick.nickRank.remove(e.getPlayer().getUniqueId());
        if (Bukkit.getOnlinePlayers().size() == 1 && speedcubingServer.restartable)
            speedcubingServer.restart();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerMoveEvent(PlayerMoveEvent e) {
        User.getUser(e.getPlayer()).lastMove = System.currentTimeMillis();
    }

}
