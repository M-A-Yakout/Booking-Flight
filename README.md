# Flight Booking System

This is a simple command-line flight booking system built with Java and MySQL. It allows users to register, log in, search for flights, book flights, and manage their bookings. It also provides an admin panel for managing flights, users, and bookings.

## Features

### Customer Features

- **User Authentication**: Secure registration and login for customers.
- **Search Flights**: Search for available flights based on origin and destination.
- **Book Flights**: Book a seat on a flight.
- **View Bookings**: View a list of all personal flight bookings.
- **Cancel Bookings**: Cancel an existing booking.
- **Password Management**: Ability to change the account password.

### Admin Features

- **Flight Management**: Add new flights to the system.
- **View All Flights**: See a complete list of all flights.
- **View All Bookings**: See a complete list of all bookings made by all users.
- **View All Users**: See a list of all registered users.
- **Password Management**: Ability to change the admin account password.

## Technologies Used

- **Backend**: Java
- **Database**: MySQL
- **JDBC Driver**: [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/)

## Project Structure

The project is organized into the following packages:

- `src/model`: Contains the data models for the application (`User`, `Flight`, `Booking`, etc.).
- `src/service`: Contains the business logic for the application, including services for authentication, booking, and flight management, as well as DAOs for database interaction.
- `src/ui`: Contains the user interface logic for the command-line application.
- `src/sql`: Contains the SQL script for setting up the database schema.
- `lib`: Contains the MySQL JDBC driver.

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- MySQL Server
- An IDE like IntelliJ IDEA, Eclipse, or VS Code

### Database Setup

1.  Make sure your MySQL server is running.
2.  Create a new database named `basem_flight`.
3.  Execute the SQL script located at `src/sql/database_setup.sql` to create the necessary tables and insert some sample data. This will create the `users`, `flights`, and `bookings` tables, and an initial admin user with username `admin` and password `admin123`.

### Configuration

If your MySQL database credentials are not the default (`root` user with no password), you will need to update the connection details in the `src/service/DatabaseConnection.java` file:

```java
private static final String DB_URL = "your_DB_URL";
private static final String USER = "your_username";
private static final String PASS = "your_password";
```

### Running the Application

1.  Open the project in your favorite IDE.
2.  Make sure the `mysql-connector-java-8.0.28.jar` (or your version) is included in the project's build path.
3.  Compile the project.
4.  Run the `main` method in the `src/Main.java` file to start the application.

## How to Use

- Once the application is running, you can interact with it through the console.
- You can log in as an admin using the credentials `admin`/`admin123`.
- You can register a new customer account and then log in to use the customer features.