package top.speedcubing.server.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import top.speedcubing.server.libs.User;
import top.speedcubing.server.speedcubingServer;

public class BackListen implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void FoodLevelChangeEvent(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void CreatureSpawnEvent(CreatureSpawnEvent e) {
        if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER && e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void InventoryOpenEvent(InventoryOpenEvent e) {
        InventoryType type = e.getInventory().getType();
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            if (type == InventoryType.BEACON || type == InventoryType.HOPPER || type == InventoryType.ANVIL)
                e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerDeathEvent(PlayerDeathEvent e) {
        e.setDeathMessage("");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerKickEvent(PlayerKickEvent e) {
        if (e.getReason().equals("disconnect.spam"))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerQuitEvent(PlayerQuitEvent e) {
        e.setQuitMessage("");
        Player player = e.getPlayer();
        User.usersByID.remove(User.getUser(player).id);
        User.usersByUUID.remove(player.getUniqueId());
        if (Bukkit.getOnlinePlayers().size() == 1 && speedcubingServer.restartable)
            speedcubingServer.restart();
    }
}
