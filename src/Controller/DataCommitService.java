package Controller;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
public class DataCommitService {
    private static final String PRODUCTS_FILE = "products1.dat";
    private static final String DATABASE_FILE = "database.dat";

    public void commitData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCTS_FILE));
             BufferedWriter writer = new BufferedWriter(new FileWriter(DATABASE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }

            System.out.println("Data committed successfully from " + PRODUCTS_FILE + " to " + DATABASE_FILE);
        } catch (IOException e) {
            System.err.println("Error committing data: " + e.getMessage());
        }
        // Clear the contents of products.dat
        clearFile(PRODUCTS_FILE);
    }

    private void clearFile(String filePath) {
        try {
            Files.write(Paths.get(filePath), "".getBytes());
        } catch (IOException e) {
            System.err.println("Error clearing file: " + e.getMessage());
        }
    }

    public void checkDataCommit() {
        File productFile = new File(PRODUCTS_FILE);
        if (productFile.length() == 0) {
            System.out.println("");
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Do you want to commit data changes before exiting? ((y)es/no): ");
            String confirm = scanner.nextLine().toLowerCase();
            if (confirm.equalsIgnoreCase("y")) {
                commitData(); // Assuming this is the method to commit data
            } else {
                clearFile(PRODUCTS_FILE);
            }
        }
    }
}
