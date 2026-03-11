package net.cmd.resourcepack;

import java.io.File;

public class ResourcePackBackupManager {
    public File backup(File worldFolder) {
        File current = new File(worldFolder, "resources.zip");
        if (!current.exists()) return current;

        int index = 1;
        File target;
        do {
            target = new File(worldFolder, "resources-pre-cmd" + index + ".zip");
            index++;
        } while (target.exists());

        current.renameTo(target);
        return target;
    }
}
