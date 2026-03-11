package net.cmd.command;

import net.cmd.apply.CMDSecureItemApplicator;
import net.cmd.compat.CMDResolvedModelCase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

/**
 * Builds item stacks used for chat hover previews.
 *
 * Preview rules:
 * - category/subgroup -> representative vanilla item from preview config
 * - material row -> representative base item
 * - model row -> actual base item with resolved CMD data + lore applied
 */
public class CMDChatPreviewFactory {

    public static ItemStack createBaseItemPreview(String itemId) {
        Item item = resolveVanillaItem(itemId);
        if (item == Items.AIR) return new ItemStack(Items.PAPER);
        return new ItemStack(item);
    }

    public static ItemStack createCategoryPreview(String categoryName) {
        String itemId = CMDPreviewResolver.categoryItem(categoryName);
        if (itemId == null || itemId.isBlank()) return new ItemStack(Items.PAPER);
        return createBaseItemPreview(itemId);
    }

    public static ItemStack createSubgroupPreview(String subgroupName) {
        String itemId = CMDPreviewResolver.subgroupItem(subgroupName);
        if (itemId == null || itemId.isBlank()) return new ItemStack(Items.PAPER);
        return createBaseItemPreview(itemId);
    }

    public static ItemStack createAppliedModelPreview(String itemId, CMDResolvedModelCase resolvedCase) {
        ItemStack stack = createBaseItemPreview(itemId);
        if (stack.isEmpty()) return stack;
        if (resolvedCase != null) CMDSecureItemApplicator.applyResolvedCase(stack, resolvedCase);
        return stack;
    }

    private static Item resolveVanillaItem(String itemId) {
        try {
            Identifier id = Identifier.of("minecraft", itemId);
            Item item = Registries.ITEM.get(id);
            return item != null ? item : Items.AIR;
        } catch (Exception e) {
            return Items.AIR;
        }
    }
}
