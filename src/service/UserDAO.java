package service;

import model.User;
import java.sql.*;
import java.util.List;
import model.RegularUser;
import java.util.ArrayList;

public class UserDAO {
    private static final String CHECK_USER_EXISTS = "SELECT username FROM users WHERE username=?";

    public UserDAO() {}

    private static final String INSERT_USER = "INSERT INTO users (username, password, email, full_name, role) VALUES (?, ?, ?, ?, ?)";

    public void createUser(User user) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            PreparedStatement stmt = conn.prepareStatement(INSERT_USER);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getRole());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            System.err.println("Error creating user: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public boolean userExists(String username) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_USER_EXISTS)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public User getUserByUsername(String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new RegularUser();
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setName(rs.getString("full_name"));
                try {
    user.setRole(rs.getString("role"));
} catch (SQLException e) {
    user.setRole("regular"); // Default role if column doesn't exist
}
                return user;
            }
            return null;
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                User user = new RegularUser();
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setName(rs.getString("full_name"));
                try {
    user.setRole(rs.getString("role"));
} catch (SQLException e) {
    user.setRole("regular"); // Default role if column doesn't exist
}
                users.add(user);
            }
        }
        return users;
    }
    
    public boolean isUserSaved(String username) throws SQLException {
        String query = "SELECT username FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }
    
    public void updateUser(User user) throws SQLException {
        String query = "UPDATE users SET password=?, email=?, full_name=?, role=? WHERE username=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, user.getPassword());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getUsername());
            
            stmt.executeUpdate();
        }
    }
}