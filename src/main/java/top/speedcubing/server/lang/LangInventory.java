package top.speedcubing.server.lang;

import java.util.function.Consumer;
import top.speedcubing.lib.bukkit.events.inventory.ClickInventoryEvent;
import top.speedcubing.lib.bukkit.events.inventory.CloseInventoryEvent;
import top.speedcubing.lib.bukkit.inventory.InventoryBuilder;
import top.speedcubing.server.player.User;

public class LangInventory {
    private final InventoryBuilder[] inventory = new InventoryBuilder[LanguageSystem.langCount];

    public InventoryBuilder get(int lang) {
        return inventory[lang];
    }

    public LangInventory(int size, String unformatted, String... param) {
        this(null, size, Lang.of(unformatted), param);
    }

    public LangInventory(int size, Lang title, String... param) {
        this(null, size, title, param);
    }

    public LangInventory(User user, int size, String unformatted, String... param) {
        this(user, size, Lang.of(unformatted), param);
    }

    public LangInventory(User user, int size, Lang title, String... param) {
        title.param(param);
        if (user != null) {
            inventory[user.lang] = new InventoryBuilder(user.player, size, title.getString(user.lang));
        } else {
            for (int i = 0; i < LanguageSystem.langCount; i++) {
                inventory[i] = new InventoryBuilder(null, size, title.getString(i));
            }
        }
    }

    public LangInventory setCloseEvent(Consumer<CloseInventoryEvent> e) {
        for (InventoryBuilder b : inventory) {
            if (b != null) {
                b.setCloseEvent(e);
            }
        }
        return this;
    }

    public LangInventory setItem(LangItemStack stack, int... slots) {
        for (int i = 0; i < LanguageSystem.langCount; i++) {
            if (inventory[i] != null) {
                inventory[i].setItem(stack.get(i), slots);
            }
        }
        return this;
    }

    public LangInventory setItem(LangItemStack stack, int start, int end) {
        for (int i = 0; i < LanguageSystem.langCount; i++) {
            if (inventory[i] != null) {
                inventory[i].setItem(stack.get(i), start, end);
            }
        }
        return this;
    }

    public LangInventory setItem(LangItemStack stack, Consumer<ClickInventoryEvent> event, int... slots) {
        for (int i = 0; i < LanguageSystem.langCount; i++) {
            if (inventory[i] != null) {
                inventory[i].setItem(stack.get(i), event, slots);
            }
        }
        return this;
    }

    public LangInventory setItem(LangItemStack stack, Consumer<ClickInventoryEvent> event, int start, int end) {
        for (int i = 0; i < LanguageSystem.langCount; i++) {
            if (inventory[i] != null) {
                inventory[i].setItem(stack.get(i), event, start, end);
            }
        }
        return this;
    }

    public LangInventory setClickEvent(Consumer<ClickInventoryEvent> event, int... slots) {
        for (InventoryBuilder b : inventory) {
            if (b != null) {
                b.setClickEvent(event, slots);
            }
        }
        return this;
    }

    public LangInventory setClickEvent(Consumer<ClickInventoryEvent> event, int start, int end) {
        for (InventoryBuilder b : inventory) {
            if (b != null) {
                b.setClickEvent(event, start, end);
            }
        }
        return this;
    }

    public LangInventory setAllClickEvent(Consumer<ClickInventoryEvent> event) {
        for (InventoryBuilder b : inventory) {
            if (b != null) {
                b.setAllClickEvent(event);
            }
        }
        return this;
    }

    public LangInventory setClickable(boolean flag, int... slots) {
        for (InventoryBuilder b : inventory) {
            if (b != null) {
                b.setClickable(flag, slots);
            }
        }
        return this;
    }

    public LangInventory setClickable(boolean flag, int start, int end) {
        for (InventoryBuilder b : inventory) {
            if (b != null) {
                b.setClickable(flag, start, end);
            }
        }
        return this;
    }
}