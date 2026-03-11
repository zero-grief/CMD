package net.cmd.model;

import net.cmd.config.CMDCategoryConfig;
import net.cmd.config.CMDConfigManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Classifies items into groups and sub-groups using config-driven category data.
 */
public class CMDItemClassifier {

    public static String configuredCategory(String itemId) {
        CMDCategoryConfig config = CMDConfigManager.getCategoryConfig();
        for (Map.Entry<String, CMDCategoryConfig.CategoryDefinition> categoryEntry : config.categories.entrySet()) {
            for (Map.Entry<String, List<String>> subgroupEntry : categoryEntry.getValue().subgroups.entrySet()) {
                if (subgroupEntry.getValue().contains(itemId)) return categoryEntry.getKey();
            }
        }
        return "Other";
    }

    public static String configuredSubGroup(String itemId) {
        CMDCategoryConfig config = CMDConfigManager.getCategoryConfig();
        for (Map.Entry<String, CMDCategoryConfig.CategoryDefinition> categoryEntry : config.categories.entrySet()) {
            for (Map.Entry<String, List<String>> subgroupEntry : categoryEntry.getValue().subgroups.entrySet()) {
                if (subgroupEntry.getValue().contains(itemId)) return subgroupEntry.getKey();
            }
        }
        return "Other";
    }

    public static String material(String itemId) {
        if (itemId.startsWith("wooden_")) return "Wooden";
        if (itemId.startsWith("stone_")) return "Stone";
        if (itemId.startsWith("iron_")) return "Iron";
        if (itemId.startsWith("golden_")) return "Golden";
        if (itemId.startsWith("diamond_")) return "Diamond";
        if (itemId.startsWith("netherite_")) return "Netherite";
        if (itemId.startsWith("leather_")) return "Leather";
        if (itemId.startsWith("chainmail_")) return "Chainmail";
        if (itemId.equals("turtle_helmet")) return "Turtle";
        if (itemId.equals("shield")) return "Shield";
        if (itemId.equals("trident")) return "Trident";
        if (itemId.equals("mace")) return "Mace";
        if (itemId.equals("bow")) return "Bow";
        if (itemId.equals("crossbow")) return "Crossbow";
        if (itemId.equals("elytra")) return "Elytra";
        if (itemId.equals("totem_of_undying")) return "Totem";
        if (itemId.equals("carved_pumpkin")) return "Carved Pumpkin";
        if (itemId.equals("fishing_rod")) return "Fishing Rod";
        if (itemId.equals("shears")) return "Shears";
        if (itemId.equals("flint_and_steel")) return "Flint and Steel";
        if (itemId.equals("brush")) return "Brush";
        if (itemId.equals("carrot_on_a_stick")) return "Carrot on a Stick";
        if (itemId.equals("warped_fungus_on_a_stick")) return "Warped Fungus on a Stick";
        return itemId;
    }

    public static List<String> configuredCategories() {
        CMDCategoryConfig config = CMDConfigManager.getCategoryConfig();
        return new ArrayList<>(config.categories.keySet());
    }

    public static List<String> configuredSubGroups() {
        CMDCategoryConfig config = CMDConfigManager.getCategoryConfig();
        List<String> out = new ArrayList<>();
        for (CMDCategoryConfig.CategoryDefinition def : config.categories.values()) out.addAll(def.subgroups.keySet());
        return out;
    }

    public static List<String> configuredItemIds() {
        CMDCategoryConfig config = CMDConfigManager.getCategoryConfig();
        List<String> out = new ArrayList<>();
        for (CMDCategoryConfig.CategoryDefinition def : config.categories.values()) {
            for (List<String> items : def.subgroups.values()) out.addAll(items);
        }
        return out;
    }
}
