package net.cmd.config;

import java.util.Map;
import java.util.TreeMap;

/**
 * Compatibility view over the unified preview config.
 */
public class CMDPreviewConfig {

    public boolean enabled = true;
    public Map<String, String> categoryItems = new TreeMap<>();
    public Map<String, String> subgroupItems = new TreeMap<>();

    public static CMDPreviewConfig createDefault() {
        return fromMainConfig(CMDMainConfig.createDefault());
    }

    public static CMDPreviewConfig fromMainConfig(CMDMainConfig config) {
        CMDPreviewConfig out = new CMDPreviewConfig();

        if (config != null && config.preview != null) {
            out.enabled = config.preview.enabled;
            if (config.preview.categoryItems != null) out.categoryItems.putAll(config.preview.categoryItems);
            if (config.preview.subgroupItems != null) out.subgroupItems.putAll(config.preview.subgroupItems);
        }

        return out;
    }
}
