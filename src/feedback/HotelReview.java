package feedback;

import util.DateUtil;
import util.IDGenerator;

public class HotelReview {
    private String reviewId;
    private String userId;
    private String userName;
    private String hotelId;
    private String hotelName;
    private String comments;
    private double rating;
    private String reviewDate;
    private boolean isVerified;
    private int helpfulCount;
    private String reviewTitle;
    private String roomType;
    private String stayDuration;
    private String checkInDate;
    private String checkOutDate;
    
    // Detailed ratings
    private double cleanlinessRating;
    private double serviceRating;
    private double locationRating;
    private double valueForMoneyRating;
    private double amenitiesRating;
    
    private String pros;
    private String cons;
    private boolean wouldRecommend;
    private String guestType; // Business, Leisure, Family, Couple
    private boolean isAnonymous;

    public HotelReview(String reviewId, String userId, String hotelId, String comments, double rating) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.hotelId = hotelId;
        this.comments = comments;
        this.rating = rating;
        this.reviewDate = DateUtil.getCurrentDate();
        this.isVerified = false;
        this.helpfulCount = 0;
        this.wouldRecommend = rating >= 4.0;
        this.isAnonymous = false;
        
        // Initialize detailed ratings with overall rating as default
        this.cleanlinessRating = rating;
        this.serviceRating = rating;
        this.locationRating = rating;
        this.valueForMoneyRating = rating;
        this.amenitiesRating = rating;
    }

    public HotelReview(String userId, String hotelId, String comments, double rating, String reviewTitle) {
        this(IDGenerator.generateRandomString(10), userId, hotelId, comments, rating);
        this.reviewTitle = reviewTitle;
    }

    public void setDetailedRatings(double cleanliness, double service, double location, 
                                 double valueForMoney, double amenities) {
        this.cleanlinessRating = cleanliness;
        this.serviceRating = service;
        this.locationRating = location;
        this.valueForMoneyRating = valueForMoney;
        this.amenitiesRating = amenities;
        
        // Recalculate overall rating based on detailed ratings
        this.rating = (cleanliness + service + location + valueForMoney + amenities) / 5.0;
        this.wouldRecommend = this.rating >= 4.0;
    }

    public void markAsHelpful() {
        this.helpfulCount++;
    }

    public void verifyReview() {
        this.isVerified = true;
    }

    public void unverifyReview() {
        this.isVerified = false;
    }

    public String getReviewSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Hotel Review by ").append(isAnonymous ? "Anonymous" : userName).append("\n");
        summary.append("Overall Rating: ").append(rating).append("/5.0 ").append(getRatingStars()).append("\n");
        
        if (reviewTitle != null) {
            summary.append("Title: ").append(reviewTitle).append("\n");
        }
        
        summary.append("Date: ").append(reviewDate).append("\n");
        summary.append("Verified Stay: ").append(isVerified ? "Yes" : "No").append("\n");
        summary.append("Helpful votes: ").append(helpfulCount).append("\n");
        summary.append("Would Recommend: ").append(wouldRecommend ? "Yes" : "No").append("\n");
        
        if (roomType != null) {
            summary.append("Room Type: ").append(roomType).append("\n");
        }
        
        if (stayDuration != null) {
            summary.append("Stay Duration: ").append(stayDuration).append("\n");
        }
        
        if (checkInDate != null && checkOutDate != null) {
            summary.append("Stay Period: ").append(checkInDate).append(" to ").append(checkOutDate).append("\n");
        }
        
        if (guestType != null) {
            summary.append("Guest Type: ").append(guestType).append("\n");
        }
        
        // Detailed ratings
        summary.append("\nDetailed Ratings:\n");
        summary.append("  Cleanliness: ").append(cleanlinessRating).append("/5.0\n");
        summary.append("  Service: ").append(serviceRating).append("/5.0\n");
        summary.append("  Location: ").append(locationRating).append("/5.0\n");
        summary.append("  Value for Money: ").append(valueForMoneyRating).append("/5.0\n");
        summary.append("  Amenities: ").append(amenitiesRating).append("/5.0\n");
        
        summary.append("\nComments: ").append(comments);
        
        if (pros != null && !pros.trim().isEmpty()) {
            summary.append("\n\nPros: ").append(pros);
        }
        
        if (cons != null && !cons.trim().isEmpty()) {
            summary.append("\nCons: ").append(cons);
        }
        
        return summary.toString();
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

    public boolean isPositiveReview() {
        return rating >= 4.0;
    }

    public boolean isNegativeReview() {
        return rating <= 2.0;
    }

    public boolean isNeutralReview() {
        return rating > 2.0 && rating < 4.0;
    }

    public String getReviewCategory() {
        if (isPositiveReview()) {
            return "Positive";
        } else if (isNegativeReview()) {
            return "Negative";
        } else {
            return "Neutral";
        }
    }

    public void setStayDetails(String checkInDate, String checkOutDate, String roomType) {
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.roomType = roomType;
        
        // Calculate stay duration if both dates are provided
        if (checkInDate != null && checkOutDate != null) {
            try {
                long days = util.DateUtil.daysBetween(checkInDate, checkOutDate);
                this.stayDuration = days + " night" + (days > 1 ? "s" : "");
            } catch (Exception e) {
                this.stayDuration = "Unknown";
            }
        }
    }

    public double getOverallSatisfactionScore() {
        return (cleanlinessRating + serviceRating + locationRating + valueForMoneyRating + amenitiesRating) / 5.0;
    }

    public String getTopRatedAspect() {
        double maxRating = Math.max(Math.max(cleanlinessRating, serviceRating), 
                                  Math.max(Math.max(locationRating, valueForMoneyRating), amenitiesRating));
        
        if (cleanlinessRating == maxRating) return "Cleanliness";
        if (serviceRating == maxRating) return "Service";
        if (locationRating == maxRating) return "Location";
        if (valueForMoneyRating == maxRating) return "Value for Money";
        if (amenitiesRating == maxRating) return "Amenities";
        
        return "Overall Experience";
    }

    public String getLowestRatedAspect() {
        double minRating = Math.min(Math.min(cleanlinessRating, serviceRating), 
                                  Math.min(Math.min(locationRating, valueForMoneyRating), amenitiesRating));
        
        if (cleanlinessRating == minRating) return "Cleanliness";
        if (serviceRating == minRating) return "Service";
        if (locationRating == minRating) return "Location";
        if (valueForMoneyRating == minRating) return "Value for Money";
        if (amenitiesRating == minRating) return "Amenities";
        
        return "Overall Experience";
    }

    public void updateReview(String newComments, double newRating) {
        this.comments = newComments;
        this.rating = newRating;
        this.wouldRecommend = newRating >= 4.0;
        this.isVerified = false; // Reset verification when updated
    }

    public void addProsCons(String pros, String cons) {
        this.pros = pros;
        this.cons = cons;
    }

    public int getCommentsLength() {
        return comments != null ? comments.length() : 0;
    }

    public boolean isDetailedReview() {
        return getCommentsLength() >= 100;
    }

    // Getters and Setters
    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getHotelId() { return hotelId; }
    public void setHotelId(String hotelId) { this.hotelId = hotelId; }
    
    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }
    
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    
    public double getRating() { return rating; }
    public void setRating(double rating) { 
        this.rating = rating;
        this.wouldRecommend = rating >= 4.0;
    }
    
    public String getReviewDate() { return reviewDate; }
    public void setReviewDate(String reviewDate) { this.reviewDate = reviewDate; }
    
    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }
    
    public int getHelpfulCount() { return helpfulCount; }
    public void setHelpfulCount(int helpfulCount) { this.helpfulCount = helpfulCount; }
    
    public String getReviewTitle() { return reviewTitle; }
    public void setReviewTitle(String reviewTitle) { this.reviewTitle = reviewTitle; }
    
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    
    public String getStayDuration() { return stayDuration; }
    public void setStayDuration(String stayDuration) { this.stayDuration = stayDuration; }
    
    public String getCheckInDate() { return checkInDate; }
    public void setCheckInDate(String checkInDate) { this.checkInDate = checkInDate; }
    
    public String getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }
    
    public double getCleanlinessRating() { return cleanlinessRating; }
    public void setCleanlinessRating(double cleanlinessRating) { this.cleanlinessRating = cleanlinessRating; }
    
    public double getServiceRating() { return serviceRating; }
    public void setServiceRating(double serviceRating) { this.serviceRating = serviceRating; }
    
    public double getLocationRating() { return locationRating; }
    public void setLocationRating(double locationRating) { this.locationRating = locationRating; }
    
    public double getValueForMoneyRating() { return valueForMoneyRating; }
    public void setValueForMoneyRating(double valueForMoneyRating) { this.valueForMoneyRating = valueForMoneyRating; }
    
    public double getAmenitiesRating() { return amenitiesRating; }
    public void setAmenitiesRating(double amenitiesRating) { this.amenitiesRating = amenitiesRating; }
    
    public String getPros() { return pros; }
    public void setPros(String pros) { this.pros = pros; }
    
    public String getCons() { return cons; }
    public void setCons(String cons) { this.cons = cons; }
    
    public boolean isWouldRecommend() { return wouldRecommend; }
    public void setWouldRecommend(boolean wouldRecommend) { this.wouldRecommend = wouldRecommend; }
    
    public String getGuestType() { return guestType; }
    public void setGuestType(String guestType) { this.guestType = guestType; }
    
    public boolean isAnonymous() { return isAnonymous; }
    public void setAnonymous(boolean anonymous) { isAnonymous = anonymous; }

    @Override
    public String toString() {
        return "HotelReview{" +
                "reviewId='" + reviewId + '\'' +
                ", hotelId='" + hotelId + '\'' +
                ", rating=" + rating +
                ", reviewDate='" + reviewDate + '\'' +
                ", verified=" + isVerified +
                ", helpful=" + helpfulCount +
                ", recommend=" + wouldRecommend +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        HotelReview that = (HotelReview) obj;
        return reviewId.equals(that.reviewId);
    }

    @Override
    public int hashCode() {
        return reviewId.hashCode();
    }
}
