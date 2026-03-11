package net.cmd.command;

import net.cmd.compat.CMDResolvedModelCase;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

/**
 * Resolves the exact Name Tag text a player should use in an anvil.
 */
public class CMDAnvilNameResolver {

    public static String requiredNameTag(CMDResolvedModelCase modelCase) {
        if (modelCase == null) {
            return "";
        }
        if (modelCase.suggestedAnvilText != null && !modelCase.suggestedAnvilText.isBlank()) {
            return modelCase.suggestedAnvilText;
        }
        return requiredNameTag(modelCase.baseItemId, modelCase.displayId);
    }

    public static String requiredNameTag(String baseItemId, String modelId) {
        if (modelId == null || modelId.isBlank()) {
            return "";
        }

        if (isEquippableItem(baseItemId)) {
            return modelId;
        }

        int colonIndex = modelId.indexOf(':');
        if (colonIndex > 0 && colonIndex < modelId.length() - 1) {
            return modelId.substring(colonIndex + 1);
        }

        return modelId;
    }

    public static boolean isEquippableItem(String baseItemId) {
        try {
            Item item = Registries.ITEM.get(Identifier.of("minecraft", baseItemId));

            if (item == null || item == Items.AIR) {
                return false;
            }

            return item.getDefaultComponents().contains(DataComponentTypes.EQUIPPABLE);
        } catch (Exception e) {
            return false;
        }
    }
}
