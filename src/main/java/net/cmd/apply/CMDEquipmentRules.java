package net.cmd.apply;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;

/**
 * Minimal compatibility helper.
 *
 * CMD now supports all items that already carry the EQUIPPABLE component.
 */
public class CMDEquipmentRules {

    public static boolean supportsEquipmentAsset(ItemStack stack) {
        return stack != null && stack.getDefaultComponents().contains(DataComponentTypes.EQUIPPABLE);
    }
}
