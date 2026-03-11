package net.cmd.apply;

import net.cmd.compat.CMDModelCase;
import net.minecraft.component.ComponentChanges;
import net.minecraft.item.ItemStack;

/**
 * Compatibility wrapper kept for project continuity.
 *
 * The real component logic now lives in CMDModelChangeHelper.
 */
public class CMDEquipmentComponentFactory {

    public static ComponentChanges create(ItemStack stack, CMDModelCase modelCase) {
        if (stack == null || modelCase == null || modelCase.modelId == null) {
            return ComponentChanges.EMPTY;
        }

        return CMDModelChangeHelper.buildApplyChanges(stack, modelCase.modelId);
    }
}
