package service;

import model.Flight;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class FlightService {
    private FlightDAO flightDAO;
    private List<Flight> flights;

    public FlightService() {
        this.flightDAO = new FlightDAO();
        try {
            this.flights = flightDAO.getAllFlights();
        } catch (SQLException e) {
            this.flights = new ArrayList<>();
            e.printStackTrace();
        }
    }

    public boolean addFlight(Flight flight) {
        try {
            // Check if flight already exists
            if (flightDAO.getFlightByNumber(flight.getFlightNumber()) != null) {
                return false;
            }
            flightDAO.addFlight(flight);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFlight(String flightNumber, Flight updatedFlight) {
        try {
            flightDAO.updateFlight(updatedFlight);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeFlight(String flightNumber) {
        try {
            flightDAO.deleteFlight(flightNumber);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Flight getFlightByNumber(String flightNumber) {
        try {
            return flightDAO.getFlightByNumber(flightNumber);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Flight> getAllFlights() {
        try {
            return flightDAO.getAllFlights();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Flight> searchFlights(String origin, String destination, LocalDateTime date) {
        return flights.stream()
                .filter(flight -> flight.getOrigin().equalsIgnoreCase(origin) &&
                        flight.getDestination().equalsIgnoreCase(destination) &&
                        flight.getDepartureTime().toLocalDate().equals(date.toLocalDate()) &&
                        flight.getAvailableSeats() > 0)
                .collect(Collectors.toList());
    }

    public boolean bookFlight(Flight flight) {
        if (flight.getAvailableSeats() > 0) {
            flight.bookSeat();
            try {
                flightDAO.updateSeats(flight.getFlightNumber(), flight.getAvailableSeats());
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public void cancelFlightSeat(Flight flight) {
        flight.cancelSeat();
        try {
            flightDAO.updateSeats(flight.getFlightNumber(), flight.getAvailableSeats());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
