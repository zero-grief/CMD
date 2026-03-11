package net.cmd.apply;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class CMDItemTypeResolver {
    public static String resolveBaseItem(ItemStack stack) {
        Identifier id = Registries.ITEM.getId(stack.getItem());
        if (id == null) return "";
        return id.getPath();
    }
}
