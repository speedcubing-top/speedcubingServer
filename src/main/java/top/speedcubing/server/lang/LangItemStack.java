package top.speedcubing.server.lang;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import top.speedcubing.lib.bukkit.inventory.ItemBuilder;
import top.speedcubing.server.player.User;

public class LangItemStack {
    ItemStack[] s;

    public LangItemStack(Material material, LangMessage m) {
        this(new ItemBuilder(material), m.s);
    }

    public LangItemStack(ItemBuilder item, LangMessage m) {
        this(item, m.s);
    }

    public LangItemStack(Material material, String langMessageID) {
        this(new ItemBuilder(material), LangMessage.load(langMessageID));
    }

    public LangItemStack(ItemBuilder item, String langMessageID) {
        this(item, LangMessage.load(langMessageID));
    }

    public LangItemStack(ItemBuilder item) {
        this(item, new String[0]);
    }

    public LangItemStack(Material material) {
        this(new ItemBuilder(material), new String[0]);
    }

    public LangItemStack(ItemBuilder item, String... s) {
        int r = Math.max(s.length, 1);
        this.s = new ItemStack[r];

        ItemStack stack = item.build();
        for (int i = 0; i < r; i++) {
            ItemStack s2 = stack.clone();
            ItemMeta meta = s2.getItemMeta();

            if (s.length == 1)
                meta.setDisplayName(s[0]);
            else if (s.length != 0)
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
