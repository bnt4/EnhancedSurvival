package com.github.bnt4.enhancedsurvival.gravestone;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.github.bnt4.enhancedsurvival.EnhancedSurvival;
import com.github.bnt4.enhancedsurvival.config.GravestoneConfig;
import com.github.bnt4.enhancedsurvival.util.file.FileUtil;
import com.github.bnt4.enhancedsurvival.util.file.JsonFile;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class GravestoneManager {

    private final EnhancedSurvival plugin;

    private final GravestoneConfig config;

    private final NamespacedKey gravestoneIdKey;

    private final JsonFile file;

    public GravestoneManager(EnhancedSurvival plugin, GravestoneConfig config) {
        this.plugin = plugin;
        this.config = config;

        this.gravestoneIdKey = new NamespacedKey(plugin, "gravestone_id");

        this.file = new JsonFile(plugin.getUserDataFolder(), "gravestones.json", new JsonObject());
        this.file.save();

        if (this.config.isGravestones()) {
            plugin.registerListener(new GravestoneListener(plugin, this));

            if (this.config.removeGravestoneAfterMinutes() > 0) {
                Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new GravestoneRunnable(plugin, this), 20, 20*60); // 60 seconds in ticks
            }
        }
    }

    public GravestoneConfig getConfig() {
        return config;
    }

    public Set<Map.Entry<String, JsonElement>> getGravestoneEntries() {
        return this.file.getData().asMap().entrySet();
    }

    public NamespacedKey getGravestoneIdKey() {
        return gravestoneIdKey;
    }

    public synchronized UUID saveGravestone(GravestoneData data) {
        UUID gravestoneId = UUID.randomUUID();

        JsonObject gravestoneData = new JsonObject();
        gravestoneData.addProperty("player_uuid", data.playerUniqueId().toString());
        gravestoneData.add("location", FileUtil.locationToJson(data.location()));
        gravestoneData.addProperty("time", data.time().getTime());
        gravestoneData.addProperty("destroyed", data.destroyed());
        gravestoneData.add("items", FileUtil.itemsToJson(data.items()));
        gravestoneData.addProperty("level", data.level());

        this.file.getData().add(gravestoneId.toString(), gravestoneData);
        this.file.save();
        return gravestoneId;
    }

    public synchronized GravestoneData loadGravestone(UUID gravestoneId) {
        JsonElement jsonElement = this.file.getData().get(gravestoneId.toString());
        if (jsonElement == null || !jsonElement.isJsonObject()) {
            return null;
        }
        JsonObject gravestoneData = jsonElement.getAsJsonObject();
        return getGravestoneDataFromJsonObject(gravestoneData);
    }

    GravestoneData getGravestoneDataFromJsonObject(JsonObject gravestoneData) {
        return new GravestoneData(UUID.fromString(gravestoneData.get("player_uuid").getAsString()),
                FileUtil.locationFromJson(gravestoneData.getAsJsonObject("location")),
                new Date(gravestoneData.get("time").getAsLong()),
                gravestoneData.get("destroyed").getAsBoolean(),
                FileUtil.itemsFromJson(gravestoneData.getAsJsonArray("items")),
                gravestoneData.get("level").getAsInt());
    }

    public synchronized void markGravestoneAsDestroyed(UUID gravestoneId) {
        JsonElement jsonElement = this.file.getData().get(gravestoneId.toString());
        if (jsonElement == null || !jsonElement.isJsonObject()) {
            throw new RuntimeException("Gravestone with id " + gravestoneId.toString() + " not found");
        }
        JsonObject gravestoneData = jsonElement.getAsJsonObject();
        gravestoneData.addProperty("destroyed", true);
        this.file.getData().add(gravestoneId.toString(), gravestoneData);
        this.file.save();
    }

}
