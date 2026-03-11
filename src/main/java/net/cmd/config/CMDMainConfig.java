package net.cmd.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unified main config model for CMD.
 */
public class CMDMainConfig {

    public General general = new General();
    public Build build = new Build();
    public Chat chat = new Chat();
    public Preview preview = new Preview();
    public Commands commands = new Commands();
    public PackHandling packHandling = new PackHandling();

    public static class General {
        public int listPageSize = 25;
    }

    public static class Build {
        public boolean reloadTriggersRebuild = false;
        public String generatedPackDescription = "CMD Generated Definitions Pack";
        public String generatedPackMinValue = "55";
        public String generatedPackMaxValue = "75.0";
    }

    public static class Chat {
        public Map<String, String> categoryByItem = new HashMap<>();
        public Map<String, String> subgroupByItem = new HashMap<>();

        public Chat() {
            categoryByItem.put("wooden_sword", "Weapons");
            categoryByItem.put("stone_sword", "Weapons");
            categoryByItem.put("iron_sword", "Weapons");
            categoryByItem.put("golden_sword", "Weapons");
            categoryByItem.put("diamond_sword", "Weapons");
            categoryByItem.put("netherite_sword", "Weapons");
            categoryByItem.put("bow", "Weapons");
            categoryByItem.put("crossbow", "Weapons");
            categoryByItem.put("trident", "Weapons");

            subgroupByItem.put("wooden_sword", "Swords");
            subgroupByItem.put("stone_sword", "Swords");
            subgroupByItem.put("iron_sword", "Swords");
            subgroupByItem.put("golden_sword", "Swords");
            subgroupByItem.put("diamond_sword", "Swords");
            subgroupByItem.put("netherite_sword", "Swords");
            subgroupByItem.put("bow", "Ranged");
            subgroupByItem.put("crossbow", "Ranged");
            subgroupByItem.put("trident", "Polearms");

            categoryByItem.put("wooden_pickaxe", "Tools");
            categoryByItem.put("stone_pickaxe", "Tools");
            categoryByItem.put("iron_pickaxe", "Tools");
            categoryByItem.put("golden_pickaxe", "Tools");
            categoryByItem.put("diamond_pickaxe", "Tools");
            categoryByItem.put("netherite_pickaxe", "Tools");

            subgroupByItem.put("wooden_pickaxe", "Pickaxes");
            subgroupByItem.put("stone_pickaxe", "Pickaxes");
            subgroupByItem.put("iron_pickaxe", "Pickaxes");
            subgroupByItem.put("golden_pickaxe", "Pickaxes");
            subgroupByItem.put("diamond_pickaxe", "Pickaxes");
            subgroupByItem.put("netherite_pickaxe", "Pickaxes");

            categoryByItem.put("leather_helmet", "Equipment");
            categoryByItem.put("chainmail_helmet", "Equipment");
            categoryByItem.put("iron_helmet", "Equipment");
            categoryByItem.put("golden_helmet", "Equipment");
            categoryByItem.put("diamond_helmet", "Equipment");
            categoryByItem.put("netherite_helmet", "Equipment");

            categoryByItem.put("leather_chestplate", "Equipment");
            categoryByItem.put("chainmail_chestplate", "Equipment");
            categoryByItem.put("iron_chestplate", "Equipment");
            categoryByItem.put("golden_chestplate", "Equipment");
            categoryByItem.put("diamond_chestplate", "Equipment");
            categoryByItem.put("netherite_chestplate", "Equipment");

            categoryByItem.put("leather_leggings", "Equipment");
            categoryByItem.put("chainmail_leggings", "Equipment");
            categoryByItem.put("iron_leggings", "Equipment");
            categoryByItem.put("golden_leggings", "Equipment");
            categoryByItem.put("diamond_leggings", "Equipment");
            categoryByItem.put("netherite_leggings", "Equipment");

            categoryByItem.put("leather_boots", "Equipment");
            categoryByItem.put("chainmail_boots", "Equipment");
            categoryByItem.put("iron_boots", "Equipment");
            categoryByItem.put("golden_boots", "Equipment");
            categoryByItem.put("diamond_boots", "Equipment");
            categoryByItem.put("netherite_boots", "Equipment");

            subgroupByItem.put("leather_helmet", "Helmets");
            subgroupByItem.put("chainmail_helmet", "Helmets");
            subgroupByItem.put("iron_helmet", "Helmets");
            subgroupByItem.put("golden_helmet", "Helmets");
            subgroupByItem.put("diamond_helmet", "Helmets");
            subgroupByItem.put("netherite_helmet", "Helmets");

            subgroupByItem.put("leather_chestplate", "Chestplates");
            subgroupByItem.put("chainmail_chestplate", "Chestplates");
            subgroupByItem.put("iron_chestplate", "Chestplates");
            subgroupByItem.put("golden_chestplate", "Chestplates");
            subgroupByItem.put("diamond_chestplate", "Chestplates");
            subgroupByItem.put("netherite_chestplate", "Chestplates");

            subgroupByItem.put("leather_leggings", "Leggings");
            subgroupByItem.put("chainmail_leggings", "Leggings");
            subgroupByItem.put("iron_leggings", "Leggings");
            subgroupByItem.put("golden_leggings", "Leggings");
            subgroupByItem.put("diamond_leggings", "Leggings");
            subgroupByItem.put("netherite_leggings", "Leggings");

            subgroupByItem.put("leather_boots", "Boots");
            subgroupByItem.put("chainmail_boots", "Boots");
            subgroupByItem.put("iron_boots", "Boots");
            subgroupByItem.put("golden_boots", "Boots");
            subgroupByItem.put("diamond_boots", "Boots");
            subgroupByItem.put("netherite_boots", "Boots");

            categoryByItem.put("carved_pumpkin", "Equipment");
            subgroupByItem.put("carved_pumpkin", "Headgear");

            categoryByItem.put("totem_of_undying", "Special");
            subgroupByItem.put("totem_of_undying", "Totems");
        }
    }

    public static class Preview {
        public boolean enabled = true;
        public Map<String, String> categoryItems = new HashMap<>();
        public Map<String, String> subgroupItems = new HashMap<>();

        public Preview() {
            categoryItems.put("Weapons", "diamond_sword");
            categoryItems.put("Tools", "diamond_pickaxe");
            categoryItems.put("Equipment", "diamond_chestplate");
            categoryItems.put("Special", "totem_of_undying");

            subgroupItems.put("Swords", "iron_sword");
            subgroupItems.put("Ranged", "bow");
            subgroupItems.put("Polearms", "trident");
            subgroupItems.put("Pickaxes", "iron_pickaxe");
            subgroupItems.put("Helmets", "iron_helmet");
            subgroupItems.put("Chestplates", "iron_chestplate");
            subgroupItems.put("Leggings", "iron_leggings");
            subgroupItems.put("Boots", "iron_boots");
            subgroupItems.put("Headgear", "carved_pumpkin");
            subgroupItems.put("Totems", "totem_of_undying");
        }
    }

    public static class Commands {
        public CommandPermission root = CommandPermission.open("cmd.command.root");
        public CommandPermission help = CommandPermission.open("cmd.command.help");
        public CommandPermission list = CommandPermission.open("cmd.command.list");
        public CommandPermission listFilter = CommandPermission.open("cmd.command.list.filter");
        public CommandPermission filters = CommandPermission.open("cmd.command.filters");

        public CommandPermission apply = CommandPermission.adminWithCreative("cmd.command.apply");
        public CommandPermission reset = CommandPermission.adminWithCreative("cmd.command.reset");

        public CommandPermission reload = CommandPermission.adminOnly("cmd.command.reload");
        public CommandPermission rebuild = CommandPermission.adminOnly("cmd.command.rebuild");
        public CommandPermission rebuildConfirm = CommandPermission.adminOnly("cmd.command.rebuild.confirm");
        public CommandPermission rebuildDeny = CommandPermission.adminOnly("cmd.command.rebuild.deny");
    }

    public static class CommandPermission {
        public boolean usePermissionNode;
        public String permissionNode;
        public boolean requiresAdminFallback;
        public boolean allowCreativeBypass;

        public static CommandPermission open(String node) {
            CommandPermission out = new CommandPermission();
            out.usePermissionNode = false;
            out.permissionNode = node;
            out.requiresAdminFallback = false;
            out.allowCreativeBypass = false;
            return out;
        }

        public static CommandPermission adminOnly(String node) {
            CommandPermission out = new CommandPermission();
            out.usePermissionNode = false;
            out.permissionNode = node;
            out.requiresAdminFallback = true;
            out.allowCreativeBypass = false;
            return out;
        }

        public static CommandPermission adminWithCreative(String node) {
            CommandPermission out = new CommandPermission();
            out.usePermissionNode = false;
            out.permissionNode = node;
            out.requiresAdminFallback = true;
            out.allowCreativeBypass = true;
            return out;
        }
    }

    public static class PackHandling {
        public boolean includeWorldResourcesZip = true;
        public boolean includeWorldDatapacks = true;
        public boolean scanModsFolder = true;
        public List<String> scanSources = new ArrayList<>();
        public List<String> extraDefinitionTargets = new ArrayList<>();

        public PackHandling() {
            scanSources.add("resourcepacks");
            scanSources.add("resourcepacks/custom");
        }
    }
}
