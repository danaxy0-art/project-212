package project212;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class Customers {
    private LinkedList<Customer> customers;
    private String filePath;

    public Customers() {
        customers = new LinkedList<>();
    }

    public Customers(LinkedList<Customer> input_customers) {
        customers = input_customers;
    }

    public void setFilePath(String path) { this.filePath = path; }

    public LinkedList<Customer> get_customers() { return customers; }

    public Customer searchById(int id) {
        if (customers.empty()) return null;
        customers.findfirst();
        while (true) {
            if (customers.retrieve().getCustomerId() == id) return customers.retrieve();
            if (customers.last()) break;
            customers.findenext();
        }
        return null;
    }

    public void addCustomer(Customer c) {
        if (searchById(c.getCustomerId()) == null) {
            customers.addLast(c);
            System.out.println(" Added customer: " + c.getName());
            saveAll();
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
            if (customers.last()) break;
            customers.findenext();
        }
    }

    public static Customer convert_String_to_Customer(String Line) {
        String a[] = Line.split(",", 3);
        Customer p = new Customer(Integer.parseInt(a[0].trim()), a[1].trim(), a[2].trim());
        return p;
    }

    public void loadCustomers(String fileName) {
        try {
            filePath = fileName;
            File f = new File(fileName);
            Scanner read = new Scanner(f);
            System.out.println("Reading file: " + fileName);
            if (read.hasNextLine()) read.nextLine(); // skip header
            while (read.hasNextLine()) {
                String line = read.nextLine().trim();
                if (line.isEmpty()) continue;
                Customer c = convert_String_to_Customer(line);
                customers.addLast(c);
            }
            read.close();
            System.out.println("Customers loaded successfully!\n");
        } catch (Exception e) {
            System.out.println("Error loading customers: " + e.getMessage());
        }
    }

    private void saveAll() {
        if (filePath == null || filePath.isEmpty()) return;
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println("customerId,name,email");
            if (!customers.empty()) {
                customers.findfirst();
                while (true) {
                    Customer c = customers.retrieve();
                    pw.println(c.getCustomerId() + "," + c.getName() + "," + c.getEmail());
                    if (customers.last()) break;
                    customers.findenext();
                }
            }
        } catch (Exception e) {
            System.out.println("Error saving customers: " + e.getMessage());
        }
    }
}
