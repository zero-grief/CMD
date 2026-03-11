package net.cmd.resourcepack;

import java.io.File;

/**
 * Builds and prepares CMD's temporary/generated pack directory structure.
 */
public class PackStructureBuilder {

    /**
     * Creates the basic work tree used by the generated pack.
     */
    public File createWorkTree(File workDir) {
        new File(workDir, "assets/minecraft/items").mkdirs();
        new File(workDir, "assets/cmd/lang").mkdirs();
        return workDir;
    }

    /**
     * Prepares a fresh build directory on disk and returns it.
     *
     * This method exists because CMDResourcePackBuilder already expects it.
     * It restores that expected helper API without changing project direction.
     */
    public static File prepareFreshBuildDirectory() {
        File buildDir = new File("build/cmd-generated-pack");

        if (buildDir.exists()) {
            deleteRecursive(buildDir);
        }

        buildDir.mkdirs();
        return new PackStructureBuilder().createWorkTree(buildDir);
    }

    private static void deleteRecursive(File file) {
        if (file == null || !file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursive(child);
                }
            }
        }

        file.delete();
    }
}