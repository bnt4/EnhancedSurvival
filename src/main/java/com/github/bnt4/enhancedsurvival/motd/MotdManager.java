package com.github.bnt4.enhancedsurvival.motd;

import com.github.bnt4.enhancedsurvival.EnhancedSurvival;
import com.github.bnt4.enhancedsurvival.config.MotdConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class MotdManager {

    private final EnhancedSurvival plugin;

    private final MotdConfig config;

    private World motdWorld;

    public MotdManager(EnhancedSurvival plugin, MotdConfig config) {
        this.plugin = plugin;
        this.config = config;

        if (!this.config.isMotd()) {
            this.motdWorld = null;
            return;
        }

        this.plugin.registerListener(new MotdListener(this));

        World configuredMotdWorld = Bukkit.getWorld(this.config.getMotdWorldName());

        if (configuredMotdWorld == null) {
            for (World world : Bukkit.getWorlds()) {
                if (world.getEnvironment() != World.Environment.NORMAL) {
                    continue;
                }
                this.motdWorld = world;
            }
            plugin.getLogger().warning("Invalid motd world '" + this.config.getMotdWorldName() + "', using '" + this.motdWorld.getName() + "' instead");
            return;
        }

        this.motdWorld = configuredMotdWorld;
    }

    public Component getFullMotd() {
        return replacePlaceholders(this.config.getMotdLineOne())
                .appendNewline()
                .append(replacePlaceholders(this.config.getMotdLineTwo()));
    }

    public Component replacePlaceholders(String text) {
        return MiniMessage.miniMessage().deserialize(text,
                Placeholder.unparsed("v-version", Bukkit.getMinecraftVersion()),
                Placeholder.unparsed("v-time", this.motdWorld.isDayTime() ? "Day" : "Night"),
                Placeholder.unparsed("v-weather", this.motdWorld.isClearWeather() ? "Sunny" : "Rainy"));
    }

}
