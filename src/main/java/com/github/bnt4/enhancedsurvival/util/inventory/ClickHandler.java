package com.github.bnt4.enhancedsurvival.util.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface ClickHandler {

    /**
     * Method that gets called by the inventory click listener of the API if the inventory has been clicked.
     * @param event event
     * @param player player who clicked
     */
    void handleClick(InventoryClickEvent event, Player player);

}
