package net.cmd.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Compatibility access wrapper around the unified config.
 */
public class CMDSettings {

    public int listPageSize = 8;
    public boolean scanResourcepacks = true;
    public boolean scanDatapacks = true;
    public boolean scanModsFolder = false;
    public boolean includeWorldResourcesZip = true;
    public List<String> scanSources = new ArrayList<>();
    public List<String> definitionTargets = new ArrayList<>();

    public static CMDSettings fromMainConfig(CMDMainConfig config) {
        CMDSettings out = new CMDSettings();
        if (config != null && config.general != null) {
            out.listPageSize = config.general.listPageSize;
        }
        if (config != null && config.packhandling != null) {
            out.scanResourcepacks = config.packhandling.enabled;
            out.scanDatapacks = config.packhandling.includeDatapackZips;
            out.scanModsFolder = config.packhandling.includeModJars;
            out.includeWorldResourcesZip = config.packhandling.includeWorldResourcesZip;
            if (config.packhandling.scanSources != null) out.scanSources.addAll(config.packhandling.scanSources);
            out.definitionTargets.addAll(CMDTargetItems.defaultDefinitionTargets());
            if (config.packhandling.extraDefinitionTargets != null) {
                for (String value : config.packhandling.extraDefinitionTargets) {
                    String normalized = CMDTargetItems.normalizeDefinitionTarget(value);
                    if (!out.definitionTargets.contains(normalized)) {
                        out.definitionTargets.add(normalized);
                    }
                }
            }
        } else {
            out.definitionTargets.addAll(CMDTargetItems.defaultDefinitionTargets());
        }
        return out;
    }
}
