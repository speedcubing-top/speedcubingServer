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
                lore(
                        "§7- Chat Prefix: §d[VIP]",
                        "§7- §3KnockBackFFA §7All §aKill Effects.",
                        "§7- §3KnockBackFFA §7All §aProjectile Effects.",
                        "§7- /fly in §3lobby",
                        "§7- /reset in §3MLGRush",
                        "§7Preview: " + getFormatName(player, "vip"),
                        "§7Coming Soon...",
                        "",
                        "§7Price: §65$",
                        "§7Buy: §aspeedcubing.top/discord §7Open a ticket."
                )
                .build();
        ItemStack vipPlus = new ItemBuilder(Material.IRON_BLOCK).name("§d[VIP+]").glow().
                lore(
                        "§7- Chat Prefix: §d[VIP+]",
                        "§7- §3KnockBackFFA §7All §aKill Effects.",
                        "§7- §3KnockBackFFA §7All §aProjectile Effects.",
                        "§7- /fly in §3lobby",
                        "§7- /reset in §3MLGRush",
                        "§7Preview: " + getFormatName(player, "vipplus"),
                        "§7Coming Soon...",
                        "",
                        "§7Price: §610$",
                        "§7Buy: §aspeedcubing.top/discord §7Open a ticket."
                )

                .build();
        ItemStack premium = new ItemBuilder(Material.GOLD_BLOCK).name("§6[Premium]").
                lore(
                        "§7- Chat Prefix: §6[Premium]",
                        "§7- §3KnockBackFFA §7All §aKill Effects.",
                        "§7- §3KnockBackFFA §7All §aProjectile Effects.",
                        "§7- /fly in §3lobby",
                        "§7- /reset in §3MLGRush",
                        "§7- /skin <name> - §aChange Your Skin",
                        "§7Preview: " + getFormatName(player, "premium"),
                        "§7Coming Soon...",
                        "",
                        "§7Price: §615$",
                        "§7Buy: §aspeedcubing.top/discord §7Open a ticket."
                )
                .build();
        ItemStack premiumPlus = new ItemBuilder(Material.GOLD_BLOCK).name("§6[Premium+]").glow().
                lore(
                        "§7- Chat Prefix: §6[Premium+]",
                        "§7- §3KnockBackFFA §7All §aKill Effects.",
                        "§7- §3KnockBackFFA §7All §aProjectile Effects.",
                        "§7- /fly in §3lobby",
                        "§7- /reset in §3MLGRush",
                        "§7- /skin <name> - §aChange Your Skin",
                        "§7- /nick - §aHide Your Real Name In Game",
                        "§7Preview: " + getFormatName(player, "premiumplus"),
                        "§7Coming Soon...",
                        "",
                        "§7Price: §63$/month",
                        "§7Note: You need have §6[Premium] §7to buy this rank.",
                        "§7Buy: §aspeedcubing.top/discord §7Open a ticket."
                )
                .build();
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
