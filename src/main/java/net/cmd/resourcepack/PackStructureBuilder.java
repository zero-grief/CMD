package net.cmd.resourcepack;

import java.io.File;

public class PackStructureBuilder {
    public File createWorkTree(File workDir) {
        new File(workDir, "assets/minecraft/items").mkdirs();
        new File(workDir, "assets/cmd/lang").mkdirs();
        return workDir;
    }
}
