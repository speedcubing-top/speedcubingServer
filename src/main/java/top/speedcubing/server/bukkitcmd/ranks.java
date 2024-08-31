package top.speedcubing.server.bukkitcmd;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.speedcubing.common.rank.Rank;
import top.speedcubing.lib.bukkit.inventory.InventoryBuilder;
import top.speedcubing.lib.bukkit.inventory.ItemBuilder;
import top.speedcubing.server.player.User;

public class ranks implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;
        open(player);
        return true;
    }

    private void open(Player player) {
        InventoryBuilder inv = new InventoryBuilder(54, "§6Ranks");
        ItemStack grayGlass = new ItemBuilder(Material.STAINED_GLASS_PANE).name(" ").durability(7).build();
        int rankWeight = Rank.rankByName.get(User.getUser(player).realRank).getWeight();
        ItemStack champ = new ItemBuilder(Material.WATCH).name("§3[Champ]").
                lore(
                        "§7- Chat Prefix: §3[Champ]",
                        "§7- §3KnockBackFFA §7Champ §aKill Effects.",
                        "§7- §3KnockBackFFA §7Champ §aProjectile Effects.",
                        "§7- /fly in §3lobby",
                        "§7Preview: " + getFormatName(player, "champ"),
                        "§7Coming Soon...",
                        "",
                        "§7Require: Lifetime/Daily §6Top 1 §7in §3KnockBackFFA"
                )
                .build();
        ItemBuilder vip = new ItemBuilder(Material.IRON_BLOCK).name("§d[VIP]");
        if (rankWeight < Rank.rankByName.get("vip").getWeight()) {
            vip.lore(
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
            );
        } else {
            vip.lore(
                    "§7- Chat Prefix: §d[VIP]",
                    "§7- §3KnockBackFFA §7All §aKill Effects.",
                    "§7- §3KnockBackFFA §7All §aProjectile Effects.",
                    "§7- /fly in §3lobby",
                    "§7- /reset in §3MLGRush",
                    "§7Preview: " + getFormatName(player, "vip"),
                    "§7Coming Soon...",
                    "",
                    "§7Price: §65$",
                    "§cPURCHASED!",
                    "§7Buy: §aspeedcubing.top/discord §7Open a ticket."
            );
        }

        ItemBuilder vipPlus = new ItemBuilder(Material.IRON_BLOCK).name("§d[VIP+]").glow();
        if (rankWeight < Rank.rankByName.get("vipplus").getWeight() && rankWeight != Rank.rankByName.get("vip").getWeight()) {
            vipPlus.lore(
                    "§7- Chat Prefix: §d[VIP+]",
                    "§7- §3KnockBackFFA §7All §aKill Effects.",
                    "§7- §3KnockBackFFA §7All §aProjectile Effects.",
                    "§7- §3KnockBackFFA §aCustom Armor Color.",
                    "§7- /fly in §3lobby",
                    "§7- /reset in §3MLGRush",
                    "§7Preview: " + getFormatName(player, "vipplus"),
                    "§7Coming Soon...",
                    "",
                    "§7Price: §610$",
                    "§7Buy: §aspeedcubing.top/discord §7Open a ticket."
            );
        } else if (rankWeight == Rank.rankByName.get("vip").getWeight()) {
            vipPlus.lore(
                    "§7- Chat Prefix: §d[VIP+]",
                    "§7- §3KnockBackFFA §7All §aKill Effects.",
                    "§7- §3KnockBackFFA §7All §aProjectile Effects.",
                    "§7- §3KnockBackFFA §aCustom Armor Color.",
                    "§7- /fly in §3lobby",
                    "§7- /reset in §3MLGRush",
                    "§7Preview: " + getFormatName(player, "vipplus"),
                    "§7Coming Soon...",
                    "",
                    "§7Price: §c§m10$§r §a5$",
                    "§7Upgrade Price: $5 (Others: $10)",
                    "§7Buy: §aspeedcubing.top/discord §7Open a ticket."
            );
        } else if (rankWeight >= Rank.rankByName.get("vipplus").getWeight()) {
            vipPlus.lore(
                    "§7- Chat Prefix: §d[VIP+]",
                    "§7- §3KnockBackFFA §7All §aKill Effects.",
                    "§7- §3KnockBackFFA §7All §aProjectile Effects.",
                    "§7- §3KnockBackFFA §aCustom Armor Color.",
                    "§7- /fly in §3lobby",
                    "§7- /reset in §3MLGRush",
                    "§7Preview: " + getFormatName(player, "vipplus"),
                    "§7Coming Soon...",
                    "",
                    "§7Price: §610$",
                    "§cPURCHASED!",
                    "§7Buy: §aspeedcubing.top/discord §7Open a ticket."
            );
        }
        ItemBuilder premium = new ItemBuilder(Material.GOLD_BLOCK).name("§6[Premium]");
        if (rankWeight < Rank.rankByName.get("premium").getWeight() && rankWeight != Rank.rankByName.get("vipplus").getWeight() && rankWeight != Rank.rankByName.get("vip").getWeight()) {
            premium.lore(
                    "§7- Chat Prefix: §6[Premium]",
                    "§7- §3KnockBackFFA §7All §aKill Effects.",
                    "§7- §3KnockBackFFA §7All §aProjectile Effects.",
                    "§7- §3KnockBackFFA §aCustom Armor Color.",
                    "§7- §3KnockBackFFA §aWeapon Selector.",
                    "§7- /fly in §3lobby",
                    "§7- /reset in §3MLGRush",
                    "§7- /skin <name> - §aChange Your Skin",
                    "§7Preview: " + getFormatName(player, "premium"),
                    "§7Coming Soon...",
                    "",
                    "§7Price: §615$",
                    "§7Buy: §aspeedcubing.top/discord §7Open a ticket."
            );
        } else if (rankWeight == Rank.rankByName.get("vip").getWeight()) {
            premium.lore(
                    "§7- Chat Prefix: §6[Premium]",
                    "§7- §3KnockBackFFA §7All §aKill Effects.",
                    "§7- §3KnockBackFFA §7All §aProjectile Effects.",
                    "§7- §3KnockBackFFA §aCustom Armor Color.",
                    "§7- §3KnockBackFFA §aWeapon Selector.",
                    "§7- /fly in §3lobby",
                    "§7- /reset in §3MLGRush",
                    "§7- /skin <name> - §aChange Your Skin",
                    "§7Preview: " + getFormatName(player, "premium"),
                    "§7Coming Soon...",
                    "",
                    "§7Price: §c§m15$§r §a10$",
                    "§7Upgrade Price: $10 (Others: $15)",
                    "§7Buy: §aspeedcubing.top/discord §7Open a ticket."
            );
        } else if (rankWeight == Rank.rankByName.get("vipplus").getWeight()) {
            premium.lore(
                    "§7- Chat Prefix: §6[Premium]",
                    "§7- §3KnockBackFFA §7All §aKill Effects.",
                    "§7- §3KnockBackFFA §7All §aProjectile Effects.",
                    "§7- §3KnockBackFFA §aCustom Armor Color.",
                    "§7- §3KnockBackFFA §aWeapon Selector.",
                    "§7- /fly in §3lobby",
                    "§7- /reset in §3MLGRush",
                    "§7- /skin <name> - §aChange Your Skin",
                    "§7Preview: " + getFormatName(player, "premium"),
                    "§7Coming Soon...",
                    "",
                    "§7Price: §c§m15$§r §a5$",
                    "§7Upgrade Price: $5 (Others: $15)",
                    "§7Buy: §aspeedcubing.top/discord §7Open a ticket."
            );
        } else if (rankWeight >= Rank.rankByName.get("premium").getWeight()) {
            premium.lore(
                    "§7- Chat Prefix: §6[Premium]",
                    "§7- §3KnockBackFFA §7All §aKill Effects.",
                    "§7- §3KnockBackFFA §7All §aProjectile Effects.",
                    "§7- §3KnockBackFFA §aCustom Armor Color.",
                    "§7- §3KnockBackFFA §aWeapon Selector.",
                    "§7- /fly in §3lobby",
                    "§7- /reset in §3MLGRush",
                    "§7- /skin <name> - §aChange Your Skin",
                    "§7Preview: " + getFormatName(player, "premium"),
                    "§7Coming Soon...",
                    "",
                    "§7Price: §615$",
                    "§cPURCHASED!",
                    "§7Buy: §aspeedcubing.top/discord §7Open a ticket."
            );
        }
        ItemStack premiumPlus = new ItemBuilder(Material.GOLD_BLOCK).name("§6[Premium+]").glow().
                lore(
                        "§7- Chat Prefix: §6[Premium+]",
                        "§7- §3KnockBackFFA §7All §aKill Effects.",
                        "§7- §3KnockBackFFA §7All §aProjectile Effects.",
                        "§7- §3KnockBackFFA §aCustom Armor Color.",
                        "§7- §3KnockBackFFA §aWeapon Selector.",
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
        ItemStack yt = new ItemBuilder(Material.WOOL).durability(10).name("§5[YT]").glow().
                lore(
                        "§7- Chat Prefix: §5[YT]",
                        "§7- §3KnockBackFFA §7All §aKill Effects.",
                        "§7- §3KnockBackFFA §7All §aProjectile Effects.",
                        "§7- §3KnockBackFFA §aCustom Armor Color.",
                        "§7- §3KnockBackFFA §aWeapon Selector.",
                        "§7- /fly in §3lobby",
                        "§7- /reset in §3MLGRush",
                        "§7- /skin <name> - §aChange Your Skin",
                        "§7- /nick - §aHide Your Real Name In Game",
                        "§7Preview: " + getFormatName(player, "yt"),
                        "§7Coming Soon...",
                        "",
                        "§7Require: §c1000 Subscribers",
                        "§7Note: Other social media application conditions can be found at speedcubing.top/discord.",
                        "§7Application: §aspeedcubing.top/discord §7Open a ticket."
                ).build();
        ItemStack ytPlus = new ItemBuilder(Material.WOOL).durability(14).name("§4[YT+]").glow().
                lore(
                        "§7- Chat Prefix: §4[YT+]",
                        "§7- §3KnockBackFFA §7All §aKill Effects.",
                        "§7- §3KnockBackFFA §7All §aProjectile Effects.",
                        "§7- §3KnockBackFFA §aCustom Armor Color.",
                        "§7- §3KnockBackFFA §aWeapon Selector.",
                        "§7- /fly in §3lobby",
                        "§7- /reset in §3MLGRush",
                        "§7- /skin <name> - §aChange Your Skin",
                        "§7- /nick - §aHide Your Real Name In Game (Custom Name)",
                        "§7Preview: " + getFormatName(player, "ytplus"),
                        "§7Coming Soon...",
                        "",
                        "§7Require: §c5000 Subscribers",
                        "§7Note: Other social media application conditions can be found at speedcubing.top/discord.",
                        "§7Application: §aspeedcubing.top/discord §7Open a ticket."
                ).build();
        ItemStack builder = new ItemBuilder(Material.EMERALD_BLOCK).name("§3[Builder]").
                lore(
                        "§7- Chat Prefix: §3[Builder]",
                        "§7- §3All",
                        "§7Preview: " + getFormatName(player, "builder"),
                        "",
                        "§7Apply: Not Available"
                ).build();
        ItemStack helper = new ItemBuilder(Material.getMaterial(351)).durability(4).name("§9[Helper]").
                lore(
                        "§7- Chat Prefix: §9[Helper]",
                        "§7- §3All",
                        "§7Preview: " + getFormatName(player, "helper"),
                        "",
                        "§7Apply: Not Available"
                ).build();
        ItemStack mod = new ItemBuilder(Material.REDSTONE).name("§c[Mod]").
                lore(
                        "§7- Chat Prefix: §c[Mod]",
                        "§7- §3All",
                        "§7Preview: " + getFormatName(player, "mod"),
                        "",
                        "§7Apply: Not Available"
                ).build();
        ItemStack dev = new ItemBuilder(Material.LAPIS_BLOCK).name("§b[Dev]").
                lore(
                        "§7- Chat Prefix: §b[Dev]",
                        "§7- §3All",
                        "§7Preview: " + getFormatName(player, "developer"),
                        "",
                        "§7Apply: Not Available"
                ).build();
        ItemStack admin = new ItemBuilder(Material.REDSTONE_BLOCK).name("§4[Admin]").
                lore(
                        "§7- Chat Prefix: §4[Admin]",
                        "§7- §3All",
                        "§7Preview: " + getFormatName(player, "admin"),
                        "",
                        "§7Apply: Not Available"
                ).build();
        ItemStack owner = new ItemBuilder(Material.BARRIER).name("§4[Owner]").
                lore(
                        "§7- Chat Prefix: §4[Owner]",
                        "§7- §3All",
                        "§7Preview: " + getFormatName(player, "owner"),
                        "",
                        "§7Apply: Not Available"
                ).build();
        //more ranks
        for (int i = 0; i < 9; i++) {
            inv.setItem(grayGlass, i);
            inv.setItem(grayGlass, i + 45);
        }
        for (int i = 0; i < 6; i++) {
            inv.setItem(grayGlass, i * 9);
            inv.setItem(grayGlass, i * 9 + 8);
        }
        inv.setItem(champ, 10);
        inv.setItem(vip.build(), 11);
        inv.setItem(vipPlus.build(), 12);
        inv.setItem(premium.build(), 13);
        inv.setItem(premiumPlus, 14);
        inv.setItem(yt, 15);
        inv.setItem(ytPlus, 16);
        inv.setItem(builder, 19);
        inv.setItem(helper, 20);
        inv.setItem(mod, 21);
        inv.setItem(dev, 22);
        inv.setItem(admin, 23);
        inv.setItem(owner, 24);

        inv.setClickable(false);
        player.openInventory(inv.getInventory());
    }

    private String getFormatName(Player player, String rank) {
        User user = User.getUser(player);
        String prefix = Rank.getFormat(rank, 0).getPrefix();
        return prefix + user.realName;
    }
}
