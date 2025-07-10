-- Database schema for flight booking system

-- Create database
CREATE DATABASE IF NOT EXISTS basem_flight;
USE basem_flight;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL DEFAULT 'customer'
);

-- Flights table
CREATE TABLE IF NOT EXISTS flights (
    flight_number VARCHAR(20) PRIMARY KEY,
    airline VARCHAR(50) NOT NULL,
    origin VARCHAR(50) NOT NULL,
    destination VARCHAR(50) NOT NULL,
    departure_time DATETIME NOT NULL,
    arrival_time DATETIME NOT NULL,
    total_seats INT NOT NULL,
    available_seats INT NOT NULL,
    price DECIMAL(10,2) NOT NULL
);

-- Bookings table
CREATE TABLE IF NOT EXISTS bookings (
    booking_id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    flight_number VARCHAR(20) NOT NULL,
    booking_date DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(username),
    FOREIGN KEY (flight_number) REFERENCES flights(flight_number)
);

-- Sample data insertion
INSERT IGNORE INTO users VALUES ('admin', 'admin123', 'admin@example.com', 'Admin User', '1234567890', 'admin');
INSERT IGNORE INTO flights VALUES
    ('FL100', 'EgyptAir', 'Alex.', 'Cairo', '2023-12-15 08:00:00', '2023-12-15 16:00:00', 200, 150, 500.00),
    ('FL200', 'EgyptAir', 'Cairo', 'Alex.', '2023-12-16 10:00:00', '2023-12-16 12:00:00', 150, 100, 200.00);