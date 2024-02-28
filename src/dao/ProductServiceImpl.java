package dao;
import ProductModel.Product;
import Utils.PaginatedList;
import menu.ClassUI;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProductServiceImpl implements ProductService{
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    private static final int PROGRESS_BAR_LENGTH = 100;
    static Scanner scanner=new Scanner(System.in);
    private static final String FILE_NAME = "products1.dat";
    private static final String FILE_NAME1 = "database.dat";
    private static final String BACKUP_DIR = "backup";
    String backupDir = "backup";
    String backupFileName = "database_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".bak";
    private static List<Product> products = new ArrayList<>();
    private static long time;
    public void randomProduct() {
        System.out.println("Enter the number of records you want to generate: ");
        int numberOfRecord = scanner.nextInt();
        Random random = new Random();
        for (int i =  0; i < numberOfRecord; i++) {
            String id = String.valueOf(random.nextInt(123456789) +  1);
            String name = "Product::" + id;
            double price = random.nextDouble() *  1000;
            Product product = new Product(id, name, price,12, LocalDate.now());
            products.add(product);
            int progress = (int) ((double) (i +  1) / numberOfRecord * PROGRESS_BAR_LENGTH);
            String progressBar = new String(new char[progress]).replace('\0', '=') + new String(new char[PROGRESS_BAR_LENGTH - progress]).replace('\0', ' ');
            System.out.print(ANSI_GREEN+"\rGenerating products: [" + progressBar + "] " + (int) ((double) (i +  1) / numberOfRecord *  100) + "%"+ANSI_RESET);
        }
        System.out.println();
        long start = System.currentTimeMillis();
        writeProductsToFileDatabase();
        long end = System.currentTimeMillis();
        System.out.println("\nTime taken to write products: " + (end - start)/1000 + "s");
    }

    static {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME1))) {
            long start = System.currentTimeMillis();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String id =parts[0].replaceAll("[']", "").split("=")[1];
                String name  = parts[1].replaceAll("[']", "").split("=")[1];
                double price = Double.parseDouble(parts[2].split("=")[1]);
                int quantity = Integer.parseInt(parts[3].split("=")[1]);
                String dateString = parts[4].split("=")[1].replace("}", ""); // Remove unnecessary character '}'
                LocalDate localDate = LocalDate.parse(dateString);
                Product product = new Product(id, name, price, quantity, localDate);
                products.add(product);
            }
            long end = System.currentTimeMillis();
            time = (end - start)/1000;
            System.out.println("Time taken to read products: " + time + "s");
        } catch (FileNotFoundException ex) {
            // Handle the case where the file does not exist
            try {
                new File(FILE_NAME).createNewFile();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void createProduct(Product product) {
        long start = System.currentTimeMillis();
        products.add(product);
        writeProductsToFile();
        long end = System.currentTimeMillis();
        System.out.println("Time taken to create product: " + (end - start) + "ms");
    }
    public void updateProduct(String id) {
        for (Product product : products) {
            if (product.getId().equals(id)) {
                ClassUI.Product(product);
                System.out.println("Enter   1 Update Id,   2 Update Name,   3 Update Price,   4 Update QTY,");
                String op=validate("Enter option=",new Scanner(System.in),"[1-4]+");
                int op1=Integer.parseInt(op);
                switch (op1)
                {
                    case 1->{
                        System.out.print("Please enter new ID=");
                        product.setId(scanner.nextLine());
                    }
                    case 2-> {
                        System.out.print("Please enter new Product Name=");
                        product.setName(scanner.nextLine());
                    }
                    case 3-> {
                        System.out.print("Please enter new Product Price=");
                        product.setPrice(scanner.nextDouble());
                    }
                    case 4-> {
                        System.out.print("Please Enter New Product QTY=");
                        product.setQty(scanner.nextInt());
                    }
                }
                ;
                String message=validate("Please enter <Yes> or <No> for delete product=",scanner,"^(?:Yes|No)$");
                if(message.equalsIgnoreCase("yes"))
                {
                    ClassUI.Product(product);
                }else{
                    System.out.println("Thanks You Product already cancel");
                }
                break;
            }
        }
        writeProductsToFile();
    }

    public  void deleteProduct(String id) {
        boolean productFound = false;
        for (Product product : products) {
            if (product.getId().equals(id)) {
                productFound = true;
                ClassUI.Product(product);
                break;
            }
        }
        if (!productFound) {
            System.out.println("Product with ID " + id + " not found.");
            return;
        }
        String message=validate("Please enter <Yes> or <No> for delete product=",scanner,"^(?i:Yes|No)$");
        if(message.equalsIgnoreCase("yes"))
        {
            products.removeIf(product -> product.getId().equals(id));
            writeProductsToFile();
        }else{
            System.out.println("Thanks You Product already cancel");
        }

    }

    public  Product readProduct(String id) {
        for (Product product : products) {
            if (product.getId().equals(id)) {
                ClassUI.Product(product);
            }
        }
        return null;
    }
    public Product readProductByName(String name) {
        for (Product product : products) {
            if (product.getName().equalsIgnoreCase(name)) {
                ClassUI.Product(product);
            }
        }
        return null;
    }
    public void  showProducts() {
        int DefaultPage=5;
        PaginatedList<Product> paginatedProducts = new PaginatedList<>(products,   DefaultPage); // Show   5 products per page
        Scanner scanner = new Scanner(System.in);
        while (true) {
            List<Product> currentPageProducts = paginatedProducts.getPage(paginatedProducts.currentPage +   1);
            ClassUI.viewProductList(currentPageProducts);
            System.out.print(ANSI_GREEN+"*");
            System.out.println(ANSI_GREEN+"~".repeat(130)+ANSI_RESET);
            System.out.println("Total Recode="+products.size()+"                                                                                          Page " + (((PaginatedList<Product>) paginatedProducts).currentPage +   1) + " of " + paginatedProducts.numberOfPages());
            System.out.println("Enter   (n) for next page,   (p) for previous page,   (f) for first page,   (l) for last page,   (g) to go to a specific page,   (e) to exit:");
            System.out.print(ANSI_GREEN+"*");
            System.out.println(ANSI_GREEN+"~".repeat(130)+ANSI_RESET);
            String choice= validate("<<<<<<<<<<<<<< Please enter Option=",scanner,"[a-zA-Z]+");
            switch (choice) {
                case   "n": // Next page
                    paginatedProducts.nextPage();
                    break;
                case   "p": // Previous page
                    paginatedProducts.previousPage();
                    break;
                case   "f": // First page
                    paginatedProducts.currentPage =   0;
                    break;
                case   "l": // Last page
                    paginatedProducts.currentPage = paginatedProducts.numberOfPages() -  1;
                    break;
                case   "g": // Go to a specific page
                    System.out.print("Enter the page number: ");
                    int pageNumber = scanner.nextInt();
                    scanner.nextLine(); // Consume newline left-over
                    if (pageNumber >=  1 && pageNumber <= paginatedProducts.numberOfPages()) {
                        ((PaginatedList<Product>) paginatedProducts).currentPage = pageNumber -  1;
                    } else {
                        System.out.println("Invalid page number.");
                    }
                    break;
                   case   "e": // Exit
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

        }

    }
    public void writeProductsToFileDatabase() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FILE_NAME1))) {
            for (Product product : products) {
                bufferedWriter.write(product.toString() + "\n");
            }
            System.out.println("Data has been successfully written to the file.");
        } catch (IOException e) {
            System.err.println("Error writing data to the file: " + e.getMessage());
        }
    }
    public void writeProductsToFile() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Product product : products) {
                bufferedWriter.write(product.toString() + "\n");
            }
            System.out.println("Data has been successfully written to the file.");
        } catch (IOException e) {
            System.err.println("Error writing data to the file: " + e.getMessage());
        }
    }
    public void Display()
    {
        ClassUI.viewProductList(products);
    }
    public String validate(String message, Scanner scanner, String regex)
    {
        while(true)
        {
            System.out.print(message);
            String userInput=scanner.nextLine();
            Pattern pattern=Pattern.compile(regex);
            if(pattern.matcher(userInput).matches())
            {
                return userInput;
            }else{
                System.out.println("Invalid format !");
            }
        }
    }
    public void SetRow() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please Enter Number of Product Row=");
        int row=scanner.nextInt();
        PaginatedList<Product> paginatedProducts1 = new PaginatedList<>(products,   row); // Show   5 products per page
        while (true) {
            System.out.println("Page " + (((PaginatedList<Product>) paginatedProducts1).currentPage +   1) + " of " + paginatedProducts1.numberOfPages());
            List<Product> currentPageProducts = paginatedProducts1.getPage(paginatedProducts1.currentPage +   1);
            ClassUI.viewProductList(currentPageProducts);
            System.out.print("*");
            System.out.println("~".repeat(130));
            System.out.println("Total Recode="+products.size());
            System.out.println("Enter   n for next page,   p for previous page,   f for first page,   l for last page,   g to go to a specific page,   e to exit:");
            System.out.print("*");
            System.out.println("~".repeat(130));
            String choice= validate("<<<<<<<<<<<<<< Please enter Option=",scanner,"[a-zA-Z]+");
            switch (choice) {
                case   "n": // Next page
                    paginatedProducts1.nextPage();
                    break;
                case   "p": // Previous page
                    paginatedProducts1.previousPage();
                    break;
                case   "f": // First page
                    paginatedProducts1.currentPage =   0;
                    break;
                case   "l": // Last page
                    paginatedProducts1.currentPage = paginatedProducts1.numberOfPages() -  1;
                    break;
                case   "g": // Go to a specific page
                    System.out.print("Enter the page number: ");
                    int pageNumber = scanner.nextInt();
                    scanner.nextLine(); // Consume newline left-over
                    if (pageNumber >=  1 && pageNumber <= paginatedProducts1.numberOfPages()) {
                        ((PaginatedList<Product>) paginatedProducts1).currentPage = pageNumber -  1;
                    } else {
                        System.out.println("Invalid page number.");
                    }
                    break;
                case   "e": // Exit
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    public void BackupProducts() {
        File dir = new File(backupDir);
        if (!dir.exists()) {
            dir.mkdir();
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME1));
             BufferedWriter writer = new BufferedWriter(new FileWriter(backupDir + File.separator + backupFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Backup created: " + backupDir + File.separator + backupFileName);
        } catch (IOException e) {
            System.err.println("Backup failed: " + e.getMessage());
        }
    }
    public void commitData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME));
             BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME1))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }

            System.out.println("Data committed successfully from " + FILE_NAME + " to " + FILE_NAME1);
        } catch (IOException e) {
            System.err.println("Error committing data: " + e.getMessage());
        }
        // Clear the contents of products.dat
        clearFile(FILE_NAME);
    }

    public void clearFile(String filePath) {
        try {
            Files.write(Paths.get(filePath), "".getBytes());
        } catch (IOException e) {
            System.err.println("Error clearing file: " + e.getMessage());
        }
    }

    public void checkDataCommit() {
        File productFile = new File(FILE_NAME);
        if (productFile.length() == 0) {
            System.out.println("");
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Do you want to commit data changes before exiting? ((y)es/no): ");
            String confirm = scanner.nextLine().toLowerCase();
            if (confirm.equalsIgnoreCase("y")) {
                commitData(); // Assuming this is the method to commit data
            } else {
                clearFile(FILE_NAME);
            }
        }
    }
//    public void restoreProducts() {
//        File backupDir = new File(BACKUP_DIR);
//        if (!backupDir.exists() || !backupDir.isDirectory()) {
//            System.out.println("Backup directory does not exist.");
//            return;
//        }
//        File[] backupFiles = backupDir.listFiles((dir, name) -> name.endsWith(".bak"));
//        if (backupFiles == null || backupFiles.length == 0) {
//            System.out.println("No backup files found.");
//            return;
//        }
//
//        List<String> backupFileNames = Arrays.stream(backupFiles)
//                .map(File::getName)
//                .collect(Collectors.toList());
//
//        System.out.println("Backup files:");
//        for (int i = 0; i < backupFileNames.size(); i++) {
//            System.out.println((i + 1) + ". " + backupFileNames.get(i));
//        }
//
//        System.out.print("Select a backup file to restore (enter the number): ");
//        Scanner scanner = new Scanner(System.in);
//        int selectedIndex;
//        try {
//            selectedIndex = scanner.nextInt() - 1;
//        } catch (NumberFormatException e) {
//            System.out.println("Invalid input. Please enter a number.");
//            return;
//        }
//
//        if (selectedIndex < 0 || selectedIndex >= backupFileNames.size()) {
//            System.out.println("Invalid selection.");
//            return;
//        }
//        File selectedBackupFile = backupFiles[selectedIndex];
//        try (BufferedReader reader = new BufferedReader(new FileReader(selectedBackupFile));
//             FileWriter fileWriter = new FileWriter(FILE_NAME1, true)) { // true for append mode
//            String line;
//            while ((line = reader.readLine()) != null) {
//                fileWriter.write(line + System.lineSeparator());
//            }
//            System.out.println("Restored from: " + selectedBackupFile.getName());
//        } catch (IOException e) {
//            System.out.println("Restore failed: " + e.getMessage());
//        }
//    }
public void restoreProducts() {
    List<Product> restoredData = new ArrayList<>();
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
    } catch (InputMismatchException e) {
        System.out.println("Invalid input. Please enter a number.");
        return;
    }

    if (selectedIndex < 0 || selectedIndex >= backupFileNames.size()) {
        System.out.println("Invalid selection.");
        return;
    }
    File selectedBackupFile = backupFiles[selectedIndex];
    try (BufferedReader reader = new BufferedReader(new FileReader(selectedBackupFile))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            String id =parts[0].replaceAll("[']", "").split("=")[1];
            String name  = parts[1].replaceAll("[']", "").split("=")[1];
            double price = Double.parseDouble(parts[2].split("=")[1]);
            int quantity = Integer.parseInt(parts[3].split("=")[1]);
            String dateString = parts[4].split("=")[1].replace("}", ""); // Remove unnecessary character '}'
            LocalDate localDate = LocalDate.parse(dateString);
            Product product = new Product(id, name, price, quantity, localDate);
           restoredData.add(product);
        }
    } catch (IOException e) {
        System.out.println("Restore failed: " + e.getMessage());
        return;
    }

// Override the existing data in the file with the restored data
    try (FileWriter fileWriter = new FileWriter(FILE_NAME1)) {
        for (Product product : restoredData) {
            fileWriter.write(product.toString() + System.lineSeparator());
        }
        System.out.println("Restored from: " + selectedBackupFile.getName());
    } catch (IOException e) {
        System.out.println("Error writing restored data: " + e.getMessage());
        return;
    }
    products.clear();
    products.addAll(restoredData);


}
}

