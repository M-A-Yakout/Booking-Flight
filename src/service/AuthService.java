package service;

import model.User;
import model.Admin;
import model.Customer;

import java.util.List;

public class AuthService {
    private List<User> users;
    private User currentUser;

    public AuthService() {
        this.users = FileService.loadUsers();
        this.currentUser = null;
        
        // Create default admin if not exists
        if (getUserByUsername("admin") == null) {
            registerAdmin("admin", "admin123", "Administrator", "admin@flight.com");
        }
    }

    public User login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                currentUser = user;
                return user;
            }
        }
        return null;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public User getUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public User register(String username, String password, String fullName, String email) {
        // Check if username already exists
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return null;
            }
        }

        // Create new customer
        User newUser = new Customer(username, password, fullName, email);
        users.add(newUser);
        FileService.saveUsers(users);
        return newUser;
    }

    public boolean registerCustomer(String username, String password, String name, String email) {
        User user = register(username, password, name, email);
        return user != null;
    }

    public boolean registerAdmin(String username, String password, String name, String email) {
        // Check if username already exists
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return false;
            }
        }

        // Create new admin
        User newUser = new Admin(username, password, name, email);
        users.add(newUser);
        FileService.saveUsers(users);
        return true;
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(oldPassword)) {
                user.setPassword(newPassword);
                FileService.saveUsers(users);
                return true;
            }
        }
        return false;
    }

    public List<User> getAllUsers() {
        return users;
    }
    
    public UserDAO getUserDAO() {
        return FileService.getUserDAO();
    }
}
