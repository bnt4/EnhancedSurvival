package com.github.bnt4.enhancedsurvival.config;

public interface BackpackConfig {

    /**
     * @return true if backpack system is enabled
     */
    boolean isBackpacks();

    /**
     * @return an array with 3 strings, representing the 3 lines in a crafting table
     */
    String[] getBackpackCraftingPattern();

    /**
     * @return true if the backpack item name should be used as inventory title
     */
    boolean isBackpackItemNameAsInventoryTitle();

    /**
     * @return true if a backpack should be opened when clicking in while another backpack is open
     */
    boolean isBackpackAllowInventoryClickToOpen();

}
