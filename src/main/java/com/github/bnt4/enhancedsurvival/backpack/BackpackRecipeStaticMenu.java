package com.github.bnt4.enhancedsurvival.backpack;

import com.github.bnt4.enhancedsurvival.util.inventory.StaticMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class BackpackRecipeStaticMenu extends StaticMenu {

    public BackpackRecipeStaticMenu(String[] pattern, Map<Character, Material> items) {
        super(Component.text("Backpack Crafting"), InventoryType.DROPPER);

        char[] patternChars = String.join("", pattern).toCharArray();
        inventory.setContents(createItemStackContents(items, patternChars));
    }

    private ItemStack[] createItemStackContents(Map<Character, Material> items, char[] pattern) {
        ItemStack[] contents = new ItemStack[9];
        for (int i = 0; i < pattern.length; i++) {
            contents[i] = pattern[i] == ' ' ? null : new ItemStack(items.get(pattern[i]));
        }
        return contents;
    }

}
