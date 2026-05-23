import java.sql.*;
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class InventoryManagementSystem {
    private static final String URL = "jdbc:mysql://localhost:3306/inventorydb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final Scanner sc = new Scanner(System.in);
    private static Connection con;
    private static ProductDAO productDAO;
    private static CategoryDAO categoryDAO;
    private static SupplierDAO supplierDAO;

    public static void main(String[] args) {
        int attempts = 3;
        try {
            con = DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to database!");
            productDAO = new ProductDAO(con);
            categoryDAO = new CategoryDAO(con);
            supplierDAO = new SupplierDAO(con);

            while (true) {
                System.out.println("\n=== Inventory Management ===");
                System.out.println("1. Sign Up\n2. Login\n3. Exit");
                System.out.print("Enter your choice: ");
                int choice = sc.nextInt();
                sc.nextLine(); // consume newline

                if (choice == 1) {
                    // SIGN UP
                    String username;
                    while (true) {
                        System.out.print("Enter username: ");
                        username = sc.nextLine();
                        if (!username.isEmpty() && username.length() >= 3 && username.length() <= 15) {
                            break;
                        } else {
                            System.out.println("Username must be between 3 to 15 characters and cannot be empty.");
                        }
                    }
                    String password;
                    while (true) {
                        System.out.print("Create password (8-15 characters): ");
                        password = sc.nextLine();
                        if (password.length() >= 8 && password.length() <= 15) break;
                        System.out.println("Password must be between 8 to 15 characters.");
                    }
                    String role;
                    while (true) {
                        System.out.print("Enter role (admin/user): ");
                        role = sc.nextLine().toLowerCase();
                        if (role.equals("admin") || role.equals("user")) {
                            break;
                        } else {
                            System.out.println("Invalid role! Please enter 'admin' or 'user'.");
                        }
                    }

                    String insertQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
                    PreparedStatement pst = con.prepareStatement(insertQuery);
                    pst.setString(1, username);
                    pst.setString(2, password);
                    pst.setString(3, role);

                    int rowsInserted = pst.executeUpdate();
                    System.out.println(rowsInserted > 0 ? "Registration successful!" : "Registration failed!");

                } else if (choice == 2) {
                    // LOGIN
                    System.out.print("Enter username: ");
                    String username = sc.nextLine();
                    System.out.print("Enter password: ");
                    String password = sc.nextLine();

                    String loginQuery = "SELECT role FROM users WHERE username = ? AND password = ?";
                    PreparedStatement pst = con.prepareStatement(loginQuery);
                    pst.setString(1, username);
                    pst.setString(2, password);
                    ResultSet rs = pst.executeQuery();

                    if (rs.next()) {
                        String role = rs.getString("role");
                        System.out.println("Login successful as " + role.toUpperCase());
                        if (role.equals("admin")) {
                            adminMenu();
                        } else {
                            userMenu(username);
                        }
                    } else {
                        attempts--;
                        System.out.println("Invalid credentials. Attempts left: " + attempts);
                        if (attempts == 0) {
                            System.out.println("Too many failed attempts. Exiting...");
                            break;
                        }
                    }
                } else if (choice == 3) {
                    System.out.println("Exiting the system. Goodbye!");
                    break;
                } else {
                    System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    private static void adminMenu() {
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Product Management");
            System.out.println("2. Category Management");
            System.out.println("3. Supplier Management");
            System.out.println("4. View Stock Report");
            System.out.println("5. View Product Requests");
            System.out.println("6. Back to Main Menu");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    productManagementMenu();
                    break;
                case 2:
                    categoryManagementMenu();
                    break;
                case 3:
                    supplierManagementMenu();
                    break;
                case 4:
                    displayProducts();
                    break;
                case 5:
                    viewProductRequests();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    private static void productManagementMenu() {
        while (true) {
            System.out.println("\n--- Product Management ---");
            System.out.println("1. Add Product");
            System.out.println("2. View All Products");
            System.out.println("3. Update Product");
            System.out.println("4. Delete Product");
            System.out.println("5. Add Purchase Order (Stock In)");
            System.out.println("6. Add Sales Order (Stock Out)");
            System.out.println("7. View Purchase Orders");
            System.out.println("8. View Sales Orders");
            System.out.println("9. Back to Admin Menu");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    addProductFlow();
                    break;
                case 2:
                    displayProducts();
                    break;
                case 3:
                    updateProductFlow();
                    break;
                case 4:
                    deleteProductFlow();
                    break;
                case 5:
                    addPurchaseOrderFlow();
                    break;
                case 6:
                    addSalesOrderFlow();
                    break;
                case 7:
                    viewPurchaseOrders();
                    break;
                case 8:
                    viewSalesOrders();
                    break;
                case 9:
                    return;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    private static void categoryManagementMenu() {
        while (true) {
            System.out.println("\n--- Category Management ---");
            System.out.println("1. Add Category");
            System.out.println("2. View All Categories");
            System.out.println("3. Back to Admin Menu");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    addCategoryFlow();
                    break;
                case 2:
                    displayCategories();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    private static void supplierManagementMenu() {
        while (true) {
            System.out.println("\n--- Supplier Management ---");
            System.out.println("1. Add Supplier");
            System.out.println("2. View All Suppliers");
            System.out.println("3. Back to Admin Menu");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    addSupplierFlow();
                    break;
                case 2:
                    displaySuppliers();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }
    private static void viewProductRequests() {
        List<Map<String, String>> requests = productDAO.getAllProductRequests();

        System.out.println("\n=== Product Requests ===");
        System.out.println("-------------------------------------------------------------------------------");
        System.out.printf("%-12s %-25s %-20s %-15s %-10s\n",
                "Request ID", "Product Name", "Request Date", "Requested By", "Role");
        System.out.println("-------------------------------------------------------------------------------");

        if (requests.isEmpty()) {
            System.out.println("No product requests found.");
        } else {
            for (Map<String, String> req : requests) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                String formattedDate = sdf.format(Timestamp.valueOf(req.get("request_date")));

                System.out.printf("%-12s %-25s %-20s %-15s %-10s\n",
                        req.get("request_id"),
                        req.get("product_name").length() > 23 ?
                                req.get("product_name").substring(0, 20) + "..." : req.get("product_name"),
                        formattedDate,
                        req.get("requested_by"),
                        req.get("user_role"));

                if (!req.get("message").isEmpty()) {
                    System.out.println("   Message: " + req.get("message"));
                }
                System.out.println("-------------------------------------------------------------------------------");
            }
        }
    }

    private static void userMenu(String username) {
        while (true) {
            System.out.println("\n--- User Menu ---");
            System.out.println("1. View All Products");
            System.out.println("2. Search Product");
            System.out.println("3. Place Order");
            System.out.println("4. View Order History");
            System.out.println("5. Filter Products");
            System.out.println("6. Request Product");
            System.out.println("7. Generate Bill for Existing Order");
            System.out.println("8. Back to Main Menu");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    displayProducts();
                    break;
                case 2:
                    searchProduct();
                    break;
                case 3:
                    placeOrder(username);
                    break;
                case 4:
                    viewUserOrders(username);
                    break;
                case 5:
                    filterProducts();
                    break;
                case 6:
                    requestProduct(username);
                    break;
                case 7:
                    System.out.print("Enter Order ID to generate bill: ");
                    int orderId = sc.nextInt();
                    sc.nextLine();
                    createBill(username, orderId);
                    break;
                case 8:
                    return;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    private static void addProductFlow() {
        Product p = new Product();
        System.out.print("Enter Product Name: ");
        p.setProductName(sc.nextLine());
        System.out.print("Enter Description: ");
        p.setDescription(sc.nextLine());
        displayCategories();
        System.out.print("Enter Category ID: ");
        p.setCategoryId(sc.nextInt());
        displaySuppliers();
        System.out.print("Enter Supplier ID: ");
        p.setSupplierId(sc.nextInt());
        System.out.print("Enter Quantity In Stock: ");
        int quantity = sc.nextInt();
        while (quantity < 0) {
            System.out.print("Quantity cannot be negative. Enter Quantity In Stock: ");
            quantity = sc.nextInt();
        }
        p.setQuantityInStock(quantity);
        System.out.print("Enter Unit Price: ");
        double price = sc.nextDouble();
        while (price < 0) {
            System.out.print("Price cannot be negative. Enter Unit Price: ");
            price = sc.nextDouble();
        }
        p.setUnitPrice(price);
        sc.nextLine();
        boolean added = productDAO.addProduct(p);
        System.out.println(added ? "Product added successfully!" : "Error adding product.");
    }

    private static void displayProducts() {
        List<Product> products = productDAO.getAllProducts();
        System.out.println("\n--- Product List ---");
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }
        for (Product p : products) {
            System.out.printf("ID: %d | Name: %s | Qty: %d | Price: Rs.%.2f\n",
                    p.getProductId(), p.getProductName(), p.getQuantityInStock(), p.getUnitPrice());
        }
    }

    private static void updateProductFlow() {
        displayProducts();
        System.out.print("Enter Product ID to update: ");
        int id = sc.nextInt();
        sc.nextLine();

        Product p = productDAO.getProductById(id);
        if (p == null) {
            System.out.println("Product not found.");
            return;
        }

        System.out.printf("Current Name: %s. Enter new name (or press Enter to keep): ", p.getProductName());
        String name = sc.nextLine();
        if (!name.isEmpty()) {
            p.setProductName(name);
        }

        System.out.printf("Current Description: %s. Enter new description (or press Enter to keep): ", p.getDescription());
        String description = sc.nextLine();
        if (!description.isEmpty()) {
            p.setDescription(description);
        }

        System.out.printf("Current Quantity: %d. Enter new quantity (or -1 to keep): ", p.getQuantityInStock());
        int quantity = sc.nextInt();
        if (quantity != -1) {
            while (quantity < 0) {
                System.out.print("Quantity cannot be negative. Enter new quantity (or -1 to keep): ");
                quantity = sc.nextInt();
            }
            p.setQuantityInStock(quantity);
        }

        System.out.printf("Current Price: %.2f. Enter new price (or -1 to keep): ", p.getUnitPrice());
        double price = sc.nextDouble();
        if (price != -1) {
            while (price < 0) {
                System.out.print("Price cannot be negative. Enter new price (or -1 to keep): ");
                price = sc.nextDouble();
        }
            p.setUnitPrice(price);
        }
        sc.nextLine();

        boolean updated = productDAO.updateProduct(p);
        System.out.println(updated ? "Product updated successfully!" : "Error updating product.");
    }

    private static void deleteProductFlow() {
        displayProducts();
        System.out.print("Enter Product ID to delete: ");
        int id = sc.nextInt();
        sc.nextLine();
        boolean deleted = productDAO.deleteProduct(id);
        System.out.println(deleted ? "Product deleted successfully!" : "Product not found.");
    }

    private static void addPurchaseOrderFlow() {
        displayProducts();
        System.out.print("Enter Product ID: ");
        int productId = sc.nextInt();
        System.out.print("Enter Quantity to Add: ");
        int quantity = sc.nextInt();
        displaySuppliers();
        System.out.print("Enter Supplier ID: ");
        int supplierId = sc.nextInt();

        try {
            // Check if product exists
            Product product = productDAO.getProductById(productId);
            if (product != null) {
                // Update product stock
                int newQuantity = product.getQuantityInStock() + quantity;
                product.setQuantityInStock(newQuantity);
                productDAO.updateProduct(product);

                // Insert into purchaseorders table
                insertPurchaseOrder(productId, quantity, supplierId);
                System.out.println("Stock added and purchase order recorded successfully!");
            } else {
                System.out.println("Product not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding purchase order: " + e.getMessage());
        }
    }

    private static void addSalesOrderFlow() {
        displayProducts();
        System.out.print("Enter Product ID: ");
        int productId = sc.nextInt();
        System.out.print("Enter Quantity to Sell: ");
        int quantity = sc.nextInt();
        sc.nextLine(); // consume newline
        System.out.print("Enter Customer Name: ");
        String customerName = sc.nextLine();

        try {
            // Check if product exists and if there is enough stock
            Product product = productDAO.getProductById(productId);
            if (product != null) {
                if (product.getQuantityInStock() >= quantity) {
                    // Update product stock
                    int newQuantity = product.getQuantityInStock() - quantity;
                    product.setQuantityInStock(newQuantity);
                    productDAO.updateProduct(product);

                    // Insert into salesorders table
                    insertSalesOrder(productId, quantity, customerName);
                    System.out.println("Stock deducted and sales order recorded successfully!");
                } else {
                    System.out.println("Insufficient stock.");
                }
            } else {
                System.out.println("Product not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding sales order: " + e.getMessage());
        }
    }

    private static void insertPurchaseOrder(int productId, int quantity, int supplierId) throws SQLException {
        String query = "INSERT INTO purchaseorders (product_id, quantity, supplier_id) VALUES (?, ?, ?)";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, productId);
            pst.setInt(2, quantity);
            pst.setInt(3, supplierId);
            pst.executeUpdate();
        }
    }

    private static void insertSalesOrder(int productId, int quantity, String customerName) throws SQLException {
        String query = "INSERT INTO salesorders (product_id, quantity, customer_name) VALUES (?, ?, ?)";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, productId);
            pst.setInt(2, quantity);
            pst.setString(3, customerName);
            pst.executeUpdate();
        }
    }

    private static void viewPurchaseOrders() {
        String query = "SELECT po.purchase_id, p.ProductName, po.quantity, po.purchase_date, s.supplier_name " +
                "FROM purchaseorders po " +
                "JOIN products p ON po.product_id = p.ProductID " +
                "JOIN suppliers s ON po.supplier_id = s.supplier_id";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            System.out.println("\n=== Purchase Orders ===");
            while (rs.next()) {
                System.out.printf("ID: %d | Product: %s | Qty: %d | Date: %s | Supplier: %s\n",
                        rs.getInt("purchase_id"),
                        rs.getString("ProductName"),
                        rs.getInt("quantity"),
                        rs.getTimestamp("purchase_date"),
                        rs.getString("supplier_name"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching purchase orders: " + e.getMessage());
        }
    }

    private static void viewSalesOrders() {
        String query = "SELECT so.sales_id, p.ProductName, so.quantity, so.sales_date, so.customer_name " +
                "FROM salesorders so " +
                "JOIN products p ON so.product_id = p.ProductID";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            System.out.println("\n=== Sales Orders ===");
            while (rs.next()) {
                System.out.printf("ID: %d | Product: %s | Qty: %d | Date: %s | Customer: %s\n",
                        rs.getInt("sales_id"),
                        rs.getString("ProductName"),
                        rs.getInt("quantity"),
                        rs.getTimestamp("sales_date"),
                        rs.getString("customer_name"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching sales orders: " + e.getMessage());
        }
    }

    private static void searchProduct() {
        System.out.println("1. Search by ID\n2. Search by Name");
        System.out.print("Enter your choice: ");
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice == 1) {
            displayProducts();
            System.out.print("Enter Product ID: ");
            int id = sc.nextInt();
            sc.nextLine();
            Product p = productDAO.getProductById(id);
            if (p != null) {
                System.out.printf("ID: %d | Name: %s | Qty: %d | Price: Rs.%.2f\n",
                        p.getProductId(), p.getProductName(), p.getQuantityInStock(), p.getUnitPrice());
            } else {
                System.out.println("Product not found.");
            }
        } else if (choice == 2) {
            System.out.print("Enter product name to search: ");
            String name = sc.nextLine();
            List<Product> products = productDAO.searchProductsByName(name);
            if (products.isEmpty()) {
                System.out.println("No matching products found.");
            } else {
                for (Product p : products) {
                    System.out.printf("ID: %d | Name: %s | Qty: %d | Price: $%.2f\n",
                            p.getProductId(), p.getProductName(), p.getQuantityInStock(), p.getUnitPrice());
                }
            }
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private static void placeOrder(String username) {
        displayProducts();
        System.out.print("Enter Product ID to order: ");
        int productId = sc.nextInt();
        System.out.print("Enter quantity to order: ");
        int qty = sc.nextInt();
        sc.nextLine();

        int orderId = productDAO.placeOrder(productId, qty, username);
        if (orderId != -1) {
            System.out.println("Order placed successfully! Your Order ID is: " + orderId);
            System.out.println("Proceeding to billing...");
            createBill(username, orderId);
        } else {
            System.out.println("Order failed. Not enough stock or product not found.");
        }
    }

    private static void viewUserOrders(String username) {
        List<String> orders = productDAO.getUserOrders(username);
        System.out.println("\nYour Orders:");
        if (orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }
        for (String order : orders) {
            System.out.println(order);
        }
    }

    private static void createBill(String username, int orderid) {
        Map<String, Object> orderDetails = productDAO.getOrderDetailsWithProduct(orderid);

        if (orderDetails == null) {
            System.out.println("Order not found.");
            return;
        }
        // Check if the order belongs to the current user
        if (!orderDetails.get("placed_by").equals(username)) {
            System.out.println("You can only generate bills for your own orders.");
            return;
        }

        // Check if order is already paid or cancelled
        String status = (String) orderDetails.get("status");
        if ("paid".equals(status)) {
            System.out.println("This order has already been paid.");
            return;
        } else if ("cancelled".equals(status)) {
            System.out.println("This order has been cancelled.");
            return;
        }

        System.out.println("\n=== BILL DETAILS ===");
        System.out.println("Order ID: " + orderDetails.get("order_id"));
        System.out.println("Product: " + orderDetails.get("product_name"));
        System.out.println("Quantity: " + orderDetails.get("quantity"));
        System.out.println("Unit Price: Rs." + String.format("%.2f", orderDetails.get("unit_price")));
        System.out.println("-----------------------------------");

        double totalAmount = (Double) orderDetails.get("total_amount");
        System.out.println("TOTAL AMOUNT: Rs." + String.format("%.2f", totalAmount));
        System.out.println("Order Date: " + orderDetails.get("order_date"));
        System.out.println("===================================");

        // Proceed with payment

        System.out.println("Select Payment Method:");
        System.out.println("1. Cash");
        System.out.println("2. UPI");
        System.out.println("3. Card");
        System.out.print("Enter your choice: ");
        int paymentChoice = sc.nextInt();
        sc.nextLine();

        boolean paymentSuccess = false;

        switch (paymentChoice) {
            case 1:
                System.out.println("Payment of Rs." + String.format("%.2f", totalAmount) + " in cash accepted.");
                System.out.println("Bill generated successfully!");
                paymentSuccess = true;
                break;
            case 2:
                System.out.print("Enter UPI ID (format: name@bank): ");
                String upiId = sc.nextLine();
                if (upiId.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+$")) {
                    int otp = generateOtp();
                    writeOtpToFile(otp);
                    System.out.print("Enter the OTP sent to your email for payment of Rs." + String.format("%.2f", totalAmount) + ": ");
                    int enteredOtp = sc.nextInt();
                    paymentSuccess = (enteredOtp == otp) ;
                    if(paymentSuccess)
                    {
                        System.out.println("Payment of Rs." + String.format("%.2f", totalAmount) + " successful via UPI!");
                    } else {
                        System.out.println("Invalid OTP. Payment failed.");
                    }
                } else {
                    System.out.println("Invalid UPI ID format.");
                }
                break;
            case 3:
                System.out.print("Enter 16-digit Card Number: ");
                String cardNumber = sc.nextLine();
                if (cardNumber.matches("\\d{16}")) {
                    int otpCard = generateOtp();
                    writeOtpToFile(otpCard);
                    System.out.print("Enter the OTP sent to your email for payment of Rs." + String.format("%.2f", totalAmount) + ": ");
                    int enteredOtpCard = sc.nextInt();
                    paymentSuccess =(enteredOtpCard == otpCard);

                    if(paymentSuccess){
                        System.out.println("Payment of Rs." + String.format("%.2f", totalAmount) + " successful via Card!");
                    } else {
                        System.out.println("Invalid OTP. Payment failed.");
                    }
                } else {
                    System.out.println("Invalid Card Number. It should be 12 digits.");
                }
                break;
            case 4:
                System.out.println("Order cancelled.");
                productDAO.cancelOrder(orderid);
                return;
            default:
                System.out.println("Invalid payment method.");
                break;
        }

        // Handle payment result
        if (paymentSuccess) {
            // Update stock and mark order as paid
            boolean stockUpdated = productDAO.updateStockAfterPayment(orderid);
            if (stockUpdated) {
                System.out.println("Payment processed successfully! Stock updated.");
            } else {
                System.out.println("Payment successful but error updating stock. Please contact administrator.");
            }
        } else {
            // Payment failed, cancel the order
            productDAO.cancelOrder(orderid);
            System.out.println("Payment failed. Order has been cancelled.");
        }
    }

    private static int generateOtp() {
        Random random = new Random();
        return 1000 + random.nextInt(9000); // Generate a 4-digit OTP
    }

    private static void writeOtpToFile(int otp) {
        try (FileWriter writer = new FileWriter("otp.txt")) {
            writer.write("Your OTP is: " + otp);
        } catch (IOException e) {
            System.out.println("Error writing OTP to file: " + e.getMessage());
        }
    }

    private static void filterProducts() {
        System.out.println("1. Filter by Category\n2. Filter by Price Range");
        System.out.print("Enter your choice: ");
        int choice = sc.nextInt();
        sc.nextLine();

        List<Product> filteredProducts = new ArrayList<>();
        if (choice == 1) {
            displayCategories();
            System.out.print("Enter category ID: ");
            int catId = sc.nextInt();
            sc.nextLine();
            filteredProducts = productDAO.filterByCategory(catId);
        } else if (choice == 2) {
            System.out.print("Min price: ");
            double min = sc.nextDouble();
            System.out.print("Max price: ");
            double max = sc.nextDouble();
            sc.nextLine();
            filteredProducts = productDAO.filterByPriceRange(min, max);
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        if (filteredProducts.isEmpty()) {
            System.out.println("No matching products found.");
            return;
        }
        for (Product p : filteredProducts) {
            System.out.printf("ID: %d | Name: %s | Qty: %d | Price: $%.2f\n",
                    p.getProductId(), p.getProductName(), p.getQuantityInStock(), p.getUnitPrice());
        }
    }

    private static void requestProduct(String username) {
        System.out.print("Enter product name you want to request: ");
        String pname = sc.nextLine();
        System.out.print("Optional note (or press Enter): ");
        String msg = sc.nextLine();

        boolean success = productDAO.submitProductRequest(pname, username, msg);
        System.out.println(success ? "Request submitted successfully!" : "Request failed.");
    }

    private static void addCategoryFlow() {
        System.out.print("Enter Category Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Description: ");
        String desc = sc.nextLine();
        Category c = new Category(0, name, desc);
        boolean added = categoryDAO.addCategory(c);
        System.out.println(added ? "Category added successfully!" : "Failed to add category.");
    }

    private static void displayCategories() {
        List<Category> categories = categoryDAO.getAllCategories();
        if (categories.isEmpty()) {
            System.out.println("No categories found.");
        } else {
            System.out.println("\n--- All Categories ---");
            for (Category c : categories) {
                System.out.println(c);
            }
        }
    }
    private static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }
    private static boolean isValidPhone(String phone) {
        String phoneRegex = "^[6-9]\\d{9}$"; // Indian phone numbers starting with 6-9
        return Pattern.compile(phoneRegex).matcher(phone).matches();
    }
    private static String getValidInput(String prompt, String errorMessage, java.util.function.Predicate<String> validator) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = sc.nextLine().trim();
            if (validator.test(input)) {
                return input;
            } else {
                System.out.println(errorMessage);
            }
        }
    }
    private static void addSupplierFlow() {
        System.out.print("Enter Supplier Name: ");
        String name = sc.nextLine();

        String phone = getValidInput("Enter Phone Number: ", "Please enter a valid 10-digit Indian phone number starting with 6-9.", InventoryManagementSystem::isValidPhone);

        String email = getValidInput("Enter Email: ", "Please enter a valid email address.", InventoryManagementSystem::isValidEmail);
        System.out.print("Enter Address: ");
        String address = sc.nextLine();
        Supplier s = new Supplier(0, name, phone, email, address);
        boolean added = supplierDAO.addSupplier(s);
        System.out.println(added ? "Supplier added successfully!" : "Failed to add supplier.");
    }

    private static void displaySuppliers() {
        List<Supplier> suppliers = supplierDAO.getAllSuppliers();
        if (suppliers.isEmpty()) {
            System.out.println("No suppliers found.");
        } else {
            System.out.println("\n--- All Suppliers ---");
            for (Supplier s : suppliers) {
                System.out.println(s);
            }
        }
    }
}


class Product {
    private int productId;
    private String productName;
    private String description;
    private int categoryId;
    private int supplierId;
    private int quantityInStock;
    private double unitPrice;

    // Getters and Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}

class ProductDAO {
    private final Connection con;

    public ProductDAO(Connection con) {
        this.con = con;
    }

    public boolean addProduct(Product p) {
        String query = "INSERT INTO products (ProductName, Description, CategoryID, SupplierID,QuantityInStock,UnitPrice) VALUES (?, ?, ?, ?, ?,?)";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, p.getProductName());
            pst.setString(2, p.getDescription());
            pst.setInt(3, p.getCategoryId());
            pst.setInt(4, p.getSupplierId());
            pst.setInt(5, p.getQuantityInStock());
            pst.setDouble(6, p.getUnitPrice());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error adding product: " + e.getMessage());
            return false;
        }
    }

    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String query = "SELECT * FROM products";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Product p = new Product();
                p.setProductId(rs.getInt("ProductID"));
                p.setProductName(rs.getString("ProductName"));
                p.setDescription(rs.getString("Description"));
                p.setCategoryId(rs.getInt("CategoryID"));
                p.setSupplierId(rs.getInt("SupplierID"));
                p.setQuantityInStock(rs.getInt("QuantityInStock"));
                p.setUnitPrice(rs.getDouble("unitPrice"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching products: " + e.getMessage());
        }
        return list;
    }

    public Product getProductById(int id) {
        String query = "SELECT * FROM products WHERE ProductID = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Product p = new Product();
                    p.setProductId(rs.getInt("ProductID"));
                    p.setProductName(rs.getString("ProductName"));
                    p.setDescription(rs.getString("Description"));
                    p.setQuantityInStock(rs.getInt("QuantityInStock"));
                    p.setUnitPrice(rs.getDouble("UnitPrice"));
                    return p;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching product: " + e.getMessage());
        }
        return null;
    }

    public boolean updateProduct(Product p) {
        String query = "UPDATE products SET ProductName = ?, Description = ?, QuantityInStock = ?, UnitPrice = ?WHERE ProductID = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, p.getProductName());
            pst.setString(2, p.getDescription());
            pst.setInt(3, p.getQuantityInStock());
            pst.setDouble(4, p.getUnitPrice());
            pst.setInt(5, p.getProductId());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating product: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteProduct(int id) {
        String query = "DELETE FROM products WHERE ProductID = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting product: " + e.getMessage());
            return false;
        }
    }

    public List<Product> searchProductsByName(String name) {
        List<Product> list = new ArrayList<>();
        String query = "SELECT * FROM products WHERE ProductName LIKE ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, "%" + name + "%");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product();
                    p.setProductId(rs.getInt("ProductID"));
                    p.setProductName(rs.getString("ProductName"));
                    p.setQuantityInStock(rs.getInt("QuantityInStock"));
                    p.setUnitPrice(rs.getDouble("UnitPrice"));
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error searching products: " + e.getMessage());
        }
        return list;
    }

    public int placeOrder(int productId, int qty, String username) {
        try {
            con.setAutoCommit(false); // Start transaction

            // 1. Check current stock
            String checkQuery = "SELECT QuantityInStock FROM products WHERE ProductID = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkQuery);
            checkStmt.setInt(1, productId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                con.rollback();
                return -1; // Product not found
            }
            int availableQty = rs.getInt("QuantityInStock");
            if (qty > availableQty) {
                con.rollback();
                return -1; // Not enough stock
            }

            // 2. Insert order with "pending" status (don't reduce stock yet)
            String orderQuery = "INSERT INTO orders (product_id, quantity, placed_by, status) VALUES (?, ?, ?, 'pending')";
            PreparedStatement orderStmt = con.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, productId);
            orderStmt.setInt(2, qty);
            orderStmt.setString(3, username);
            orderStmt.executeUpdate();

            // 3. Get the generated order ID
            int orderId = -1;
            try (ResultSet generatedKeys = orderStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getInt(1);
                }
            }

            con.commit();
            return orderId;

        } catch (SQLException e) {
            try {
                con.rollback(); // Rollback on error
            } catch (SQLException ex) {
                System.out.println("Rollback failed: " + ex.getMessage());
            }
            System.out.println("Error placing order: " + e.getMessage());
            return -1;
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    public boolean updateStockAfterPayment(int orderId) {
        try {
            con.setAutoCommit(false);

            // 1. Get order details
            String getOrderQuery = "SELECT product_id, quantity FROM orders WHERE order_id = ?";
            PreparedStatement getOrderStmt = con.prepareStatement(getOrderQuery);
            getOrderStmt.setInt(1, orderId);
            ResultSet rs = getOrderStmt.executeQuery();

            if (!rs.next()) {
                con.rollback();
                return false;
            }

            int productId = rs.getInt("product_id");
            int quantity = rs.getInt("quantity");

            // 2. Update stock
            String updateQuery = "UPDATE products SET QuantityInStock = QuantityInStock - ? WHERE ProductID = ?";
            PreparedStatement updateStmt = con.prepareStatement(updateQuery);
            updateStmt.setInt(1, quantity);
            updateStmt.setInt(2, productId);
            updateStmt.executeUpdate();

            // 3. Update order status to "paid"
            String statusQuery = "UPDATE orders SET status = 'paid' WHERE order_id = ?";
            PreparedStatement statusStmt = con.prepareStatement(statusQuery);
            statusStmt.setInt(1, orderId);
            statusStmt.executeUpdate();

            con.commit();
            return true;

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                System.out.println("Rollback failed: " + ex.getMessage());
            }
            System.out.println("Error updating stock after payment: " + e.getMessage());
            return false;
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    public boolean cancelOrder(int orderId) {
        String query = "UPDATE orders SET status = 'cancelled' WHERE order_id = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, orderId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error cancelling order: " + e.getMessage());
            return false;
        }
    }


    public Map<String, Object> getOrderDetailsWithProduct(int orderId) {
        Map<String, Object> orderDetails = new HashMap<>();
        String query = "SELECT o.order_id, o.quantity, o.order_date, o.placed_by,o.status, " +
                "p.ProductID, p.ProductName, p.UnitPrice, p.UnitPrice * o.quantity as total_amount " +
                "FROM orders o " +
                "JOIN products p ON o.product_id = p.ProductID " +
                "WHERE o.order_id = ?";

        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, orderId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    orderDetails.put("order_id", rs.getInt("order_id"));
                    orderDetails.put("quantity", rs.getInt("quantity"));
                    orderDetails.put("order_date", rs.getTimestamp("order_date"));
                    orderDetails.put("placed_by", rs.getString("placed_by"));
                    orderDetails.put("status", rs.getString("status"));
                    orderDetails.put("product_id", rs.getInt("ProductID"));
                    orderDetails.put("product_name", rs.getString("ProductName"));
                    orderDetails.put("unit_price", rs.getDouble("UnitPrice"));
                    orderDetails.put("total_amount", rs.getDouble("total_amount"));
                    return orderDetails;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching order details: " + e.getMessage());
        }
        return null;
    }

    public List<String> getUserOrders(String username) {
        List<String> orders = new ArrayList<>();
        String query = "SELECT o.order_id, p.ProductName, o.quantity, o.order_date,o.status FROM orders o JOIN products p ON o.product_id = p.ProductID WHERE o.placed_by = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, username);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String a =rs.getString("status");
                    if(a.equals("paid")) {
                        orders.add(String.format("Order ID: %d | Product: %s | Qty: %d | Date: %s",
                                rs.getInt("order_id"),
                                rs.getString("ProductName"),
                                rs.getInt("quantity"),
                                rs.getTimestamp("order_date")));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user orders: " + e.getMessage());
        }
        return orders;
    }

    public List<Product> filterByCategory(int categoryId) {
        List<Product> list = new ArrayList<>();
        String query = "SELECT * FROM products WHERE CategoryID = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, categoryId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product();
                    p.setProductId(rs.getInt("ProductID"));
                    p.setProductName(rs.getString("ProductName"));
                    p.setQuantityInStock(rs.getInt("QuantityInStock"));
                    p.setUnitPrice(rs.getDouble("UnitPrice"));
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error filtering by category: " + e.getMessage());
        }
        return list;
    }

    public List<Product> filterByPriceRange(double min, double max) {
        List<Product> list = new ArrayList<>();
        String query = "SELECT * FROM products WHERE UnitPrice BETWEEN ? AND ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setDouble(1, min);
            pst.setDouble(2, max);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product();
                    p.setProductId(rs.getInt("ProductID"));
                    p.setProductName(rs.getString("ProductName"));
                    p.setQuantityInStock(rs.getInt("QuantityInStock"));
                    p.setUnitPrice(rs.getDouble("UnitPrice"));
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error filtering by price: " + e.getMessage());
        }
        return list;
    }

    public boolean submitProductRequest(String productName, String username, String message) {
        String query = "INSERT INTO product_requests (product_name, requested_by, message, request_date) VALUES (?, ?, ?, NOW())";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, productName);
            pst.setString(2, username);
            pst.setString(3, message);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error submitting product request: " + e.getMessage());
            return false;
        }
    }

    // In ProductDAO.java - Add this method
    public List<Map<String, String>> getAllProductRequests() {
        List<Map<String, String>> requests = new ArrayList<>();
        String query = "SELECT pr.request_id, pr.product_name, pr.message, pr.request_date, " +
                "u.username, u.role FROM product_requests pr " +
                "JOIN users u ON pr.requested_by = u.username " +
                "ORDER BY pr.request_date DESC";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Map<String, String> request = new HashMap<>();
                request.put("request_id", rs.getString("request_id"));
                request.put("product_name", rs.getString("product_name"));
                request.put("message", rs.getString("message"));
                request.put("request_date", rs.getTimestamp("request_date").toString());
                request.put("requested_by", rs.getString("username"));
                request.put("user_role", rs.getString("role"));
                requests.add(request);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching product requests: " + e.getMessage());
        }
        return requests;
    }

}

class Category {
    private int categoryId;
    private String categoryName;
    private String description;

    public Category() {}

    public Category(int categoryId, String categoryName, String description) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
    }

    // Getters and Setters
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("ID: %d | Name: %s | Description: %s", categoryId, categoryName, description);
    }
}


class CategoryDAO {
    private final Connection con;

    public CategoryDAO(Connection con) {
        this.con = con;
    }

    public boolean addCategory(Category c) {
        String query = "INSERT INTO categories (category_name, description) VALUES (?, ?)";
        try (PreparedStatement pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, c.getCategoryName());
            pst.setString(2, c.getDescription());
            int rowsInserted = pst.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        c.setCategoryId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error adding category: " + e.getMessage());
        }
        return false;
    }

    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String query = "SELECT * FROM categories";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Category c = new Category();
                c.setCategoryId(rs.getInt("category_id"));
                c.setCategoryName(rs.getString("category_name"));
                c.setDescription(rs.getString("description"));
                list.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching categories: " + e.getMessage());
        }
        return list;
    }
}

class Supplier {
    private int supplierId;
    private String supplierName;
    private String phone;
    private String email;
    private String address;

    public Supplier() {}

    public Supplier(int supplierId, String supplierName, String phone, String email, String address) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    // Getters and Setters
    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return String.format("ID: %d | Name: %s | Phone: %s | Email: %s | Address: %s", supplierId, supplierName, phone, email, address);
    }
}


class SupplierDAO {
    private final Connection con;

    public SupplierDAO(Connection con) {
        this.con = con;
    }

    public boolean addSupplier(Supplier s) {
        String query = "INSERT INTO suppliers (supplier_name, phone, email, address) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, s.getSupplierName());
            pst.setString(2, s.getPhone());
            pst.setString(3, s.getEmail());
            pst.setString(4, s.getAddress());
            int rowsInserted = pst.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        s.setSupplierId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error adding supplier: " + e.getMessage());
        }
        return false;
    }

    public List<Supplier> getAllSuppliers() {
        List<Supplier> list = new ArrayList<>();
        String query = "SELECT * FROM suppliers";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Supplier s = new Supplier();
                s.setSupplierId(rs.getInt("supplier_id"));
                s.setSupplierName(rs.getString("supplier_name"));
                s.setPhone(rs.getString("phone"));
                s.setEmail(rs.getString("email"));
                s.setAddress(rs.getString("address"));
                list.add(s);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching suppliers: " + e.getMessage());
        }
        return list;
    }
}