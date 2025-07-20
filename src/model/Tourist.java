package model;

import java.util.List;
import java.util.ArrayList;

public class Tourist extends user {
    private List<String> preferences;
    private String nationality;
    private int loyaltyPoints;
    private List<String> bookingHistory;

    public Tourist(String id, String name, String email, String password, String phone) {
        super(id, name, email, password, phone);
        this.preferences = new ArrayList<>();
        this.loyaltyPoints = 0;
        this.bookingHistory = new ArrayList<>();
    }

    @Override
    public String getRole() {
        return "TOURIST";
    }

    public void addLoyaltyPoints(int points) {
        this.loyaltyPoints += points;
    }

    // Getters and Setters
    public List<String> getPreferences() { return preferences; }
    public void setPreferences(List<String> preferences) { this.preferences = preferences; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public int getLoyaltyPoints() { return loyaltyPoints; }
    public List<String> getBookingHistory() { return bookingHistory; }
}
