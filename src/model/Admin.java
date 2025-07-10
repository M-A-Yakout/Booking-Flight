package model;

public class Admin extends User {
    
    public Admin(String username, String password, String name, String email) {
        super(username, password, name, email, "ADMIN");
    }

    @Override
    public void displayMenu() {
        System.out.println("\n===== ADMIN MENU =====");
        System.out.println("1. Add New Flight");
        System.out.println("2. View All Flights");
        System.out.println("3. Update Flight Details");
        System.out.println("4. Remove Flight");
        System.out.println("5. View All Bookings");
        System.out.println("6. View All Users");
        System.out.println("7. Change Password");
        System.out.println("8. Logout");
        System.out.print("Enter your choice: ");
    }
}
