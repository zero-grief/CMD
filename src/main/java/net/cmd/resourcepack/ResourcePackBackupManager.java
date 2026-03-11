package net.cmd.resourcepack;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Conservative backup helper for CMD generated resource-pack archives.
 *
 * This class is intentionally small and does not introduce new workflow.
 * It only restores the file/class identity that was broken when this file
 * accidentally contained CMDResourcePackBuilder.
 */
public class ResourcePackBackupManager {

    /**
     * Creates a timestamped backup copy of the provided pack file, if it exists.
     *
     * The backup is written into a sibling "backups" directory.
     * Returns the created backup file, or null if no backup was made.
     */
    public static File backupIfPresent(File packFile) {
        try {
            if (packFile == null || !packFile.exists() || !packFile.isFile()) {
                return null;
            }

            File parent = packFile.getParentFile();
            if (parent == null) {
                return null;
            }

            File backupDir = new File(parent, "backups");
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
            String backupName = stripExtension(packFile.getName()) + "-" + timestamp + ".zip";
            File backupFile = new File(backupDir, backupName);

            Files.copy(packFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return backupFile;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String stripExtension(String name) {
        int dot = name.lastIndexOf('.');
        return dot >= 0 ? name.substring(0, dot) : name;
    }
}