package net.cmd.merge;

import com.google.gson.JsonObject;
import net.cmd.compat.CMDItemModelRegistry;
import net.cmd.compat.CMDResolvedModelRegistry;
import net.cmd.config.CMDBlacklist;
import net.cmd.config.CMDConfigManager;
import net.cmd.config.CMDSettings;
import net.cmd.resourcepack.CMDResourcePackBuilder;
import net.cmd.scanner.CMDPackScanner;
import net.cmd.scanner.CMDScanResult;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Main merge pipeline for CMD.
 *
 * Current responsibility:
 * - scan configured pack sources
 * - populate both the richer resolved registry and the legacy merge registry
 * - generate merged vanilla item-definition files
 * - build the generated resource-pack archive
 *
 * This patch does not redesign the merge architecture.
 * It only reconnects this class to the scanner/result/builder APIs that
 * currently exist in the project.
 */
public class CMDMergeEngineV3 {

    public CMDBuildResult rebuild() {
        System.out.println("[CMD] Merge Engine V3 rebuild started");

        CMDBuildResult result = new CMDBuildResult();
        CMDSettings settings = CMDConfigManager.getSettings();
        CMDBlacklist blacklist = new CMDBlacklist();

        CMDPackScanner scanner = new CMDPackScanner(settings, blacklist);
        CMDScanResult scanResult = scanner.scan();

        result.scannedSources = scanResult.archivesFound;
        result.parsedItemDefinitions = scanResult.legacyRegistry.size();
        result.registeredCases = countLegacyCases(scanResult.legacyRegistry);
        result.parseErrors = 0;

        CMDResolvedModelRegistry.loadFromMap(scanResult.resolvedRegistry);
        CMDItemModelRegistry.loadFromMap(scanResult.legacyRegistry);

        Map<String, JsonObject> mergedDefinitions = buildMergedDefinitions();
        Map<String, String> generatedLang = new LinkedHashMap<>();

        CMDResourcePackBuilder builder = new CMDResourcePackBuilder();
        CMDResourcePackBuilder.BuildOutput buildOutput =
                builder.build(mergedDefinitions, generatedLang, scanResult.sourceIds);

        result.success = buildOutput != null && buildOutput.generatedPack != null && buildOutput.generatedPack.exists();
        result.writtenItemFiles = mergedDefinitions.size();
        result.outputZipPath = buildOutput != null && buildOutput.generatedPack != null
                ? buildOutput.generatedPack.getAbsolutePath()
                : "";
        result.message = result.success
                ? "CMD rebuild completed."
                : "CMD rebuild did not produce a generated pack archive.";

        System.out.println("[CMD] Merge Engine V3 rebuild finished");
        return result;
    }

    public CMDScanResult reloadRegistryOnly() {
        CMDSettings settings = CMDConfigManager.getSettings();
        CMDBlacklist blacklist = new CMDBlacklist();

        CMDPackScanner scanner = new CMDPackScanner(settings, blacklist);
        CMDScanResult scanResult = scanner.scan();

        CMDResolvedModelRegistry.loadFromMap(scanResult.resolvedRegistry);
        CMDItemModelRegistry.loadFromMap(scanResult.legacyRegistry);
        return scanResult;
    }

    public Map<String, JsonObject> buildMergedDefinitions() {
        Map<String, JsonObject> merged = new LinkedHashMap<>();

        for (Map.Entry<String, List<net.cmd.compat.CMDModelCase>> entry : CMDItemModelRegistry.getAll().entrySet()) {
            merged.put(entry.getKey(), CMDItemDefinitionMerger.merge(entry.getKey(), entry.getValue()));
        }

        return merged;
    }

    public static CMDBuildResult runFullBuild() {
        return new CMDMergeEngineV3().rebuild();
    }

    private int countLegacyCases(Map<String, List<net.cmd.compat.CMDModelCase>> registry) {
        int total = 0;
        for (List<net.cmd.compat.CMDModelCase> cases : registry.values()) {
            total += cases.size();
        }
        return total;
    }
}