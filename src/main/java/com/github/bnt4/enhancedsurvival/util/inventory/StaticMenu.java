package com.github.bnt4.enhancedsurvival.util.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

/**
 * A menu represents an inventory with functionality, defined once with the same items for every player
 */
public abstract class StaticMenu extends Menu {

    public StaticMenu(Component title, int rows) {
        super(title, rows);
    }

    public StaticMenu(Component title, InventoryType inventoryType) {
        super(title, inventoryType);
    }

    public void open(Player player) {
        player.openInventory(super.inventory);
    }

}
