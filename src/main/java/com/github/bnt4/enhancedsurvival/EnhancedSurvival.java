package com.github.bnt4.enhancedsurvival;

import com.github.bnt4.enhancedsurvival.backpack.BackpackManager;
import com.github.bnt4.enhancedsurvival.command.PingCommand;
import com.github.bnt4.enhancedsurvival.command.TrashCommand;
import com.github.bnt4.enhancedsurvival.config.PluginConfig;
import com.github.bnt4.enhancedsurvival.farmprotection.FarmProtectionListener;
import com.github.bnt4.enhancedsurvival.gravestone.GravestoneManager;
import com.github.bnt4.enhancedsurvival.motd.MotdManager;
import com.github.bnt4.enhancedsurvival.playtime.PlaytimeManager;
import com.github.bnt4.enhancedsurvival.recipe.RecipeManager;
import com.github.bnt4.enhancedsurvival.updater.UpdaterManager;
import com.github.bnt4.enhancedsurvival.util.inventory.MenuListener;
import com.github.bnt4.enhancedsurvival.waypoint.WaypointManager;
import com.github.bnt4.enhancedsurvival.joinquitmessage.JoinQuitMessageListener;
import com.github.bnt4.enhancedsurvival.navigation.NavigationManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class EnhancedSurvival extends JavaPlugin {

    private PlaytimeManager playtimeManager;

    @Override
    public void onEnable() {
        PluginConfig pluginConfig = new PluginConfig(this);
        try {
            pluginConfig.reloadConfig();
        } catch (Exception ex) {
            this.getLogger().severe("Failed to load config: " + ex.getMessage());
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // features
        new UpdaterManager(this, pluginConfig);
        new BackpackManager(this, pluginConfig);
        new RecipeManager(this, pluginConfig);
        WaypointManager waypointManager = new WaypointManager(this, pluginConfig);
        new NavigationManager(this, pluginConfig, waypointManager);
        this.playtimeManager = new PlaytimeManager(this, pluginConfig);
        new GravestoneManager(this, pluginConfig);
        new MotdManager(this, pluginConfig);

        // listener
        registerListener(new MenuListener());
        new FarmProtectionListener(this, pluginConfig);
        new JoinQuitMessageListener(this, pluginConfig);

        // commands
        registerCommand("ping", new PingCommand());
        registerCommand("trash", new TrashCommand(pluginConfig));

        this.getLogger().info("Enabled " + this.getName());
    }

    @Override
    public void onDisable() {
        this.playtimeManager.saveActiveSessions();
    }

    /**
     * Folder used to store data (not configs), e.g. backpack items
     */
    public File getUserDataFolder() {
        File folder = new File(getDataFolder(), "data");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }

    /**
     * Registers the given event listener.
     *
     * @param listener the event listener to register
     */
    public void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    /**
     * Registers the given command.
     *
     * @param name the name of the command
     * @param executor the command, if it is a {@link org.bukkit.command.TabExecutor} it will be registered as well
     */
    public void registerCommand(String name, CommandExecutor executor) {
        PluginCommand command = this.getCommand(name);
        if (command != null) {
            command.setExecutor(executor);
            if (executor instanceof TabCompleter tabCompleter) {
                command.setTabCompleter(tabCompleter);
            }
        } else {
            this.getLogger().severe("Failed to register command " + name);
        }
    }

}
