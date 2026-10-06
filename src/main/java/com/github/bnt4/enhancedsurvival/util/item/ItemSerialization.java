package com.github.bnt4.enhancedsurvival.util.item;

import com.github.bnt4.enhancedsurvival.EnhancedSurvival;
import org.bukkit.inventory.ItemStack;

import java.util.Base64;
import java.util.logging.Level;

public class ItemSerialization {

    /**
     * Serializes an {@link ItemStack} to a string using {@link ItemStack#serializeAsBytes()} and the {@link Base64.Encoder}.
     * If an error occurs, null is returned.
     *
     * @param itemStack item to serialize
     * @return serialized item as string or null in case of an exception
     */
    public static String serializeItemStack(ItemStack itemStack) {
        if (itemStack == null) return null;
        try {
            return Base64.getEncoder().encodeToString(itemStack.serializeAsBytes());
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Deserializes an {@link ItemStack} from a string using {@link ItemStack#deserializeBytes(byte[])} and the {@link Base64.Decoder}.
     * If an error occurs, null is returned.
     *
     * @param s string to create the item from
     * @return deserialized item or null in case of an exception
     */
    public static ItemStack deserializeItemStack(String s) {
        if (s == null || s.equals("null")) return null;
        try {
            return ItemStack.deserializeBytes(Base64.getDecoder().decode(s));
        } catch (Exception ex) {
            // for backwards compatibility, try to deserialize the data with the old method.
            // they will be overwritten with the newly serialized string on the next save.
            try {
                return LegacyItemSerialization.deserializeItemStack(s);
            } catch (Exception legacyEx) {
                EnhancedSurvival.LOGGER.log(Level.SEVERE, "Failed to deserialize legacy item base64: " + s, legacyEx);
            }
            return null;
        }
    }

}
