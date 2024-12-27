package com.github.bnt4.enhancedsurvival.gravestone;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.UUID;

public record GravestoneData(UUID playerUniqueId, Location location, Date time, boolean destroyed, ItemStack[] items, int level) {

    public GravestoneData(UUID playerUniqueId, Location location, ItemStack[] items, int level) {
        this(playerUniqueId, location, new Date(), false, items, level);
    }

}