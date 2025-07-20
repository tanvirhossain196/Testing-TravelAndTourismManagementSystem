package model;

import enumtype.PackageCategory;
import enumtype.TourType;
import util.DateUtil;
import java.util.List;
import java.util.ArrayList;

public class TourPackage {
    // Core package fields
    private String packageId;
    private String name;
    private String location;
    private double basePrice;
    private int duration;
    private String description;
    private PackageCategory category;
    private TourType tourType;
    private boolean isActive;
    private String createdBy;
    private String createdDate;
    private String lastModified;
    
    // List fields
    private List<String> itinerary;
    private List<String> inclusions;
    private List<String> exclusions;
    private List<String> highlights;
    
    // Booking fields
    private int currentBookings;
    private int maxCapacity;
    
    // Rating fields
    private double rating;
    private int totalReviews;
    
    // Feature fields
    private String imageUrl;
    private boolean isFeatured;
    private double discountPercentage;
    
    // Validity fields
    private String validFrom;
    private String validTo;

    // Constructor
    public TourPackage(String packageId, String name, String location, double basePrice, int duration, String description) {
        this.packageId = packageId;
        this.name = name;
        this.location = location;
        this.basePrice = basePrice;
        this.duration = duration;
        this.description = description;
        this.isActive = true;
        this.createdDate = DateUtil.getCurrentDate();
        this.lastModified = DateUtil.getCurrentDate();
        this.itinerary = new ArrayList<>();
        this.inclusions = new ArrayList<>();
        this.exclusions = new ArrayList<>();
        this.highlights = new ArrayList<>();
        this.currentBookings = 0;
        this.maxCapacity = 50; // Default capacity
        this.rating = 0.0;
        this.totalReviews = 0;
        this.isFeatured = false;
        this.discountPercentage = 0.0;
        this.validFrom = null;
        this.validTo = null;
    }

    // Booking management methods
    public void cancelBooking() {
        if (currentBookings > 0) {
            currentBookings--;
            updateLastModified();
        }
    }

    public void addBooking() {
        if (currentBookings < maxCapacity) {
            currentBookings++;
            updateLastModified();
        }
    }

    public int getCurrentBookings() {
        return currentBookings;
    }

    public void setCurrentBookings(int currentBookings) {
        this.currentBookings = currentBookings;
        updateLastModified();
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        updateLastModified();
    }

    public boolean isAvailable() {
        return isActive && currentBookings < maxCapacity;
    }

    public int getAvailableSlots() {
        return maxCapacity - currentBookings;
    }

    public double getOccupancyRate() {
        return maxCapacity > 0 ? (double) currentBookings / maxCapacity * 100 : 0.0;
    }

    public boolean isFullyBooked() {
        return currentBookings >= maxCapacity;
    }

    // Itinerary management
    public void addItineraryItem(String item) {
        if (item != null && !item.trim().isEmpty()) {
            itinerary.add(item);
            updateLastModified();
        }
    }

    public void removeItineraryItem(String item) {
        if (itinerary.remove(item)) {
            updateLastModified();
        }
    }

    public void clearItinerary() {
        itinerary.clear();
        updateLastModified();
    }

    // Inclusions management
    public void addInclusion(String inclusion) {
        if (inclusion != null && !inclusion.trim().isEmpty()) {
            inclusions.add(inclusion);
            updateLastModified();
        }
    }

    public void removeInclusion(String inclusion) {
        if (inclusions.remove(inclusion)) {
            updateLastModified();
        }
    }

    // Exclusions management
    public void addExclusion(String exclusion) {
        if (exclusion != null && !exclusion.trim().isEmpty()) {
            exclusions.add(exclusion);
            updateLastModified();
        }
    }

    public void removeExclusion(String exclusion) {
        if (exclusions.remove(exclusion)) {
            updateLastModified();
        }
    }

    // Highlights management
    public void addHighlight(String highlight) {
        if (highlight != null && !highlight.trim().isEmpty()) {
            highlights.add(highlight);
            updateLastModified();
        }
    }

    public void removeHighlight(String highlight) {
        if (highlights.remove(highlight)) {
            updateLastModified();
        }
    }

    // Rating management
    public void updateRating(double newRating) {
        if (newRating >= 0.0 && newRating <= 5.0) {
            // Calculate new average rating
            double totalRatingPoints = this.rating * this.totalReviews;
            this.totalReviews++;
            this.rating = (totalRatingPoints + newRating) / this.totalReviews;
            updateLastModified();
        }
    }

    public String getRatingStars() {
        StringBuilder stars = new StringBuilder();
        int fullStars = (int) rating;
        boolean hasHalfStar = (rating - fullStars) >= 0.5;
        
        for (int i = 0; i < fullStars; i++) {
            stars.append("★");
        }
        
        if (hasHalfStar) {
            stars.append("☆");
        }
        
        int remainingStars = 5 - fullStars - (hasHalfStar ? 1 : 0);
        for (int i = 0; i < remainingStars; i++) {
            stars.append("☆");
        }
        
        return stars.toString();
    }

    // Price calculations
    public double getDiscountedPrice() {
        return basePrice * (1 - discountPercentage / 100);
    }

    public double getDiscountAmount() {
        return basePrice * (discountPercentage / 100);
    }

    public double calculateTotalPrice(int numberOfPeople) {
        return getDiscountedPrice() * numberOfPeople;
    }

    // Validity checks
    public boolean isValidForDate(String date) {
        if (validFrom == null || validTo == null) {
            return true; // No date restrictions
        }
        
        return date.compareTo(validFrom) >= 0 && date.compareTo(validTo) <= 0;
    }

    public boolean isCurrentlyValid() {
        String currentDate = DateUtil.getCurrentDate();
        return isValidForDate(currentDate);
    }

    // Package status management
    public void activate() {
        this.isActive = true;
        updateLastModified();
    }

    public void deactivate() {
        this.isActive = false;
        updateLastModified();
    }

    public void setFeatured(boolean featured) {
        this.isFeatured = featured;
        updateLastModified();
    }

    // Utility methods
    private void updateLastModified() {
        this.lastModified = DateUtil.getCurrentDate();
    }

    public String getPackageStatus() {
        if (!isActive) {
            return "Inactive";
        } else if (isFullyBooked()) {
            return "Fully Booked";
        } else if (isAvailable()) {
            return "Available";
        } else {
            return "Limited Availability";
        }
    }

    public String getPopularityLevel() {
        double occupancyRate = getOccupancyRate();
        if (occupancyRate >= 80) {
            return "Very Popular";
        } else if (occupancyRate >= 60) {
            return "Popular";
        } else if (occupancyRate >= 40) {
            return "Moderately Popular";
        } else {
            return "New/Limited Interest";
        }
    }

    public String generatePackageSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Package: ").append(name).append("\n");
        summary.append("Location: ").append(location).append("\n");
        summary.append("Duration: ").append(duration).append(" days\n");
        summary.append("Price: $").append(getDiscountedPrice()).append("\n");
        summary.append("Rating: ").append(rating).append("/5.0 ").append(getRatingStars()).append(" (").append(totalReviews).append(" reviews)\n");
        summary.append("Availability: ").append(getAvailableSlots()).append("/").append(maxCapacity).append(" slots\n");
        summary.append("Status: ").append(getPackageStatus()).append("\n");
        
        if (discountPercentage > 0) {
            summary.append("Discount: ").append(discountPercentage).append("% OFF\n");
        }
        
        if (isFeatured) {
            summary.append("⭐ Featured Package\n");
        }
        
        return summary.toString();
    }

    // Getters and Setters
    public String getPackageId() { 
        return packageId; 
    }
    
    public void setPackageId(String packageId) { 
        this.packageId = packageId; 
    }
    
    public String getName() { 
        return name; 
    }
    
    public void setName(String name) { 
        this.name = name;
        updateLastModified();
    }
    
    public String getLocation() { 
        return location; 
    }
    
    public void setLocation(String location) { 
        this.location = location;
        updateLastModified();
    }
    
    public double getBasePrice() { 
        return basePrice; 
    }
    
    public void setBasePrice(double basePrice) { 
        this.basePrice = basePrice;
        updateLastModified();
    }
    
    public int getDuration() { 
        return duration; 
    }
    
    public void setDuration(int duration) { 
        this.duration = duration;
        updateLastModified();
    }
    
    public String getDescription() { 
        return description; 
    }
    
    public void setDescription(String description) { 
        this.description = description;
        updateLastModified();
    }
    
    public PackageCategory getCategory() { 
        return category; 
    }
    
    public void setCategory(PackageCategory category) { 
        this.category = category;
        updateLastModified();
    }
    
    public TourType getTourType() { 
        return tourType; 
    }
    
    public void setTourType(TourType tourType) { 
        this.tourType = tourType;
        updateLastModified();
    }
    
    public boolean isActive() { 
        return isActive; 
    }
    
    public void setActive(boolean active) { 
        isActive = active;
        updateLastModified();
    }
    
    public String getCreatedBy() { 
        return createdBy; 
    }
    
    public void setCreatedBy(String createdBy) { 
        this.createdBy = createdBy; 
    }
    
    public String getCreatedDate() { 
        return createdDate; 
    }
    
    public void setCreatedDate(String createdDate) { 
        this.createdDate = createdDate; 
    }
    
    public String getLastModified() { 
        return lastModified; 
    }
    
    public void setLastModified(String lastModified) { 
        this.lastModified = lastModified; 
    }
    
    public List<String> getItinerary() { 
        return new ArrayList<>(itinerary); 
    }
    
    public void setItinerary(List<String> itinerary) { 
        this.itinerary = new ArrayList<>(itinerary);
        updateLastModified();
    }
    
    public List<String> getInclusions() { 
        return new ArrayList<>(inclusions); 
    }
    
    public void setInclusions(List<String> inclusions) { 
        this.inclusions = new ArrayList<>(inclusions);
        updateLastModified();
    }
    
    public List<String> getExclusions() { 
        return new ArrayList<>(exclusions); 
    }
    
    public void setExclusions(List<String> exclusions) { 
        this.exclusions = new ArrayList<>(exclusions);
        updateLastModified();
    }
    
    public double getRating() { 
        return rating; 
    }
    
    public void setRating(double rating) { 
        this.rating = rating; 
    }
    
    public int getTotalReviews() { 
        return totalReviews; 
    }
    
    public void setTotalReviews(int totalReviews) { 
        this.totalReviews = totalReviews; 
    }
    
    public String getImageUrl() { 
        return imageUrl; 
    }
    
    public void setImageUrl(String imageUrl) { 
        this.imageUrl = imageUrl;
        updateLastModified();
    }
    
    public boolean isFeatured() { 
        return isFeatured; 
    }
    
    public double getDiscountPercentage() { 
        return discountPercentage; 
    }
    
    public void setDiscountPercentage(double discountPercentage) { 
        this.discountPercentage = discountPercentage;
        updateLastModified();
    }
    
    public String getValidFrom() { 
        return validFrom; 
    }
    
    public void setValidFrom(String validFrom) { 
        this.validFrom = validFrom;
        updateLastModified();
    }
    
    public String getValidTo() { 
        return validTo; 
    }
    
    public void setValidTo(String validTo) { 
        this.validTo = validTo;
        updateLastModified();
    }
    
    public List<String> getHighlights() { 
        return new ArrayList<>(highlights); 
    }
    
    public void setHighlights(List<String> highlights) { 
        this.highlights = new ArrayList<>(highlights);
        updateLastModified();
    }

    @Override
    public String toString() {
        return "TourPackage{" +
                "packageId='" + packageId + '\'' +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", basePrice=" + basePrice +
                ", duration=" + duration +
                ", category=" + category +
                ", tourType=" + tourType +
                ", isActive=" + isActive +
                ", currentBookings=" + currentBookings +
                ", maxCapacity=" + maxCapacity +
                ", rating=" + rating +
                ", isFeatured=" + isFeatured +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TourPackage that = (TourPackage) obj;
        return packageId.equals(that.packageId);
    }

    @Override
    public int hashCode() {
        return packageId.hashCode();
    }
}
