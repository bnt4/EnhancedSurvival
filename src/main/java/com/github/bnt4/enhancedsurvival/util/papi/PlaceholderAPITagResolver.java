package com.github.bnt4.enhancedsurvival.util.papi;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

public class PlaceholderAPITagResolver {

    /**
     * Custom 'papi' tag for minimessage to support the usage of PlaceholderAPI placeholders.
     * @param forPlayer Player to parse the placeholders against
     * @return TagResolver
     */
    public static TagResolver placeholderApiTagResolver(final Player forPlayer) {
        return TagResolver.resolver("papi", (argumentQueue, context) -> {
            // require placeholder name in tag (e.g. '<papi:placeholder_name>')
            final String papiPlaceholder = argumentQueue.popOr("papi tag requires an argument").value();

            // whether the placeholder style should end at the placeholder or be passed onto the following text (defaults to true).
            // to not pass the style, e.g. '<papi:placeholder_name:false>' can be used
            final Tag.Argument passStyleArgument = argumentQueue.peek();
            final boolean passStyle = passStyleArgument == null || passStyleArgument.isTrue();

            // parse placeholder
            final String parsedPlaceholder = PlaceholderAPI.setPlaceholders(forPlayer, '%' + papiPlaceholder + '%');

            // allow and parse legacy ampersand in placeholder content, since a lot of plugins still use that for colors
            final Component componentPlaceholder = LegacyComponentSerializer.legacyAmpersand().deserialize(parsedPlaceholder);

            // return the placeholder tag
            return passStyle ? Tag.inserting(componentPlaceholder) : Tag.selfClosingInserting(componentPlaceholder);
        });
    }

}
