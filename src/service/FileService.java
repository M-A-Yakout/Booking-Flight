package service;

import model.*;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;

public class FileService {

    public static void saveUsers(List<User> users) {
        try {
            UserDAO userDAO = getUserDAO();
            for (User user : users) {
                if (userDAO.userExists(user.getUsername())) {
                    userDAO.updateUser(user);
                } else {
                    userDAO.createUser(user);
                }
            }
            System.out.println("Users saved successfully");
        } catch (SQLException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    public static List<User> loadUsers() {
        try {
            UserDAO userDAO = getUserDAO();
            List<User> users = userDAO.getAllUsers();
            
            // Create default admin if no users exist
            if (users.isEmpty()) {
                User admin = new Admin("admin", "admin123", "System Administrator", "admin@flightbooking.com");
                userDAO.createUser(admin);
                users.add(admin);
            }
            
            return users;
        } catch (SQLException e) {
            System.out.println("Error loading users: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void saveFlights(List<Flight> flights) {
        try {
            FlightDAO flightDAO = new FlightDAO();
            for (Flight flight : flights) {
                if (flightDAO.getFlightByNumber(flight.getFlightNumber()) != null) {
                    flightDAO.updateFlight(flight);
                } else {
                    flightDAO.addFlight(flight);
                }
            }
            System.out.println("Flights saved successfully");
        } catch (SQLException e) {
            System.out.println("Error saving flights: " + e.getMessage());
        }
    }

    public static List<Flight> loadFlights() {
        try {
            FlightDAO flightDAO = new FlightDAO();
            return flightDAO.getAllFlights();
        } catch (SQLException e) {
            System.out.println("Error loading flights: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void saveBookings(List<Booking> bookings) {
        try {
            BookingDAO bookingDAO = new BookingDAO(new UserDAO(), new FlightService());
            for (Booking booking : bookings) {
                bookingDAO.createBooking(booking);
            }
            System.out.println("Bookings saved successfully");
        } catch (SQLException e) {
            System.out.println("Error saving bookings: " + e.getMessage());
        }
    }

    public static List<Booking> loadBookings() {
        try {
            BookingDAO bookingDAO = new BookingDAO(new UserDAO(), new FlightService());
            return bookingDAO.getAllBookings();
        } catch (SQLException e) {
            System.out.println("Error loading bookings: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public static UserDAO getUserDAO() {
        return new UserDAO();
    }
}
