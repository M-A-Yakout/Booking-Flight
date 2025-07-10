package service;

import java.sql.*;
import model.Flight;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

public class FlightDAO {
    private static final String INSERT_FLIGHT = "INSERT INTO flights (flight_number, airline, origin, destination, departure_time, arrival_time, total_seats, available_seats, price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_FLIGHT = "UPDATE flights SET airline=?, origin=?, destination=?, departure_time=?, arrival_time=?, total_seats=?, available_seats=?, price=? WHERE flight_number=?";
    private static final String DELETE_FLIGHT = "DELETE FROM flights WHERE flight_number=?";
    private static final String GET_FLIGHT_BY_NUMBER = "SELECT * FROM flights WHERE flight_number=?";
    private static final String GET_ALL_FLIGHTS = "SELECT * FROM flights";
    private static final String UPDATE_SEATS = "UPDATE flights SET available_seats=? WHERE flight_number=?";

    public void addFlight(Flight flight) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            PreparedStatement stmt = conn.prepareStatement(INSERT_FLIGHT);
            stmt.setString(1, flight.getFlightNumber());
            stmt.setString(2, flight.getAirline());
            stmt.setString(3, flight.getOrigin());
            stmt.setString(4, flight.getDestination());
            stmt.setTimestamp(5, Timestamp.valueOf(flight.getDepartureTime()));
            stmt.setTimestamp(6, Timestamp.valueOf(flight.getArrivalTime()));
            stmt.setInt(7, flight.getTotalSeats());
            stmt.setInt(8, flight.getAvailableSeats());
            stmt.setDouble(9, flight.getPrice());
            
            stmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public void updateFlight(Flight flight) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_FLIGHT)) {
            
            stmt.setString(1, flight.getAirline());
            stmt.setString(2, flight.getOrigin());
            stmt.setString(3, flight.getDestination());
            stmt.setTimestamp(4, Timestamp.valueOf(flight.getDepartureTime()));
            stmt.setTimestamp(5, Timestamp.valueOf(flight.getArrivalTime()));
            stmt.setInt(6, flight.getTotalSeats());
            stmt.setInt(7, flight.getAvailableSeats());
            stmt.setDouble(8, flight.getPrice());
            stmt.setString(9, flight.getFlightNumber());
            
            stmt.executeUpdate();
        }
    }

    public void deleteFlight(String flightNumber) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_FLIGHT)) {
            
            stmt.setString(1, flightNumber);
            stmt.executeUpdate();
        }
    }

    public Flight getFlightByNumber(String flightNumber) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_FLIGHT_BY_NUMBER)) {
            
            stmt.setString(1, flightNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractFlightFromResultSet(rs);
            }
            return null;
        }
    }

    public List<Flight> getAllFlights() throws SQLException {
        List<Flight> flights = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL_FLIGHTS)) {
            
            while (rs.next()) {
                flights.add(extractFlightFromResultSet(rs));
            }
        }
        return flights;
    }

    public void updateSeats(String flightNumber, int availableSeats) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SEATS)) {
            
            stmt.setInt(1, availableSeats);
            stmt.setString(2, flightNumber);
            stmt.executeUpdate();
        }
    }

    private Flight extractFlightFromResultSet(ResultSet rs) throws SQLException {
        return new Flight(
            rs.getString("flight_number"),
            rs.getString("airline"),
            rs.getString("origin"),
            rs.getString("destination"),
            rs.getTimestamp("departure_time").toLocalDateTime(),
            rs.getTimestamp("arrival_time").toLocalDateTime(),
            rs.getInt("total_seats"),
            rs.getDouble("price")
        );
    }
}