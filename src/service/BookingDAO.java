package service;

import model.User;
import java.sql.*;
import model.Flight;
import model.Booking;
import java.util.List;
import java.util.ArrayList;

public class BookingDAO {
    private UserDAO userDAO;
    private FlightService flightService;

    public BookingDAO(UserDAO userDAO, FlightService flightService) {
        this.userDAO = userDAO;
        this.flightService = flightService;
    }
    private static final String INSERT_BOOKING = "INSERT INTO bookings (booking_id, user_id, flight_number, booking_date, status) VALUES (?, ?, ?, ?, ?)";
    private static final String DELETE_BOOKING = "DELETE FROM bookings WHERE booking_id=?";
    private static final String GET_BOOKING_BY_ID = "SELECT * FROM bookings WHERE booking_id=?";
    private static final String GET_ALL_BOOKINGS = "SELECT * FROM bookings";
    private static final String GET_BOOKINGS_BY_USER = "SELECT * FROM bookings WHERE user_id=?";

    public void createBooking(Booking booking) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_BOOKING)) {
            
            stmt.setString(1, booking.getBookingId());
            stmt.setString(2, booking.getUser().getUsername());
            stmt.setString(3, booking.getFlight().getFlightNumber());
            stmt.setTimestamp(4, Timestamp.valueOf(booking.getBookingDate()));
            stmt.setString(5, booking.getStatus());
            
            stmt.executeUpdate();
        }
    }

    public void cancelBooking(String bookingId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_BOOKING)) {
            
            stmt.setString(1, bookingId);
            stmt.executeUpdate();
        }
    }

    public Booking getBookingById(String bookingId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_BOOKING_BY_ID)) {
            
            stmt.setString(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractBookingFromResultSet(rs);
            }
            return null;
        }
    }

    public List<Booking> getAllBookings() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL_BOOKINGS)) {
            
            while (rs.next()) {
                bookings.add(extractBookingFromResultSet(rs));
            }
        }
        return bookings;
    }

    public List<Booking> getBookingsByUser(String username) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_BOOKINGS_BY_USER)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                bookings.add(extractBookingFromResultSet(rs));
            }
        }
        return bookings;
    }

    private Booking extractBookingFromResultSet(ResultSet rs) throws SQLException {
        String username = rs.getString("user_id");
        String flightNumber = rs.getString("flight_number");
        
        User user = userDAO.getUserByUsername(username);
        Flight flight = flightService.getFlightByNumber(flightNumber);
        
        if (user == null || flight == null) {
            return null;
        }
        
        return new Booking(
            user,
            flight,
            rs.getString("booking_id"),
            rs.getTimestamp("booking_date").toLocalDateTime(),
            rs.getString("status")
        );
    }
}