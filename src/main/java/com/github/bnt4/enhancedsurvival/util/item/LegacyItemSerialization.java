package com.github.bnt4.enhancedsurvival.util.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.io.ByteArrayInputStream;

/**
 * The previous, deprecated item serialization used for the migration.
 * Will be deleted in a future version. The serialize method does not
 * exist anymore because new items should not be serialized with the
 * old system.
 * @deprecated Only exists for the migration to the new system
 */
@Deprecated(since = "1.3.0")
public class LegacyItemSerialization {

    /**
     * Deserializes an {@link ItemStack} from a string using the {@link BukkitObjectInputStream}.
     * If an error occurs, an exception is thrown.
     *
     * @param s string to create the item from
     * @return deserialized item
     * @deprecated see {@link LegacyItemSerialization}
     */
    @Deprecated(since = "1.3.0")
    public static ItemStack deserializeItemStack(String s) throws Exception {
        if (s == null || s.equals("null")) return null;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder.decodeLines(org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder.decodeString(s)));
        BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
        ItemStack itemStack = (ItemStack) dataInput.readObject();
        dataInput.close();
        inputStream.close();
        return itemStack;
    }

}
