package net.cmd.core;

import java.io.File;

public class CMDEnvironment {
    public static File getServerRoot() { return new File("."); }
    public static File getConfigFolder() { return new File("config/CMD"); }
    public static File getResourcepackFolder() { return new File("resourcepacks"); }
    public static File getWorldFolder() { return new File("world"); }
    public static File getModsFolder() { return new File("mods"); }
    public static File resolveFromServerRoot(String relativePath) { return new File(getServerRoot(), relativePath); }
}
