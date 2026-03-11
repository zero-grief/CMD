package net.cmd.config;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Shared vanilla target item list for scanning and chat grouping.
 */
public final class CMDTargetItems {
    private CMDTargetItems() {}

    public static List<String> defaultDefinitionTargets() {
        Set<String> out = new LinkedHashSet<>();
        out.addAll(List.of(
                sword("wooden"), sword("stone"), sword("iron"), sword("golden"), sword("diamond"), sword("netherite"),
                "bow", "crossbow", "trident", "mace",
                tool("pickaxe", "wooden"), tool("pickaxe", "stone"), tool("pickaxe", "iron"), tool("pickaxe", "golden"), tool("pickaxe", "diamond"), tool("pickaxe", "netherite"),
                tool("axe", "wooden"), tool("axe", "stone"), tool("axe", "iron"), tool("axe", "golden"), tool("axe", "diamond"), tool("axe", "netherite"),
                tool("shovel", "wooden"), tool("shovel", "stone"), tool("shovel", "iron"), tool("shovel", "golden"), tool("shovel", "diamond"), tool("shovel", "netherite"),
                tool("hoe", "wooden"), tool("hoe", "stone"), tool("hoe", "iron"), tool("hoe", "golden"), tool("hoe", "diamond"), tool("hoe", "netherite"),
                "fishing_rod", "shears", "flint_and_steel", "brush", "carrot_on_a_stick", "warped_fungus_on_a_stick",
                armor("helmet", "leather"), armor("helmet", "chainmail"), armor("helmet", "iron"), armor("helmet", "golden"), armor("helmet", "diamond"), armor("helmet", "netherite"), "turtle_helmet",
                armor("chestplate", "leather"), armor("chestplate", "chainmail"), armor("chestplate", "iron"), armor("chestplate", "golden"), armor("chestplate", "diamond"), armor("chestplate", "netherite"),
                armor("leggings", "leather"), armor("leggings", "chainmail"), armor("leggings", "iron"), armor("leggings", "golden"), armor("leggings", "diamond"), armor("leggings", "netherite"),
                armor("boots", "leather"), armor("boots", "chainmail"), armor("boots", "iron"), armor("boots", "golden"), armor("boots", "diamond"), armor("boots", "netherite"),
                "elytra", "shield", "carved_pumpkin", "totem_of_undying"
        ));
        return new ArrayList<>(out);
    }

    public static String normalizeDefinitionTarget(String value) {
        if (value == null) return "minecraft:air";
        String trimmed = value.trim().toLowerCase();
        if (trimmed.isEmpty()) return "minecraft:air";
        if (!trimmed.contains(":")) return "minecraft:" + trimmed;
        return trimmed;
    }

    public static String definitionFileName(String target) {
        String normalized = normalizeDefinitionTarget(target);
        int colon = normalized.indexOf(':');
        return colon >= 0 ? normalized.substring(colon + 1) : normalized;
    }

    private static String sword(String material) { return material + "_sword"; }
    private static String tool(String kind, String material) { return material + "_" + kind; }
    private static String armor(String slot, String material) { return material + "_" + slot; }
}
