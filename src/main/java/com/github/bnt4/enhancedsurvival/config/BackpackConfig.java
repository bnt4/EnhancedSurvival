package com.github.bnt4.enhancedsurvival.config;

import org.bukkit.Material;

import java.util.Map;

public interface BackpackConfig {

    /**
     * @return true if backpack system is enabled
     */
    boolean isBackpacks();

    /**
     * @return an array with 3 strings, representing the 3 lines in a crafting table
     */
    String[] getBackpackCraftingPattern();

}
