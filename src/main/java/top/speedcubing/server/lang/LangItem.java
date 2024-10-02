package top.speedcubing.server.lang;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import top.speedcubing.lib.bukkit.inventory.ItemBuilder;
import top.speedcubing.server.player.User;

public class LangItem {
    private final ItemBuilder[] items = new ItemBuilder[LanguageSystem.langCount];

    public LangItem(Material material) {
        for (int i = 0; i < LanguageSystem.langCount; i++) {
            items[i] = new ItemBuilder(material);
        }
    }

    public ItemStack get(Player player) {
        return get(User.getUser(player));
    }

    public ItemStack get(User user) {
        return get(user.lang);
    }

    public ItemStack get(int lang) {
        return items[lang].build();
    }

    //from itembuilder
    public LangItem amount(int amount) {
        for (ItemBuilder i : items) {
            i.amount(amount);
        }
        return this;
    }

    public LangItem durability(int s) {
        for (ItemBuilder i : items) {
            i.durability(s);
        }
        return this;
    }

    public LangItem ench(Enchantment enchantment, int level) {
        for (ItemBuilder i : items) {
            i.ench(enchantment, level);
        }
        return this;
    }


    public LangItem name(String unformatted, String... param) {
        name(Lang.of(unformatted, param));
        return this;
    }

    public LangItem name(Lang lang) {
        for (int i = 0; i < LanguageSystem.langCount; i++) {
            items[i].name(lang.getString(i));
        }
        return this;
    }

    public LangItem owner(String name) {
        for (ItemBuilder i : items) {
            i.owner(name);
        }
        return this;
    }

    public LangItem potion(PotionEffectType type, int duration, int amplifier) {
        for (ItemBuilder i : items) {
            i.potion(type, duration, amplifier);
        }
        return this;
    }

    public LangItem unBreak() {
        for (ItemBuilder i : items) {
            i.unBreak();
        }
        return this;
    }

    public LangItem lore(Lang... lore) {
        for (int i = 0; i < LanguageSystem.langCount; i++) {
            ArrayList<String> arr = new ArrayList<>();
            for (Lang l : lore) {
                arr.add(l.getString(i));
            }
            items[i].lore(arr);
        }
        return this;
    }

    public LangItem addLore(Lang... lore) {
        for (int i = 0; i < LanguageSystem.langCount; i++) {
            ArrayList<String> arr = new ArrayList<>();
            for (Lang l : lore) {
                arr.add(l.getString(i));
            }
            items[i].addLore(arr);
        }
        return this;
    }

    public LangItem hideAttr() {
        for (ItemBuilder i : items) {
            i.hideAttr();
        }
        return this;
    }

    public LangItem hideEnch() {
        for (ItemBuilder i : items) {
            i.hideEnch();
        }
        return this;
    }

    public LangItem hidePotion() {
        for (ItemBuilder i : items) {
            i.hidePotion();
        }
        return this;
    }

    public LangItem hideUnbreak() {
        for (ItemBuilder i : items) {
            i.hideUnbreak();
        }
        return this;
    }

    public LangItem glow() {
        for (ItemBuilder i : items) {
            i.glow();
        }
        return this;
    }

    public LangItem skullBase64(String textureBase64) {
        for (ItemBuilder i : items) {
            i.skullBase64(textureBase64);
        }
        return this;
    }

    public LangItem skullFromURL(String url) {
        for (ItemBuilder i : items) {
            i.skullFromURL(url);
        }
        return this;
    }

    public LangItem skullFromProfileValue(String profileValueBase64) {
        for (ItemBuilder i : items) {
            i.skullFromProfileValue(profileValueBase64);
        }
        return this;
    }
}