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
            if (!openHistoryGui(player, targetName)) {
                player.sendMessage("§cThe player has never joined this server.");
                return true;
            }
        }
        return true;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().getTitle().equals("Punishment History")) {
            e.setCancelled(true);
            switch (e.getRawSlot()) {
                case 0:
                    if (e.isLeftClick()) {

                    } else if (e.isRightClick()) {

                    }
                    break;
                case 8:
                    e.getWhoClicked().closeInventory();
                    break;
            }
        }
    }
    private boolean openHistoryGui(Player sender, String name) {
        String[] data = Database.connection.select("name,profile_textures_value").from("playersdata").where("name='" + name + "'").getStringArray();
        if (data.length == 0) return false;

        Inventory inventory = Bukkit.createInventory(null, 9, "Punishment History");

        inventory.setItem(0, new ItemBuilder(Material.SKULL_ITEM).name( "§a" + data[0] + "'s punishment history")
                .addLore("§eLeft Click to view ban history","§eRight Click to view mute history").durability(3)
                .skullFromProfileValue(data[1]).build());
        for (int i = 1; i < 8; i++) {
            inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(7).name(" ").build());
        }
        inventory.setItem(8, new ItemBuilder(Material.BARRIER).name("§cCLOSE").build());
        sender.openInventory(inventory);
        return true;
    }
}
