package net.cmd.reload;

import net.cmd.compat.CMDItemModelRegistry;
import net.cmd.compat.CMDResolvedModelRegistry;
import net.cmd.config.CMDConfigManager;
import net.cmd.config.CMDSettings;
import net.cmd.config.CMDState;
import net.cmd.core.CMDEnvironment;
import net.cmd.merge.CMDBuildResult;
import net.cmd.merge.CMDMergeEngineV3;
import net.cmd.scanner.CMDPackScanner;
import net.cmd.scanner.CMDScanResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Central runtime reload/rebuild controller.
 */
public class LiveReloadManager {

    private static File lastGeneratedPack;
    private static File lastSourcesFile;
    private static boolean pendingRebuild = false;
    private static RebuildPreviewReport lastPreviewReport;

    public static void reload() {
        CMDConfigManager.load();

        CMDSettings settings = CMDSettings.fromMainConfig(CMDConfigManager.getConfig());
        CMDBuildResult result = new CMDMergeEngineV3().run(settings);

        lastGeneratedPack = result.generatedPack;
        lastSourcesFile = result.sourcesFile;
        CMDState.loaded = true;
    }

    public static RebuildPreviewReport prepareRebuildPreview() {
        CMDConfigManager.load();
        CMDSettings settings = CMDSettings.fromMainConfig(CMDConfigManager.getConfig());

        CMDPackScanner scanner = new CMDPackScanner(settings, new net.cmd.config.CMDBlacklist());
        CMDScanResult scanResult = scanner.scan();

        List<String> pathsRead = new ArrayList<>();

        if (settings.includeWorldResourcesZip) {
            pathsRead.add(new File(CMDEnvironment.getWorldFolder(), "resources.zip").getPath());
        }

        if (settings.scanModsFolder) {
            pathsRead.add(CMDEnvironment.getModsFolder().getPath());
        }

        for (String source : settings.scanSources) {
            pathsRead.add(CMDEnvironment.resolveFromServerRoot(source).getPath());
        }

        lastPreviewReport = new RebuildPreviewReport(
                pathsRead,
                scanResult.archivesFound,
                scanResult.totalModelJsonCount,
                scanResult.itemModelJsonCount,
                scanResult.equipmentModelJsonCount,
                scanResult.humanoidModelJsonCount
        );

        pendingRebuild = true;
        return lastPreviewReport;
    }

    public static boolean hasPendingRebuild() {
        return pendingRebuild;
    }

    public static RebuildPreviewReport getLastPreviewReport() {
        return lastPreviewReport;
    }

    public static void confirmPreparedRebuild() {
        if (!pendingRebuild) {
            return;
        }

        CMDConfigManager.load();
        CMDSettings settings = CMDSettings.fromMainConfig(CMDConfigManager.getConfig());
        CMDBuildResult result = new CMDMergeEngineV3().run(settings);

        lastGeneratedPack = result.generatedPack;
        lastSourcesFile = result.sourcesFile;
        CMDState.loaded = true;

        pendingRebuild = false;
        lastPreviewReport = null;
    }

    public static void cancelPreparedRebuild() {
        pendingRebuild = false;
        lastPreviewReport = null;
    }

    public static void clearState() {
        CMDItemModelRegistry.clear();
        CMDResolvedModelRegistry.clear();
        lastGeneratedPack = null;
        lastSourcesFile = null;
        lastPreviewReport = null;
        pendingRebuild = false;
        CMDState.loaded = false;
    }

    public static File getLastGeneratedPack() {
        return lastGeneratedPack;
    }

    public static File getLastSourcesFile() {
        return lastSourcesFile;
    }

    public record RebuildPreviewReport(
            List<String> pathsRead,
            int archivesFound,
            int totalModelJsonCount,
            int itemModelJsonCount,
            int equipmentModelJsonCount,
            int humanoidModelJsonCount
    ) {}
}
