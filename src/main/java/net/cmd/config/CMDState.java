package net.cmd.config;

/**
 * Compatibility access wrapper around the unified config build module.
 */
public class CMDState {

    public boolean startupBuildCompleted = false;

    public static CMDState fromMainConfig(CMDMainConfig config) {
        CMDState out = new CMDState();
        if (config != null && config.build != null) {
            out.startupBuildCompleted = config.build.startupBuildCompleted;
        }
        return out;
    }
}
