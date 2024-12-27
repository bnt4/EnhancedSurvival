package com.github.bnt4.enhancedsurvival.backpack;

import org.bukkit.Material;

import java.util.Map;

public record BackpackRecipeSettings(String[] pattern, Map<Material, Character> items) {
}
