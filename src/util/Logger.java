package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {
    private static final String LOG_FILE = "logs/system.log";
    private static boolean logToFile = true;
    private static boolean logToConsole = false; // Disabled by default for clean output
    private static boolean developmentMode = false;

    // Main logging method
    public static void log(String message) {
        String logEntry = DateUtil.getCurrentDateTime() + " - " + message;
        
        // Only show in console if enabled
        if (logToConsole) {
            System.out.println(logEntry);
        }
        
        // Always log to file (unless disabled)
        if (logToFile) {
            writeToFile(logEntry);
        }
    }

    // Error logging (can optionally show in console even when console logging is off)
    public static void error(String message) {
        String logEntry = DateUtil.getCurrentDateTime() + " - ERROR: " + message;
        
        // Errors might be shown in console for debugging
        if (logToConsole || developmentMode) {
            System.err.println(logEntry);
        }
        
        if (logToFile) {
            writeToFile(logEntry);
        }
    }

    // Info logging
    public static void info(String message) {
        String logEntry = DateUtil.getCurrentDateTime() + " - INFO: " + message;
        
        if (logToConsole) {
            System.out.println(logEntry);
        }
        
        if (logToFile) {
            writeToFile(logEntry);
        }
    }

    // Warning logging
    public static void warning(String message) {
        String logEntry = DateUtil.getCurrentDateTime() + " - WARNING: " + message;
        
        if (logToConsole) {
            System.out.println(logEntry);
        }
        
        if (logToFile) {
            writeToFile(logEntry);
        }
    }

    // Debug logging (only shows in development mode)
    public static void debug(String message) {
        if (developmentMode) {
            String logEntry = DateUtil.getCurrentDateTime() + " - DEBUG: " + message;
            
            if (logToConsole) {
                System.out.println(logEntry);
            }
            
            if (logToFile) {
                writeToFile(logEntry);
            }
        }
    }

    // Success logging (for important successful operations)
    public static void success(String message) {
        String logEntry = DateUtil.getCurrentDateTime() + " - SUCCESS: " + message;
        
        if (logToConsole) {
            System.out.println(logEntry);
        }
        
        if (logToFile) {
            writeToFile(logEntry);
        }
    }

    // Critical error logging (always shows regardless of settings)
    public static void critical(String message) {
        String logEntry = DateUtil.getCurrentDateTime() + " - CRITICAL: " + message;
        
        // Critical errors always show in console
        System.err.println(logEntry);
        
        if (logToFile) {
            writeToFile(logEntry);
        }
    }

    // Silent logging (only to file, never to console)
    public static void silent(String message) {
        if (logToFile) {
            String logEntry = DateUtil.getCurrentDateTime() + " - " + message;
            writeToFile(logEntry);
        }
    }

    // Private method to write to file
    private static void writeToFile(String message) {
        try {
            // Create logs directory if it doesn't exist
            File logDir = new File("logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            
            // Append to log file
            try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
                writer.println(message);
            }
        } catch (IOException e) {
            // Only show file write errors in development mode to avoid infinite loops
            if (developmentMode) {
                System.err.println("Failed to write to log file: " + e.getMessage());
            }
        }
    }

    // Configuration methods
    public static void setLogToFile(boolean enabled) {
        logToFile = enabled;
    }

    public static void setLogToConsole(boolean enabled) {
        logToConsole = enabled;
    }

    public static void setDevelopmentMode(boolean enabled) {
        developmentMode = enabled;
        if (enabled) {
            logToConsole = true; // Enable console logging in development mode
        }
    }

    // Preset configurations
    public static void enableProductionMode() {
        logToConsole = false;
        logToFile = true;
        developmentMode = false;
        silent("Logger configured for production mode");
    }

    public static void enableDevelopmentMode() {
        logToConsole = true;
        logToFile = true;
        developmentMode = true;
        log("Logger configured for development mode");
    }

    public static void enableSilentMode() {
        logToConsole = false;
        logToFile = true;
        developmentMode = false;
    }

    public static void disableAllLogging() {
        logToConsole = false;
        logToFile = false;
        developmentMode = false;
    }

    // Utility methods
    public static boolean isConsoleLoggingEnabled() {
        return logToConsole;
    }

    public static boolean isFileLoggingEnabled() {
        return logToFile;
    }

    public static boolean isDevelopmentMode() {
        return developmentMode;
    }

    // Method to clear log file
    public static void clearLogFile() {
        try {
            File logFile = new File(LOG_FILE);
            if (logFile.exists()) {
                try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, false))) {
                    writer.print(""); // Clear the file
                }
                silent("Log file cleared");
            }
        } catch (IOException e) {
            if (developmentMode) {
                System.err.println("Failed to clear log file: " + e.getMessage());
            }
        }
    }

    // Method to get log file size
    public static long getLogFileSize() {
        File logFile = new File(LOG_FILE);
        return logFile.exists() ? logFile.length() : 0;
    }

    // Method to create backup of current log
    public static void backupLogFile() {
        try {
            File logFile = new File(LOG_FILE);
            if (logFile.exists()) {
                String backupFileName = "logs/system_backup_" + 
                    DateUtil.getCurrentDate().replace("-", "_") + ".log";
                File backupFile = new File(backupFileName);
                
                // Copy content to backup file
                try (PrintWriter writer = new PrintWriter(new FileWriter(backupFile));
                     java.util.Scanner scanner = new java.util.Scanner(logFile)) {
                    
                    while (scanner.hasNextLine()) {
                        writer.println(scanner.nextLine());
                    }
                }
                
                silent("Log file backed up to: " + backupFileName);
            }
        } catch (IOException e) {
            error("Failed to backup log file: " + e.getMessage());
        }
    }

    // Method to log system information
    public static void logSystemInfo() {
        log("=== SYSTEM INFORMATION ===");
        log("Java Version: " + System.getProperty("java.version"));
        log("OS Name: " + System.getProperty("os.name"));
        log("OS Version: " + System.getProperty("os.version"));
        log("User: " + System.getProperty("user.name"));
        log("Working Directory: " + System.getProperty("user.dir"));
        log("Available Memory: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + " MB");
        log("=== END SYSTEM INFORMATION ===");
    }

    // Method to log with custom level
    public static void logWithLevel(String level, String message) {
        String logEntry = DateUtil.getCurrentDateTime() + " - " + level.toUpperCase() + ": " + message;
        
        if (logToConsole) {
            System.out.println(logEntry);
        }
        
        if (logToFile) {
            writeToFile(logEntry);
        }
    }
}
