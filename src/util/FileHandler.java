package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private static final String DATA_DIR = "data";

    static {
        createDataDirectory();
    }

    private static void createDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static void writeToFile(String filename, String data) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIR + "/" + filename, true))) {
            writer.println(data);
        } catch (IOException e) {
            Logger.error("Failed to write to file " + filename + ": " + e.getMessage());
        }
    }

    public static List<String> readFromFile(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DIR + "/" + filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            Logger.info("File not found: " + filename + ". Creating new file.");
        } catch (IOException e) {
            Logger.error("Failed to read from file " + filename + ": " + e.getMessage());
        }
        return lines;
    }

    public static void clearFile(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIR + "/" + filename))) {
            writer.print("");
        } catch (IOException e) {
            Logger.error("Failed to clear file " + filename + ": " + e.getMessage());
        }
    }

    public static boolean fileExists(String filename) {
        return new File(DATA_DIR + "/" + filename).exists();
    }
}
