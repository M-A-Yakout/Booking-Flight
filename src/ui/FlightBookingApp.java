package ui;

import java.util.List;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import model.User;
import model.Flight;
import model.Booking;
import service.AuthService;
import service.FlightService;
import service.BookingService;

public class FlightBookingApp {
    private AuthService authService;
    private FlightService flightService;
    private BookingService bookingService;
    private User currentUser;

    public FlightBookingApp() {
        authService = new AuthService();
        flightService = new FlightService();
        bookingService = new BookingService(flightService, authService.getUserDAO());
    }

    public static void main(String[] args) {
        FlightBookingApp app = new FlightBookingApp();
        app.start();
    }

    public void start() {
        try (Scanner scanner = new Scanner(System.in)) {
            this.scanner = scanner;
            boolean running = true;

            while (running) {
                if (currentUser == null) {
                    displayMainMenu();
                    int choice = getIntInput();

                    switch (choice) {
                        case 1:
                            login();
                            break;
                        case 2:
                            register();
                            break;
                        case 3:
                            running = false;
                            System.out.println("Thank you for using the Flight Booking App. Goodbye!");
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                } else {
                    if (currentUser.getRole().equalsIgnoreCase("ADMIN")) {
                        displayAdminMenu();
                    } else {
                        displayUserMenu();
                    }

                    int choice = getIntInput();
                    processUserChoice(choice);
                }
            }
        }
    }

    private Scanner scanner;

    private void displayMainMenu() {
        System.out.println("\n===== FLIGHT BOOKING SYSTEM =====");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
    }

    private void displayUserMenu() {
        System.out.println("\n===== WELCOME, " + currentUser.getUsername() + " =====");
        System.out.println("1. Search and Book Flights");
        System.out.println("2. Book a Flight by Number");
        System.out.println("3. View My Bookings");
        System.out.println("4. Cancel a Booking");
        System.out.println("5. Change Password");
        System.out.println("6. Logout");
        System.out.print("Enter your choice: ");
    }

    private void displayAdminMenu() {
        System.out.println("\n===== ADMIN MENU =====");
        System.out.println("1. Add New Flight");
        System.out.println("2. View All Flights");
        System.out.println("3. View All Bookings");
        System.out.println("4. View All Users");
        System.out.println("5. Change Password");
        System.out.println("6. Logout");
        System.out.print("Enter your choice: ");
    }

    private void processUserChoice(int choice) {
        if (currentUser == null) {
            System.out.println("Error: No user logged in.");
            return;
        }

        if (currentUser.getRole().equalsIgnoreCase("ADMIN")) {
            switch (choice) {
                case 1:
                    addFlight();
                    break;
                case 2:
                    viewAllFlights();
                    break;
                case 3:
                    viewAllBookings();
                    break;
                case 4:
                    viewAllUsers();
                    break;
                case 5:
                    changePassword();
                    break;
                case 6:
                    logout();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } else {
            switch (choice) {
                case 1:
                    searchFlights();
                    break;
                case 2:
                    bookFlight();
                    break;
                case 3:
                    viewMyBookings();
                    break;
                case 4:
                    cancelBooking();
                    break;
                case 5:
                    changePassword();
                    break;
                case 6:
                    logout();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void login() {
        System.out.println("\n===== LOGIN =====");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = authService.login(username, password);

        if (user != null) {
            currentUser = user;
            System.out.println("Login successful. Welcome, " + username + "!");
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    private void register() {
        System.out.println("\n===== REGISTER =====");
        System.out.print("Username: ");
        String username = scanner.nextLine();

        if (username.trim().isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }

        if (authService.getUserByUsername(username) != null) {
            System.out.println("Username already exists. Please choose another one.");
            return;
        }

        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Confirm Password: ");
        String confirmPassword = scanner.nextLine();

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return;
        }

        System.out.print("Full Name: ");
        String fullName = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        if (fullName.trim().isEmpty() || email.trim().isEmpty()) {
            System.out.println("Full name and email cannot be empty.");
            return;
        }

        User newUser = authService.register(username, password, fullName, email);

        if (newUser != null) {
            System.out.println("Registration successful. Please login with your credentials.");
        } else {
            System.out.println("Registration failed. Please try again.");
        }
    }

    private void addFlight() {
        System.out.println("\n===== ADD NEW FLIGHT =====");
        System.out.print("Flight Number: ");
        String flightNumber = scanner.nextLine();
        if (flightNumber.trim().isEmpty()) {
            System.out.println("Flight number cannot be empty.");
            return;
        }

        System.out.print("Airline: ");
        String airline = scanner.nextLine();
        if (airline.trim().isEmpty()) {
            System.out.println("Airline cannot be empty.");
            return;
        }

        System.out.print("Origin: ");
        String origin = scanner.nextLine();
        if (origin.trim().isEmpty()) {
            System.out.println("Origin cannot be empty.");
            return;
        }

        System.out.print("Destination: ");
        String destination = scanner.nextLine();
        if (destination.trim().isEmpty()) {
            System.out.println("Destination cannot be empty.");
            return;
        }

        LocalDateTime departureTime = getDateTimeInput("Departure Time (yyyy-MM-dd HH:mm): ");
        LocalDateTime arrivalTime = getDateTimeInput("Arrival Time (yyyy-MM-dd HH:mm): ");

        if (!departureTime.isBefore(arrivalTime)) {
            System.out.println("Departure time must be before arrival time.");
            return;
        }

        System.out.print("Total Seats: ");
        int totalSeats = getIntInput();
        if (totalSeats <= 0) {
            System.out.println("Total seats must be a positive number.");
            return;
        }

        System.out.print("Price: $");
        double price = getDoubleInput();
        if (price <= 0) {
            System.out.println("Price must be a positive number.");
            return;
        }

        Flight newFlight = new Flight(flightNumber, airline, origin, destination, departureTime, arrivalTime, totalSeats, price);
        if (flightService.addFlight(newFlight)) {
            System.out.println("Flight added successfully.");
        } else {
            System.out.println("Error adding flight. Flight number may already exist.");
        }
    }

    private void viewAllFlights() {
        System.out.println("\n===== ALL FLIGHTS =====");
        List<Flight> flights = flightService.getAllFlights();

        if (flights.isEmpty()) {
            System.out.println("No flights available.");
        } else {
            System.out.println("Flight# | Airline         | Origin          | Destination     | Departure            | Arrival              | Seats | Price");
            System.out.println("--------------------------------------------------------------------------------------------------------------");
            for (Flight flight : flights) {
                System.out.println(flight);
            }
        }
    }

    private void viewAllBookings() {
        System.out.println("\n===== ALL BOOKINGS =====");
        List<Booking> bookings = bookingService.getAllBookings();

        if (bookings.isEmpty()) {
            System.out.println("No bookings available.");
        } else {
            for (Booking booking : bookings) {
                System.out.println(booking);
            }
        }
    }

    private void viewAllUsers() {
        System.out.println("\n===== ALL USERS =====");
        List<User> users = authService.getAllUsers();

        if (users.isEmpty()) {
            System.out.println("No users available.");
        } else {
            for (User user : users) {
                System.out.println(user);
            }
        }
    }

    private void changePassword() {
        System.out.println("\n===== CHANGE PASSWORD =====");
        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine();
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();
        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return;
        }

        if (authService.changePassword(currentUser.getUsername(), currentPassword, newPassword)) {
            System.out.println("Password changed successfully.");
        } else {
            System.out.println("Current password is incorrect.");
        }
    }

    private void logout() {
        authService.logout();
        currentUser = null;
        System.out.println("Logged out successfully.");
    }

    private void searchFlights() {
        System.out.println("\n===== SEARCH FLIGHTS =====");
        System.out.print("Origin: ");
        String origin = scanner.nextLine();
        System.out.print("Destination: ");
        String destination = scanner.nextLine();

        LocalDateTime departureDate = getDateTimeInput("Departure Date (yyyy-MM-dd): ", true);

        List<Flight> availableFlights = flightService.searchFlights(origin, destination, departureDate);

        if (availableFlights.isEmpty()) {
            System.out.println("No flights available for the selected criteria.");
        } else {
            System.out.println("Flight# | Airline         | Origin          | Destination     | Departure            | Arrival              | Seats | Price");
            System.out.println("--------------------------------------------------------------------------------------------------------------");
            for (int i = 0; i < availableFlights.size(); i++) {
                System.out.println((i + 1) + ". " + availableFlights.get(i));
            }

            System.out.print("\nEnter the number of the flight to book (or 0 to return to menu): ");
            int selection = getIntInput();

            if (selection > 0 && selection <= availableFlights.size()) {
                Flight selectedFlight = availableFlights.get(selection - 1);
                Booking booking = bookingService.createBooking(currentUser, selectedFlight);

                if (booking != null) {
                    System.out.println("Booking successful! Your booking ID is: " + booking.getBookingId());
                } else {
                    System.out.println("Booking failed. The flight might be full.");
                }
            }
        }
    }

    private void bookFlight() {
        System.out.println("\n===== BOOK A FLIGHT =====");
        System.out.print("Enter Flight Number: ");
        String flightNumber = scanner.nextLine();

        Flight flight = flightService.getFlightByNumber(flightNumber);

        if (flight == null) {
            System.out.println("Flight not found.");
            return;
        }

        if (flight.getAvailableSeats() <= 0) {
            System.out.println("Sorry, this flight is fully booked.");
            return;
        }

        System.out.println("Selected flight: " + flight);
        System.out.print("Confirm booking (y/n): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("y")) {
            Booking booking = bookingService.createBooking(currentUser, flight);

            if (booking != null) {
                System.out.println("Booking successful! Your booking ID is: " + booking.getBookingId());
            } else {
                System.out.println("Booking failed. The flight might be full.");
            }
        }
    }

    private void viewMyBookings() {
        System.out.println("\n===== MY BOOKINGS =====");
        List<Booking> myBookings = bookingService.getBookingsByUsername(currentUser.getUsername());

        if (myBookings.isEmpty()) {
            System.out.println("You have no bookings.");
        } else {
            for (int i = 0; i < myBookings.size(); i++) {
                System.out.println((i + 1) + ". " + myBookings.get(i));
            }
        }
    }

    private void cancelBooking() {
        System.out.println("\n===== CANCEL BOOKING =====");
        List<Booking> myBookings = bookingService.getBookingsByUsername(currentUser.getUsername());

        if (myBookings.isEmpty()) {
            System.out.println("You have no bookings to cancel.");
            return;
        }

        for (int i = 0; i < myBookings.size(); i++) {
            System.out.println((i + 1) + ". " + myBookings.get(i));
        }

        System.out.print("Enter the number of the booking to cancel (or 0 to return): ");
        int selection = getIntInput();

        if (selection > 0 && selection <= myBookings.size()) {
            Booking selectedBooking = myBookings.get(selection - 1);

            if (selectedBooking.getStatus().equals("CANCELLED")) {
                System.out.println("This booking is already cancelled.");
                return;
            }

            System.out.print("Are you sure you want to cancel this booking? (y/n): ");
            String confirm = scanner.nextLine();

            if (confirm.equalsIgnoreCase("y")) {
                if (bookingService.cancelBooking(selectedBooking.getBookingId(), currentUser)) {
                    System.out.println("Booking cancelled successfully.");
                } else {
                    System.out.println("Error cancelling booking.");
                }
            }
        }
    }

    private int getIntInput() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.print("Input cannot be empty. Please enter a valid number: ");
                    continue;
                }
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    private double getDoubleInput() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.print("Input cannot be empty. Please enter a valid number: ");
                    continue;
                }
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    private LocalDateTime getDateTimeInput(String prompt) {
        return getDateTimeInput(prompt, false);
    }

    private LocalDateTime getDateTimeInput(String prompt, boolean dateOnly) {
        DateTimeFormatter formatter = dateOnly ?
                DateTimeFormatter.ofPattern("yyyy-MM-dd") :
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("Input cannot be empty. Please use " + (dateOnly ? "yyyy-MM-dd" : "yyyy-MM-dd HH:mm"));
                    continue;
                }

                if (dateOnly) {
                    return LocalDateTime.parse(input + " 00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                } else {
                    return LocalDateTime.parse(input, formatter);
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use " + (dateOnly ? "yyyy-MM-dd" : "yyyy-MM-dd HH:mm"));
            }
        }
    }
}