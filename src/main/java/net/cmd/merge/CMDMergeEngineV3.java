package net.cmd.merge;

import com.google.gson.JsonObject;
import net.cmd.compat.CMDItemModelRegistry;
import net.cmd.compat.CMDModelCase;
import net.cmd.compat.CMDResolvedModelRegistry;
import net.cmd.config.CMDConfigManager;
import net.cmd.config.CMDSettings;
import net.cmd.resourcepack.CMDResourcePackBuilder;
import net.cmd.scanner.CMDPackScanner;
import net.cmd.scanner.CMDScanResult;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Main merge pipeline for CMD.
 */
public class CMDMergeEngineV3 {

    public CMDBuildResult rebuild() {
        System.out.println("[CMD] Merge Engine V3 rebuild started");
        CMDBuildResult result = new CMDBuildResult();
        CMDSettings settings = CMDConfigManager.getSettings();

        CMDPackScanner scanner = new CMDPackScanner();
        CMDScanResult scanResult = scanner.scanAllSources(settings);

        result.scannedSources = scanResult.scannedSources;
        result.parsedItemDefinitions = scanResult.parsedItemDefinitions;
        result.registeredCases = scanResult.registeredCases;
        result.parseErrors = scanResult.parseErrors;

        CMDResolvedModelRegistry.loadFromMap(scanResult.getResolvedCasesByItem());
        CMDItemModelRegistry.loadFromMap(scanResult.getCasesByItem());

        File worldFolder = net.cmd.core.CMDEnvironment.getWorldFolder();
        CMDResourcePackBuilder builder = new CMDResourcePackBuilder();
        CMDBuildResult buildResult = builder.build(worldFolder, CMDItemModelRegistry.getAll(), scanResult.getSourceIds());

        result.success = buildResult.success;
        result.writtenItemFiles = buildResult.writtenItemFiles;
        result.outputZipPath = buildResult.outputZipPath;
        result.message = buildResult.message;

        System.out.println("[CMD] Merge Engine V3 rebuild finished");
        return result;
    }

    public CMDScanResult reloadRegistryOnly() {
        CMDSettings settings = CMDConfigManager.getSettings();
        CMDPackScanner scanner = new CMDPackScanner();
        CMDScanResult scanResult = scanner.scanAllSources(settings);
        CMDResolvedModelRegistry.loadFromMap(scanResult.getResolvedCasesByItem());
        CMDItemModelRegistry.loadFromMap(scanResult.getCasesByItem());
        return scanResult;
    }

    public Map<String, JsonObject> buildMergedDefinitions() {
        Map<String, JsonObject> merged = new LinkedHashMap<>();
        for (Map.Entry<String, List<CMDModelCase>> entry : CMDItemModelRegistry.getAll().entrySet()) {
            merged.put(entry.getKey(), CMDItemDefinitionMerger.merge(entry.getKey(), entry.getValue()));
        }
        return merged;
    }

    public static CMDBuildResult runFullBuild() { return new CMDMergeEngineV3().rebuild(); }
}
