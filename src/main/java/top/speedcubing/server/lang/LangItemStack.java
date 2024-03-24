package top.speedcubing.server.lang;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import top.speedcubing.lib.bukkit.inventory.ItemBuilder;
import top.speedcubing.server.player.User;

public class LangItemStack {
    ItemStack[] s;

    public LangItemStack(ItemBuilder item, String... s) {
        this.s = new ItemStack[s.length];
        ItemStack stack = item.build();
        for (int i = 0; i < s.length; i++) {
            ItemStack s2 = stack.clone();
            ItemMeta meta = s2.getItemMeta();
            meta.setDisplayName(s[i]);
            s2.setItemMeta(meta);
            this.s[i] = s2;
        }
    }
    public ItemStack get(Player player) {
        return get(User.getUser(player));
    }
    public ItemStack get(int lang) {
        return s[lang];
    }

    public ItemStack get(User user) {
        return get(user.lang);
    }
}
