package project212;
import java.time.LocalDate;
import java.util.Scanner;
public class E_commerce {
	private static LinkedList<Customer> customers_list;
    private static LinkedList<Order> orders_list;
    private static LinkedList<Product> products_list;
    private static LinkedList<Review> reviews_list; 
    
    private static Reviews all_Reviews;
    private static Customers all_Customers;
    private static Orders all_Orders;
    private static Products all_products;
    public E_commerce()
    {
    customers_list=new LinkedList<Customer>();
    orders_list=new LinkedList<Order>();
    products_list=new LinkedList<Product>();
    reviews_list=new LinkedList<Review>();
    
    
    all_products=new Products(products_list);
    all_Customers=new Customers(customers_list);
    all_Orders=new Orders(customers_list,orders_list);
    all_Reviews=new Reviews(reviews_list,products_list,customers_list);   
  
    }

     public static void Load_all()
    {    
       all_products .loadProducts("C:\\Users\\danam\\Desktop\\project212\\prodcuts.csv\\");
       all_Customers.loadCustomers("C:\\Users\\danam\\Desktop\\project212\\customers.csv\\");
       all_Orders.loadOrders("C:\\Users\\danam\\Desktop\\project212\\orders.csv\\");
       all_Reviews.load_revews("C:\\Users\\danam\\Desktop\\project212\\reviews.csv\\");      
      
    
   
    }
      public static void add_Customer(Customer c)
    {
        if (all_Customers != null)
            all_Customers.addCustomer(c);
        else
            System.out.println("Error: Customer list not initialized!");
    }
      public static void add_Product(Product p)
    {
        if (all_products != null)
            all_products.addProduct(p);
        else
            System.out.println(" Error: Product list not initialized!");
    }
       public static void add_Order(Order o)
    {
        if (all_Orders != null)
            all_Orders.addOrder(o);
        else
            System.out.println("Error: Orders list not initialized!");
    }
     public static void add_Review(Review r)
     {
     all_Reviews.addReview(r);
     }
       public void displayTop3Products() {
    if (products_list.empty()) {
        System.out.println("No products available.");
        return;
    }

    Product max1 = null, max2 = null, max3 = null;
    
    products_list.findfirst();
    while (true) {
        Product cur = products_list.retrieve();

        if (max1 == null || cur.getAverageRating() > max1.getAverageRating()) {
            max3 = max2;
            max2 = max1;
            max1 = cur;
        } else if (max2 == null || cur.getAverageRating() > max2.getAverageRating()) {
            max3 = max2;
            max2 = cur;
        } else if (max3 == null || cur.getAverageRating() > max3.getAverageRating()) {
            max3 = cur;
        }

        if (products_list.last()) break;
        products_list.findenext();
    }

    System.out.println("\n⭐ Top Products by Average Rating:");

    int rank = 1;
    if (max1 != null) {
        System.out.println(rank++ + ". Product ID: " + max1.getProductId()
                + " | Name: " + max1.getName()
                + " | Avg Rating: " + String.format("%.2f", max1.getAverageRating()));
    }
    if (max2 != null) {
        System.out.println(rank++ + ". Product ID: " + max2.getProductId()
                + " | Name: " + max2.getName()
                + " | Avg Rating: " + String.format("%.2f", max2.getAverageRating()));
    }
    if (max3 != null) {
        System.out.println(rank++ + ". Product ID: " + max3.getProductId()
                + " | Name: " + max3.getName()
                + " | Avg Rating: " + String.format("%.2f", max3.getAverageRating()));
    }

    System.out.println("-----------------------------------");
}


    public static  void displayAllOrders_between2dates(LocalDate d1,LocalDate d2) {
        if (orders_list.empty()) {
            System.out.println(" No all_orders found!");
            return;
        }

        //System.out.println("OrderID\tCustomerID\tProductIDs\t\tTotalPrice\tDate\t\tStatus");
        System.out.println("--------------------------------------------------------------------------");

       // SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        orders_list.findfirst();
        while (true) {
            Order o = orders_list.retrieve();
            if(o.getOrderDate().compareTo(d1)>0&&o.getOrderDate().compareTo(d2)<0){
                System.out.println(o.getOrderId());
             // o.display();
             
            }
            if (orders_list.last())
                break;
            orders_list.findenext();
        }

        System.out.println("--------------------------------------------------------------------------");
    }

    public void showCommonHighRatedProducts(int customerId1, int customerId2) {
    System.out.println("=== 🟢 Common Products Reviewed by Both Customers (Avg > 4) ===");

    if (products_list == null || products_list.empty()) {
        System.out.println("⚠️ No products available.");
        return;
    }

    if (reviews_list == null || reviews_list.empty()) {
        System.out.println("⚠️ No reviews available.");
        return;
    }

    boolean found = false;

    products_list.findfirst();
    while (true) {
        Product p = products_list.retrieve();

        boolean reviewedByFirst = false;
        boolean reviewedBySecond = false;

        reviews_list.findfirst();
        while (true) {
            Review r = reviews_list.retrieve();

            if (r.getProductID() == p.getProductId()) {
                if (r.getCustomerID() == customerId1)
                    reviewedByFirst = true;
                if (r.getCustomerID() == customerId2)
                    reviewedBySecond = true;
            }

            if (reviews_list.last())
                break;
            else
                reviews_list.findenext();
        }

        if (reviewedByFirst && reviewedBySecond && p.getAverageRating() > 4.0) {
            System.out.println("Product: " + p.getName()
                    + " | Avg Rating: " + String.format("%.2f", p.getAverageRating()));
            found = true;
        }

        if (products_list.last())
            break;
        else
            products_list.findenext();
    }

    if (!found)
        System.out.println("No common high-rated products found.");
}

     public static void main(String[] args) 
     {
       E_commerce e1=new E_commerce();
         Scanner input=new Scanner(System.in);
         int choice=0;
         do {          
            System.out.println("1:Load all files");
            System.out.println("2:add_Product?");
            System.out.println("3:add_Customer");
            System.out.println("4:add_Order");
            System.out.println("5:add_Review");
            System.out.println("6:all_Customers");
            System.out.println("7:show Suggest top 3 products by average rating");
            System.out.println("8:display all orders");
            System.out.println("9:display all orders between 2 dates");
            System.out.println("10: Show Common High Rated Products (Avg > 4) for 2 Customers");
            System.out.println("100: Exit");
            System.out.print("enter youe choice ");
            
            choice=input.nextInt();
             
            switch (choice) {
                case 1:
                     Load_all();
                    break;                   
                case 2:
                    Product p=new Product(input.nextInt(),input.nextLine()
                            , input.nextDouble(),input.nextInt());
//                    Product p = new Product(101, "Laptop Pro 15", 1499.99, 20);
                    add_Product(p);                   
                    break;
                case 3:                    
                    Customer c=new Customer(input.nextInt(),input.next(),input.next());
                   add_Customer(c);
                    break;
                case 4:
                    Order o=new Order(input.nextInt(), input.nextInt(), input.next()
                          ,input.nextDouble(),LocalDate.parse(input.next()), input.next());
                   add_Order(o);
                    break;
                case 5:
                    Review r=new Review(input.nextInt(),input.nextInt()
                            ,input.nextInt(),input.nextInt(),input.next());
                    add_Review(r);
                    break;
                case 6:
                 all_Customers.displayAll();
                    break;
                case 7:
                 e1.displayTop3Products();
                    break;
                case 8:
                    all_Orders.displayAllOrders();
                    break;
                case 9:
                    displayAllOrders_between2dates(LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 9));
                case 10:
                System.out.print("Enter first customer ID: ");
                int c1 = input.nextInt();
                System.out.print("Enter second customer ID: ");
                int c2 = input.nextInt();
                e1.showCommonHighRatedProducts(c1, c2);
                break;
                
                case 100:
                    System.out.println("goodbye");
                    break;
                default:
                    System.out.println("");
            }
        } while (choice!= 100);
    }
      
        
    
}

