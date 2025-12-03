package project212;

import java.time.LocalDate;
import java.util.Scanner;
import java.util.Stack;



public class E_commerce {
    // ==== Lists (shared) ====
    private static LinkedList<Customer> customers_list;
    private static LinkedList<Order>    orders_list;
    private static LinkedList<Product>  products_list;
    private static LinkedList<Review>   reviews_list;

    // ==== Managers ====
    private static Reviews   all_Reviews;
    private static Customers all_Customers;
    private static Orders    all_Orders;
    private static Products  all_products;

    private static final Scanner input = new Scanner(System.in);

    // ==== CSV paths====
    private static final String BASE_PATH     = "C:\\Users\\danam\\Desktop\\project212\\";
    private static final String PRODUCTS_CSV  = BASE_PATH + "prodcuts.csv";  
    private static final String CUSTOMERS_CSV = BASE_PATH + "customers.csv";
    private static final String ORDERS_CSV    = BASE_PATH + "orders.csv";
    private static final String REVIEWS_CSV   = BASE_PATH + "reviews.csv";

    // ==== Auto-load once ====
    private static boolean dataLoaded = false;
    private static void ensureLoaded() {
        if (!dataLoaded) {
            Load_all();
            dataLoaded = true;
        }
    }

    // ==== actor ====
    public E_commerce() {
        customers_list = new LinkedList<>();
        orders_list    = new LinkedList<>();
        products_list  = new LinkedList<>();
        reviews_list   = new LinkedList<>();

        all_products   = new Products(products_list);
        all_Customers  = new Customers(customers_list);
        all_Orders     = new Orders(customers_list, orders_list);
        all_Reviews    = new Reviews(reviews_list, products_list, customers_list);
    }

    // ==== Load from CSVs ====
    public static void Load_all() {
        all_products.loadProducts(PRODUCTS_CSV);
        all_Customers.loadCustomers(CUSTOMERS_CSV);
        all_Orders.loadOrders(ORDERS_CSV);
        all_Reviews.load_revews(REVIEWS_CSV);

        System.out.println("Files loaded from:");
        System.out.println("  Products : " + PRODUCTS_CSV);
        System.out.println("  Customers: " + CUSTOMERS_CSV);
        System.out.println("  Orders   : " + ORDERS_CSV);
        System.out.println("  Reviews  : " + REVIEWS_CSV);
        System.out.println("-----------------------------------");
    }

    // ==== Safe add wrappers (guarded by ensureLoaded) ====
    public static void add_Customer(Customer c) { ensureLoaded(); all_Customers.addCustomer(c); }
    public static void add_Product(Product p)   { ensureLoaded(); all_products.addProduct(p);  }
    public static void add_Order(Order o)       { ensureLoaded(); all_Orders.addOrder(o);      }
    public static void add_Review(Review r)     { ensureLoaded(); all_Reviews.addReview(r);    }

    // ==== Features ====
    public void displayTop3Products() {
        ensureLoaded();
        if (products_list == null || products_list.empty()) {
            System.out.println("No products available.");
            return;
        }

        Product max1 = null, max2 = null, max3 = null;

        products_list.findfirst();
        while (true) {
            Product cur = products_list.retrieve();
            if (max1 == null || cur.getAverageRating() > max1.getAverageRating()) {
                max3 = max2; max2 = max1; max1 = cur;
            } else if (max2 == null || cur.getAverageRating() > max2.getAverageRating()) {
                max3 = max2; max2 = cur;
            } else if (max3 == null || cur.getAverageRating() > max3.getAverageRating()) {
                max3 = cur;
            }
            if (products_list.last()) break;
            products_list.findenext();
        }

        System.out.println("\nTop Products by Average Rating:");
        int rank = 1;
        if (max1 != null) System.out.println(rank++ + ". Product ID: " + max1.getProductId() + " | Name: " + max1.getName() + " | Avg Rating: " + String.format("%.2f", max1.getAverageRating()));
        if (max2 != null) System.out.println(rank++ + ". Product ID: " + max2.getProductId() + " | Name: " + max2.getName() + " | Avg Rating: " + String.format("%.2f", max2.getAverageRating()));
        if (max3 != null) System.out.println(rank++ + ". Product ID: " + max3.getProductId() + " | Name: " + max3.getName() + " | Avg Rating: " + String.format("%.2f", max3.getAverageRating()));
        System.out.println("-----------------------------------");
    }

    public static void displayAllOrders_between2dates(LocalDate d1, LocalDate d2) {
        ensureLoaded();
        if (orders_list == null || orders_list.empty()) {
            System.out.println("No orders found.");
            return;
        }

        System.out.println("Orders between " + d1 + " and " + d2 + ":");
        boolean any = false;

        orders_list.findfirst();
        while (true) {
            Order o = orders_list.retrieve();
            if (!o.getOrderDate().isBefore(d1) && !o.getOrderDate().isAfter(d2)) {
                System.out.println(o.getOrderId());
                any = true;
            }
            if (orders_list.last()) break;
            orders_list.findenext();
        }

        if (!any) System.out.println("No results.");
        System.out.println("-----------------------------------");
    }

    public static void showCommonHighRatedProducts(int customerId1, int customerId2) {
        ensureLoaded();
        System.out.println("Common Products Reviewed by Both Customers (Avg > 4):");

        if (products_list == null || products_list.empty()) {
            System.out.println("No products available.");
            return;
        }
        if (reviews_list == null || reviews_list.empty()) {
            System.out.println("No reviews available.");
            return;
        }

        boolean found = false;

        products_list.findfirst();
        while (true) {
            Product p = products_list.retrieve();

            boolean reviewedByFirst  = false;
            boolean reviewedBySecond = false;

            reviews_list.findfirst();
            while (true) {
                Review r = reviews_list.retrieve();

                if (r.getProductID() == p.getProductId()) {
                    if (r.getCustomerID() == customerId1) reviewedByFirst  = true;
                    if (r.getCustomerID() == customerId2) reviewedBySecond = true;
                }

                if (reviews_list.last()) break;
                reviews_list.findenext();
            }

            if (reviewedByFirst && reviewedBySecond && p.getAverageRating() > 4.0) {
                System.out.println("Product: " + p.getName()
                        + " | Avg Rating: " + String.format("%.2f", p.getAverageRating()));
                found = true;
            }

            if (products_list.last()) break;
            products_list.findenext();
        }

        if (!found) System.out.println("No common high-rated products found.");
    }

    // =========================
    // Safe input helpers
    // =========================
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (input.hasNextInt()) {
                int v = input.nextInt();
                input.nextLine();
                return v;
            } else {
                System.out.println("Invalid input. Please enter a whole number.");
                input.nextLine();
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (input.hasNextDouble()) {
                double v = input.nextDouble();
                input.nextLine();
                return v;
            } else {
                System.out.println("Invalid input. Please enter a decimal number (e.g., 12.5).");
                input.nextLine();
            }
        }
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return input.nextLine().trim();
    }

    private static boolean isValidName(String s) {
        if (s == null) return false;
        s = s.trim();
        if (s.isEmpty() || s.length() > 50) return false;
        return s.matches("[\\p{IsArabic}A-Za-z\\s\\-']+");
    }

    private static String readName(String prompt) {
        while (true) {
            System.out.print(prompt);
            String name = input.nextLine().trim().replaceAll("\\s+", " ");
            if (isValidName(name)) return name;
            System.out.println("Invalid name. Use letters only (Arabic/English). Spaces, '-' and ' are allowed.");
        }
    }

    
    private static LocalDate readDateFlexible(String prompt) {
        String[] patterns = {
            "yyyy-MM-dd", "yyyy-M-d", "yyyy-M-dd", "yyyy-MM-d",
            "yyyy/MM/dd", "yyyy/M/d", "yyyy/M/dd", "yyyy/MM/d"
        };
        while (true) {
            String s = readLine(prompt).trim();
            for (String p : patterns) {
                try {
                    java.time.format.DateTimeFormatter f =
                        java.time.format.DateTimeFormatter.ofPattern(p);
                    return LocalDate.parse(s, f);
                } catch (Exception ignore) {}
            }
            System.out.println("invalid input");
        }
    }

    // ====== Rating======
    private static int readRating(String prompt) {
        while (true) {
            int r = readInt(prompt);
            if (r >= 1 && r <= 5) return r;
            System.out.println("Rating must be between 1 and 5. Try again.");
        }
    }

    private static int readNewProductId(String prompt) {
        ensureLoaded();
        while (true) {
            System.out.print(prompt);
            if (input.hasNextInt()) {
                int id = input.nextInt(); input.nextLine();

                if (all_products != null && all_products.SearchProductById(id) != null) {
                    System.out.println(" Product ID " + id + " can't ,exist");
                    continue;
                }
                if (id >= 101 && id <= 150) {
                    System.out.println("invalid (101-150 exist)ا.");
                    return id;
                }
                return id; // أقل من 101 أو أكبر من 150، طالما غير مكرر مسموح
            } else {
                System.out.println("Invalid input. Please enter a numeric ID.");
                input.nextLine();
            }
        }
    }

    private static int readExistingProductId(String prompt) {
        ensureLoaded();
        while (true) {
            System.out.print(prompt);
            if (input.hasNextInt()) {
                int id = input.nextInt(); input.nextLine();
                if (all_products == null || all_products.SearchProductById(id) == null) {
                    System.out.println(" Product ID " + id + " doesn't exist.");
                    continue;
                }
                return id;
            } else {
                System.out.println("Invalid input. Please enter a numeric Product ID.");
                input.nextLine();
            }
        }
    }

    private static String readValidProductIds(String prompt) {
        ensureLoaded();
        while (true) {
            String s = readLine(prompt).trim();
            if (s.isEmpty()) { System.out.println("Enter at least one product ID."); continue; }
            String[] parts = s.split(";");
            boolean ok = true;
            for (String part : parts) {
                try {
                    int id = Integer.parseInt(part.trim());
                    if (all_products == null || all_products.SearchProductById(id) == null) { ok = false; break; }
                } catch (NumberFormatException ex) { ok = false; break; }
            }
            if (!ok) {
                System.out.println("must be exist");
                continue;
            }
            return s;
        }
    }

    private static int readUniqueCustomerId(String prompt) {
        ensureLoaded();
        while (true) {
            int id = readInt(prompt);
            if (all_Customers != null && all_Customers.searchById(id) != null) {
                System.out.println("⚠️ Customer with ID " + id + " already exists. Enter another ID.");
                continue;
            }
            return id;
        }
    }

    private static int readExistingCustomerId(String prompt) {
        ensureLoaded();
        while (true) {
            int id = readInt(prompt);
            if (all_Customers == null || all_Customers.searchById(id) == null) {
                System.out.println("⚠️ Customer ID " + id + " not found. Enter an existing customer ID.");
                continue;
            }
            return id;
        }
    }
    private static int readUniqueOrderId(String prompt) {
        ensureLoaded();
        while (true) {
            int id = readInt(prompt);
            if (all_Orders != null && all_Orders.searchOrderById(id) != null) {
                System.out.println(" Order with ID " + id + " already exists. Enter another ID.");
                continue;
            }
            return id;
        }
    }

    private static int readUniqueReviewId(String prompt) {
        ensureLoaded();
        while (true) {
            int id = readInt(prompt);
            if (all_Reviews != null && all_Reviews.SearchReviewById(id) != null) {
                System.out.println(" Review with ID " + id + " already exists. Enter another ID.");
                continue;
            }
            return id;
        }
    }
    private static int readExistingOrderId(String string) {
    	    ensureLoaded();
    	    while (true) {
    	        System.out.print(string);
    	        if (input.hasNextInt()) {
    	            int id = input.nextInt();
    	            input.nextLine(); // consume the rest of the line
    	            if (all_Orders != null && all_Orders.searchOrderById(id) != null) {
    	                return id;
    	            } else {
    	                System.out.println("Order ID " + id + " does not exist. Try again.");
    	            }
    	        } else {
    	            System.out.println("Invalid input. Please enter a numeric Order ID.");
    	            input.nextLine(); 
    	        }
    	    }
	}
    private static int readExistingReviewId(String prompt) {
        ensureLoaded(); 
        while (true) {
            System.out.print(prompt);
            if (input.hasNextInt()) {
                int id = input.nextInt();
                input.nextLine();
                if (all_Reviews != null && all_Reviews.SearchReviewById(id) != null) {
                    return id; 
                } else {
                    System.out.println("Review ID " + id + " doesn't exist. Try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a numeric ID.");
                input.nextLine();
            }
        }
    }


    // =========================
    // main
    // =========================
    public static void main(String[] args) {
        E_commerce e1 = new E_commerce();
        ensureLoaded(); 
        int choice;

        do {
            System.out.println("1: Load all files");
            System.out.println("2: Add Product");
            System.out.println("3: Add Customer");
            System.out.println("4: Add Order");
            System.out.println("5: Add Review");
            System.out.println("6: List all customers");
            System.out.println("7: Show top 3 products by average rating");
            System.out.println("8: Display all orders");
            System.out.println("9: Display reviews by customer ID");
            System.out.println("10: Display all orders between 2 dates");
            System.out.println("11: Show common high-rated products for 2 customers");
            System.out.println("12: Update Order Status");
            System.out.println("13: Update Product");
            System.out.println("14: remove Product");
            System.out.println("15: Show Customer order history");
            System.out.println("16: cancel order");
            System.out.println("17: Edit review");
            System.out.println("18: Exit");

            choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    Load_all();
                    dataLoaded = true;
                    break;

                case 2: { 
                    int id      = readNewProductId("Enter NEW Product ID (<101 OR >150; if 101–150 it must NOT exist now): ");
                    String name = readName("Enter Product Name: ");
                    double price= readDouble("Enter Price: ");
                    int qty     = readInt("Enter Quantity: ");
                    Product p   = new Product(id, name, price, qty);
                    add_Product(p);
                    System.out.println("Product added successfully.");
                    break;
                }

                case 3: { 
                    int id = readUniqueCustomerId("Enter Customer ID [new]: ");
                    String name = readName("Enter Customer Name: ");
                    String email= readLine("Enter Customer Email: ");
                    Customer c  = new Customer(id, name, email);
                    add_Customer(c);
                    System.out.println("Customer added successfully.");
                    break;
                }

                case 4: {
                	int oid = readUniqueOrderId("Enter Order ID [new]: ");
                	int cid = readExistingCustomerId("Enter Customer ID [existing]: ");
                	double total = 0;
                	String allProducts = "";

                	boolean addProducts = true;

                	while (addProducts) {
                	    String prod = readValidProductIds("Enter Product IDs (semicolon-separated, must exist): ");
                	    String[] prodList = prod.split(";");

                	    boolean outOfStock = false;
                	    for (String pIdStr : prodList) {
                	        int pid = Integer.parseInt(pIdStr.trim());
                	        Product p = all_products.SearchProductById(pid);

                	        if (p == null) {
                	            System.out.println("Product " + pid + " not found! Skipping this set.");
                	            outOfStock = true;
                	            break;
                	        }

                	        if (p.getStock() == 0) {
                	            System.out.println("\nProduct " + pid + " is OUT OF STOCK. Skipping this set.");
                	            outOfStock = true;
                	            break;
                	        } else {
                	            total += p.getPrice();
                	            allProducts += pid + ";"; // accumulate product IDs
                	        }
                	    }

                	    if (!outOfStock) {
                	        System.out.println("Products added to order successfully. Current total: " + total);
                	    }

                	    String ans = readLine("Do you want to add more products to the same order? (yes/no): ").toLowerCase();
                	    if (!ans.equals("yes") && !ans.equals("y")) {
                	        addProducts = false;
                	    }
                	}

                	if (allProducts.endsWith(";")) allProducts = allProducts.substring(0, allProducts.length() - 1);

                	LocalDate date = readDateFlexible("Enter Order Date (e.g., 2025-2-3): ");
                	String status = readLine("Enter Status (Pending/Processing/Shipped/Delivered/Cancelled/Returned): ");

                	Order o = new Order(oid, cid, allProducts, total, date, status);
                	add_Order(o);

                	System.out.println("Order added successfully.");
                	System.out.println("Total Price: " + total);
                	
                	break;
                }
                

                case 5: { 
                    int rid = readUniqueReviewId("Enter Review ID [new]: ");
                    int pid = readExistingProductId("Enter Product ID [existing]: ");
                    int cid = readExistingCustomerId("Enter Customer ID [existing]: ");
                    int rating = readRating("Enter Rating (1–5): "); // <<< هنا الإصلاح
                    String comment = readLine("Enter Comment: ");
                    Review r = new Review(rid, pid, cid, rating, comment);
                    add_Review(r);
                    System.out.println("Review added successfully.");
                    break;
                }

                case 6:
                    ensureLoaded();
                    all_Customers.displayAll();
                    break;

                case 7:
                    e1.displayTop3Products();
                    break;

                case 8:
                    ensureLoaded();
                    all_Orders.displayAllOrders();
                    break;
                case 9:{
                	int cid =readInt("Enter customer ID:");
                	LinkedList<Review> REVlist = all_Reviews.getReviewsByCustomer(cid);
                	if (REVlist.empty()) System.out.println("No reviews by this Customer");
                	else REVlist.display();
                	break;
                }
                case 10:{ 
                	ensureLoaded();
                    System.out.println("Display all orders between 2 dates:");

                    LocalDate startDate = readDateFlexible("Enter start date (e.g., 2025-2-1): ");
                    LocalDate endDate   = readDateFlexible("Enter end date   (e.g., 2025-2-9): ");

                    LinkedList<Order> list = all_Orders.get_orders_list(); 

                    if (list.empty()) {
                    	System.out.println("No orders found");
                    	break;
                    }
                    boolean any = false;
                    System.out.println("Order between" + startDate+ "and"+ endDate+":");
                    list.findfirst();
                    
                    while(true) {
                    	Order o1 = list.retrieve();
                    	if (!o1.getOrderDate().isBefore(startDate) &&
                    	        !o1.getOrderDate().isAfter(endDate)) {

                    	        System.out.println(
                    	            "OrderID: " + o1.getOrderId()
                    	            + " | CustomerID: " + o1.getCustomerId()
                    	            + " | Products: " + o1.getProd_Ids()
                    	            + " | TotalPrice: " + o1.getTotalPrice()
                    	            + " | Date: " + o1.getOrderDate()
                    	            + " | Status: " + o1.getStatus()
                    	        );
                    	        any = true;
                    	    }

                    	    if (list.last()) break;
                    	    list.findenext();        
                    	}

                    	if (!any) System.out.println("No results.");
                    System.out.println("-----------------------------------");
                    break;
                }
                case 11: {
                    int c1 = readExistingCustomerId("Enter first customer ID [existing]: ");
                    int c2 = readExistingCustomerId("Enter second customer ID [existing]: ");
                    showCommonHighRatedProducts(c1, c2);
                    break;
                }
                case 12:{
                	ensureLoaded();
                	System.out.println("Update Order Status.");
                	int update= readNewProductId("Enter Order ID:");
                	String newStatus = readLine("Enter new Status:");
                	LinkedList<Order> list= all_Orders.get_orders_list();
                	
                	if(list.empty()) {
                		System.out.println("No orders exist");
                		break;
                	}
                	 boolean found = false;

                	    list.findfirst();
                	    while (true) {
                	        Order o = list.retrieve();

                	        if (o.getOrderId() == update) {
                	            o.setStatus(newStatus);
                	            System.out.println("Order updated successfully.");
                	            
                	            all_Orders.saveAll(); 
                	            found = true;
                	            break;
                	        }

                	        if (list.last()) break;
                	        list.findenext();
                	    }

                	    if (!found)
                	        System.out.println("Order not found.");
                	    break;
                }
                case 13:{
                	ensureLoaded();
                    int pid = readExistingProductId("Enter Product ID to update: ");
                    Product p = all_products.SearchProductById(pid);
                    if(p != null) {
                        double newPrice = readDouble("Enter new Price (current: " + p.getPrice() + "): ");
                        p.setPrice(newPrice);

                        int newQty = readInt("Enter new Quantity (current: " + p.getStock() + "): ");
                        p.setStock(newQty);

                        System.out.println("Product updated successfully.");
                        all_products.saveAll(); 
                    } else {
                        System.out.println("Product not found.");
                        break;
                    }
                }
                case 14:{
                	ensureLoaded();
                    int pid = readExistingProductId("Enter Product ID to remove: ");
                    Product p = all_products.SearchProductById(pid);

                    if (p != null) {
                        all_products.removeProduct(pid); 
                        System.out.println("Product removed successfully.");
                        all_products.saveAll(); 
                    } else {
                        System.out.println("Product not found.");
                    }
                    break;
                }
                case 15: {
                	int cid = readExistingCustomerId("Enter Customer ID to view order history: ");

                	LinkedList<Order> list = all_Orders.get_orders_list();

                	if (list.empty()) {
                	    System.out.println("No orders exist.");
                	} else {
                	    boolean found = false;
                	    System.out.println("Order history for Customer ID: " + cid);
                	    System.out.println("OrderID\tProducts\tTotalPrice\tDate\tStatus");
                	    System.out.println("------------------------------------------------------");

                	    list.findfirst();
                	    while (true) {
                	        Order o = list.retrieve();
                	        if (o.getCustomerId() == cid) {
                	            System.out.println(" " + o.getOrderId()
                	                + "\t" + o.getProd_Ids()
                	                + "\t" + o.getTotalPrice()
                	                + "\t" + o.getOrderDate()
                	                + "\t" + o.getStatus());
                	            found = true;
                	        }

                	        if (list.last()) break;
                	        list.findenext();
                	    }

                	    if (!found) System.out.println("No orders found for this customer.");
                	}
                	System.out.println("------------------------------------------------------");

                }
                case 16: {
                    ensureLoaded();
                    int oid = readExistingOrderId("Enter Order ID to cancel: ");
                    LinkedList<Order> list = all_Orders.get_orders_list();

                    if (list.empty()) {
                        System.out.println("No orders exist.");
                    } else {
                        boolean found = false;
                        list.findfirst();
                        while (true) {
                            Order o = list.retrieve();
                            if (o.getOrderId() == oid) {
                                list.remove();          // Remove from linked list
                                System.out.println("Order ID " + oid + " has been canceled successfully.");
                                all_Orders.saveAll();   // Update CSV
                                found = true;
                                break;
                            }
                            if (list.last()) break;
                            list.findenext();
                        }
                        if (!found) System.out.println("Order not found.");
                    }
                    System.out.println("------------------------------------------------------");
                }
                case 17: {
                    ensureLoaded();
                    int rid = readExistingReviewId("Enter Review ID to edit: ");
                    LinkedList<Review> list = all_Reviews.get_reviews_list();

                    if (list.empty()) {
                        System.out.println("No reviews exist.");
                    } else {
                        boolean found = false;
                        list.findfirst();
                        while (true) {
                            Review r = list.retrieve();
                            if (r.getReviewID() == rid) {
                                String newComment = readLine("Enter new comment (leave empty to keep current): ");
                                if (!newComment.isEmpty()) r.setComment(newComment);

                                int newRating = readInt("Enter new rating (1–5, current: " + r.getRating() + "): ");
                                if (newRating >= 1 && newRating <= 5) r.setRating(newRating);
                                else System.out.println("Invalid rating. Keeping old rating.");

                                System.out.println("Review updated successfully.");
                                all_Reviews.saveAll();
                                found = true;
                                break;
                            }
                            if (list.last()) break;
                            list.findenext();
                        }

                        if (!found) System.out.println("Review not found.");
                    }
                    System.out.println("------------------------------------------------------");
                }

                case 18:{
                    System.out.println("Goodbye!");
                    break;
                }
                default:
                    System.out.println("Unknown choice.");
                    break;
            }
        } while (choice != 18);

        input.close();
    }

	

	
}
