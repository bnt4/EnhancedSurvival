package com.github.bnt4.enhancedsurvival.command;

import com.github.bnt4.enhancedsurvival.config.TrashConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * Opens an empty, new inventory. Items in it are deleted after closing it.
 */
public class TrashCommand implements CommandExecutor {

    private final TrashConfig trashConfig;

    public TrashCommand(TrashConfig trashConfig) {
        this.trashConfig = trashConfig;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Command can only be executed as player", NamedTextColor.RED));
            return true;
        }

        if (!trashConfig.isTrash()) {
            player.sendMessage(Component.text("This command was disabled by the server admin", NamedTextColor.RED));
            return true;
        }

        Inventory inventory = Bukkit.createInventory(null, 4*9, Component.text("Trashcan"));
        player.openInventory(inventory);

        return true;
    }

}
