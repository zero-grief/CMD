package net.cmd.command;

import net.minecraft.item.ItemStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;

/**
 * Central helper for building clickable and hoverable chat text.
 *
 * This keeps all hover-preview behavior consistent across:
 * - /cmd list
 * - /cmd filters
 * - future chat-based browser helpers
 */
public class CMDHoverTextFactory {

    /**
     * Creates a text line with a plain SHOW_TEXT hover.
     */
    public static Text textHover(String label, String hoverText) {
        return Text.literal(label)
                .styled(style -> style.withHoverEvent(
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(hoverText))
                ));
    }

    /**
     * Creates a text line with an item SHOW_ITEM hover.
     */
    public static Text itemHover(String label, ItemStack preview) {
        return Text.literal(label)
                .styled(style -> style.withHoverEvent(
                        new HoverEvent(
                                HoverEvent.Action.SHOW_ITEM,
                                new HoverEvent.ItemStackContent(preview)
                        )
                ));
    }

    /**
     * Creates a clickable command token with a SHOW_TEXT hover.
     */
    public static Text clickableCommandText(String label, String command, String hoverText) {
        return Text.literal(label)
                .styled(style -> style
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                        .withHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                Text.literal(hoverText)
                        )));
    }

    /**
     * Creates a clickable command token with a SHOW_ITEM hover.
     */
    public static Text clickableCommandItem(String label, String command, ItemStack preview) {
        return Text.literal(label)
                .styled(style -> style
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                        .withHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_ITEM,
                                new HoverEvent.ItemStackContent(preview)
                        )));
    }
}
