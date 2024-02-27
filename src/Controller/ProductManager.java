package Controller;

import ProductModel.Product;
import Utils.PaginatedList;
import menu.ClassUI;


import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ProductManager {
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    private static final int PROGRESS_BAR_LENGTH = 100;
    Scanner scanner=new Scanner(System.in);
    private static final String FILE_NAME = "products1.dat";
    private static final String FILE_NAME1 = "database.dat";
    private static List<Product> products = new ArrayList<>();
    private static long time;
    public void randomProduct() {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter the number of records you want to generate: ");
        int numberOfRecord = input.nextInt();
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
        writeProductsToFile();
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

//    public void randomProduct() {
//        Scanner input = new Scanner(System.in);
//        System.out.println("Enter the number of records you want to generate: ");
//        int numberOfRecord = input.nextInt();
//        Random random = new Random();
//        for (int i =  0; i <numberOfRecord; i++) {
//            String id = String.valueOf(random.nextInt(123456789) +  1);// Generate a random ID between  1 and  1,000,000
//            String name = "Product::" + id; // Simple name generation for demonstration
//            double price = random.nextDouble() *  1000; // Generate a random price between  0 and  1000
//            Product product = new Product(id, name, price,12, LocalDate.now());
//            products.add(product);
//        }
//        long start = System.currentTimeMillis();
//        writeProductsToFile();
//        long end = System.currentTimeMillis();
//        System.out.println("Time taken to generate and write products: " + (end - start)+ "ms "+" "+ (end-start)/1000+" s");
//    }
    public static void createProduct(Product product) {
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

    public static Product readProduct(String id) {
        for (Product product : products) {
            if (product.getId().equals(id)) {
                return product;
            }
        }
        return null;
    }
    public static Product readProductByName(String name) {
        for (Product product : products) {
            if (product.getName().equalsIgnoreCase(name)) {
                return product;
            }
        }
        return null;
    }
    public void showProducts() {
        // Assuming 'products' is a List<Product> containing all your products
        int DefaultPage=5;
        PaginatedList<Product> paginatedProducts = new PaginatedList<>(products,   DefaultPage); // Show   5 products per page
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Page " + (((PaginatedList<Product>) paginatedProducts).currentPage +   1) + " of " + paginatedProducts.numberOfPages());
            List<Product> currentPageProducts = paginatedProducts.getPage(paginatedProducts.currentPage +   1);
            ClassUI.viewProductList(currentPageProducts);
            System.out.print("*");
            System.out.println("~".repeat(130));
            System.out.println("Total Recode="+products.size());
            System.out.println("Enter   1 for next page,   2 for previous page,   3 for first page,   4 for last page,   5 to go to a specific page,   6 to exit:");
            System.out.print("*");
            System.out.println("~".repeat(130));
            String choice1= validate("<<<<<<<<<<<<<< Please enter Option=",scanner,"[1-6]+");
            int choice=Integer.parseInt(choice1);
            switch (choice) {
                case   1: // Next page
                    paginatedProducts.nextPage();
                    break;
                case   2: // Previous page
                    paginatedProducts.previousPage();
                    break;
                case   3: // First page
                    paginatedProducts.currentPage =   0;
                    break;
                case   4: // Last page
                    paginatedProducts.currentPage = paginatedProducts.numberOfPages() -  1;
                    break;
                case   5: // Go to a specific page
                    System.out.print("Enter the page number: ");
                    int pageNumber = scanner.nextInt();
                    scanner.nextLine(); // Consume newline left-over
                    if (pageNumber >=  1 && pageNumber <= paginatedProducts.numberOfPages()) {
                        ((PaginatedList<Product>) paginatedProducts).currentPage = pageNumber -  1;
                    } else {
                        System.out.println("Invalid page number.");
                    }
                    break;
                case   6: // Exit
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    private static void writeProductsToFile() {
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
    public static String validate(String message, Scanner scanner, String regex)
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
            System.out.println("Enter   1 for next page,   2 for previous page,   3 for first page,   4 for last page,   5 to go to a specific page,   6 to exit:");
            System.out.print("*");
            System.out.println("~".repeat(130));
            String choice1= validate("<<<<<<<<<<<<<< Please enter Option=",scanner,"[1-6]+");
            int choice=Integer.parseInt(choice1);
            switch (choice) {
                case   1: // Next page
                    paginatedProducts1.nextPage();
                    break;
                case   2: // Previous page
                    paginatedProducts1.previousPage();
                    break;
                case   3: // First page
                    paginatedProducts1.currentPage =   0;
                    break;
                case   4: // Last page
                    paginatedProducts1.currentPage = paginatedProducts1.numberOfPages() -  1;
                    break;
                case   5: // Go to a specific page
                    System.out.print("Enter the page number: ");
                    int pageNumber = scanner.nextInt();
                    scanner.nextLine(); // Consume newline left-over
                    if (pageNumber >=  1 && pageNumber <= paginatedProducts1.numberOfPages()) {
                        ((PaginatedList<Product>) paginatedProducts1).currentPage = pageNumber -  1;
                    } else {
                        System.out.println("Invalid page number.");
                    }
                    break;
                case   6: // Exit
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}

