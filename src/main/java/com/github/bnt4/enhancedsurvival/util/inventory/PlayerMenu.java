package com.github.bnt4.enhancedsurvival.util.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

/**
 * A menu represents an inventory with functionality, tied to a specific player and intended for using it once until it is closed
 */
public abstract class PlayerMenu extends Menu {

    protected final Player player;

    public PlayerMenu(Player player, Component title, int rows) {
        super(title, rows);
        this.player = player;
    }

    public PlayerMenu(Player player, Component title, InventoryType inventoryType) {
        super(title, inventoryType);
        this.player = player;
    }

    /**
     * Opens the inventory for the player
     */
    public void open() {
        this.player.openInventory(this.inventory);
    }

}
