package net.cmd.apply;

import net.cmd.compat.CMDResolvedModelCase;
import net.cmd.compat.CMDResolvedModelRegistry;
import net.minecraft.component.ComponentChanges;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

/**
 * Validated admin command application path.
 *
 * Smithing remains the main gameplay path.
 * This class is the admin shortcut to the same effective end result.
 */
public class CMDSecureItemApplicator {

    public static boolean apply(ItemStack stack, String modelId) {
        if (stack == null || stack.isEmpty() || modelId == null || modelId.isBlank()) {
            return false;
        }

        String baseItemId = Registries.ITEM.getId(stack.getItem()).getPath();
        CMDResolvedModelCase resolved = CMDResolvedModelRegistry.findByDisplayId(baseItemId, modelId);

        if (resolved == null) {
            return false;
        }

        applyResolvedCase(stack, resolved);
        return true;
    }

    public static void applyResolvedCase(ItemStack stack, CMDResolvedModelCase resolvedCase) {
        if (stack == null || stack.isEmpty() || resolvedCase == null) {
            return;
        }

        ComponentChanges changes = CMDModelChangeHelper.buildApplyChanges(stack, resolvedCase);
        stack.applyChanges(changes);
    }

    public static void reset(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        ComponentChanges changes = CMDModelChangeHelper.buildResetChanges(stack);
        stack.applyChanges(changes);
    }
}
