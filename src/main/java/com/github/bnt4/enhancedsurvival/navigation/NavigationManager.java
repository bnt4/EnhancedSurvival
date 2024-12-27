package com.github.bnt4.enhancedsurvival.navigation;

import com.github.bnt4.enhancedsurvival.EnhancedSurvival;
import com.github.bnt4.enhancedsurvival.config.NavigationConfig;
import com.github.bnt4.enhancedsurvival.waypoint.WaypointManager;
import com.github.bnt4.enhancedsurvival.navigation.navigable.Navigable;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NavigationManager {

    private final EnhancedSurvival plugin;
    private final NavigationConfig config;
    private final WaypointManager waypointManager;

    private final Map<UUID, Navigable> navigating = new ConcurrentHashMap<>();

    public NavigationManager(EnhancedSurvival plugin, NavigationConfig config, WaypointManager waypointManager) {
        this.plugin = plugin;
        this.config = config;
        this.waypointManager = waypointManager;

        plugin.registerCommand("navigate", new NavigateCommand(this, waypointManager));
        plugin.registerCommand("nwg", new NwgCommand(waypointManager));
        plugin.registerCommand("nwp", new NwpCommand(waypointManager));

        if (this.config.isNavigation()) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new NavigationRunnable(this), 20, 20);
        }
    }

    public NavigationConfig getConfig() {
        return config;
    }

    public Set<Map.Entry<UUID, Navigable>> getNavigatingEntries() {
        return navigating.entrySet();
    }

    public boolean hasActiveNavigation(UUID uuid) {
        return this.navigating.containsKey(uuid);
    }

    public void cancelNavigation(UUID uuid) {
        if (hasActiveNavigation(uuid)) {
            this.navigating.remove(uuid);
        }
    }

    public void startNavigation(UUID uuid, Navigable navigable) {
        this.navigating.put(uuid, navigable);
    }

}
