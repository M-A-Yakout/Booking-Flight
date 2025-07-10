package model;

import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    private List<Booking> bookings;

    public Customer(String username, String password, String name, String email) {
        super(username, password, name, email, "CUSTOMER");
        this.bookings = new ArrayList<>();
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        this.bookings.add(booking);
    }

    public void removeBooking(Booking booking) {
        this.bookings.remove(booking);
    }

    @Override
    public void displayMenu() {
        System.out.println("\n===== CUSTOMER MENU =====");
        System.out.println("1. Search Flights");
        System.out.println("2. Book a Flight");
        System.out.println("3. View My Bookings");
        System.out.println("4. Cancel Booking");
        System.out.println("5. Change Password");
        System.out.println("6. Logout");
        System.out.print("Enter your choice: ");
    }
}
