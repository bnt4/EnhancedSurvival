package com.github.bnt4.enhancedsurvival.backpack;

import com.github.bnt4.enhancedsurvival.util.inventory.ClickHandler;
import com.github.bnt4.enhancedsurvival.util.inventory.CloseHandler;
import com.github.bnt4.enhancedsurvival.util.inventory.PlayerMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class BackpackPlayerMenu extends PlayerMenu implements ClickHandler, CloseHandler {

    private final BackpackManager backpackManager;

    private final ItemStack backpackItem;
    private final String backpackId;

    public BackpackPlayerMenu(BackpackManager backpackManager, Player player, ItemStack backpackItem, String backpackId) {
        super(player, getBackpackInventoryName(backpackItem, backpackManager.getConfig().isBackpackItemNameAsInventoryTitle()), 4);
        this.backpackManager = backpackManager;

        this.backpackItem = backpackItem;
        this.backpackId = backpackId;
        ItemStack[] content = backpackManager.getItems(backpackId);

        this.cancelled = false;
        this.ignoreNonMenus = false;
        this.ignoreNullItems = false;

        if (content != null) {
            this.inventory.setContents(content);
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event, Player player) {
        if (event.getClick() == ClickType.NUMBER_KEY) {
            event.setCancelled(true);
            player.sendActionBar(Component.text("Please do not use hotkeys in backpacks", NamedTextColor.RED));
            return;
        }
        if (event.getCurrentItem() != null
                && event.getCurrentItem().getType() == Material.CHEST_MINECART
                && event.getCurrentItem().getItemMeta() != null
                && event.getCurrentItem().getItemMeta().getPersistentDataContainer().has(this.backpackManager.getBackpackIdKey(), PersistentDataType.STRING)) {
            event.setCancelled(true);

            if (this.backpackManager.getConfig().isBackpackAllowInventoryClickToOpen()) {
                this.backpackManager.openBackpackMenu(player, event.getCurrentItem());
            }
        }
    }

    @Override
    public void handleClose(InventoryCloseEvent event, Player player) {
        ItemMeta meta = this.backpackItem.getItemMeta();
        if (meta == null) {
            player.sendMessage(Component.text("An error occurred", NamedTextColor.RED));
            return;
        }
        this.backpackManager.setItems(this.backpackId, event.getInventory().getContents());
    }

    private static Component getBackpackInventoryName(ItemStack item, boolean isBackpackItemNameAsInventoryTitle) {
        if (isBackpackItemNameAsInventoryTitle && item.getItemMeta() != null) {
            Component displayName = item.getItemMeta().displayName();
            if (displayName != null) {
                return displayName.style(Style.empty());
            }
        }
        return Component.text("Backpack");
    }

}
