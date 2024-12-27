package com.github.bnt4.enhancedsurvival.updater;

import com.github.bnt4.enhancedsurvival.EnhancedSurvival;
import com.github.bnt4.enhancedsurvival.config.UpdaterConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.logging.Logger;

public class UpdaterManager {

    public UpdaterManager(EnhancedSurvival plugin, UpdaterConfig config) {
        // Modrinth project id
        final String projectId = "wZPsPEHH";
        if (config.shouldCheckForUpdates()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> checkForUpdates(plugin.getLogger(), plugin.getDescription().getVersion(), projectId));
        }
    }

    public void checkForUpdates(Logger logger, String currentVersionNumber, String projectId) {
        try {
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getUrlString(projectId)))
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .header("Accept", "application/json")
                    .header("User-Agent", "bnt4/EnhancedSurvival/" + currentVersionNumber)
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            try(final HttpClient httpClient = HttpClient.newHttpClient()) {
                final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                if (response.statusCode() != 200) {
                    logger.info("Failed to check for updates: Expected status 200, but got status " + response.statusCode() + " (" + response.body() + ")");
                    return;
                }

                JsonElement element = JsonParser.parseString(response.body());
                if (!element.isJsonArray()) {
                    logger.info("Failed to check for updates: Expected json array as response");
                    return;
                }

                JsonArray versionElements = element.getAsJsonArray();
                if (versionElements.size() == 0) {
                    logger.info("Failed to check for updates: No versions (matching this minecraft version) found");
                    return;
                }

                JsonObject latestVersion = versionElements.get(0).getAsJsonObject();

                if (!latestVersion.has("version_number")) {
                    logger.info("Failed to check for updates: Expected response to have \"version_number\"");
                    return;
                }
                String latestVersionNumber = latestVersion.get("version_number").getAsString();

                if (currentVersionNumber.equals(latestVersionNumber)) {
                    logger.info("You are running the latest version for MC " + Bukkit.getMinecraftVersion());
                    return;
                }

                if (!latestVersion.has("id")) {
                    logger.info("Failed to check for updates: Expected response to have \"id\"");
                    return;
                }
                String latestVersionId = latestVersion.get("id").getAsString();

                logger.info("Outdated version! Latest for MC " + Bukkit.getMinecraftVersion() + " is " + latestVersionNumber + ", you are running " + currentVersionNumber);
                logger.info("You can download it here: https://modrinth.com/project/" + projectId + "/version/" + latestVersionId);
            }
        } catch (Exception ex) {
            logger.info("Failed to check for updates (" + ex.getClass().getName() + "): " + ex.getMessage());
        }
    }

    private String getUrlString(String projectId) {
        final String apiBaseUrl = "https://api.modrinth.com";
        final String apiProjectVersionEndpoint = "/v2/project/" + projectId + "/version";

        // url to get latest version of the plugin for the current game version
        return apiBaseUrl + apiProjectVersionEndpoint + "?loaders=" + URLEncoder.encode("[\"paper\"]", StandardCharsets.UTF_8)
                + "&game_versions=" + URLEncoder.encode("[\"" + Bukkit.getMinecraftVersion() + "\"]", StandardCharsets.UTF_8)
                + "&version_type=release";
    }

}
