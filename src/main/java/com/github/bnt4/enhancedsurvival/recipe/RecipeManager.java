package com.github.bnt4.enhancedsurvival.recipe;

import com.github.bnt4.enhancedsurvival.EnhancedSurvival;
import com.github.bnt4.enhancedsurvival.config.RecipeConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class RecipeManager {

    public RecipeManager(EnhancedSurvival plugin, RecipeConfig config) {
        if (config.isRottenFleshToLeather()) {
            Bukkit.addRecipe(new FurnaceRecipe(new NamespacedKey(plugin, "rotten_flesh_to_leather"),
                    new ItemStack(Material.LEATHER), Material.ROTTEN_FLESH, 0.1f, 200));
        }

        if (config.isQuartzBlockToQuartz()) {
            ShapelessRecipe quartzRecipe = new ShapelessRecipe(new NamespacedKey(plugin, "quartz_to_quartz_block"), new ItemStack(Material.QUARTZ, 4));
            quartzRecipe.addIngredient(Material.QUARTZ_BLOCK);
            Bukkit.addRecipe(quartzRecipe);
        }

        if (config.isLogsToSticks()) {
            for (Material material : Tag.LOGS.getValues()) {
                ShapedRecipe r = new ShapedRecipe(new NamespacedKey(plugin, material.name().toLowerCase() + "_to_sticks"), new ItemStack(Material.STICK, 16));
                r.shape(" W", " W");
                r.setIngredient('W', material);
                Bukkit.addRecipe(r);
            }
        }
    }

}
