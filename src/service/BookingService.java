package service;

import model.User;
import model.Flight;
import model.Booking;
import model.Customer;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class BookingService {
    private BookingDAO bookingDAO;
    private FlightService flightService;
    private UserDAO userDAO;

    public BookingService(FlightService flightService, UserDAO userDAO) {
        this.bookingDAO = new BookingDAO(userDAO, flightService);
        this.flightService = flightService;
        this.userDAO = userDAO;
    }

    public Booking createBooking(User user, Flight flight) {
        try {
            // First check if user exists
            if (!userDAO.userExists(user.getUsername())) {
                // حاول إنشاء المستخدم إذا لم يكن موجوداً
                try {
                    userDAO.createUser(user);
                } catch (SQLException e) {
                    throw new IllegalArgumentException("User does not exist and could not be created: " + e.getMessage());
                }
            }
            
            if (flightService.bookFlight(flight)) {
                Booking booking = new Booking(user, flight);
                
                try {
                    bookingDAO.createBooking(booking);
                    
                    // If user is a customer, add booking to their list
                    if (user instanceof Customer) {
                        ((Customer) user).addBooking(booking);
                    }
                    
                    return booking;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean cancelBooking(String bookingId, User user) {
        // First verify the booking belongs to the user
        Booking bookingToCancel = getBookingById(bookingId);
        if (bookingToCancel == null || !bookingToCancel.getUser().getUsername().equals(user.getUsername())) {
            return false;
        }
        
        try {
            bookingDAO.cancelBooking(bookingId);
            // Return the seat to available
            flightService.cancelFlightSeat(bookingToCancel.getFlight());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Booking> getAllBookings() {
        try {
            return bookingDAO.getAllBookings();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Booking> getBookingsByUsername(String username) {
        try {
            return bookingDAO.getBookingsByUser(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public List<Booking> getUserBookings(User user) {
        return getBookingsByUsername(user.getUsername());
    }

    public Booking getBookingById(String bookingId) {
        try {
            return bookingDAO.getBookingById(bookingId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
