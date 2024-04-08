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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import top.speedcubing.common.database.Database;
import top.speedcubing.lib.bukkit.inventory.ItemBuilder;
import top.speedcubing.server.player.User;

public class history implements CommandExecutor, Listener {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (args.length == 0) {
            openHistoryGui(player);
            return true;
        }
        if (args.length == 1) {
            String targetName = args[0];

        }
        return false;
    }
    private void openHistoryGui(Player player) {
        openHistoryGui(player, player.getName());
    }
    private void openHistoryGui(Player player, String name) {
        if (!Database.connection.exist("playersdata","name='" + name + "'")) {
            player.sendMessage("§cThe player has never joined this server.");
            return;
        }
        Inventory inventory = Bukkit.createInventory(null,54,"Punishment History");

        inventory.setItem(0,new ItemBuilder(Material.SKULL).owner(name).build());
    }
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory().getTitle().equals("Punishment History")) {
            e.setCancelled(true);
        }
    }
}
