package com.github.bnt4.enhancedsurvival.util.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public abstract class Menu implements InventoryHolder {

    protected final Inventory inventory;

    /**
     * true if the {@link org.bukkit.event.inventory.InventoryClickEvent} should be canceled if this menu is open
     */
    public boolean cancelled = true;

    /**
     * true if inventories that aren't a menu (for example the player inventory) should be ignored by the {@link org.bukkit.event.inventory.InventoryClickEvent} if this menu open (the top inventory)
     */
    public boolean ignoreNonMenus = true;

    /**
     * true if items that are null should be ignored by the {@link org.bukkit.event.inventory.InventoryClickEvent}
     */
    public boolean ignoreNullItems = true;


    public Menu(Component title, int rows) {
        this.inventory = Bukkit.createInventory(this, rows * 9, title);
    }

    public Menu(Component title, InventoryType inventoryType) {
        this.inventory = Bukkit.createInventory(this, inventoryType, title);
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

}
