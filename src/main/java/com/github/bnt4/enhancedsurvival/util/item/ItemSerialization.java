package com.github.bnt4.enhancedsurvival.util.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ItemSerialization {

    /**
     * Serializes an {@link ItemStack} to a string using the {@link BukkitObjectOutputStream} and {@link Base64Coder}.
     * If an error occurs, null is returned.
     *
     * @param itemStack item to serialize
     * @return serialized item as string or null in case of an exception
     */
    public static String serializeItemStack(ItemStack itemStack) {
        if (itemStack == null) return null;
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(stream);
            dataOutput.writeObject(itemStack);
            dataOutput.close();
            byte[] bytes = stream.toByteArray();
            stream.close();
            return Base64Coder.encodeString(Base64Coder.encodeLines(bytes));
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Deserializes an {@link ItemStack} from a string using the {@link BukkitObjectInputStream} and {@link Base64Coder}.
     * If an error occurs, null is returned.
     *
     * @param s string to create the item from
     * @return deserialized item or null in case of an exception
     */
    public static ItemStack deserializeItemStack(String s) {
        if (s == null || s.equals("null")) return null;
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(Base64Coder.decodeString(s)));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack itemStack = (ItemStack) dataInput.readObject();
            dataInput.close();
            inputStream.close();
            return itemStack;
        } catch (Exception ex) {
            return null;
        }
    }

}
