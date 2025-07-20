package model;

import java.util.List;
import java.util.ArrayList;

public class TourGuide {
    private String guideId;
    private String name;
    private String phone;
    private String email;
    private List<String> languages;
    private double rating;
    private int totalReviews;
    private String specialization;
    private double dailyRate;
    private boolean isAvailable;
    private String experience;
    private String certifications;
    private List<String> assignedTours;

    public TourGuide(String guideId, String name, String phone, String email) {
        this.guideId = guideId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.languages = new ArrayList<>();
        this.rating = 0.0;
        this.totalReviews = 0;
        this.isAvailable = true;
        this.assignedTours = new ArrayList<>();
    }

    public void addLanguage(String language) {
        if (!languages.contains(language)) {
            languages.add(language);
        }
    }

    public void updateRating(double newRating) {
        double totalRating = rating * totalReviews;
        totalReviews++;
        rating = (totalRating + newRating) / totalReviews;
    }

    public void assignTour(String tourId) {
        assignedTours.add(tourId);
        isAvailable = false;
    }

    public void completeTour(String tourId) {
        assignedTours.remove(tourId);
        if (assignedTours.isEmpty()) {
            isAvailable = true;
        }
    }

    // Getters and Setters
    public String getGuideId() { return guideId; }
    public void setGuideId(String guideId) { this.guideId = guideId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<String> getLanguages() { return languages; }
    public void setLanguages(List<String> languages) { this.languages = languages; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public int getTotalReviews() { return totalReviews; }
    public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public double getDailyRate() { return dailyRate; }
    public void setDailyRate(double dailyRate) { this.dailyRate = dailyRate; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    public String getCertifications() { return certifications; }
    public void setCertifications(String certifications) { this.certifications = certifications; }
    public List<String> getAssignedTours() { return assignedTours; }

    @Override
    public String toString() {
        return "TourGuide{" +
                "name='" + name + '\'' +
                ", languages=" + languages +
                ", rating=" + rating +
                ", available=" + isAvailable +
                '}';
    }
}
