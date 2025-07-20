package feedback;

import util.DateUtil;
import util.IDGenerator;

public class Rating {
    private String ratingId;
    private String userId;
    private String userName;
    private String entityId; // Package ID, Hotel ID, Guide ID, etc.
    private String entityType; // PACKAGE, HOTEL, GUIDE, TRANSPORT
    private double ratingValue;
    private String comments;
    private String ratingDate;
    private boolean isVerified;
    private String criteria; // Overall, Service, Quality, Value, etc.
    private int helpfulCount;
    private boolean isAnonymous;
    private String aspectRated; // Specific aspect being rated

    public Rating(String ratingId, String userId, String entityId, double ratingValue, String entityType) {
        this.ratingId = ratingId;
        this.userId = userId;
        this.entityId = entityId;
        this.ratingValue = ratingValue;
        this.entityType = entityType;
        this.ratingDate = DateUtil.getCurrentDate();
        this.isVerified = false;
        this.criteria = "Overall";
        this.helpfulCount = 0;
        this.isAnonymous = false;
    }

    public Rating(String userId, String entityId, double ratingValue, String entityType, String comments) {
        this(IDGenerator.generateRandomString(10), userId, entityId, ratingValue, entityType);
        this.comments = comments;
    }

    public Rating(String userId, String entityId, double ratingValue, String entityType, 
                 String comments, String criteria) {
        this(userId, entityId, ratingValue, entityType, comments);
        this.criteria = criteria;
    }

    public void markAsHelpful() {
        this.helpfulCount++;
    }

    public void verifyRating() {
        this.isVerified = true;
    }

    public void unverifyRating() {
        this.isVerified = false;
    }

    public boolean isValidRating() {
        return ratingValue >= 1.0 && ratingValue <= 5.0;
    }

    public String getRatingStars() {
        StringBuilder stars = new StringBuilder();
        int fullStars = (int) ratingValue;
        boolean hasHalfStar = (ratingValue - fullStars) >= 0.5;
        
        // Add full stars
        for (int i = 0; i < fullStars; i++) {
            stars.append("★");
        }
        
        // Add half star if needed
        if (hasHalfStar) {
            stars.append("☆");
        }
        
        // Add empty stars
        int remainingStars = 5 - fullStars - (hasHalfStar ? 1 : 0);
        for (int i = 0; i < remainingStars; i++) {
            stars.append("☆");
        }
        
        return stars.toString();
    }

    public String getRatingCategory() {
        if (ratingValue >= 4.5) {
            return "Excellent";
        } else if (ratingValue >= 4.0) {
            return "Very Good";
        } else if (ratingValue >= 3.0) {
            return "Good";
        } else if (ratingValue >= 2.0) {
            return "Fair";
        } else {
            return "Poor";
        }
    }

    public boolean isPositiveRating() {
        return ratingValue >= 4.0;
    }

    public boolean isNegativeRating() {
        return ratingValue <= 2.0;
    }

    public boolean isNeutralRating() {
        return ratingValue > 2.0 && ratingValue < 4.0;
    }

    public String getRatingDescription() {
        switch ((int) Math.round(ratingValue)) {
            case 1:
                return "Very Poor - Extremely unsatisfied";
            case 2:
                return "Poor - Unsatisfied";
            case 3:
                return "Average - Neutral experience";
            case 4:
                return "Good - Satisfied";
            case 5:
                return "Excellent - Extremely satisfied";
            default:
                return "Invalid rating";
        }
    }

    public String getRatingSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Rating by ").append(isAnonymous ? "Anonymous" : userName).append("\n");
        summary.append("Entity: ").append(entityType).append(" (").append(entityId).append(")\n");
        summary.append("Rating: ").append(ratingValue).append("/5.0 ").append(getRatingStars()).append("\n");
        summary.append("Category: ").append(getRatingCategory()).append("\n");
        summary.append("Criteria: ").append(criteria).append("\n");
        summary.append("Date: ").append(ratingDate).append("\n");
        summary.append("Verified: ").append(isVerified ? "Yes" : "No").append("\n");
        summary.append("Helpful votes: ").append(helpfulCount).append("\n");
        
        if (aspectRated != null) {
            summary.append("Aspect Rated: ").append(aspectRated).append("\n");
        }
        
        if (comments != null && !comments.trim().isEmpty()) {
            summary.append("Comments: ").append(comments);
        }
        
        return summary.toString();
    }

    public void updateRating(double newRatingValue, String newComments) {
        if (newRatingValue >= 1.0 && newRatingValue <= 5.0) {
            this.ratingValue = newRatingValue;
            this.comments = newComments;
            this.isVerified = false; // Reset verification when updated
        }
    }

    public int getRatingLevel() {
        return (int) Math.round(ratingValue);
    }

    public double getRatingPercentage() {
        return (ratingValue / 5.0) * 100;
    }

    public boolean isHighRating() {
        return ratingValue >= 4.0;
    }

    public boolean isLowRating() {
        return ratingValue <= 2.0;
    }

    public boolean hasComments() {
        return comments != null && !comments.trim().isEmpty();
    }

    public int getCommentsLength() {
        return comments != null ? comments.length() : 0;
    }

    public boolean isDetailedRating() {
        return hasComments() && getCommentsLength() >= 50;
    }

    // Static utility methods
    public static double calculateAverageRating(java.util.List<Rating> ratings) {
        if (ratings == null || ratings.isEmpty()) {
            return 0.0;
        }
        
        return ratings.stream()
                .mapToDouble(Rating::getRatingValue)
                .average()
                .orElse(0.0);
    }

    public static java.util.Map<Integer, Integer> getRatingDistribution(java.util.List<Rating> ratings) {
        java.util.Map<Integer, Integer> distribution = new java.util.HashMap<>();
        
        // Initialize distribution
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, 0);
        }
        
        // Count ratings
        for (Rating rating : ratings) {
            int level = rating.getRatingLevel();
            if (level >= 1 && level <= 5) {
                distribution.put(level, distribution.get(level) + 1);
            }
        }
        
        return distribution;
    }

    public static String formatRatingDistribution(java.util.List<Rating> ratings) {
        java.util.Map<Integer, Integer> distribution = getRatingDistribution(ratings);
        int totalRatings = ratings.size();
        
        StringBuilder result = new StringBuilder();
        result.append("Rating Distribution:\n");
        
        for (int i = 5; i >= 1; i--) {
            int count = distribution.get(i);
            double percentage = totalRatings > 0 ? (count * 100.0) / totalRatings : 0.0;
            
            result.append(String.format("%d ★: %d ratings (%.1f%%)\n", i, count, percentage));
        }
        
        return result.toString();
    }

    // Getters and Setters
    public String getRatingId() { return ratingId; }
    public void setRatingId(String ratingId) { this.ratingId = ratingId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    
    public double getRatingValue() { return ratingValue; }
    public void setRatingValue(double ratingValue) { 
        if (ratingValue >= 1.0 && ratingValue <= 5.0) {
            this.ratingValue = ratingValue; 
        }
    }
    
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    
    public String getRatingDate() { return ratingDate; }
    public void setRatingDate(String ratingDate) { this.ratingDate = ratingDate; }
    
    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }
    
    public String getCriteria() { return criteria; }
    public void setCriteria(String criteria) { this.criteria = criteria; }
    
    public int getHelpfulCount() { return helpfulCount; }
    public void setHelpfulCount(int helpfulCount) { this.helpfulCount = helpfulCount; }
    
    public boolean isAnonymous() { return isAnonymous; }
    public void setAnonymous(boolean anonymous) { isAnonymous = anonymous; }
    
    public String getAspectRated() { return aspectRated; }
    public void setAspectRated(String aspectRated) { this.aspectRated = aspectRated; }

    @Override
    public String toString() {
        return "Rating{" +
                "ratingId='" + ratingId + '\'' +
                ", entityId='" + entityId + '\'' +
                ", entityType='" + entityType + '\'' +
                ", ratingValue=" + ratingValue +
                ", criteria='" + criteria + '\'' +
                ", ratingDate='" + ratingDate + '\'' +
                ", verified=" + isVerified +
                ", helpful=" + helpfulCount +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Rating rating = (Rating) obj;
        return ratingId.equals(rating.ratingId);
    }

    @Override
    public int hashCode() {
        return ratingId.hashCode();
    }
}
