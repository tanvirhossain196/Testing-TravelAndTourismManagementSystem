package feedback;

import util.DateUtil;
import util.IDGenerator;

public class PackageReview {
    private String reviewId;
    private String userId;
    private String userName;
    private String packageId;
    private String packageName;
    private String reviewText;
    private double rating;
    private String reviewDate;
    private boolean isVerified;
    private int helpfulCount;
    private String reviewTitle;
    private boolean isRecommended;
    private String travelDate;
    private String travelType; // Solo, Family, Couple, Business
    private String pros;
    private String cons;
    private boolean isAnonymous;

    public PackageReview(String reviewId, String userId, String packageId, String reviewText, double rating) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.packageId = packageId;
        this.reviewText = reviewText;
        this.rating = rating;
        this.reviewDate = DateUtil.getCurrentDate();
        this.isVerified = false;
        this.helpfulCount = 0;
        this.isRecommended = rating >= 4.0; // Auto-recommend for ratings 4+
        this.isAnonymous = false;
    }

    public PackageReview(String userId, String packageId, String reviewText, double rating, String reviewTitle) {
        this(IDGenerator.generateRandomString(10), userId, packageId, reviewText, rating);
        this.reviewTitle = reviewTitle;
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
        summary.append("Review by ").append(isAnonymous ? "Anonymous" : userName).append("\n");
        summary.append("Rating: ").append(rating).append("/5.0\n");
        if (reviewTitle != null) {
            summary.append("Title: ").append(reviewTitle).append("\n");
        }
        summary.append("Date: ").append(reviewDate).append("\n");
        summary.append("Verified: ").append(isVerified ? "Yes" : "No").append("\n");
        summary.append("Helpful votes: ").append(helpfulCount).append("\n");
        summary.append("Recommended: ").append(isRecommended ? "Yes" : "No").append("\n");
        
        if (travelDate != null) {
            summary.append("Travel Date: ").append(travelDate).append("\n");
        }
        
        if (travelType != null) {
            summary.append("Travel Type: ").append(travelType).append("\n");
        }
        
        summary.append("\nReview: ").append(reviewText);
        
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

    public int getReviewLength() {
        return reviewText != null ? reviewText.length() : 0;
    }

    public boolean isDetailedReview() {
        return getReviewLength() >= 100; // Consider reviews with 100+ characters as detailed
    }

    public void updateReview(String newReviewText, double newRating) {
        this.reviewText = newReviewText;
        this.rating = newRating;
        this.isRecommended = newRating >= 4.0;
        // Reset verification status when review is updated
        this.isVerified = false;
    }

    public void addProsCons(String pros, String cons) {
        this.pros = pros;
        this.cons = cons;
    }

    // Getters and Setters
    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getPackageId() { return packageId; }
    public void setPackageId(String packageId) { this.packageId = packageId; }
    
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    
    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }
    
    public double getRating() { return rating; }
    public void setRating(double rating) { 
        this.rating = rating;
        this.isRecommended = rating >= 4.0;
    }
    
    public String getReviewDate() { return reviewDate; }
    public void setReviewDate(String reviewDate) { this.reviewDate = reviewDate; }
    
    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }
    
    public int getHelpfulCount() { return helpfulCount; }
    public void setHelpfulCount(int helpfulCount) { this.helpfulCount = helpfulCount; }
    
    public String getReviewTitle() { return reviewTitle; }
    public void setReviewTitle(String reviewTitle) { this.reviewTitle = reviewTitle; }
    
    public boolean isRecommended() { return isRecommended; }
    public void setRecommended(boolean recommended) { isRecommended = recommended; }
    
    public String getTravelDate() { return travelDate; }
    public void setTravelDate(String travelDate) { this.travelDate = travelDate; }
    
    public String getTravelType() { return travelType; }
    public void setTravelType(String travelType) { this.travelType = travelType; }
    
    public String getPros() { return pros; }
    public void setPros(String pros) { this.pros = pros; }
    
    public String getCons() { return cons; }
    public void setCons(String cons) { this.cons = cons; }
    
    public boolean isAnonymous() { return isAnonymous; }
    public void setAnonymous(boolean anonymous) { isAnonymous = anonymous; }

    @Override
    public String toString() {
        return "PackageReview{" +
                "reviewId='" + reviewId + '\'' +
                ", packageId='" + packageId + '\'' +
                ", rating=" + rating +
                ", reviewDate='" + reviewDate + '\'' +
                ", verified=" + isVerified +
                ", helpful=" + helpfulCount +
                ", recommended=" + isRecommended +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PackageReview that = (PackageReview) obj;
        return reviewId.equals(that.reviewId);
    }

    @Override
    public int hashCode() {
        return reviewId.hashCode();
    }
}
