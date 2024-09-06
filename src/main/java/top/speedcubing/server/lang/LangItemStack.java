package top.speedcubing.server.lang;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import top.speedcubing.lib.bukkit.inventory.ItemBuilder;
import top.speedcubing.server.player.User;

public class LangItemStack {
    private final ItemStack[] s;

    public LangItemStack(Material material, Lang m, Lang... lore) {
        this(new ItemBuilder(material), m, lore);
    }

    public LangItemStack(ItemBuilder item, Lang m, Lang... lore) {
        this.s = new ItemStack[LanguageSystem.langCount];

        ItemStack stack = item.build();
        for (int i = 0; i < LanguageSystem.langCount; i++) {
            ItemStack s2 = stack.clone();
            ItemMeta meta = s2.getItemMeta();

            if (m != null) {
                meta.setDisplayName(m.getString(i));
            }

            s2.setItemMeta(meta);
            this.s[i] = s2;
        }

        setLore(lore);
    }

    public void setLore(Lang... lore) {
        for (int i = 0; i < LanguageSystem.langCount; i++) {
            ItemMeta meta = s[i].getItemMeta();

            ArrayList<String> arr = new ArrayList<>();
            for (Lang l : lore) {
                arr.add(l.getString(i));
            }

            meta.setLore(arr);

            s[i].setItemMeta(meta);
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