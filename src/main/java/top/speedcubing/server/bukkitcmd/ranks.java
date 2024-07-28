package top.speedcubing.server.bukkitcmd;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.speedcubing.common.rank.Rank;
import top.speedcubing.common.rank.RankFormat;
import top.speedcubing.lib.bukkit.inventory.InventoryBuilder;
import top.speedcubing.lib.bukkit.inventory.ItemBuilder;
import top.speedcubing.server.player.User;
import top.speedcubing.server.utils.RankSystem;

public class ranks implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;
        open(player);
        return true;
    }
    private void open(Player player) {
        InventoryBuilder inv = new InventoryBuilder(54, "§bSpeedcubing Server §aRanks");
        ItemStack grayGlass = new ItemBuilder(Material.STAINED_GLASS_PANE).name(" ").durability(7).build();
        ItemStack vip = new ItemBuilder(Material.IRON_BLOCK).name("§d[VIP]").
                lore("§7- do some shit.", getFormatName(player, "vip")).
                build();
        ItemStack vipPlus = new ItemBuilder(Material.IRON_BLOCK).name("§d[VIP+]").glow().
                lore("§7- do some shit", getFormatName(player, "vipplus")).
                build();
        ItemStack premium = new ItemBuilder(Material.GOLD_BLOCK).name("§6[Premium]").
                lore("§7- do some shit", getFormatName(player, "premium")).
                build();
        ItemStack premiumPlus = new ItemBuilder(Material.GOLD_BLOCK).name("§6[Premium+]").glow().
                lore("§7- do some shit", getFormatName(player, "premiumplus")).
                build();
        //more ranks
        for (int i = 0; i < 9; i++) {
            inv.setItem(grayGlass, i);
            inv.setItem(grayGlass, i + 45);
        }
        for (int i = 0; i < 6; i++) {
            inv.setItem(grayGlass, i * 9);
            inv.setItem(grayGlass, i * 9 + 8);
        }
        inv.setItem(vip,10);
        inv.setItem(vipPlus, 11);
        inv.setItem(premium, 12);
        inv.setItem(premiumPlus, 13);

        inv.setClickable(false);
        player.openInventory(inv.getInventory());
    }
    private String getFormatName(Player player, String rank) {
        User user = User.getUser(player);
        String prefix = Rank.getFormat(rank,0).getPrefix();
        return prefix + user.realName;
    }
}
