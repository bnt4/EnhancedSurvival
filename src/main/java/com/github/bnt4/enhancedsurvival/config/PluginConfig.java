package com.github.bnt4.enhancedsurvival.config;

import com.github.bnt4.enhancedsurvival.EnhancedSurvival;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStreamReader;
import java.util.Map;

public class PluginConfig implements Config {

    private final EnhancedSurvival plugin;

    private boolean shouldCheckForUpdates;

    private boolean isFarmProtection;

    private boolean isPlaytime;

    private boolean isTrash;

    private boolean isCustomJoinQuitMessages;
    private String customJoinMessage = "";
    private String customQuitMessage = "";

    private boolean isCustomChatFormat;
    private String customChatFormat = "";

    private boolean isBackpacks;
    private boolean isBackpackItemNameAsInventoryTitle;
    private boolean isBackpackAllowInventoryClickToOpen;

    private boolean isGravestones;
    private int removeGravestoneAfterMinutes;

    private boolean isWaypoints;
    private boolean isSetWaypointOnDeath;
    private boolean isGlobalWaypoints;
    private boolean isRequirePermissionToModifyGlobalWaypoints;

    private boolean isNavigationToWaypoints;
    private boolean isNavigationToPlayers;
    private boolean isNavigationToCoordinates;
    private int destinationReachedBlocks;

    private boolean isMotd;
    private String motdWorldName;
    private String motdLineOne;
    private String motdLineTwo;

    public PluginConfig(EnhancedSurvival plugin) {
        this.plugin = plugin;
    }

    public void reloadConfig() {
        plugin.saveDefaultConfig();

        updateConfigVersion();

        shouldCheckForUpdates = plugin.getConfig().getBoolean("config.check-for-updates");

        isFarmProtection = plugin.getConfig().getBoolean("config.farm-protection");

        isPlaytime = plugin.getConfig().getBoolean("config.playtime");

        isTrash = plugin.getConfig().getBoolean("config.trash");

        isCustomJoinQuitMessages = plugin.getConfig().getBoolean("config.custom-join-quit-messages.enabled");
        if (isCustomJoinQuitMessages) {
            customJoinMessage = plugin.getConfig().getString("config.custom-join-quit-messages.join");
            customQuitMessage = plugin.getConfig().getString("config.custom-join-quit-messages.quit");
        }

        isCustomChatFormat = plugin.getConfig().getBoolean("config.custom-chat-format.enabled");
        if (isCustomChatFormat) {
            customChatFormat = plugin.getConfig().getString("config.custom-chat-format.format");
        }

        isBackpacks = plugin.getConfig().getBoolean("config.backpacks.enabled");
        isBackpackItemNameAsInventoryTitle = plugin.getConfig().getBoolean("config.backpacks.backpack-item-name-as-inventory-title");
        isBackpackAllowInventoryClickToOpen = plugin.getConfig().getBoolean("config.backpacks.allow-inventory-click-to-open");

        isGravestones = plugin.getConfig().getBoolean("config.gravestones.enabled");
        removeGravestoneAfterMinutes = plugin.getConfig().getInt("config.gravestones.remove-after-minutes");

        isWaypoints = plugin.getConfig().getBoolean("config.waypoints.enabled");
        isSetWaypointOnDeath = plugin.getConfig().getBoolean("config.waypoints.set-waypoint-on-death");
        isGlobalWaypoints = plugin.getConfig().getBoolean("config.waypoints.global-waypoints.enabled");
        isRequirePermissionToModifyGlobalWaypoints = plugin.getConfig().getBoolean("config.waypoints.global-waypoints.require-permission-to-modify");

        isNavigationToWaypoints = plugin.getConfig().getBoolean("config.navigation.to-waypoints");
        isNavigationToPlayers = plugin.getConfig().getBoolean("config.navigation.to-players");
        isNavigationToCoordinates = plugin.getConfig().getBoolean("config.navigation.to-coordinates");
        destinationReachedBlocks = plugin.getConfig().getInt("config.navigation.destination-reached-blocks");
        if (destinationReachedBlocks < 0 || destinationReachedBlocks > 20) {
            throw new RuntimeException("Invalid number " + destinationReachedBlocks + " for 'config.navigation.destination-reached-blocks': must be between (including) 0 and (including) 20");
        }

        isMotd = plugin.getConfig().getBoolean("config.motd.enabled");
        motdWorldName = plugin.getConfig().getString("config.motd.world-name");
        motdLineOne = plugin.getConfig().getString("config.motd.line-one");
        motdLineTwo = plugin.getConfig().getString("config.motd.line-two");
    }

    /**
     * Updates the configuration version of the plugin by comparing the current config version with the default (potentially new) config version.
     * If the current version is outdated, it copies over missing or updated values from the default configuration file.
     * This method preserves most of the current configuration settings, except for values that are no longer valid.
     */
    private void updateConfigVersion() {
        try {
            int currentConfigVersion = plugin.getConfig().getInt("config.version", 0);
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("config.yml")));
            int defaultConfigVersion = defaultConfig.getInt("config.version", 0);

            if (currentConfigVersion != 0 && currentConfigVersion >= defaultConfigVersion) {
                return;
            }

            plugin.getLogger().info("Updating from config version " + currentConfigVersion + " to " + defaultConfigVersion + "...");

            Map<String, Object> currentConfigValues = plugin.getConfig().getValues(true);
            plugin.saveResource("config.yml", true);

            plugin.reloadConfig();

            for (Map.Entry<String, Object> entry : currentConfigValues.entrySet()) {
                if (entry.getValue() instanceof MemorySection) {
                    continue;
                }
                if (entry.getKey().equals("config.version")) {
                    continue;
                }
                if (!plugin.getConfig().isSet(entry.getKey())) {
                    continue;
                }
                plugin.getConfig().set(entry.getKey(), entry.getValue());
            }

            plugin.saveConfig();

            plugin.getLogger().info("Updated config version to " + plugin.getConfig().getInt("config.version"));
        } catch (Exception ex) {
            plugin.getLogger().severe("Error updating config version: " + ex.getMessage());
        }
    }


    @Override
    public boolean shouldCheckForUpdates() {
        return shouldCheckForUpdates;
    }

    @Override
    public boolean isFarmProtection() {
        return isFarmProtection;
    }

    @Override
    public boolean isPlaytime() {
        return isPlaytime;
    }

    @Override
    public boolean isTrash() {
        return isTrash;
    }

    @Override
    public boolean isCustomJoinQuitMessages() {
        return isCustomJoinQuitMessages;
    }

    @Override
    public String getCustomJoinMessage() {
        return customJoinMessage;
    }

    @Override
    public String getCustomQuitMessage() {
        return customQuitMessage;
    }

    @Override
    public boolean isCustomChatFormat() {
        return isCustomChatFormat;
    }

    @Override
    public String getCustomChatFormat() {
        return customChatFormat;
    }

    @Override
    public boolean isBackpacks() {
        return isBackpacks;
    }

    @Override
    public String[] getBackpackCraftingPattern() {
        String[] pattern = new String[3];
        pattern[0] = plugin.getConfig().getString("config.backpacks.crafting-recipe.top-row");
        pattern[1] = plugin.getConfig().getString("config.backpacks.crafting-recipe.center-row");
        pattern[2] = plugin.getConfig().getString("config.backpacks.crafting-recipe.bottom-row");
        return pattern;
    }

    @Override
    public boolean isBackpackItemNameAsInventoryTitle() {
        return isBackpackItemNameAsInventoryTitle;
    }

    @Override
    public boolean isBackpackAllowInventoryClickToOpen() {
        return isBackpackAllowInventoryClickToOpen;
    }

    @Override
    public boolean isRottenFleshToLeather() {
        return plugin.getConfig().getBoolean("config.recipes.rotten-flesh-to-leather");
    }

    @Override
    public boolean isQuartzBlockToQuartz() {
        return plugin.getConfig().getBoolean("config.recipes.quartz-block-to-quartz");
    }

    @Override
    public boolean isLogsToSticks() {
        return plugin.getConfig().getBoolean("config.recipes.logs-to-sticks");
    }

    @Override
    public boolean isGravestones() {
        return isGravestones;
    }

    @Override
    public int removeGravestoneAfterMinutes() {
        return removeGravestoneAfterMinutes;
    }

    @Override
    public boolean isWaypoints() {
        return isWaypoints;
    }

    @Override
    public boolean isSetWaypointOnDeath() {
        return isSetWaypointOnDeath;
    }

    @Override
    public boolean isGlobalWaypoints() {
        return isGlobalWaypoints;
    }

    @Override
    public boolean isRequirePermissionToModifyGlobalWaypoints() {
        return isRequirePermissionToModifyGlobalWaypoints;
    }

    @Override
    public boolean isNavigation() {
        return isNavigationToWaypoints() || isNavigationToPlayers() || isNavigationToCoordinates();
    }

    @Override
    public boolean isNavigationToWaypoints() {
        return isWaypoints && isNavigationToWaypoints;
    }

    @Override
    public boolean isNavigationToPlayers() {
        return isNavigationToPlayers;
    }

    @Override
    public boolean isNavigationToCoordinates() {
        return isNavigationToCoordinates;
    }

    @Override
    public int destinationReachedBlocks() {
        return destinationReachedBlocks;
    }

    @Override
    public boolean isMotd() {
        return isMotd;
    }

    @Override
    public String getMotdWorldName() {
        return motdWorldName;
    }

    @Override
    public String getMotdLineOne() {
        return motdLineOne;
    }

    @Override
    public String getMotdLineTwo() {
        return motdLineTwo;
    }

}
