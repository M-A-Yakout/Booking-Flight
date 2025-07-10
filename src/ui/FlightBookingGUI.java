package ui;

import java.awt.*;
import javax.swing.*;
import java.util.List;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import javax.swing.border.EmptyBorder;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;import java.time.format.DateTimeParseException;

import model.User;
import model.Flight;
import model.Booking;
import service.AuthService;
import service.FlightService;
import service.BookingService;

public class FlightBookingGUI {
    // Colors
    private static final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private static final Color SECONDARY_COLOR = new Color(240, 240, 240);
    private static final Color ACCENT_COLOR = new Color(0, 153, 204);
    private static final Color TEXT_COLOR = new Color(51, 51, 51);
    private static final Color ERROR_COLOR = new Color(220, 53, 69);

    // Fonts
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private AuthService authService;
    private FlightService flightService;
    private BookingService bookingService;
    private User currentUser;

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    // Card names
    private static final String MAIN_MENU = "MAIN_MENU";
    private static final String LOGIN = "LOGIN";
    private static final String REGISTER = "REGISTER";
    private static final String USER_MENU = "USER_MENU";
    private static final String ADMIN_MENU = "ADMIN_MENU";

    public FlightBookingGUI() {
        authService = new AuthService();
        flightService = new FlightService();
        bookingService = new BookingService(flightService, authService.getUserDAO());

        initializeGUI();
    }

    private void initializeGUI() {
        frame = new JFrame("Flight Booking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);

        // Modern UI styling
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            JFrame.setDefaultLookAndFeelDecorated(true);

            // Custom UI defaults
            UIManager.put("Button.background", PRIMARY_COLOR);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.font", BUTTON_FONT);
            UIManager.put("Button.border", BorderFactory.createEmptyBorder(10, 20, 10, 20));
            UIManager.put("Label.font", LABEL_FONT);
            UIManager.put("TextField.font", TEXT_FONT);
            UIManager.put("PasswordField.font", TEXT_FONT);
            UIManager.put("Table.font", TEXT_FONT);
            UIManager.put("TableHeader.font", LABEL_FONT);
            UIManager.put("OptionPane.messageFont", TEXT_FONT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Color.WHITE);

        // Create and add cards
        cardPanel.add(createMainMenuPanel(), MAIN_MENU);
        cardPanel.add(createLoginPanel(), LOGIN);
        cardPanel.add(createRegisterPanel(), REGISTER);
        cardPanel.add(createUserMenuPanel(), USER_MENU);
        cardPanel.add(createAdminMenuPanel(), ADMIN_MENU);

        frame.add(cardPanel);
        showMainMenu();
    }

    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Header
        JLabel titleLabel = new JLabel("Flight Booking System", JLabel.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 40, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(3, 1, 15, 15));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setBorder(new EmptyBorder(20, 150, 20, 150));

        JButton loginButton = createStyledButton("Login");
        loginButton.addActionListener(e -> showLogin());

        JButton registerButton = createStyledButton("Register");
        registerButton.addActionListener(e -> showRegister());

        JButton exitButton = createStyledButton("Exit");
        exitButton.setBackground(ERROR_COLOR);
        exitButton.addActionListener(e -> System.exit(0));

        buttonsPanel.add(loginButton);
        buttonsPanel.add(registerButton);
        buttonsPanel.add(exitButton);

        panel.add(buttonsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Header
        JLabel titleLabel = new JLabel("Login", JLabel.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 40, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username field
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username:");
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        JTextField usernameField = new JTextField(25);
        usernameField.setPreferredSize(new Dimension(250, 35));
        formPanel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("Password:");
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(25);
        passwordField.setPreferredSize(new Dimension(250, 35));
        formPanel.add(passwordField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(Color.WHITE);

        JButton loginButton = createStyledButton("Login");
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            User user = authService.login(username, password);
            if (user != null) {
                currentUser = user;
                if (user.getRole().equalsIgnoreCase("ADMIN")) {
                    showAdminMenu();
                } else {
                    showUserMenu();
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton backButton = createStyledButton("Back");
        backButton.setBackground(SECONDARY_COLOR);
        backButton.setForeground(TEXT_COLOR);
        backButton.addActionListener(e -> showMainMenu());

        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Header
        JLabel titleLabel = new JLabel("Register", JLabel.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 40, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username field
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username:");
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        JTextField usernameField = new JTextField(25);
        usernameField.setPreferredSize(new Dimension(250, 35));
        formPanel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("Password:");
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(25);
        passwordField.setPreferredSize(new Dimension(250, 35));
        formPanel.add(passwordField, gbc);

        // Confirm Password field
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        formPanel.add(confirmPasswordLabel, gbc);

        gbc.gridx = 1;
        JPasswordField confirmPasswordField = new JPasswordField(25);
        confirmPasswordField.setPreferredSize(new Dimension(250, 35));
        formPanel.add(confirmPasswordField, gbc);

        // Full Name field
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel fullNameLabel = new JLabel("Full Name:");
        formPanel.add(fullNameLabel, gbc);

        gbc.gridx = 1;
        JTextField fullNameField = new JTextField(25);
        fullNameField.setPreferredSize(new Dimension(250, 35));
        formPanel.add(fullNameField, gbc);

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel emailLabel = new JLabel("Email:");
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        JTextField emailField = new JTextField(25);
        emailField.setPreferredSize(new Dimension(250, 35));
        formPanel.add(emailField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(Color.WHITE);

        JButton registerButton = createStyledButton("Register");
        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String fullName = fullNameField.getText();
            String email = emailField.getText();

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(frame, "Passwords do not match", "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (username.trim().isEmpty() || fullName.trim().isEmpty() || email.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields are required", "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User newUser = authService.register(username, password, fullName, email);
            if (newUser != null) {
                JOptionPane.showMessageDialog(frame, "Registration successful. Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
                showLogin();
            } else {
                JOptionPane.showMessageDialog(frame, "Registration failed. Username may already exist.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton backButton = createStyledButton("Back");
        backButton.setBackground(SECONDARY_COLOR);
        backButton.setForeground(TEXT_COLOR);
        backButton.addActionListener(e -> showMainMenu());

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createUserMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Header
        String welcomeText = currentUser != null ?
                "Welcome, " + currentUser.getFullName() + "!" : "Welcome!";
        JLabel titleLabel = new JLabel(welcomeText, JLabel.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 40, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setBorder(new EmptyBorder(20, 100, 20, 100));

        JButton searchFlightsButton = createStyledButton("Search and Book Flights");
        searchFlightsButton.addActionListener(e -> showFlightSearchPanel());

        JButton bookFlightButton = createStyledButton("Book a Flight by Number");
        bookFlightButton.addActionListener(e -> showFlightBookingPanel());

        JButton viewBookingsButton = createStyledButton("View My Bookings");
        viewBookingsButton.addActionListener(e -> showUserBookingsPanel());

        JButton cancelBookingButton = createStyledButton("Cancel a Booking");
        cancelBookingButton.addActionListener(e -> showCancelBookingPanel());

        JButton changePasswordButton = createStyledButton("Change Password");
        changePasswordButton.addActionListener(e -> showChangePasswordPanel());

        JButton logoutButton = createStyledButton("Logout");
        logoutButton.setBackground(ERROR_COLOR);
        logoutButton.addActionListener(e -> {
            authService.logout();
            currentUser = null;
            showMainMenu();
        });

        buttonsPanel.add(searchFlightsButton);
        buttonsPanel.add(bookFlightButton);
        buttonsPanel.add(viewBookingsButton);
        buttonsPanel.add(cancelBookingButton);
        buttonsPanel.add(changePasswordButton);
        buttonsPanel.add(logoutButton);

        panel.add(buttonsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAdminMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Header
        String welcomeText = currentUser != null ?
                "Welcome, Admin " + currentUser.getFullName() + "!" : "Welcome, Admin!";
        JLabel titleLabel = new JLabel(welcomeText, JLabel.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 40, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setBorder(new EmptyBorder(20, 100, 20, 100));

        JButton addFlightButton = createStyledButton("Add New Flight");
        addFlightButton.addActionListener(e -> showAddFlightPanel());

        JButton viewFlightsButton = createStyledButton("View All Flights");
        viewFlightsButton.addActionListener(e -> showAllFlightsPanel());

        JButton viewBookingsButton = createStyledButton("View All Bookings");
        viewBookingsButton.addActionListener(e -> showAllBookingsPanel());

        JButton viewUsersButton = createStyledButton("View All Users");
        viewUsersButton.addActionListener(e -> showAllUsersPanel());

        JButton changePasswordButton = createStyledButton("Change Password");
        changePasswordButton.addActionListener(e -> showChangePasswordPanel());

        JButton logoutButton = createStyledButton("Logout");
        logoutButton.setBackground(ERROR_COLOR);
        logoutButton.addActionListener(e -> {
            authService.logout();
            currentUser = null;
            showMainMenu();
        });

        buttonsPanel.add(addFlightButton);
        buttonsPanel.add(viewFlightsButton);
        buttonsPanel.add(viewBookingsButton);
        buttonsPanel.add(viewUsersButton);
        buttonsPanel.add(changePasswordButton);
        buttonsPanel.add(logoutButton);

        panel.add(buttonsPanel, BorderLayout.CENTER);

        return panel;
    }

    // Flight list panel
    private JPanel createFlightListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Get all available flights
        List<Flight> allFlights = flightService.getAllFlights().stream()
                .filter(f -> f.getAvailableSeats() > 0)
                .collect(Collectors.toList());

        showFlightResults(allFlights);

        // Back button
        JButton backButton = createStyledButton("Back");
        backButton.setBackground(SECONDARY_COLOR);
        backButton.setForeground(TEXT_COLOR);
        backButton.addActionListener(e -> showUserMenu());
        panel.add(backButton, BorderLayout.SOUTH);

        return panel;
    }

    private void showFlightResults(List<Flight> flights) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JLabel titleLabel = new JLabel("Available Flights", JLabel.CENTER);
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        if (flights.isEmpty()) {
            panel.add(new JLabel("No flights found matching your criteria.", JLabel.CENTER), BorderLayout.CENTER);
        } else {
            String[] columnNames = {"#", "Flight#", "Airline", "Origin", "Destination", "Departure", "Arrival", "Seats", "Price"};
            Object[][] data = new Object[flights.size()][9];

            for (int i = 0; i < flights.size(); i++) {
                Flight flight = flights.get(i);
                data[i][0] = i + 1;
                data[i][1] = flight.getFlightNumber();
                data[i][2] = flight.getAirline();
                data[i][3] = flight.getOrigin();
                data[i][4] = flight.getDestination();
                data[i][5] = flight.getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                data[i][6] = flight.getArrivalTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                data[i][7] = flight.getAvailableSeats();
                data[i][8] = "$" + flight.getPrice();
            }

            JTable flightTable = new JTable(data, columnNames);
            flightTable.setFillsViewportHeight(true);
            flightTable.setRowHeight(30);
            flightTable.setShowGrid(true);
            flightTable.setGridColor(new Color(230, 230, 230));
            flightTable.setFont(TEXT_FONT);
            flightTable.getTableHeader().setFont(LABEL_FONT);

            JScrollPane scrollPane = new JScrollPane(flightTable);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            panel.add(scrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
            buttonPanel.setBackground(Color.WHITE);

            JButton bookButton = createStyledButton("Book Selected Flight");
            bookButton.addActionListener(e -> {
                int selectedRow = flightTable.getSelectedRow();
                if (selectedRow >= 0) {
                    Flight selectedFlight = flights.get(selectedRow);
                    Booking booking = bookingService.createBooking(currentUser, selectedFlight);
                    if (booking != null) {
                        JOptionPane.showMessageDialog(frame, "Booking successful! Your booking ID is: " + booking.getBookingId(), "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Booking failed. The flight might be full.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a flight to book", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            JButton backButton = createStyledButton("Back");
            backButton.setBackground(SECONDARY_COLOR);
            backButton.setForeground(TEXT_COLOR);
            backButton.addActionListener(e -> showUserMenu());

            buttonPanel.add(bookButton);
            buttonPanel.add(backButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);
        }

        cardPanel.add(panel, "FLIGHT_RESULTS");
        cardLayout.show(cardPanel, "FLIGHT_RESULTS");
    }

    private void showMainMenu() {
        cardLayout.show(cardPanel, MAIN_MENU);
        frame.setVisible(true);
    }

    private void showLogin() {
        cardLayout.show(cardPanel, LOGIN);
    }

    private void showRegister() {
        cardLayout.show(cardPanel, REGISTER);
    }

    private void showUserMenu() {
        cardLayout.show(cardPanel, USER_MENU);
    }

    private void showFlightSearchPanel() {
        cardPanel.add(createFlightListPanel(), "FLIGHT_LIST");
        cardLayout.show(cardPanel, "FLIGHT_SEARCH");
    }

    private void showFlightBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Header
        JLabel titleLabel = new JLabel("Book Flight by Number", JLabel.CENTER);
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 40, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel flightNumberLabel = new JLabel("Flight Number:");
        formPanel.add(flightNumberLabel, gbc);

        gbc.gridx = 1;
        JTextField flightNumberField = new JTextField(25);
        flightNumberField.setPreferredSize(new Dimension(250, 35));
        formPanel.add(flightNumberField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(Color.WHITE);

        JButton bookButton = createStyledButton("Book Flight");
        bookButton.addActionListener(e -> {
            String flightNumber = flightNumberField.getText();
            Flight flight = flightService.getFlightByNumber(flightNumber);
            if (flight != null) {
                Booking booking = bookingService.createBooking(currentUser, flight);
                if (booking != null) {
                    JOptionPane.showMessageDialog(frame, "Booking successful! Your booking ID is: " + booking.getBookingId(), "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Booking failed. The flight might be full.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Flight not found", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton backButton = createStyledButton("Back");
        backButton.setBackground(SECONDARY_COLOR);
        backButton.setForeground(TEXT_COLOR);
        backButton.addActionListener(e -> showUserMenu());

        buttonPanel.add(bookButton);
        buttonPanel.add(backButton);

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        cardPanel.add(panel, "FLIGHT_BOOKING");
        cardLayout.show(cardPanel, "FLIGHT_BOOKING");
    }

    private void showUserBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JLabel titleLabel = new JLabel("My Bookings", JLabel.CENTER);
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        List<Booking> bookings = bookingService.getUserBookings(currentUser);

        if (bookings.isEmpty()) {
            panel.add(new JLabel("You have no bookings yet.", JLabel.CENTER), BorderLayout.CENTER);
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
            
            JButton backButton = createStyledButton("Back");
            backButton.setBackground(SECONDARY_COLOR);
            backButton.setForeground(TEXT_COLOR);
            backButton.addActionListener(e -> showUserMenu());
            buttonPanel.add(backButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);
        } else {
            String[] columnNames = {"Booking ID", "Flight Number", "Airline", "Origin", "Destination", "Departure", "Status"};
            Object[][] data = new Object[bookings.size()][7];

            for (int i = 0; i < bookings.size(); i++) {
                Booking booking = bookings.get(i);
                if (booking == null) {
                    continue;
                }
                data[i][0] = booking.getBookingId() != null ? booking.getBookingId() : "N/A";
                Flight flight = booking.getFlight();
                if (flight != null) {
                    data[i][1] = flight.getFlightNumber() != null ? flight.getFlightNumber() : "N/A";
                    data[i][2] = flight.getAirline() != null ? flight.getAirline() : "N/A";
                    data[i][3] = flight.getOrigin() != null ? flight.getOrigin() : "N/A";
                    data[i][4] = flight.getDestination() != null ? flight.getDestination() : "N/A";
                    data[i][5] = flight.getDepartureTime() != null ?
                            flight.getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A";
                } else {
                    data[i][1] = "N/A";
                    data[i][2] = "N/A";
                    data[i][3] = "N/A";
                    data[i][4] = "N/A";
                    data[i][5] = "N/A";
                }
                data[i][6] = booking.getStatus() != null ? booking.getStatus() : "N/A";
            }

            JTable bookingsTable = new JTable(data, columnNames);
            bookingsTable.setRowHeight(30);
            bookingsTable.setShowGrid(true);
            bookingsTable.setGridColor(new Color(230, 230, 230));
            bookingsTable.setFont(TEXT_FONT);
            bookingsTable.getTableHeader().setFont(LABEL_FONT);

            JScrollPane scrollPane = new JScrollPane(bookingsTable);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            panel.add(scrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

            JButton backButton = createStyledButton("Back");
            backButton.setBackground(SECONDARY_COLOR);
            backButton.setForeground(TEXT_COLOR);
            backButton.addActionListener(e -> showUserMenu());
            buttonPanel.add(backButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);
        }

        cardPanel.add(panel, "USER_BOOKINGS");
        cardLayout.show(cardPanel, "USER_BOOKINGS");
    }

    private void showCancelBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Header
        JLabel titleLabel = new JLabel("Cancel Booking", JLabel.CENTER);
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 40, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel bookingIdLabel = new JLabel("Booking ID:");
        formPanel.add(bookingIdLabel, gbc);

        gbc.gridx = 1;
        JTextField bookingIdField = new JTextField(25);
        bookingIdField.setPreferredSize(new Dimension(250, 35));
        formPanel.add(bookingIdField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(Color.WHITE);

        JButton cancelButton = createStyledButton("Cancel Booking");
        cancelButton.setBackground(ERROR_COLOR);
        cancelButton.addActionListener(e -> {
            String bookingId = bookingIdField.getText();
            boolean success = bookingService.cancelBooking(bookingId, currentUser);
            if (success) {
                JOptionPane.showMessageDialog(frame, "Booking cancelled successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                showUserMenu();
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to cancel booking", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton backButton = createStyledButton("Back");
        backButton.setBackground(SECONDARY_COLOR);
        backButton.setForeground(TEXT_COLOR);
        backButton.addActionListener(e -> showUserMenu());

        buttonPanel.add(cancelButton);
        buttonPanel.add(backButton);

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        cardPanel.add(panel, "CANCEL_BOOKING");
        cardLayout.show(cardPanel, "CANCEL_BOOKING");
    }

    private void showChangePasswordPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Header
        JLabel titleLabel = new JLabel("Change Password", JLabel.CENTER);
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 40, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Current Password
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel currentPasswordLabel = new JLabel("Current Password:");
        formPanel.add(currentPasswordLabel, gbc);

        gbc.gridx = 1;
        JPasswordField currentPasswordField = new JPasswordField(25);
        currentPasswordField.setPreferredSize(new Dimension(250, 35));
        formPanel.add(currentPasswordField, gbc);

        // New Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel newPasswordLabel = new JLabel("New Password:");
        formPanel.add(newPasswordLabel, gbc);

        gbc.gridx = 1;
        JPasswordField newPasswordField = new JPasswordField(25);
        newPasswordField.setPreferredSize(new Dimension(250, 35));
        formPanel.add(newPasswordField, gbc);

        // Confirm New Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel confirmPasswordLabel = new JLabel("Confirm New Password:");
        formPanel.add(confirmPasswordLabel, gbc);

        gbc.gridx = 1;
        JPasswordField confirmPasswordField = new JPasswordField(25);
        confirmPasswordField.setPreferredSize(new Dimension(250, 35));
        formPanel.add(confirmPasswordField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(Color.WHITE);

        JButton changeButton = createStyledButton("Change Password");
        changeButton.addActionListener(e -> {
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(frame, "New passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = authService.changePassword(currentUser.getUsername(), currentPassword, newPassword);
            if (success) {
                JOptionPane.showMessageDialog(frame, "Password changed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                if (currentUser.getRole().equalsIgnoreCase("ADMIN")) {
                    showAdminMenu();
                } else {
                    showUserMenu();
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to change password. Please check your current password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton backButton = createStyledButton("Back");
        backButton.setBackground(SECONDARY_COLOR);
        backButton.setForeground(TEXT_COLOR);
        backButton.addActionListener(e -> {
            if (currentUser.getRole().equalsIgnoreCase("ADMIN")) {
                showAdminMenu();
            } else {
                showUserMenu();
            }
        });

        buttonPanel.add(changeButton);
        buttonPanel.add(backButton);

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        cardPanel.add(panel, "CHANGE_PASSWORD");
        cardLayout.show(cardPanel, "CHANGE_PASSWORD");
    }

    private void showAddFlightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Header
        JLabel titleLabel = new JLabel("Add New Flight", JLabel.CENTER);
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 40, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Flight Number
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel flightNumberLabel = new JLabel("Flight Number:");
        formPanel.add(flightNumberLabel, gbc);

        gbc.gridx = 1;
        JTextField flightNumberField = new JTextField(25);
        flightNumberField.setPreferredSize(new Dimension(250, 35));
        formPanel.add(flightNumberField, gbc);

        // Airline
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel airlineLabel = new JLabel("Airline:");
        formPanel.add(airlineLabel, gbc);

        gbc.gridx = 1;
        JTextField airlineField = new JTextField(25);
        airlineField.setPreferredSize(new Dimension(250, 35));
        formPanel.add(airlineField, gbc);

        // Origin
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel originLabel = new JLabel("Origin:");
        formPanel.add(originLabel, gbc);

        gbc.gridx = 1;
        JTextField originField = new JTextField(25);
        originField.setPreferredSize(new Dimension(250, 35));
        formPanel.add(originField, gbc);

        // Destination
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel destinationLabel = new JLabel("Destination:");
        formPanel.add(destinationLabel, gbc);

        gbc.gridx = 1;
        JTextField destinationField = new JTextField(25);
        destinationField.setPreferredSize(new Dimension(250, 35));
        formPanel.add(destinationField, gbc);

        // Departure Time
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel departureLabel = new JLabel("Departure (yyyy-MM-dd HH:mm):");
        formPanel.add(departureLabel, gbc);

        gbc.gridx = 1;
        JTextField departureField = new JTextField(25);
        departureField.setPreferredSize(new Dimension(250, 35));
        formPanel.add(departureField, gbc);

        // Arrival Time
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel arrivalLabel = new JLabel("Arrival (yyyy-MM-dd HH:mm):");
        formPanel.add(arrivalLabel, gbc);

        gbc.gridx = 1;
        JTextField arrivalField = new JTextField(25);
        arrivalField.setPreferredSize(new Dimension(250, 35));
        formPanel.add(arrivalField, gbc);

        // Price
        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel priceLabel = new JLabel("Price:");
        formPanel.add(priceLabel, gbc);

        gbc.gridx = 1;
        JTextField priceField = new JTextField(25);
        priceField.setPreferredSize(new Dimension(250, 35));
        formPanel.add(priceField, gbc);

        // Seats
        gbc.gridx = 0;
        gbc.gridy = 7;
        JLabel seatsLabel = new JLabel("Available Seats:");
        formPanel.add(seatsLabel, gbc);

        gbc.gridx = 1;
        JTextField seatsField = new JTextField(25);
        seatsField.setPreferredSize(new Dimension(250, 35));
        formPanel.add(seatsField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(Color.WHITE);

        JButton addButton = createStyledButton("Add Flight");
        addButton.addActionListener(e -> {
            try {
                String flightNumber = flightNumberField.getText();
                String airline = airlineField.getText();
                String origin = originField.getText();
                String destination = destinationField.getText();
                LocalDateTime departure = LocalDateTime.parse(departureField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                LocalDateTime arrival = LocalDateTime.parse(arrivalField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                double price = Double.parseDouble(priceField.getText());
                int seats = Integer.parseInt(seatsField.getText());

                Flight flight = new Flight(flightNumber, airline, origin, destination, departure, arrival, seats, price);
                boolean success = flightService.addFlight(flight);

                if (success) {
                    JOptionPane.showMessageDialog(frame, "Flight added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    showAdminMenu();
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to add flight", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton backButton = createStyledButton("Back");
        backButton.setBackground(SECONDARY_COLOR);
        backButton.setForeground(TEXT_COLOR);
        backButton.addActionListener(e -> showAdminMenu());

        buttonPanel.add(addButton);
        buttonPanel.add(backButton);

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        cardPanel.add(panel, "ADD_FLIGHT");
        cardLayout.show(cardPanel, "ADD_FLIGHT");
    }

    private void showAllFlightsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JLabel titleLabel = new JLabel("All Flights", JLabel.CENTER);
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        List<Flight> flights = flightService.getAllFlights();

        if (flights.isEmpty()) {
            panel.add(new JLabel("No flights available.", JLabel.CENTER), BorderLayout.CENTER);
        } else {
            String[] columnNames = {"Flight#", "Airline", "Origin", "Destination", "Departure", "Arrival", "Seats", "Price"};
            Object[][] data = new Object[flights.size()][8];

            for (int i = 0; i < flights.size(); i++) {
                Flight flight = flights.get(i);
                data[i][0] = flight.getFlightNumber();
                data[i][1] = flight.getAirline();
                data[i][2] = flight.getOrigin();
                data[i][3] = flight.getDestination();
                data[i][4] = flight.getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                data[i][5] = flight.getArrivalTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                data[i][6] = flight.getAvailableSeats();
                data[i][7] = "$" + flight.getPrice();
            }

            JTable flightsTable = new JTable(data, columnNames);
            flightsTable.setRowHeight(30);
            flightsTable.setShowGrid(true);
            flightsTable.setGridColor(new Color(230, 230, 230));
            flightsTable.setFont(TEXT_FONT);
            flightsTable.getTableHeader().setFont(LABEL_FONT);

            JScrollPane scrollPane = new JScrollPane(flightsTable);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            panel.add(scrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

            JButton backButton = createStyledButton("Back");
            backButton.setBackground(SECONDARY_COLOR);
            backButton.setForeground(TEXT_COLOR);
            backButton.addActionListener(e -> showAdminMenu());
            buttonPanel.add(backButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);
        }

        cardPanel.add(panel, "ALL_FLIGHTS");
        cardLayout.show(cardPanel, "ALL_FLIGHTS");
    }

    private void showAllBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JLabel titleLabel = new JLabel("All Bookings", JLabel.CENTER);
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        List<Booking> bookings = bookingService.getAllBookings();

        if (bookings.isEmpty()) {
            panel.add(new JLabel("No bookings found.", JLabel.CENTER), BorderLayout.CENTER);
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

            JButton backButton = createStyledButton("Back");
            backButton.setBackground(SECONDARY_COLOR);
            backButton.setForeground(TEXT_COLOR);
            backButton.addActionListener(e -> showAdminMenu());
            buttonPanel.add(backButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);
        } else {
            String[] columnNames = {"Booking ID", "User", "Flight#", "Airline", "Origin", "Destination", "Departure", "Status"};
            Object[][] data = new Object[bookings.size()][8];

            for (int i = 0; i < bookings.size(); i++) {
                Booking booking = bookings.get(i);
                Flight flight = booking.getFlight();
                User user = booking.getUser();
                data[i][0] = booking.getBookingId();
                data[i][1] = user.getUsername();
                data[i][2] = flight.getFlightNumber();
                data[i][3] = flight.getAirline();
                data[i][4] = flight.getOrigin();
                data[i][5] = flight.getDestination();
                data[i][6] = flight.getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                data[i][7] = booking.getStatus();
            }

            JTable bookingsTable = new JTable(data, columnNames);
            bookingsTable.setRowHeight(30);
            bookingsTable.setShowGrid(true);
            bookingsTable.setGridColor(new Color(230, 230, 230));
            bookingsTable.setFont(TEXT_FONT);
            bookingsTable.getTableHeader().setFont(LABEL_FONT);

            JScrollPane scrollPane = new JScrollPane(bookingsTable);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            panel.add(scrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

            JButton backButton = createStyledButton("Back");
            backButton.setBackground(SECONDARY_COLOR);
            backButton.setForeground(TEXT_COLOR);
            backButton.addActionListener(e -> showAdminMenu());
            buttonPanel.add(backButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);
        }

        cardPanel.add(panel, "ALL_BOOKINGS");
        cardLayout.show(cardPanel, "ALL_BOOKINGS");
    }

    private void showAllUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JLabel titleLabel = new JLabel("All Users", JLabel.CENTER);
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        List<User> users = authService.getAllUsers();

        if (users.isEmpty()) {
            panel.add(new JLabel("No users found.", JLabel.CENTER), BorderLayout.CENTER);
        } else {
            String[] columnNames = {"Username", "Full Name", "Email", "Role"};
            Object[][] data = new Object[users.size()][4];

            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                data[i][0] = user.getUsername();
                data[i][1] = user.getFullName();
                data[i][2] = user.getEmail();
                data[i][3] = user.getRole();
            }

            JTable usersTable = new JTable(data, columnNames);
            usersTable.setRowHeight(30);
            usersTable.setShowGrid(true);
            usersTable.setGridColor(new Color(230, 230, 230));
            usersTable.setFont(TEXT_FONT);
            usersTable.getTableHeader().setFont(LABEL_FONT);

            JScrollPane scrollPane = new JScrollPane(usersTable);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            panel.add(scrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

            JButton backButton = createStyledButton("Back");
            backButton.setBackground(SECONDARY_COLOR);
            backButton.setForeground(TEXT_COLOR);
            backButton.addActionListener(e -> showAdminMenu());
            buttonPanel.add(backButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);
        }

        cardPanel.add(panel, "ALL_USERS");
        cardLayout.show(cardPanel, "ALL_USERS");
    }

    private void showAdminMenu() {
        cardLayout.show(cardPanel, ADMIN_MENU);
    }

    private Booking getCurrentBooking() {
        if (currentUser == null) {
            return null;
        }
        List<Booking> bookings = bookingService.getBookingsByUsername(currentUser.getUsername());
        return bookings.isEmpty() ? null : bookings.get(0);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFont(BUTTON_FONT);
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FlightBookingGUI());
    }
}