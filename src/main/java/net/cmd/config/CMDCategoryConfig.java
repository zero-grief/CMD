package net.cmd.config;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Compatibility view over the unified chat category config.
 */
public class CMDCategoryConfig {

    public Map<String, CategoryDefinition> categories = new TreeMap<>();

    public static CMDCategoryConfig createDefault() {
        return fromMainConfig(CMDMainConfig.createDefault());
    }

    public static CMDCategoryConfig fromMainConfig(CMDMainConfig config) {
        CMDCategoryConfig out = new CMDCategoryConfig();

        if (config != null && config.chat != null && config.chat.categories != null) {
            for (Map.Entry<String, CMDMainConfig.CategoryGroup> entry : config.chat.categories.entrySet()) {
                CategoryDefinition def = new CategoryDefinition();
                if (entry.getValue() != null && entry.getValue().subgroups != null) {
                    def.subgroups.putAll(entry.getValue().subgroups);
                }
                out.categories.put(entry.getKey(), def);
            }
        }

        return out;
    }

    public static class CategoryDefinition {
        public Map<String, List<String>> subgroups = new TreeMap<>();
    }
}
