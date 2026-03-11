package net.cmd.core;

import net.cmd.config.CMDConfigManager;
import net.cmd.config.CMDMainConfig;
import net.cmd.merge.CMDMergeEngineV3;

import java.io.File;

/**
 * Handles first-start resource pack generation behavior.
 */
public class CMDStartupBuildManager {

    public static void handleStartupBuild() {
        CMDMainConfig cfg = CMDConfigManager.getConfig();

        if (!cfg.build.startupBuildEnabled) {
            return;
        }

        File worldFolder = CMDEnvironment.getWorldFolder();
        File generatedPack = new File(worldFolder, "resources.zip");

        if (!generatedPack.exists() || !cfg.build.startupBuildCompleted) {
            System.out.println("[CMD] Running startup resource pack build...");
            CMDMergeEngineV3.runFullBuild();
            cfg.build.startupBuildCompleted = true;
            CMDConfigManager.saveMainConfig(cfg);
            return;
        }

        if (!cfg.build.requireConfirmAfterFirstBuild) {
            System.out.println("[CMD] Startup rebuild allowed automatically.");
            CMDMergeEngineV3.runFullBuild();
        } else {
            System.out.println("[CMD] Existing generated resources pack found. Use /cmd rebuild confirm if you want to rebuild.");
        }
    }
}
