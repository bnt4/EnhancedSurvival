package com.github.bnt4.enhancedsurvival.util.file;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Represents a JSON file that can be read from and written to disk.
 * This class provides methods to load data from a JSON file and to save data back to it.
 */
public class JsonFile {

    private final File file;
    private JsonObject data;

    /**
     * Constructs a new JsonFile with the specified file location and default JSON data.
     * If the file does not already exist, it is created with the provided default data.
     * Otherwise, existing data is loaded from the file.
     *
     * @param dataFolder The directory where the JSON file is located.
     * @param name       The name of the JSON file.
     * @param def        The default JsonObject data to use if the file does not exist.
     * @throws RuntimeException if the file cannot be created or read.
     */
    public JsonFile(File dataFolder, String name, JsonObject def) {
        try {
            this.file = new File(dataFolder, name);
            if(FileUtil.createFileIfNotExists(file)) {
                this.data = def;
                save();
            } else {
                reloadFromDisk();
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to create file " + name, ex);
        }
    }

    /**
     * Gets the current JSON data stored in memory for this file.
     * Not a copy, can be modified and saved with {@link #save()} later.
     *
     * @return the JsonObject representing the data.
     */
    public JsonObject getData() {
        return data;
    }

    /**
     * Sets new JSON data for this file in memory.
     *
     * @param data The new JsonObject data to set.
     */
    public void setData(JsonObject data) {
        this.data = data;
    }

    /**
     * Reloads the JSON data from the file on disk into memory.
     *
     * @throws RuntimeException if the file cannot be read or parsed.
     */
    public synchronized void reloadFromDisk() {
        try (FileReader reader = new FileReader(file)) {
            this.data = FileUtil.GSON.fromJson(reader, JsonObject.class);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to parse json file " + file.getName(), ex);
        }
    }

    /**
     * Saves the current JSON data from memory to the file on disk.
     *
     * @throws RuntimeException if the file cannot be written.
     */
    public synchronized void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(FileUtil.GSON.toJson(data));
            writer.flush();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to save json file " + file.getName(), ex);
        }
    }

    /**
     * Gets the file associated with this JsonFile instance.
     *
     * @return the File object representing the JSON file.
     */
    public File getFile() {
        return file;
    }

    /**
     * Gets the name of the JSON file.
     *
     * @return the name of the file as a String.
     */
    public String getName() {
        return file.getName();
    }

    /**
     * Returns a string representation of the JsonFile, including file path and JSON data.
     *
     * @return a String describing this JsonFile.
     */
    @Override
    public String toString() {
        return "JsonFile{" +
                "file=" + file.getAbsolutePath() +
                ", data=" + FileUtil.GSON.toJson(data) +
                '}';
    }

}
