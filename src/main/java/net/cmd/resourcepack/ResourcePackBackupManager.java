package net.cmd.resourcepack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.cmd.config.CMDConfigManager;
import net.cmd.config.CMDMainConfig;
import net.cmd.util.ZipUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds CMD's generated resource-pack archive.
 *
 * Current responsibilities:
 * - write pack.mcmeta
 * - write merged item-definition files
 * - write generated language file
 * - write SOURCES.txt
 * - zip the output directory into a single archive
 *
 * Full asset copying/rewriting is still a later stage goal.
 */
public class CMDResourcePackBuilder {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    public static class BuildOutput {
        public File generatedPack;
        public File sourcesFile;
        public File buildDirectory;
    }

    public BuildOutput build(
            Map<String, JsonObject> mergedDefinitions,
            Map<String, String> generatedLang,
            List<String> sourceIds
    ) {
        BuildOutput out = new BuildOutput();

        try {
            File buildDir = PackStructureBuilder.prepareFreshBuildDirectory();
            out.buildDirectory = buildDir;

            writePackMcmeta(buildDir);
            writeMergedItemDefinitions(buildDir, mergedDefinitions);
            writeGeneratedLang(buildDir, generatedLang);

            File sourcesFile = writeSourcesFile(buildDir, sourceIds, mergedDefinitions);
            out.sourcesFile = sourcesFile;

            File zipFile = new File(buildDir.getParentFile(), "resources.zip");
            if (zipFile.exists()) {
                zipFile.delete();
            }

            ZipUtils.zipDirectory(buildDir, zipFile);
            out.generatedPack = zipFile;
        } catch (Exception e) {
        }

        return out;
    }

    private void writePackMcmeta(File buildDir) throws Exception {
        CMDMainConfig config = CMDConfigManager.getConfig();

        JsonObject root = new JsonObject();
        JsonObject pack = new JsonObject();
        JsonObject description = new JsonObject();

        description.addProperty("text", config.build.generatedPackDescription);
        pack.add("description", description);
        pack.addProperty("min_format", config.build.generatedPackMinValue);
        pack.addProperty("max_format", config.build.generatedPackMaxValue);
        root.add("pack", pack);

        File packMcmeta = new File(buildDir, "pack.mcmeta");
        java.nio.file.Files.writeString(packMcmeta.toPath(), GSON.toJson(root), StandardCharsets.UTF_8);
    }

    private void writeMergedItemDefinitions(File buildDir, Map<String, JsonObject> mergedDefinitions) throws Exception {
        File itemsDir = new File(buildDir, "assets/minecraft/items");
        itemsDir.mkdirs();

        for (Map.Entry<String, JsonObject> entry : mergedDefinitions.entrySet()) {
            File outFile = new File(itemsDir, entry.getKey() + ".json");
            java.nio.file.Files.writeString(outFile.toPath(), GSON.toJson(entry.getValue()), StandardCharsets.UTF_8);
        }
    }

    private void writeGeneratedLang(File buildDir, Map<String, String> generatedLang) throws Exception {
        File langDir = new File(buildDir, "assets/cmd/lang");
        langDir.mkdirs();

        Map<String, String> ordered = new LinkedHashMap<>(generatedLang);
        File outFile = new File(langDir, "en_us.json");
        java.nio.file.Files.writeString(outFile.toPath(), GSON.toJson(ordered), StandardCharsets.UTF_8);
    }

    private File writeSourcesFile(
            File buildDir,
            List<String> sourceIds,
            Map<String, JsonObject> mergedDefinitions
    ) throws Exception {
        File outFile = new File(buildDir, "SOURCES.txt");

        StringBuilder sb = new StringBuilder();
        sb.append("CMD merged sources\n");
        sb.append("Sources:\n");
        for (String sourceId : sourceIds) {
            sb.append("- ").append(sourceId).append("\n");
        }

        sb.append("\n");
        sb.append("Definitions by item:\n");
        for (Map.Entry<String, JsonObject> entry : mergedDefinitions.entrySet()) {
            sb.append("\n");
            sb.append(entry.getKey()).append("\n");
            sb.append("- merged definition written\n");
        }

        java.nio.file.Files.writeString(outFile.toPath(), sb.toString(), StandardCharsets.UTF_8);
        return outFile;
    }
}