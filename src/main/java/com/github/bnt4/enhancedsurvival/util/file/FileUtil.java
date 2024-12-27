package com.github.bnt4.enhancedsurvival.util.file;

import com.google.gson.*;
import com.github.bnt4.enhancedsurvival.util.item.ItemSerialization;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;

public class FileUtil {

    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    /**
     * Creates a file and it's parent directories
     * @param file file to create
     * @return true if file does not exist and was created, else false
     * @throws IOException if an exception is thrown at {@link File#createNewFile()}
     */
    public static boolean createFileIfNotExists(File file) throws IOException {
        if (!file.exists()) {
            if (file.getParentFile() != null) {
                File dir = file.getParentFile();
                if (!dir.exists()) {
                    dir.mkdirs();
                }
            }
            return file.createNewFile();
        }
        return false;
    }

    public static Location locationFromJson(JsonObject object) {
        String worldName = object.get("world").getAsString();
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new NullPointerException("Failed to load location from json: Invalid world name " + worldName);
        }
        double x = object.get("x").getAsDouble();
        double y = object.get("y").getAsDouble();
        double z = object.get("z").getAsDouble();
        float yaw = object.has("yaw") ? object.get("yaw").getAsFloat() : 0;
        float pitch = object.has("pitch") ? object.get("pitch").getAsFloat() : 0;
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static JsonObject locationToJson(Location location) {
        JsonObject object = new JsonObject();
        object.addProperty("world", location.getWorld().getName());
        object.addProperty("x", location.getX());
        object.addProperty("y", location.getY());
        object.addProperty("z", location.getZ());
        if (location.getYaw() != 0) {
            object.addProperty("yaw", location.getYaw());
        }
        if (location.getPitch() != 0) {
            object.addProperty("pitch", location.getPitch());
        }
        return object;
    }

    public static JsonArray itemsToJson(ItemStack[] itemStacks) {
        JsonArray itemsArray = new JsonArray(itemStacks.length);
        for (ItemStack itemStack : itemStacks) {
            itemsArray.add(itemStack == null ? JsonNull.INSTANCE : new JsonPrimitive(ItemSerialization.serializeItemStack(itemStack)));
        }
        return itemsArray;
    }

    public static ItemStack[] itemsFromJson(JsonArray itemsArray) {
        if (itemsArray == null || itemsArray.size() == 0) {
            return new ItemStack[0];
        }
        ItemStack[] itemStacks = new ItemStack[itemsArray.size()];
        for (int i = 0; i < itemsArray.size(); i++) {
            JsonElement item = itemsArray.get(i);
            itemStacks[i] = item.isJsonNull() ? null : ItemSerialization.deserializeItemStack(item.getAsString());
        }
        return itemStacks;
    }

}
