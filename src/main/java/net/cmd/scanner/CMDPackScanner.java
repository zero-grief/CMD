package net.cmd.scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.cmd.compat.CMDModelCase;
import net.cmd.compat.CMDResolvedModelCase;
import net.cmd.compat.ModelDefinitionTranslator;
import net.cmd.config.CMDBlacklist;
import net.cmd.config.CMDSettings;
import net.cmd.core.CMDEnvironment;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Scans configured source archives/directories for:
 * - pack.mcmeta
 * - assets/minecraft/items/*.json
 * - assets/<namespace>/models/.../*.json
 *
 * The scanner's job is to read real pack data conservatively and pass that data
 * into the translator, rather than inventing behavior from loose strings alone.
 */
public class CMDPackScanner {

    private static final Gson GSON = new Gson();

    private final CMDSettings settings;
    private final CMDBlacklist blacklist;

    public CMDPackScanner(CMDSettings settings, CMDBlacklist blacklist) {
        this.settings = settings;
        this.blacklist = blacklist;
    }

    public CMDScanResult scan() {
        CMDScanResult result = new CMDScanResult();
        List<File> sources = discoverSources(result);
        Map<String, ModelDefinitionTranslator.CMDPackModelFile> modelIndex = new LinkedHashMap<>();

        for (File source : sources) {
            String sourceId = sourceId(source);
            result.sourceIds.add(sourceId);

            if (source.isDirectory()) {
                scanDirectorySource(source, sourceId, result, modelIndex);
            } else if (source.isFile() && (source.getName().toLowerCase().endsWith(".zip") || source.getName().toLowerCase().endsWith(".jar"))) {
                scanZipSource(source, sourceId, result, modelIndex);
            }
        }

        return result;
    }

    private List<File> discoverSources(CMDScanResult result) {
        List<File> out = new ArrayList<>();

        if (settings.includeWorldResourcesZip) {
            File worldResources = new File(CMDEnvironment.getWorldFolder(), "resources.zip");
            if (worldResources.exists() && worldResources.isFile()) {
                out.add(worldResources);
                result.archivesFound++;
            }
        }

        if (settings.scanModsFolder) {
            File modsFolder = CMDEnvironment.getModsFolder();
            File[] modFiles = modsFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
            if (modFiles != null) {
                for (File file : modFiles) {
                    if (!blacklist.blocks(file.getName())) {
                        out.add(file);
                        result.archivesFound++;
                    }
                }
            }
        }

        for (String relativeSource : settings.scanSources) {
            File root = CMDEnvironment.resolveFromServerRoot(relativeSource);
            if (!root.exists()) continue;

            if (root.isDirectory()) {
                File[] zipFiles = root.listFiles((dir, name) ->
                        name.toLowerCase().endsWith(".zip") || name.toLowerCase().endsWith(".jar"));
                if (zipFiles != null) {
                    for (File file : zipFiles) {
                        if (!blacklist.blocks(file.getName())) {
                            out.add(file);
                            result.archivesFound++;
                        }
                    }
                }
            } else if (root.isFile() && (root.getName().toLowerCase().endsWith(".zip") || root.getName().toLowerCase().endsWith(".jar"))) {
                if (!blacklist.blocks(root.getName())) {
                    out.add(root);
                    result.archivesFound++;
                }
            }
        }

        return out;
    }

    private void scanDirectorySource(
            File root,
            String sourceId,
            CMDScanResult result,
            Map<String, ModelDefinitionTranslator.CMDPackModelFile> modelIndex
    ) {
        scanDirectoryModels(root, sourceId, result, modelIndex);
        scanDirectoryItemDefinitions(root, sourceId, result, modelIndex);
        readDirectoryPackMetadata(root, sourceId, result);
    }

    private void scanZipSource(
            File zipFile,
            String sourceId,
            CMDScanResult result,
            Map<String, ModelDefinitionTranslator.CMDPackModelFile> modelIndex
    ) {
        try (ZipFile zip = new ZipFile(zipFile, StandardCharsets.UTF_8)) {
            scanZipModels(zip, sourceId, result, modelIndex);
            scanZipItemDefinitions(zip, sourceId, result, modelIndex);
            readZipPackMetadata(zip, sourceId, result);
        } catch (Exception ignored) {
        }
    }

    private void readDirectoryPackMetadata(File root, String sourceId, CMDScanResult result) {
        File packMcmeta = new File(root, "pack.mcmeta");
        if (!packMcmeta.exists()) {
            return;
        }

        try (FileReader reader = new FileReader(packMcmeta, StandardCharsets.UTF_8)) {
            JsonObject json = GSON.fromJson(reader, JsonObject.class);
            String display = CMDScanResult.extractSourceDisplayName(json);
            if (display != null) {
                result.sourceDisplayNames.put(sourceId, display);
            }
        } catch (Exception ignored) {
        }
    }

    private void readZipPackMetadata(ZipFile zip, String sourceId, CMDScanResult result) {
        try {
            ZipEntry entry = zip.getEntry("pack.mcmeta");
            if (entry == null) return;

            try (InputStreamReader reader = new InputStreamReader(zip.getInputStream(entry), StandardCharsets.UTF_8)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                String display = CMDScanResult.extractSourceDisplayName(json);
                if (display != null) {
                    result.sourceDisplayNames.put(sourceId, display);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void scanDirectoryModels(
            File root,
            String sourceId,
            CMDScanResult result,
            Map<String, ModelDefinitionTranslator.CMDPackModelFile> modelIndex
    ) {
        File assetsRoot = new File(root, "assets");
        if (!assetsRoot.exists()) return;

        File[] namespaces = assetsRoot.listFiles(File::isDirectory);
        if (namespaces == null) return;

        for (File namespaceDir : namespaces) {
            File modelsRoot = new File(namespaceDir, "models");
            if (!modelsRoot.exists()) continue;
            scanDirectoryModelTree(namespaceDir.getName(), modelsRoot, modelsRoot, sourceId, result, modelIndex);
        }
    }

    private void scanDirectoryModelTree(
            String namespace,
            File modelsRoot,
            File current,
            String sourceId,
            CMDScanResult result,
            Map<String, ModelDefinitionTranslator.CMDPackModelFile> modelIndex
    ) {
        File[] children = current.listFiles();
        if (children == null) return;

        for (File file : children) {
            if (file.isDirectory()) {
                scanDirectoryModelTree(namespace, modelsRoot, file, sourceId, result, modelIndex);
                continue;
            }

            if (!file.getName().toLowerCase().endsWith(".json")) continue;

            String rel = modelsRoot.toPath().relativize(file.toPath()).toString().replace("\\", "/");
            String path = rel.substring(0, rel.length() - 5);

            countModelPath(rel, result);

            ModelDefinitionTranslator.CMDPackModelFile model = new ModelDefinitionTranslator.CMDPackModelFile(
                    namespace,
                    path,
                    "assets/" + namespace + "/models/" + rel,
                    sourceId
            );
            modelIndex.putIfAbsent(model.fullId(), model);
        }
    }

    private void scanZipModels(
            ZipFile zip,
            String sourceId,
            CMDScanResult result,
            Map<String, ModelDefinitionTranslator.CMDPackModelFile> modelIndex
    ) {
        Enumeration<? extends ZipEntry> entries = zip.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String path = entry.getName().replace("\\", "/");

            if (!path.startsWith("assets/")) continue;
            if (!path.contains("/models/")) continue;
            if (!path.toLowerCase().endsWith(".json")) continue;

            String[] split = path.split("/", 4);
            if (split.length < 4) continue;

            String namespace = split[1];
            String afterNamespace = split[3];
            if (!afterNamespace.startsWith("models/")) continue;

            String rel = afterNamespace.substring("models/".length());
            String modelPath = rel.substring(0, rel.length() - 5);

            countModelPath(rel, result);

            ModelDefinitionTranslator.CMDPackModelFile model = new ModelDefinitionTranslator.CMDPackModelFile(
                    namespace,
                    modelPath,
                    path,
                    sourceId
            );
            modelIndex.putIfAbsent(model.fullId(), model);
        }
    }

    private void countModelPath(String rel, CMDScanResult result) {
        String normalized = rel.toLowerCase();
        result.totalModelJsonCount++;

        if (normalized.startsWith("item/")) {
            result.itemModelJsonCount++;
        }
        if (normalized.startsWith("equipment/")) {
            result.equipmentModelJsonCount++;
        }
        if (normalized.startsWith("humanoid/") || normalized.startsWith("humanoid_leggings/")) {
            result.humanoidModelJsonCount++;
        }
    }

    private void scanDirectoryItemDefinitions(
            File root,
            String sourceId,
            CMDScanResult result,
            Map<String, ModelDefinitionTranslator.CMDPackModelFile> modelIndex
    ) {
        File itemsRoot = new File(root, "assets/minecraft/items");
        if (!itemsRoot.exists()) return;

        for (String itemId : settings.definitionTargets) {
            File definition = new File(itemsRoot, itemId + ".json");
            if (!definition.exists()) continue;

            try (FileReader reader = new FileReader(definition, StandardCharsets.UTF_8)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);

                List<CMDResolvedModelCase> resolved = ModelDefinitionTranslator.translateResolved(
                        json,
                        itemId,
                        "assets/minecraft/items/" + itemId + ".json",
                        sourceId,
                        modelIndex
                );
                result.resolvedRegistry.computeIfAbsent(itemId, k -> new ArrayList<>()).addAll(resolved);

                List<CMDModelCase> legacy = ModelDefinitionTranslator.translate(json, itemId);
                result.legacyRegistry.computeIfAbsent(itemId, k -> new ArrayList<>()).addAll(legacy);
            } catch (Exception ignored) {
            }
        }
    }

    private void scanZipItemDefinitions(
            ZipFile zip,
            String sourceId,
            CMDScanResult result,
            Map<String, ModelDefinitionTranslator.CMDPackModelFile> modelIndex
    ) {
        for (String itemId : settings.definitionTargets) {
            String path = "assets/minecraft/items/" + itemId + ".json";
            ZipEntry entry = zip.getEntry(path);
            if (entry == null) continue;

            try (InputStreamReader reader = new InputStreamReader(zip.getInputStream(entry), StandardCharsets.UTF_8)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);

                List<CMDResolvedModelCase> resolved = ModelDefinitionTranslator.translateResolved(
                        json,
                        itemId,
                        path,
                        sourceId,
                        modelIndex
                );
                result.resolvedRegistry.computeIfAbsent(itemId, k -> new ArrayList<>()).addAll(resolved);

                List<CMDModelCase> legacy = ModelDefinitionTranslator.translate(json, itemId);
                result.legacyRegistry.computeIfAbsent(itemId, k -> new ArrayList<>()).addAll(legacy);
            } catch (Exception ignored) {
            }
        }
    }

    private String sourceId(File file) {
        if (file.isDirectory()) {
            return "dir:" + file.getName();
        }

        String name = file.getName().toLowerCase();
        if (name.endsWith(".jar")) {
            return "mod:" + file.getName();
        }

        return "archive:" + file.getName();
    }
}