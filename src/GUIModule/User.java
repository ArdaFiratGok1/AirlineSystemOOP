package GUIModule;

import java.io.Serializable;

public class User implements Serializable {
    private int userID;
    private String username;
    private String password;
    private String role; // "ADMIN" veya "PASSENGER"
    
    public User(int userID, String username, String password, String role) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.role = role;
    }
    
    // Şifre kontrolü için yardımcı metod
    public boolean checkPassword(String inputPass) {
        return this.password.equals(inputPass);
    }
    
    // --- Getters & Setters ---
    
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    @Override
    public String toString() {
        return role + ": " + username; 
    }
}