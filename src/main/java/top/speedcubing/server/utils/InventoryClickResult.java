package top.speedcubing.server.utils;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class InventoryClickResult {
    SlotType type;
    boolean isOpeningChest = false;

    enum SlotType {
        INVENTORY, BAG, QUICKBAR, ARMOR, CRAFTING, RESULT, OTHER, FAIL
    }

    public static InventoryClickResult from(InventoryClickEvent e) {
        return new InventoryClickResult(e);
    }

    private InventoryClickResult(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) {
            type = SlotType.FAIL;
            return;
        }
        if (e.getSlotType() == InventoryType.SlotType.RESULT) {
            type = SlotType.RESULT;
            return;
        }

        if (e.getSlotType() == InventoryType.SlotType.CRAFTING) {
            type = SlotType.CRAFTING;
            return;
        }

        if (e.getSlotType() == InventoryType.SlotType.ARMOR) {
            type = SlotType.ARMOR;
            return;
        }

        if (e.getInventory().getName().equals("container.crafting")) {
            if (e.getSlotType() == InventoryType.SlotType.CONTAINER) {
                type = SlotType.BAG;
                return;
            }
            if (e.getSlotType() == InventoryType.SlotType.QUICKBAR) {
                type = SlotType.QUICKBAR;
                return;
            }
        }

        if (e.getInventory().getName().endsWith("chest")) {
            isOpeningChest = true;
            if (e.getClickedInventory().getName().endsWith("chest")) {
                type = SlotType.INVENTORY;
                return;
            }
            if (e.getClickedInventory().getName().equals("container.inventory")) {
                if (e.getSlotType() == InventoryType.SlotType.CONTAINER) {
                    type = SlotType.BAG;
                    return;
                }
                if (e.getSlotType() == InventoryType.SlotType.QUICKBAR) {
                    type = SlotType.QUICKBAR;
                    return;
                }
                return;
            }
        }

        type = SlotType.OTHER;
    }
}
