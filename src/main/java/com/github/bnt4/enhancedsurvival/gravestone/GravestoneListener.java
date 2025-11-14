package com.github.bnt4.enhancedsurvival.gravestone;

import com.github.bnt4.enhancedsurvival.EnhancedSurvival;
import com.github.bnt4.enhancedsurvival.util.location.LocationUtil;
import com.github.bnt4.enhancedsurvival.util.time.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Campfire;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GravestoneListener implements Listener {

    private final EnhancedSurvival plugin;
    private final GravestoneManager gravestoneManager;

    public GravestoneListener(EnhancedSurvival plugin, GravestoneManager gravestoneManager) {
        this.plugin = plugin;
        this.gravestoneManager = gravestoneManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.CAMPFIRE) {
            return;
        }

        Player player = event.getPlayer();

        Campfire campfire = (Campfire) event.getBlock().getState();
        String id = campfire.getPersistentDataContainer().get(this.gravestoneManager.getGravestoneIdKey(), PersistentDataType.STRING);
        if (id == null) {
            return;
        }

        UUID gravestoneId;
        try {
            gravestoneId = UUID.fromString(id);
        } catch (Exception ex) {
            event.setCancelled(true);
            player.sendMessage(Component.text("Error: Invalid gravestone id: " + id));
            this.plugin.getLogger().warning("Invalid gravestone id " + id + ", " + player.getName() + " tried to break it at " + LocationUtil.formatLocation(campfire.getLocation()));
            return;
        }

        GravestoneData gravestoneData = this.gravestoneManager.loadGravestone(gravestoneId);
        if (gravestoneData == null) {
            event.setCancelled(true);
            player.sendMessage(Component.text("Error: Invalid gravestone id: " + id));
            this.plugin.getLogger().warning("Invalid gravestone id " + id + ", " + player.getName() + " tried to break it at " + LocationUtil.formatLocation(campfire.getLocation()));
            return;
        }

        String deathTime = TimeUtil.formatDate(gravestoneData.time());
        ItemStack[] items = gravestoneData.items();
        Location location = event.getBlock().getLocation();
        String name = Bukkit.getOfflinePlayer(gravestoneData.playerUniqueId()).getName();

        this.gravestoneManager.markGravestoneAsDestroyed(gravestoneId);

        event.setDropItems(false);
        for (ItemStack i : items) {
            location.getWorld().dropItem(location.clone().add(0.5, 1, 0.5), i);
        }

        player.sendMessage(Component.text("Death of " + name + " at " + deathTime));
        this.plugin.getLogger().info("Gravestone with id " + gravestoneId.toString() + " at " + LocationUtil.formatLocation(location) + " destroyed by " + player.getName());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        ItemStack[] contents = player.getInventory().getStorageContents();
        ItemStack[] armor = player.getInventory().getArmorContents();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        List<ItemStack> items = new ArrayList<>();
        for (ItemStack i : contents) {
            if (i != null) {
                items.add(i);
            }
        }

        for (ItemStack i : armor) {
            if (i != null) {
                items.add(i);
            }
        }

        if (offHand.getType() != Material.AIR) {
            items.add(offHand);
        }

        ItemStack[] itemArray = items.toArray(new ItemStack[0]);

        Location blockLocation = player.getLocation().clone();

        int counter = 0;
        while (blockLocation.getBlock().getState(false) instanceof TileState) {
            if (counter > 10) {
                player.sendMessage(Component.text("Location for gravestone blocked - items are dropped normally", NamedTextColor.RED));
                this.plugin.getLogger().warning("Gravestone of " + player.getName() + " blocked at " + LocationUtil.formatLocation(blockLocation));
                return;
            }
            blockLocation.add(0, 1, 0);
            counter++;
        }
        Block block = blockLocation.getBlock();

        block.setType(Material.CAMPFIRE);
        Campfire campfire;
        try {
            campfire = (Campfire) block.getState();
        } catch (Exception ex) {
            player.sendMessage(Component.text("An error occurred while creating the gravestone - items are dropped normally", NamedTextColor.RED));
            this.plugin.getLogger().warning("Couldn't pass death event for " + player.getName() + " - doing nothing. Items are dropped normally at " + LocationUtil.formatLocation(player.getLocation()) + ". (" + ex.getMessage() + ")");
            return;
        }
        try {
            UUID gravestoneId = this.gravestoneManager.saveGravestone(new GravestoneData(player.getUniqueId(), block.getLocation(), itemArray, player.getLevel()));

            campfire.getPersistentDataContainer().set(this.gravestoneManager.getGravestoneIdKey(), PersistentDataType.STRING, gravestoneId.toString());
            campfire.update();

            event.getDrops().clear();

            int removeAfterMinutes = this.gravestoneManager.getConfig().removeGravestoneAfterMinutes();
            Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
                player.sendMessage(Component.text("A gravestone with your items was placed. Your items will drop when breaking it."
                        + (removeAfterMinutes > 0 ? " The gravestone will be destroyed in " + removeAfterMinutes + " minutes ("
                        + TimeUtil.formatDate(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(removeAfterMinutes))) + ")." : ""), NamedTextColor.RED));
            }, 20);

            this.plugin.getLogger().info("Created gravestone " + gravestoneId.toString() + " at " + LocationUtil.formatLocation(block.getLocation()) + " for " + player.getName());
        } catch (Exception ex) {
            this.plugin.getLogger().warning("Couldn't create gravestone at " + LocationUtil.formatLocation(block.getLocation()) + " for " + player.getName() + ": " + ex.getMessage());
        }
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        if (event.getBlock().getType() == Material.CAMPFIRE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        removeGravestones(event.blockList());
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        removeGravestones(event.blockList());
    }

    /**
     * Removes all gravestones from the given block list. Used in explode events above,
     * so that gravestones are not destroyed by explosions, but other blocks are.
     * @param blocks block list
     */
    private void removeGravestones(List<Block> blocks) {
        blocks.removeIf(block -> {
            if (block.getType() != Material.CAMPFIRE) {
                return false;
            }
            Campfire campfire = (Campfire) block.getState();
            String id = campfire.getPersistentDataContainer().get(this.gravestoneManager.getGravestoneIdKey(), PersistentDataType.STRING);
            return id != null;
        });
    }

}
