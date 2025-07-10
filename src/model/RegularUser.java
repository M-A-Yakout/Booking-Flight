package model;

public class RegularUser extends User {
    public RegularUser() {
        super("", "", "", "", "regular");
    }

    public RegularUser(String username, String password, String name, String email) {
        super(username, password, name, email, "regular");
    }

    @Override
    public void displayMenu() {
        // Implementation for regular user menu
        System.out.println("Regular User Menu");
        System.out.println("1. View Profile");
        System.out.println("2. Edit Profile");
        System.out.println("3. Search Flights");
        System.out.println("4. Book Flight");
        System.out.println("5. View Bookings");
        System.out.println("6. Logout");
    }
}