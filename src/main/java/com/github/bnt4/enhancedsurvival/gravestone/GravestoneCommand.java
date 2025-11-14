package com.github.bnt4.enhancedsurvival.gravestone;

import com.github.bnt4.enhancedsurvival.util.command.CommandUtil;
import com.github.bnt4.enhancedsurvival.util.location.LocationUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GravestoneCommand implements TabExecutor {

    private final GravestoneManager gravestoneManager;

    public GravestoneCommand(GravestoneManager gravestoneManager) {
        this.gravestoneManager = gravestoneManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Command can only be executed as player", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Component.text("Usage: /gravestone <Identifier> - Opens an inventory containing a copy of all items from the specified gravestone. This command does not affect the original gravestone.", NamedTextColor.RED));
            return true;
        }

        String identifier = args[0];
        if (!identifier.contains("@")) {
            player.sendMessage(Component.text("Invalid gravestone identifier (required: DATE_@UUID)", NamedTextColor.RED));
            return true;
        }

        UUID uniqueId;
        try {
            // Ignore tab completed date, just use the UUID. The date is only used to make the ids more readable.
            uniqueId = UUID.fromString(identifier.substring(identifier.indexOf('@')+1));
        } catch (IndexOutOfBoundsException | IllegalArgumentException ex) {
            player.sendMessage(Component.text("Invalid gravestone identifier (required: DATE_@UUID)", NamedTextColor.RED));
            return true;
        }

        GravestoneData gravestoneData = this.gravestoneManager.loadGravestone(uniqueId);
        if (gravestoneData == null) {
            player.sendMessage(Component.text("No gravestone with this identifier found (" + uniqueId + ")", NamedTextColor.RED));
            return true;
        }

        Inventory inventory = Bukkit.createInventory(null, 9*6, Component.text("Item Copy"));
        inventory.setContents(Arrays.copyOf(gravestoneData.items(), inventory.getSize()));

        player.openInventory(inventory);

        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("This is an inventory containing a copy of all items (" + gravestoneData.items().length + ") from the specified gravestone. This command does not affect the original gravestone.", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("   Location: " + LocationUtil.formatLocation(gravestoneData.location()), NamedTextColor.YELLOW));
        player.sendMessage(Component.text("   Player Level: " + gravestoneData.level(), NamedTextColor.YELLOW));
        Player target = Bukkit.getPlayer(gravestoneData.playerUniqueId());
        if (target != null) {
            player.sendMessage(Component.text("   Player Name: " + target.getName(), NamedTextColor.YELLOW));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> matches = new ArrayList<>();
        if (args.length == 1) {
            CommandUtil.addMatches(matches, args[0], gravestoneManager.getGravestoneIdentifiers());
        }
        return matches;
    }

}
