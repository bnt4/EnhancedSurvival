package com.github.bnt4.enhancedsurvival.playtime;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.github.bnt4.enhancedsurvival.EnhancedSurvival;
import com.github.bnt4.enhancedsurvival.config.PlaytimeConfig;
import com.github.bnt4.enhancedsurvival.util.file.JsonFile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class PlaytimeManager {

    private final EnhancedSurvival plugin;
    private final PlaytimeConfig config;

    private final JsonFile file;

    private final Map<UUID, Long> playtimeSession;

    public PlaytimeManager(EnhancedSurvival plugin, PlaytimeConfig config) {
        this.plugin = plugin;
        this.config = config;

        this.file = new JsonFile(plugin.getUserDataFolder(), "playtime.json", new JsonObject());
        this.file.save();

        this.playtimeSession = new HashMap<>();

        plugin.registerCommand("playtime", new PlaytimeCommand(this));
        plugin.registerListener(new PlaytimeListener(this));

        for (Player all : Bukkit.getOnlinePlayers()) {
            if (this.playtimeSession.containsKey(all.getUniqueId())) {
                continue;
            }
            startSession(all.getUniqueId());
        }
    }

    public PlaytimeConfig getConfig() {
        return config;
    }

    public synchronized void saveActiveSessions() {
        Set<UUID> session = new HashSet<>(this.playtimeSession.keySet());
        plugin.getLogger().info("Saving " + session.size() + " playtime sessions...");
        for (UUID uniqueId : session) {
            savePlaytime(uniqueId);
        }
    }

    public synchronized void startSession(UUID uniqueId) {
        this.playtimeSession.put(uniqueId, System.currentTimeMillis());
    }

    public synchronized void savePlaytime(UUID uniqueId) {
        String uniqueIdString = uniqueId.toString();
        long newPlaytime;
        if (file.getData().has(uniqueIdString)) {
            newPlaytime = file.getData().get(uniqueIdString).getAsLong();
        } else {
            newPlaytime = 0;
        }
        if (this.playtimeSession.containsKey(uniqueId)) {
            newPlaytime += System.currentTimeMillis() - this.playtimeSession.get(uniqueId);
            this.playtimeSession.remove(uniqueId);
        }
        file.getData().add(uniqueIdString, new JsonPrimitive(newPlaytime));
        file.save();
    }

    public synchronized long getPlaytime(UUID uniqueId) {
        if (this.playtimeSession.containsKey(uniqueId)) {
            savePlaytime(uniqueId);
            startSession(uniqueId);
        }

        String uniqueIdString = uniqueId.toString();
        long playtime = 0;
        if (file.getData().has(uniqueIdString)) {
            playtime = file.getData().get(uniqueIdString).getAsLong();
        }
        if (this.playtimeSession.containsKey(uniqueId)) {
            playtime += System.currentTimeMillis() - this.playtimeSession.get(uniqueId);
        }
        return playtime;
    }

}
