package top.speedcubing.server.lang;

import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import top.speedcubing.lib.bukkit.events.inventory.ClickInventoryEvent;
import top.speedcubing.lib.bukkit.events.inventory.CloseInventoryEvent;
import top.speedcubing.lib.bukkit.inventory.InventoryBuilder;
import top.speedcubing.server.player.User;

public class LangInventory {
    public final InventoryBuilder[] s;

    public InventoryBuilder get(Player player) {
        return get(User.getUser(player));
    }

    public InventoryBuilder get(User user) {
        return get(user.lang);
    }

    public InventoryBuilder get(int lang) {
        return s[lang];
    }

    public LangInventory(int size, String unformatted, String... param) {
        this(null, size, Lang.of(unformatted), param);
    }

    public LangInventory(int size, Lang title, String... param) {
        this(null, size, title, param);
    }

    public LangInventory(Player player, int size, String unformatted, String... param) {
        this(player, size, Lang.of(unformatted), param);
    }

    public LangInventory(Player player, int size, Lang title, String... param) {
        title.param(param);
        this.s = new InventoryBuilder[LanguageSystem.langCount];
        for (int i = 0; i < LanguageSystem.langCount; i++) {
            this.s[i] = new InventoryBuilder(player, size, title.getString(i));
        }
    }

    public LangInventory setCloseInventory(Consumer<CloseInventoryEvent> e) {
        for (InventoryBuilder b : s)
            b.setCloseInventory(e);
        return this;
    }

    public LangInventory setItem(LangItemStack stack, int... slots) {
        for (int i = 0; i < LanguageSystem.langCount; i++)
            s[i].setItem(stack.get(i), slots);
        return this;
    }

    public LangInventory setItem(LangItemStack stack, int start, int end) {
        for (int i = 0; i < LanguageSystem.langCount; i++)
            s[i].setItem(stack.get(i), start, end);
        return this;
    }

    public LangInventory setItem(LangItemStack stack, Consumer<ClickInventoryEvent> event, int... slots) {
        for (int i = 0; i < LanguageSystem.langCount; i++)
            s[i].setItem(stack.get(i), event, slots);
        return this;
    }

    public LangInventory setItem(LangItemStack stack, Consumer<ClickInventoryEvent> event, int start, int end) {
        for (int i = 0; i < LanguageSystem.langCount; i++)
            s[i].setItem(stack.get(i), event, start, end);
        return this;
    }

    public LangInventory setClickEvent(Consumer<ClickInventoryEvent> event, int... slots) {
        for (InventoryBuilder b : s)
            b.setClickEvent(event, slots);
        return this;
    }

    public LangInventory setClickEvent(Consumer<ClickInventoryEvent> event, int start, int end) {
        for (InventoryBuilder b : s)
            b.setClickEvent(event, start, end);
        return this;
    }

    public LangInventory setAllClickEvent(Consumer<ClickInventoryEvent> event) {
        for (InventoryBuilder b : s)
            b.setAllClickEvent(event);
        return this;
    }

    public LangInventory setClickable(boolean flag, int... slots) {
        for (InventoryBuilder b : s)
            b.setClickable(flag, slots);
        return this;
    }

    public LangInventory setClickable(boolean flag, int start, int end) {
        for (InventoryBuilder b : s)
            b.setClickable(flag, start, end);
        return this;
    }
}