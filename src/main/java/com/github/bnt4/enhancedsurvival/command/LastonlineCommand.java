package com.github.bnt4.enhancedsurvival.command;

import com.github.bnt4.enhancedsurvival.config.LastOnlineConfig;
import com.github.bnt4.enhancedsurvival.util.time.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * Shows the last online date of other players
 */
public class LastonlineCommand implements CommandExecutor {

    private final LastOnlineConfig lastonlineConfig;

    public LastonlineCommand(LastOnlineConfig lastonlineConfig) {
        this.lastonlineConfig = lastonlineConfig;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Command can only be executed as player", NamedTextColor.RED));
            return true;
        }

        if (!lastonlineConfig.isLastOnline()) {
            player.sendMessage(Component.text("This command was disabled by the server admin", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Component.text("Usage: /lastonline <Name>", NamedTextColor.RED));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.isOnline() && !target.hasPlayedBefore()) {
            player.sendMessage(Component.text("This player has never been on the server", NamedTextColor.RED));
            return true;
        }

        if (target.isOnline()) {
            player.sendMessage(Component.text("This player is currently online", NamedTextColor.RED));
            return true;
        }

        player.sendMessage(Component.text(target.getName() + " was last online on ")
                .append(Component.text(TimeUtil.formatDate(new Date(target.getLastSeen())), NamedTextColor.GREEN)));

        return true;
    }

}
