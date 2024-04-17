package top.speedcubing.server.commands.staff;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import top.speedcubing.common.database.Database;
import top.speedcubing.lib.bukkit.inventory.ItemBuilder;

public class historyUi implements CommandExecutor, Listener {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (args.length == 0) {
            openHistoryGui(player, player.getName());
            return true;
        }
        if (args.length == 1) {
            String targetName = args[0];
            if (!openHistoryGui(player,targetName)) {
                player.sendMessage("§cThe player has never joined this server.");
                return true;
            }
        }
        return true;
    }
    private boolean openHistoryGui(Player player, String name) {
        if (!Database.connection.exist("playersdata","name='" + name + "'")) return false;

        Inventory inventory = Bukkit.createInventory(null,54,"Punishment History");

        inventory.setItem(0,new ItemBuilder(Material.SKULL).owner(name).build());
        player.openInventory(inventory);
        return true;
    }
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory().getTitle().equals("Punishment History")) {
            e.setCancelled(true);
        }
    }
}
