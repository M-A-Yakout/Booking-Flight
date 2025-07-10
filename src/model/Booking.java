package model;

import java.util.UUID;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Booking implements Serializable {
    private String bookingId;
    private User user;
    private Flight flight;
    private LocalDateTime bookingTime;
    private String status;

    public Booking(User user, Flight flight) {
        this.bookingId = UUID.randomUUID().toString().substring(0, 8);
        this.user = user;
        this.flight = flight;
        this.bookingTime = LocalDateTime.now();
        this.status = "CONFIRMED";
    }
    
    public Booking(User user, Flight flight, String bookingId, LocalDateTime bookingDate, String status) {
        this.bookingId = bookingId;
        this.user = user;
        this.flight = flight;
        this.bookingTime = bookingDate;
        this.status = status;
    }

    public String getBookingId() {
        return bookingId;
    }

    public User getUser() {
        return user;
    }
    
    

    public Flight getFlight() {
        return flight;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }
    
    public LocalDateTime getBookingDate() {
        return bookingTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("Booking ID: %s | User: %s | Flight: %s | %s to %s | Booked: %s | Status: %s",
                bookingId, user.getUsername(), flight.getFlightNumber(), 
                flight.getOrigin(), flight.getDestination(), 
                bookingTime.format(formatter), status);
    }
}
