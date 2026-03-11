package net.cmd.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
    public static void zipFolder(File sourceDir, File zipFile) {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            zipDirectoryRecursive(sourceDir, sourceDir.toPath(), zos);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static void zipDirectoryRecursive(File current, Path rootPath, ZipOutputStream zos) throws Exception {
        File[] files = current.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                zipDirectoryRecursive(file, rootPath, zos);
                continue;
            }

            String entryName = rootPath.relativize(file.toPath()).toString().replace("\\", "/");
            zos.putNextEntry(new ZipEntry(entryName));
            try (FileInputStream fis = new FileInputStream(file)) {
                fis.transferTo(zos);
            }
            zos.closeEntry();
        }
    }
}
