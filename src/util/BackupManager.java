package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class BackupManager {
    private static final String BACKUP_DIR = "backups";
    private static final String DATA_DIR = "data";

    public static void createBackup() {
        try {
            String timestamp = DateUtil.getCurrentDateTime().replace(":", "-").replace(" ", "_");
            String backupFolderName = "backup_" + timestamp;
            Path backupPath = Paths.get(BACKUP_DIR, backupFolderName);
            
            Files.createDirectories(backupPath);
            
            File dataDirectory = new File(DATA_DIR);
            if (dataDirectory.exists()) {
                copyDirectory(dataDirectory.toPath(), backupPath);
                Logger.log("Backup created successfully: " + backupFolderName);
            } else {
                Logger.warning("Data directory not found for backup");
            }
        } catch (IOException e) {
            Logger.error("Failed to create backup: " + e.getMessage());
        }
    }

    private static void copyDirectory(Path source, Path target) throws IOException {
        Files.walk(source).forEach(sourcePath -> {
            try {
                Path targetPath = target.resolve(source.relativize(sourcePath));
                if (Files.isDirectory(sourcePath)) {
                    Files.createDirectories(targetPath);
                } else {
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                Logger.error("Failed to copy: " + sourcePath + " - " + e.getMessage());
            }
        });
    }

    public static void restoreBackup(String backupFolderName) {
        try {
            Path backupPath = Paths.get(BACKUP_DIR, backupFolderName);
            Path dataPath = Paths.get(DATA_DIR);
            
            if (Files.exists(backupPath)) {
                Files.createDirectories(dataPath);
                copyDirectory(backupPath, dataPath);
                Logger.log("Backup restored successfully: " + backupFolderName);
            } else {
                Logger.error("Backup folder not found: " + backupFolderName);
            }
        } catch (IOException e) {
            Logger.error("Failed to restore backup: " + e.getMessage());
        }
    }
}
