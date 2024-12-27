package com.github.bnt4.enhancedsurvival.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Displays the ping of the player.
 */
public class PingCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Command can only be executed as player", NamedTextColor.RED));
            return true;
        }

        player.sendMessage(Component.text("Your Ping: ").append(Component.text(player.getPing() + "ms", NamedTextColor.GREEN)));

        return true;
    }

}
