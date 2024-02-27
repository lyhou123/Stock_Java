package Utils;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class RestoreData {
    private static final String FILE_NAME = "products1.dat";
    private static final String BACKUP_DIR = "backup";

    public void restoreProducts() {
        File backupDir = new File(BACKUP_DIR);
        if (!backupDir.exists() || !backupDir.isDirectory()) {
            System.out.println("Backup directory does not exist.");
            return;
        }

        File[] backupFiles = backupDir.listFiles((dir, name) -> name.endsWith(".bak"));
        if (backupFiles == null || backupFiles.length == 0) {
            System.out.println("No backup files found.");
            return;
        }

        List<String> backupFileNames = Arrays.stream(backupFiles)
                .map(File::getName)
                .collect(Collectors.toList());

        System.out.println("Backup files:");
        for (int i = 0; i < backupFileNames.size(); i++) {
            System.out.println((i + 1) + ". " + backupFileNames.get(i));
        }

        System.out.print("Select a backup file to restore (enter the number): ");
        Scanner scanner = new Scanner(System.in);
        int selectedIndex;
        try {
            selectedIndex = scanner.nextInt() - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        if (selectedIndex < 0 || selectedIndex >= backupFileNames.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        File selectedBackupFile = backupFiles[selectedIndex];
        try (BufferedReader reader = new BufferedReader(new FileReader(selectedBackupFile));
             FileWriter fileWriter = new FileWriter(FILE_NAME)) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileWriter.write(line + System.lineSeparator());
            }
            System.out.println("Restored from: " + selectedBackupFile.getName());
        } catch (IOException e) {
            System.out.println("Restore failed: " + e.getMessage());
        }
    }
}
