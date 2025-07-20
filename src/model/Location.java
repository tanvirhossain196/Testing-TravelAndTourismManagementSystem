package model;

import java.util.List;
import java.util.ArrayList;

public class Location {
    private String locationId;
    private String city;
    private String country;
    private String state;
    private List<String> landmarks;
    private double latitude;
    private double longitude;
    private String description;
    private String climate;
    private String bestTimeToVisit;

    public Location(String locationId, String city, String country) {
        this.locationId = locationId;
        this.city = city;
        this.country = country;
        this.landmarks = new ArrayList<>();
    }

    public void addLandmark(String landmark) {
        landmarks.add(landmark);
    }

    public void removeLandmark(String landmark) {
        landmarks.remove(landmark);
    }

    // Getters and Setters
    public String getLocationId() { return locationId; }
    public void setLocationId(String locationId) { this.locationId = locationId; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public List<String> getLandmarks() { return landmarks; }
    public void setLandmarks(List<String> landmarks) { this.landmarks = landmarks; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getClimate() { return climate; }
    public void setClimate(String climate) { this.climate = climate; }
    public String getBestTimeToVisit() { return bestTimeToVisit; }
    public void setBestTimeToVisit(String bestTimeToVisit) { this.bestTimeToVisit = bestTimeToVisit; }

    @Override
    public String toString() {
        return "Location{" +
                "city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", landmarks=" + landmarks.size() +
                '}';
    }
}
