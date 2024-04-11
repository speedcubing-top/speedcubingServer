package top.speedcubing.server.lang;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import top.speedcubing.lib.bukkit.inventory.ItemBuilder;
import top.speedcubing.server.player.User;

public class LangItemStack {
    ItemStack[] s;

    public LangItemStack(ItemBuilder item, String... s) {
        int r;
        if (s.length == 0) {
            r = 1;
        } else
            r = s.length;
        this.s = new ItemStack[r];

        ItemStack stack = item.build();
        for (int i = 0; i < r; i++) {
            ItemStack s2 = stack.clone();
            ItemMeta meta = s2.getItemMeta();
            if (s.length != 0)
                meta.setDisplayName(s[i]);
            s2.setItemMeta(meta);
            this.s[i] = s2;
        }
    }

    public ItemStack get(Player player) {
        if (s.length == 1)
            return s[0];
        return s[User.getUser(player).lang];
    }

    public ItemStack get(int lang) {
        if (s.length == 1)
            return s[0];
        return s[lang];
    }

    public ItemStack get(User user) {
        if (s.length == 1)
            return s[0];
        return s[user.lang];
    }
}
