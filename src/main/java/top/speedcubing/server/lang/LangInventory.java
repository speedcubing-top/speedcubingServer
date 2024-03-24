package top.speedcubing.server.lang;

import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import top.speedcubing.lib.bukkit.inventory.InventoryBuilder;
import top.speedcubing.lib.events.inventory.ClickInventoryEvent;
import top.speedcubing.lib.events.inventory.CloseInventoryEvent;
import top.speedcubing.server.player.User;

public class LangInventory {
    private final InventoryBuilder[] s;
    public Inventory get(Player player) {
        return get(User.getUser(player));
    }

    public Inventory get(User user) {
        return get(user.lang);
    }

    public Inventory get(int lang) {
        return s[lang].getInventory();
    }

    public LangInventory(int size, String... s) {
        this.s = new InventoryBuilder[s.length];
        for (int i = 0; i < s.length; i++) {
            this.s[i] = new InventoryBuilder(size, s[i]);
        }
    }

    public LangInventory(InventoryBuilder... s) {
        this.s = s;
    }

    public LangInventory deleteOnClose(boolean flag) {
        for (InventoryBuilder b : s)
            b.deleteOnClose(flag);
        return this;
    }

    public LangInventory setCloseInventory(Consumer<CloseInventoryEvent> e) {
        for (InventoryBuilder b : s)
            b.setCloseInventory(e);
        return this;
    }

    public LangInventory setItem(LangItemStack stack, int... slots) {
        for (int i = 0; i < s.length; i++)
            s[i].setItem(stack.get(i), slots);
        return this;
    }

    public LangInventory setItem(LangItemStack stack, int start, int end) {
        for (int i = 0; i < s.length; i++)
            s[i].setItem(stack.get(i), start, end);
        return this;
    }

    public LangInventory setItem(LangItemStack stack, Consumer<ClickInventoryEvent> event, int... slots) {
        for (int i = 0; i < s.length; i++)
            s[i].setItem(stack.get(i), event, slots);
        return this;
    }

    public LangInventory setItem(LangItemStack stack, Consumer<ClickInventoryEvent> event, int start, int end) {
        for (int i = 0; i < s.length; i++)
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
