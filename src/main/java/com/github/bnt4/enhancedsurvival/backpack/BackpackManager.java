package com.github.bnt4.enhancedsurvival.backpack;

import com.google.gson.*;
import com.github.bnt4.enhancedsurvival.EnhancedSurvival;
import com.github.bnt4.enhancedsurvival.config.BackpackConfig;
import com.github.bnt4.enhancedsurvival.util.file.JsonFile;
import com.github.bnt4.enhancedsurvival.util.item.ItemSerialization;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class BackpackManager {

    private final EnhancedSurvival plugin;

    private final BackpackConfig config;

    private final NamespacedKey backpackIdKey;
    private final BackpackRecipeStaticMenu backpackRecipeMenu;

    private final Map<String, ItemStack[]> itemCache;

    private final JsonFile file;

    public BackpackManager(EnhancedSurvival plugin, BackpackConfig config) {
        this.plugin = plugin;
        this.config = config;

        this.backpackIdKey = new NamespacedKey(plugin, "backpack_id");
        this.itemCache = new HashMap<>();

        this.file = new JsonFile(plugin.getUserDataFolder(), "backpacks.json", new JsonObject());
        this.file.save();

        plugin.registerCommand("backpack", new BackpackCommand(this));

        if (config.isBackpacks()) {
            BackpackRecipeSettings recipeSettings = this.getBackpackRecipeSettings();
            String[] pattern = recipeSettings.pattern();
            Map<Material, Character> items = recipeSettings.items();

            ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "backpack"), getRawBackpack());
            recipe.shape(pattern);
            Map<Character, Material> swappedItems = new HashMap<>();
            for (Map.Entry<Material, Character> entry : items.entrySet()) {
                recipe.setIngredient(entry.getValue(), entry.getKey());
                swappedItems.put(entry.getValue(), entry.getKey());
            }
            Bukkit.addRecipe(recipe);

            this.backpackRecipeMenu = new BackpackRecipeStaticMenu(pattern, swappedItems);

            plugin.registerListener(new BackpackListener(this));
        } else {
            this.backpackRecipeMenu = null;
        }
    }

    private BackpackRecipeSettings getBackpackRecipeSettings() {
        String[] rawPattern = config.getBackpackCraftingPattern();
        String[] finalPattern = new String[rawPattern.length];

        char character = 'A';
        Map<Material, Character> items = new HashMap<>();

        for (int i = 0; i < rawPattern.length; i++) {
            String[] materialNames = rawPattern[i].split(" ");
            StringBuilder patternRow = new StringBuilder();
            for (String materialName : materialNames) {
                if (materialName.equals("_")) {
                    patternRow.append(" ");
                    continue;
                }
                try {
                    Material material = Material.valueOf(materialName.toUpperCase());
                    if (!items.containsKey(material)) {
                        items.put(material, character);
                        character++;
                    }
                    patternRow.append(items.get(material));
                } catch (IllegalArgumentException ex) {
                    plugin.getLogger().severe("Invalid material '" + materialName + "' while parsing backpack recipe");
                    throw new IllegalArgumentException("Invalid material: " + materialName);
                }
            }
            finalPattern[i] = patternRow.toString();
        }

        return new BackpackRecipeSettings(finalPattern, items);
    }

    public BackpackConfig getConfig() {
        return config;
    }

    public void openBackpackRecipeMenu(Player player) {
        this.backpackRecipeMenu.open(player);
    }

    public ItemStack getRawBackpack() {
        ItemStack itemStack = new ItemStack(Material.CHEST_MINECART);
        itemStack.editMeta(m -> {
            m.displayName(Component.text("Backpack", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
            // value is empty string until first interaction with the backpack
            m.getPersistentDataContainer().set(this.backpackIdKey, PersistentDataType.STRING, "");
        });
        return itemStack;
    }

    public String createBackpackId() {
        return UUID.randomUUID().toString();
    }

    public synchronized ItemStack[] getItems(String id) {
        if (itemCache.containsKey(id)) {
            return itemCache.get(id);
        }

        JsonElement element = file.getData().get(id);
        ItemStack[] itemStacks = new ItemStack[9 * 4];
        if (element == null) {
            return itemStacks;
        }

        JsonArray items = element.getAsJsonArray();
        for (int i = 0; i < items.size(); i++) {
            JsonElement item = items.get(i);
            itemStacks[i] = item.isJsonNull() ? null : ItemSerialization.deserializeItemStack(item.getAsString());
        }

        itemCache.put(id, itemStacks);
        return itemStacks;
    }

    public synchronized void setItems(String id, ItemStack[] itemStacks) {
        JsonObject items = new JsonObject();
        JsonArray array = new JsonArray(itemStacks.length);
        for (ItemStack itemStack : itemStacks) {
            array.add(itemStack == null ? JsonNull.INSTANCE : new JsonPrimitive(ItemSerialization.serializeItemStack(itemStack)));
        }
        file.getData().add(id, array);
        file.save();
        itemCache.put(id, itemStacks);
    }

    public NamespacedKey getBackpackIdKey() {
        return backpackIdKey;
    }

}
