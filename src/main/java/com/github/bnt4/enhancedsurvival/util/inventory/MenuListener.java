package com.github.bnt4.enhancedsurvival.util.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * Provides functionality for {@link Menu} and executes {@link ClickHandler} and {@link CloseHandler}
 */
public class MenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof Menu menu) {
            event.setCancelled(menu.cancelled);

            if (!(menu instanceof ClickHandler clickHandler)) {
                return;
            }

            if (event.getClickedInventory() == null) {
                return;
            }

            if (menu.ignoreNonMenus && !(event.getClickedInventory().getHolder() instanceof Menu)) {
                return;
            }

            if (menu.ignoreNullItems && event.getCurrentItem() == null) {
                return;
            }

            clickHandler.handleClick(event, (Player) event.getWhoClicked());
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof CloseHandler closeHandler) {
            closeHandler.handleClose(event, (Player) event.getPlayer());
        }
    }

}
