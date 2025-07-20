package model;

import java.util.List;
import java.util.ArrayList;

public class Itinerary {
    private String itineraryId;
    private String packageId;
    private int day;
    private List<String> activities;
    private List<String> locationsVisited;
    private String accommodationDetails;
    private String mealPlan;
    private String transportDetails;
    private String startTime;
    private String endTime;
    private String specialInstructions;

    public Itinerary(String itineraryId, String packageId, int day) {
        this.itineraryId = itineraryId;
        this.packageId = packageId;
        this.day = day;
        this.activities = new ArrayList<>();
        this.locationsVisited = new ArrayList<>();
    }

    public void addActivity(String activity) {
        activities.add(activity);
    }

    public void addLocation(String location) {
        locationsVisited.add(location);
    }

    // Getters and Setters
    public String getItineraryId() { return itineraryId; }
    public void setItineraryId(String itineraryId) { this.itineraryId = itineraryId; }
    public String getPackageId() { return packageId; }
    public void setPackageId(String packageId) { this.packageId = packageId; }
    public int getDay() { return day; }
    public void setDay(int day) { this.day = day; }
    public List<String> getActivities() { return activities; }
    public void setActivities(List<String> activities) { this.activities = activities; }
    public List<String> getLocationsVisited() { return locationsVisited; }
    public void setLocationsVisited(List<String> locationsVisited) { this.locationsVisited = locationsVisited; }
    public String getAccommodationDetails() { return accommodationDetails; }
    public void setAccommodationDetails(String accommodationDetails) { this.accommodationDetails = accommodationDetails; }
    public String getMealPlan() { return mealPlan; }
    public void setMealPlan(String mealPlan) { this.mealPlan = mealPlan; }
    public String getTransportDetails() { return transportDetails; }
    public void setTransportDetails(String transportDetails) { this.transportDetails = transportDetails; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }
}
