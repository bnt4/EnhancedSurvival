package com.github.bnt4.enhancedsurvival.gravestone;

import com.google.gson.JsonElement;
import com.github.bnt4.enhancedsurvival.EnhancedSurvival;
import com.github.bnt4.enhancedsurvival.util.location.LocationUtil;
import com.github.bnt4.enhancedsurvival.util.time.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GravestoneRunnable implements Runnable {

    private final EnhancedSurvival plugin;
    private final GravestoneManager gravestoneManager;

    public GravestoneRunnable(EnhancedSurvival plugin, GravestoneManager gravestoneManager) {
        this.plugin = plugin;
        this.gravestoneManager = gravestoneManager;
    }

    @Override
    public void run() {
        for (Map.Entry<String, JsonElement> entry : this.gravestoneManager.getGravestoneEntries()) {
            try {
                final GravestoneData gravestoneData = this.gravestoneManager.getGravestoneDataFromJsonObject(entry.getValue().getAsJsonObject());
                if (gravestoneData.destroyed()) {
                    continue;
                }
                long time = gravestoneData.time().getTime();
                if (time + TimeUnit.MINUTES.toMillis(this.gravestoneManager.getConfig().removeGravestoneAfterMinutes()) >= System.currentTimeMillis()) {
                    continue;
                }
                Block block = gravestoneData.location().getBlock();
                if (block.getType() != Material.CAMPFIRE) {
                    continue;
                }
                Bukkit.getScheduler().runTask(plugin, () -> {
                    this.gravestoneManager.markGravestoneAsDestroyed(UUID.fromString(entry.getKey()));
                    block.setType(Material.AIR);
                    this.plugin.getLogger().info("Gravestone " + entry.getKey() + " at " + LocationUtil.formatLocation(gravestoneData.location()) + " has been destroyed automatically because " + this.gravestoneManager.getConfig().removeGravestoneAfterMinutes() + " minutes passed");
                    Player player = Bukkit.getPlayer(gravestoneData.playerUniqueId());
                    if (player != null) {
                        player.sendMessage(Component.text("The gravestone containing your items from your death at "
                                        + TimeUtil.formatDate(gravestoneData.time()) + " has been destroyed because "
                                        + this.gravestoneManager.getConfig().removeGravestoneAfterMinutes() + " minutes have passed.", NamedTextColor.RED));
                    }
                });
            } catch (Exception ex) {
                this.plugin.getLogger().warning("Failed to check expiry of gravestone with id " + entry.getKey());
            }
        }
    }

}
