package net.cmd.command;

import net.cmd.config.CMDConfigManager;
import net.cmd.config.CMDPreviewConfig;

/**
 * Resolves representative vanilla item ids for category and subgroup previews.
 */
public class CMDPreviewResolver {

    private static CMDPreviewConfig cfg() {
        return CMDPreviewConfig.fromMainConfig(CMDConfigManager.getConfig());
    }

    public static String categoryItem(String categoryName) {
        CMDPreviewConfig config = cfg();
        if (!config.enabled) return null;
        return config.categoryItems.get(categoryName);
    }

    public static String subgroupItem(String subgroupName) {
        CMDPreviewConfig config = cfg();
        if (!config.enabled) return null;
        return config.subgroupItems.get(subgroupName);
    }
}
