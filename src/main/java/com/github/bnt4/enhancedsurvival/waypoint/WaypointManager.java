package com.github.bnt4.enhancedsurvival.waypoint;

import com.google.gson.*;
import com.github.bnt4.enhancedsurvival.EnhancedSurvival;
import com.github.bnt4.enhancedsurvival.config.WaypointConfig;
import com.github.bnt4.enhancedsurvival.util.file.FileUtil;
import com.github.bnt4.enhancedsurvival.util.file.JsonFile;
import org.bukkit.Location;

import java.util.*;

public class WaypointManager {

    private final EnhancedSurvival plugin;
    private final WaypointConfig config;

    private final JsonFile file;

    public WaypointManager(EnhancedSurvival plugin, WaypointConfig config) {
        this.plugin = plugin;
        this.config = config;

        this.file = new JsonFile(plugin.getUserDataFolder(), "waypoints.json", new JsonObject());
        this.file.save();

        plugin.registerCommand("waypoint", new WaypointCommand(this));

        if (this.config.isSetWaypointOnDeath()) {
            plugin.registerListener(new WaypointListener(plugin, this));
        }
    }

    public WaypointConfig getConfig() {
        return config;
    }

    /**
     * Retrieves a list of all waypoints for a specific player or global waypoints if the UUID is null.
     *
     * @param uniqueId the {@link UUID} of the player or {@code null} for global waypoints.
     * @return a {@link List} of {@link Waypoint} objects for the specified player or global waypoints.
     */
    public synchronized List<Waypoint> getWaypoints(UUID uniqueId) {
        String uniqueIdString = getUniqueIdString(uniqueId);
        List<Waypoint> waypoints = new ArrayList<>();
        JsonObject waypointsData = getWaypointsData(uniqueId);
        if (waypointsData == null) {
            return waypoints;
        }
        // Waypoints are stored as key-value pairs where the key is the waypoint name
        for (Map.Entry<String, JsonElement> entry : waypointsData.entrySet()) {
            try {
                Location location = FileUtil.locationFromJson(entry.getValue().getAsJsonObject());
                waypoints.add(new Waypoint(entry.getKey(), location));
            } catch (Exception ex) {
                // Log errors, continue loading other waypoints
                this.plugin.getLogger().severe("Failed to load waypoint '" + entry.getKey() + "' for uuid " + uniqueIdString + ": " + ex.getMessage());
            }
        }
        return waypoints;
    }

    /**
     * Retrieves a list of all waypoint names for a specific player or global waypoints if the UUID is null.
     *
     * @param uniqueId the {@link UUID} of the player or {@code null} for global waypoints.
     * @return a {@link List} of strings for the specified player or global waypoints.
     */
    public synchronized List<String> getWaypointNames(UUID uniqueId) {String uniqueIdString = getUniqueIdString(uniqueId);
        JsonObject waypointsData = getWaypointsData(uniqueId);
        if (waypointsData == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(waypointsData.keySet());
    }

    /**
     * Retrieves a specific waypoint by name for a player or global waypoint if the UUID is null.
     *
     * @param uniqueId the {@link UUID} of the player or {@code null} for global waypoints.
     * @param name the name of the waypoint to retrieve.
     * @return the {@link Waypoint} with the specified name.
     * @throws NullPointerException if the waypoint does not exist.
     * @throws RuntimeException if loading the waypoint fails due to an exception.
     */
    public synchronized Waypoint getWaypoint(UUID uniqueId, String name) {
        if (!existsWaypoint(uniqueId, name)) {
            throw new NullPointerException("Cannot get waypoint: Waypoint '" + name + "' for uuid " + uniqueId + " doesn't exist");
        }
        try {
            JsonElement waypointData = getWaypointsData(uniqueId).get(name);
            return new Waypoint(name, FileUtil.locationFromJson(waypointData.getAsJsonObject()));
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load waypoint '" + name + "' for uuid " + uniqueId + ": " + ex.getMessage(), ex);
        }
    }

    /**
     * Sets a new waypoint with the given name and location for a player or as a global waypoint if the UUID is null.
     *
     * @param uniqueId the {@link UUID} of the player or {@code null} for global waypoints.
     * @param name the name of the waypoint.
     * @param location the {@link Location} of the waypoint.
     */
    public synchronized void setWaypoint(UUID uniqueId, String name, Location location) {
        String uniqueIdString = getUniqueIdString(uniqueId);
        JsonObject waypointsData;
        if (file.getData().has(uniqueIdString)) {
            waypointsData = file.getData().getAsJsonObject(uniqueIdString);
        } else {
            waypointsData = new JsonObject();
        }
        waypointsData.add(name, FileUtil.locationToJson(location));
        file.getData().add(uniqueIdString, waypointsData);
        file.save();
    }

    /**
     * Deletes a waypoint by name for a player or from global waypoints if the UUID is null.
     *
     * @param uniqueId the {@link UUID} of the player or {@code null} for global waypoints.
     * @param name the name of the waypoint to delete.
     * @throws NullPointerException if the waypoint does not exist.
     */
    public synchronized void deleteWaypoint(UUID uniqueId, String name) {
        if (!existsWaypoint(uniqueId, name)) {
            throw new NullPointerException("Cannot delete waypoint: Waypoint '" + name + "' for uuid " + uniqueId + " doesn't exist");
        }
        String uniqueIdString = getUniqueIdString(uniqueId);
        JsonObject waypointsData = getWaypointsData(uniqueId);
        waypointsData.remove(name);
        file.getData().add(uniqueIdString, waypointsData);
        file.save();
    }

    /**
     * Renames an existing waypoint for a player or global waypoint if the UUID is null.
     *
     * @param uniqueId the {@link UUID} of the player or {@code null} for global waypoints.
     * @param oldName the current name of the waypoint.
     * @param newName the new name for the waypoint.
     * @throws NullPointerException if the waypoint with the old name does not exist.
     */
    public synchronized void renameWaypoint(UUID uniqueId, String oldName, String newName) {
        if (!existsWaypoint(uniqueId, oldName)) {
            throw new NullPointerException("Cannot rename waypoint: Waypoint '" + oldName + "' for uuid " + uniqueId + " doesn't exist");
        }
        String uniqueIdString = getUniqueIdString(uniqueId);
        JsonObject waypointsData = getWaypointsData(uniqueId);
        JsonObject waypoint = waypointsData.getAsJsonObject(oldName);
        waypointsData.remove(oldName);
        waypointsData.add(newName, waypoint);
        file.getData().add(uniqueIdString, waypointsData);
        file.save();
    }

    /**
     * Checks if a waypoint with the given name exists for a player or as a global waypoint if the UUID is null.
     *
     * @param uniqueId the {@link UUID} of the player or {@code null} for global waypoints.
     * @param name the name of the waypoint.
     * @return {@code true} if the waypoint exists, {@code false} otherwise.
     */
    public synchronized boolean existsWaypoint(UUID uniqueId, String name) {
        JsonObject waypointsData = getWaypointsData(uniqueId);
        return waypointsData != null && waypointsData.has(name);
    }

    /**
     * Retrieves the waypoint data (as a JSON object) for a player or global waypoint if the UUID is null.
     *
     * @param uniqueId the {@link UUID} of the player or {@code null} for global waypoints.
     * @return the {@link JsonObject} containing all waypoints for the player or global waypoints, or {@code null} if none exist.
     */
    public synchronized JsonObject getWaypointsData(UUID uniqueId) {
        String uniqueIdString = getUniqueIdString(uniqueId);
        return file.getData().has(uniqueIdString) ? file.getData().getAsJsonObject(uniqueIdString) : null;
    }

    /**
     * Converts the UUID of the player to a string. If the UUID is {@code null}, returns "global".
     *
     * @param uniqueId the {@link UUID} of the player or {@code null} for global waypoints.
     * @return the string representation of the UUID, or "global" if the UUID is {@code null}.
     */
    public String getUniqueIdString(UUID uniqueId) {
        return uniqueId == null ? "global" : uniqueId.toString();
    }

}
