package com.github.bnt4.enhancedsurvival.util.file;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * An improved {@link YamlConfiguration} with a few more options like a better location serialization and a save method.
 */
public class YamlFile extends YamlConfiguration {

    private final File file;

    /**
     * Creates a new YamlFile at the given file path.
     *
     * @param dataFolder folder to create config in
     * @param name name of the config
     */
    public YamlFile(File dataFolder, String name) {
        try {
            this.file = new File(dataFolder, name);
            FileUtil.createFileIfNotExists(file);
            super.load(file);
        } catch (IOException | InvalidConfigurationException ex) {
            throw new RuntimeException("Failed to create file " + name, ex);
        }
    }

    /**
     * Sets the location at the given path, not using Bukkits location serialization, but rather splitting the location in world, x, y, z, yaw and pitch.
     * If the location is null, null is set at the given path.
     * Only locations set with this method can be got with {@link #getLocation(String)}
     *
     * @param path path of the location to set
     * @param location location to set
     */
    public void setLocation(@NotNull String path, @Nullable Location location) {
        if (location == null) {
            super.set(path, null);
            return;
        }
        super.set(path + ".world", location.getWorld().getName());
        super.set(path + ".x", location.getX());
        super.set(path + ".y", location.getY());
        super.set(path + ".z", location.getZ());
        super.set(path + ".yaw", location.getYaw());
        super.set(path + ".pitch", location.getPitch());
    }

    /**
     * Returns the location at the given path, not using Bukkits location serialization, but rather splitting the location in world, x, y, z, yaw and pitch.
     * If the location is not set, null is returned. This doesn't use any default values of the {@link YamlConfiguration}.
     *
     * @param path path of the location to get
     * @return location at the path, null if not set (ignoring default values)
     */
    @Override
    public @Nullable Location getLocation(@NotNull String path) {
        if (!isSet(path)) {
            return null;
        }
        try {
            World world = Bukkit.getWorld(super.getString(path + ".world"));
            double x = super.getDouble(path + ".x");
            double y = super.getDouble(path + ".y");
            double z = super.getDouble(path + ".z");
            float yaw = getFloat(path + ".yaw");
            float pitch = getFloat(path + ".pitch");
            return new Location(world, x, y, z, yaw, pitch);
        } catch (NullPointerException ignored) {
            return null;
        }
    }

    /**
     * @param path path to the float to get
     * @return double casted as float
     */
    public float getFloat(@NotNull String path) {
        return (float) getDouble(path);
    }

    /**
     * @see YamlConfigurationOptions#copyDefaults(boolean)
     * @return this instance for method chaining
     */
    public YamlFile copyDefaults(boolean copyDefaults) {
        super.options().copyDefaults(copyDefaults);
        return this;
    }

    /**
     * Deletes this file
     * @return result of {@link File#delete()}
     */
    public boolean delete() {
        return this.file.delete();
    }

    /**
     * Saves this configuration using {@link YamlConfiguration#save(File)} with the given file in the constructor.
     * @throws RuntimeException if an {@link IOException} occurs
     */
    public void save() {
        try {
            super.save(file);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to save file " + file.getAbsolutePath(), ex);
        }
    }

    /**
     * @return given file in the constructor
     */
    public File getFile() {
        return file;
    }

    /**
     * @return true if the file exists
     */
    public boolean exists() {
        return file.exists();
    }

}
