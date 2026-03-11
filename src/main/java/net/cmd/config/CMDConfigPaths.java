package net.cmd.config;

import net.cmd.core.CMDEnvironment;

import java.io.File;

/**
 * Central config path definitions for CMD.
 */
public class CMDConfigPaths {

    public static final String CONFIG = "config.json";

    public static File getConfigFolder() {
        return CMDEnvironment.getConfigFolder();
    }

    public static File getMainConfigFile() {
        return new File(getConfigFolder(), CONFIG);
    }
}
