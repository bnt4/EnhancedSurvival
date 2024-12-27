package com.github.bnt4.enhancedsurvival.farmprotection;

import com.github.bnt4.enhancedsurvival.config.FarmProtectionConfig;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import com.github.bnt4.enhancedsurvival.EnhancedSurvival;

public class FarmProtectionListener implements Listener {

    public FarmProtectionListener(EnhancedSurvival plugin, FarmProtectionConfig config) {
        if (config.isFarmProtection()) {
            plugin.registerListener(this);
        }
    }

    @EventHandler
    public void onEntityInteract(EntityInteractEvent event) {
        if(event.getBlock().getType() == Material.FARMLAND) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.PHYSICAL) {
            if(event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.FARMLAND) {
                event.setCancelled(true);
            }
        }
    }

}
