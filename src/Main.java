import Controller.DataCommitService;
import Controller.ProductManager;
import ProductModel.Product;
import Utils.BackupData;
import Utils.RestoreData;
import Utils.Tables;
import exception.ProductInputValidator;
import menu.ClassUI;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public void run()
    {
        Scanner scanner = new Scanner(System.in);
        ProductManager productManager = new ProductManager();
        DataCommitService dataCommitService=new DataCommitService();
        dataCommitService.checkDataCommit();
        RestoreData restoreData = new RestoreData();
        String op;
        while (true) {
            ClassUI.MainMenu();
            op = ProductManager.validate("Enter Option=", scanner, "[a-zA-Z]");
            switch (op) {
                case "l"->productManager.showProducts();
                case  "w"-> {
                    String id = ProductInputValidator.getStringInput1("Enter Product ID: ");
                    String name = ProductInputValidator.getStringInput("Enter Product Name: ");
                    double price = ProductInputValidator.getDoubleInput("Enter Product Price: ");
                    int qty = ProductInputValidator.getIntegerInput("Enter Product Quantity: ");
                    ProductManager.createProduct(new Product(id, name, price, qty, LocalDate.now()));
                }
                case  "u"-> { // Update a product
                    System.out.print("Enter ID of the product to update: ");
                    String updateId = scanner.nextLine();
                    productManager.updateProduct(updateId);
                }
                case  "d"-> { // Delete a product
                    System.out.print("Enter ID of the product to delete: ");
                    String deleteId = scanner.nextLine();
                    productManager.deleteProduct(deleteId);
                }
                case  "s"-> {
                    List<Product> products=new ArrayList<>();// Search for a product
                    System.out.println("1. Search By Product ID       2. Search By Product Name");
                    String op2=ProductManager.validate("Enter option=",new Scanner(System.in),"[1-4]+");
                    int op1=Integer.parseInt(op2);
                    Product product = null;
                    switch (op1) {
                        case 1-> {
                            System.out.print("Enter ID for Search=");
                            String id =scanner.nextLine();
                            product = ProductManager.readProduct(id);
                        }
                        case 2->{
                            String name  = ProductInputValidator.getStringInput("Enter Product Name: ");
                            product = ProductManager.readProductByName(name);
                        }
                    }
                    if (product != null) {
                        products.add(product);
                        ClassUI.viewProductList(products);
                        System.out.println("Press Key To Continuous");
                        scanner.nextLine();
                    } else {
                        System.out.println("ProductModel.Product not found.");
                    }
                }
                case  "e"->{
                    dataCommitService.checkDataCommit();
                    scanner.close();
                    return ;

                }
                case  "k"->BackupData.backupProducts();
                case "r"->productManager.SetRow();
                case  "m"->productManager.randomProduct();
                case  "t"-> restoreData.restoreProducts();
                case "h"->ClassUI.displayHelp();
                case "c"->dataCommitService.commitData();
                default->
                        System.out.println("Invalid choice. Please try again.");
            }
        }

    }
    public static void main(String[] args) {
      new Main().run();
    }
}
