package project212;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class Reviews {
    private static LinkedList<Review> reviews; 
    private Products all_products;
    private Customers all_Customers;
    private String filePath; // للحفظ التلقائي

    public void setFilePath(String path) { this.filePath = path; }

    public Reviews(LinkedList<Review> reviews, LinkedList<Product> input_products, LinkedList<Customer> input_customers) {
        this.reviews = reviews;
        all_products = new Products(input_products);
        all_Customers = new Customers(input_customers);
    }

    public Reviews() {
        reviews = new LinkedList<>();
        all_products = new Products();
        all_Customers = new Customers();
    }

    public LinkedList<Review> get_all_Reviews() { return reviews; }
    public Products get_all_Products() { return all_products; }

    // Time Complexity: O(n)
    public Review SearchReviewById(int id) {
        if (reviews.empty()) 
        	return null;
        reviews.findfirst();
        while (true) {
            if (reviews.retrieve().getReviewID() == id) 
            	return reviews.retrieve();
            if (reviews.last()) break;
            reviews.findenext();
        }
        return null;
    }

    // Time Complexity: O(n)
    public void assign_to_product(Review r) {
        Product p = all_products.SearchProductById(r.getProductID());
        if (p != null) p.addReview(r);
    }

    // Time Complexity: O(n)
    public void assign_to_customer(Review r) {
        Customer c = all_Customers.searchById(r.getCustomerID());
        if (c != null) c.addReview(r);
    }

    // Time Complexity: O(n)
    public void addReview(Review r) {
        if (SearchReviewById(r.getReviewID()) == null) {
            reviews.addLast(r);
            assign_to_product(r);
            assign_to_customer(r);
            System.out.println("Review added: " + r.getReviewID());
            saveAll(); // حفظ تلقائي
        } else {
            System.out.println("Review with ID " + r.getReviewID() + " already exists");
        }
    }

    // Time Complexity: O(n)
    public void updateReview(int id, Review p) {
        Review old = SearchReviewById(id);
        if (old == null)
            System.out.println("not exist to make update");
        else {
            old.UpdateReview(p);
            saveAll(); // حفظ تلقائي
        }
    }

    // Time Complexity: O(n)
    public LinkedList<Review> getReviewsByCustomer(int customerId) {
        LinkedList<Review> result = new LinkedList<>();
        if (reviews.empty()) return result;
        reviews.findfirst();
        while (true) {
            if (reviews.retrieve().getCustomerID() == customerId)
                result.addLast(reviews.retrieve());
            if (reviews.last()) break;
            reviews.findenext();
        }
        return result;
    }
    
    public LinkedList<Review> get_reviews_list() {
        return reviews;
    }


    // Time Complexity: O(n)
    public void displayAllReviews() {
        System.out.println("All Reviews");
        if (reviews.empty()) {
            System.out.println("no reviews exist");
            return;
        }
        reviews.findfirst();
        while (true) {
            Review p = reviews.retrieve();
            System.out.println(p.toString());
            if (reviews.last()) break;
            reviews.findenext();
        }
    }

    // Time Complexity: O(1)
    public static Review convert_String_to_Review(String Line) {
        String[] a = Line.split(",", 5);
        Review r = new Review(
            Integer.parseInt(a[0].trim()),
            Integer.parseInt(a[1].trim()),
            Integer.parseInt(a[2].trim()),
            Integer.parseInt(a[3].trim()),
            a[4]
        );
        return r;
    }

    // Time Complexity: O(n)
    public void load_revews(String fileName) {
        try {
            filePath = fileName;
            File f = new File(fileName);
            Scanner read = new Scanner(f);
            System.out.println("Reading file: " + fileName);
            if (read.hasNextLine()) read.nextLine(); // skip header
            while (read.hasNextLine()) {
                String line = read.nextLine().trim();
                if (!line.isEmpty()) {
                    Review r = convert_String_to_Review(line);
                    reviews.addLast(r);
                }
            }
            read.close();
            System.out.println("File loaded successfully!\n");
        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    // حفظ تلقائي للـ CSV
    void saveAll() {
        if (filePath == null || filePath.isEmpty()) return;
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println("reviewId,productId,customerId,rating,comment");
            if (!reviews.empty()) {
                reviews.findfirst();
                while (true) {
                    Review r = reviews.retrieve();
                    pw.println(r.getReviewID()+","+r.getProductID()+","+r.getCustomerID()+","+r.getRating()+","+r.getComment());
                    if (reviews.last()) break;
                    reviews.findenext();
                }
            }
        } catch (Exception e) {
            System.out.println("Error saving reviews: " + e.getMessage());
        }
    }
}
