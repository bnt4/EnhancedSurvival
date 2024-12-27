package com.github.bnt4.enhancedsurvival.backpack;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BackpackListener implements Listener {

    private final BackpackManager backpackManager;

    public BackpackListener(BackpackManager backpackManager) {
        this.backpackManager = backpackManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().isRightClick()
                && event.getItem() != null
                && event.getItem().getType() == Material.CHEST_MINECART
                && event.getItem().getItemMeta() != null
                && event.getItem().getItemMeta().getPersistentDataContainer().has(backpackManager.getBackpackIdKey(), PersistentDataType.STRING)) {
            event.setCancelled(true);

            if (!backpackManager.getConfig().isBackpacks()) {
                event.getPlayer().sendMessage(Component.text("Backpacks were disabled by the server admin", NamedTextColor.RED));
                return;
            }

            ItemStack item = event.getItem();
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();

            String backpackId = container.get(backpackManager.getBackpackIdKey(), PersistentDataType.STRING);
            if (backpackId == null) {
                event.getPlayer().sendMessage(Component.text("An error occurred", NamedTextColor.RED));
                return;
            }

            if (backpackId.length() == 0) {
                backpackId = backpackManager.createBackpackId();
                container.set(backpackManager.getBackpackIdKey(), PersistentDataType.STRING, backpackId);
                item.setItemMeta(meta);
            }

            try {
                new BackpackPlayerMenu(backpackManager, event.getPlayer(), item, backpackId).open();
            } catch (Exception ex) {
                event.getPlayer().sendMessage(Component.text("An error occurred while opening this backpack", NamedTextColor.RED));
                throw new RuntimeException("Error opening backpack " + backpackId + " (action by " + event.getPlayer().getName() + ") - This is most likely due to malformed data in the storage. If somebody modified the backpack data file manually, do not contact the plugin authors - this is your fault.", ex);
            }
        }
    }

}
