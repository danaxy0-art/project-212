package project212;
import java.io.File;
import java.util.Scanner;
public class Customers {
	private LinkedList<Customer> customers;
	   
    public Customers() {
        customers = new LinkedList<>();       
    }

    Customers(LinkedList<Customer> input_customers) {
      customers=input_customers;
    }
public LinkedList<Customer> get_customers()
{
return customers;
}

    public Customer searchById(int id) {
        if (customers.empty()) 
            return null;
        customers.findfirst();
        while (true) {
            if (customers.retrieve().getCustomerId() == id)
                return customers.retrieve();
            if (customers.last()) break;
            customers.findenext();
        }
        return null;
    }

  
    public void addCustomer(Customer c) {
        if (searchById(c.getCustomerId()) == null) {
            customers.addLast(c);
            System.out.println(" Added customer: " + c.getName());
        } else {
            System.out.println(" Customer with ID " + c.getCustomerId() + " already exists!");
        }
    }

    public void displayAll() {
        if (customers.empty()) {
            System.out.println(" No customers found!");
            return;
        }
        System.out.println("=== All Customers ===");
        customers.findfirst();
        while (true) {
            customers.retrieve().display();
            if (customers.last()) 
                break;
            customers.findenext();
        }
    }
public static Customer convert_String_to_Customer(String Line)
    {
    String a[]=Line.split(",");
     Customer p=new Customer(Integer.parseInt(a[0].trim()),a[1].trim(),a[2].trim());
    return p;
    }
  
    public void loadCustomers(String fileName) {
        try {
            File f = new File(fileName);
            Scanner read = new Scanner(f);
            System.out.println("Reading file: " + fileName);
            System.out.println("-----------------------------------");
            if (read.hasNextLine()) read.nextLine(); 

            while (read.hasNextLine()) {
                String line = read.nextLine().trim();
                if (line.isEmpty()) continue;  
                Customer c = convert_String_to_Customer(line);
                customers.addLast(c);
            }

            read.close();
            System.out.println("-----------------------------------");
            System.out.println("Customers loaded successfully!\n");
        } catch (Exception e) {
            System.out.println("Error loading customers: " + e.getMessage());
        }
    }

public static void test1() {
        Customers all = new Customers();
        Customer c1 = new Customer(201, "Alice Johnson", "alice.johnson@example.com");
        Customer c2 = new Customer(202, "Bob Smith", "bob.smith@example.com");

        all.addCustomer(c1);
        all.addCustomer(c2);

        System.out.println("\nAfter adding manually:");
        all.displayAll();
    }
    public static void test2() {
        Customers all = new Customers();
        all.loadCustomers("C:\\Users\\danam\\Desktop\\project212\\customers.csv");
        all.displayAll();
    }

    

    public static void main(String[] args) {       
        test1();      
        test2();
    }
}
