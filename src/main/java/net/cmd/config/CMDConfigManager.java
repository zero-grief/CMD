package net.cmd.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;

/**
 * Handles loading and saving the unified CMD config file.
 */
public class CMDConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static CMDMainConfig config;

    public static void load() {
        File folder = CMDConfigPaths.getConfigFolder();
        if (!folder.exists()) folder.mkdirs();
        config = loadMainConfig();
        System.out.println("[CMD] Configuration loaded.");
    }

    private static CMDMainConfig loadMainConfig() {
        File file = CMDConfigPaths.getMainConfigFile();

        try {
            if (!file.exists()) {
                CMDMainConfig defaults = CMDMainConfig.createDefault();
                saveMainConfig(defaults);
                return defaults;
            }

            String raw = java.nio.file.Files.readString(file.toPath());
            String stripped = raw
                    .replaceAll("(?m)//.*$", "")
                    .replaceAll("(?s)/\\*.*?\\*/", "");

            CMDMainConfig loaded = GSON.fromJson(stripped, CMDMainConfig.class);
            if (loaded == null) loaded = CMDMainConfig.createDefault();

            if (loaded.general == null) loaded.general = new CMDMainConfig.General();
            if (loaded.build == null) loaded.build = new CMDMainConfig.Build();
            if (loaded.packhandling == null) loaded.packhandling = new CMDMainConfig.PackHandling();
            if (loaded.chat == null) loaded.chat = new CMDMainConfig.Chat();
            if (loaded.preview == null) loaded.preview = new CMDMainConfig.Preview();
            if (loaded.commands == null) loaded.commands = new CMDMainConfig.Commands();
            if (loaded.packhandling.scanSources == null || loaded.packhandling.scanSources.isEmpty()) {
                loaded.packhandling.scanSources = CMDMainConfig.createDefault().packhandling.scanSources;
            }
            if (loaded.packhandling.extraDefinitionTargets == null) {
                loaded.packhandling.extraDefinitionTargets = new java.util.ArrayList<>();
            }
            if (loaded.general.listPageSize < 1) loaded.general.listPageSize = 8;

            saveMainConfig(loaded);
            return loaded;

        } catch (Exception e) {
            e.printStackTrace();
            CMDMainConfig fallback = CMDMainConfig.createDefault();
            saveMainConfig(fallback);
            return fallback;
        }
    }

    public static void saveMainConfig(CMDMainConfig updated) {
        try (FileWriter writer = new FileWriter(CMDConfigPaths.getMainConfigFile())) {
            writer.write(buildCommentedTemplate(updated));
            config = updated;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CMDMainConfig getConfig() {
        if (config == null) config = CMDMainConfig.createDefault();
        return config;
    }

    public static CMDSettings getSettings() { return CMDSettings.fromMainConfig(getConfig()); }
    public static CMDState getState() { return CMDState.fromMainConfig(getConfig()); }
    public static CMDCategoryConfig getCategoryConfig() { return CMDCategoryConfig.fromMainConfig(getConfig()); }
    public static CMDPreviewConfig getPreviewConfig() { return CMDPreviewConfig.fromMainConfig(getConfig()); }
    public static CMDCommandPermissionsConfig getCommandPermissionsConfig() { return CMDCommandPermissionsConfig.fromMainConfig(getConfig()); }

    private static String buildCommentedTemplate(CMDMainConfig updated) {
        StringBuilder sb = new StringBuilder();
        sb.append("// CMD unified config file\n");
        sb.append("// Modules:\n");
        sb.append("// - general      : core runtime settings\n");
        sb.append("// - build        : startup/build/rebuild behavior\n");
        sb.append("// - packhandling : scan sources, world resources.zip, extra definition targets\n");
        sb.append("// - chat         : category and subgroup structure used by /cmd list and filters\n");
        sb.append("// - preview      : representative vanilla items used in show_item previews\n");
        sb.append("// - commands     : permission rules for admin commands\n");
        sb.append("//\n");
        sb.append("// packhandling examples:\n");
        sb.append("// includeWorldResourcesZip: true/false\n");
        sb.append("// includeDatapackZips: true/false\n");
        sb.append("// includeModJars: true/false\n");
        sb.append("// scanSources: [\"resourcepacks\", \"world/datapacks\", \"config/CMD/packs\", \"serverfolder/customfoldername\"]\n");
        sb.append("// extraDefinitionTargets: [\"minecraft:stick\", \"minecraft:paper\", \"custom:item_token\"]\n\n");
        sb.append(GSON.toJson(updated));
        return sb.toString();
    }
}
