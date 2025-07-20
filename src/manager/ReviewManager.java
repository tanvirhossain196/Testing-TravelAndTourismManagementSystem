package manager;

import feedback.PackageReview;
import feedback.HotelReview;
import feedback.Rating;
import util.Logger;
import util.FileHandler;
import util.DateUtil;
import util.IDGenerator;
import java.util.*;
import java.util.stream.Collectors;

public class ReviewManager {
    private Map<String, PackageReview> packageReviews;
    private Map<String, HotelReview> hotelReviews;
    private Map<String, Rating> ratings;
    private static final String PACKAGE_REVIEWS_FILE = "package_reviews.dat";
    private static final String HOTEL_REVIEWS_FILE = "hotel_reviews.dat";
    private static final String RATINGS_FILE = "ratings.dat";

    public ReviewManager() {
        this.packageReviews = new HashMap<>();
        this.hotelReviews = new HashMap<>();
        this.ratings = new HashMap<>();
        loadReviewsFromFile();
    }

    // Package Review Management
    public PackageReview addPackageReview(String userId, String packageId, String reviewText, double rating) {
        try {
            String reviewId = IDGenerator.generateRandomString(10);
            PackageReview review = new PackageReview(reviewId, userId, packageId, reviewText, rating);
            
            packageReviews.put(reviewId, review);
            
            // Also add a rating entry
            addRating(userId, packageId, rating, "PACKAGE");
            
            savePackageReviewsToFile();
            Logger.log("Package review added: " + reviewId + " for package " + packageId);
            
            return review;
        } catch (Exception e) {
            Logger.error("Error adding package review: " + e.getMessage());
            return null;
        }
    }

    public void removePackageReview(String reviewId) {
        try {
            PackageReview removed = packageReviews.remove(reviewId);
            if (removed != null) {
                savePackageReviewsToFile();
                Logger.log("Package review removed: " + reviewId);
            }
        } catch (Exception e) {
            Logger.error("Error removing package review: " + e.getMessage());
        }
    }

    public PackageReview getPackageReviewById(String reviewId) {
        return packageReviews.get(reviewId);
    }

    public List<PackageReview> getAllPackageReviews() {
        return new ArrayList<>(packageReviews.values());
    }

    public List<PackageReview> getPackageReviewsByPackage(String packageId) {
        return packageReviews.values().stream()
                .filter(review -> review.getPackageId().equals(packageId))
                .collect(Collectors.toList());
    }

    public List<PackageReview> getPackageReviewsByUser(String userId) {
        return packageReviews.values().stream()
                .filter(review -> review.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public double getAveragePackageRating(String packageId) {
        List<PackageReview> reviews = getPackageReviewsByPackage(packageId);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        
        return reviews.stream()
                .mapToDouble(PackageReview::getRating)
                .average()
                .orElse(0.0);
    }

    public int getPackageReviewCount(String packageId) {
        return getPackageReviewsByPackage(packageId).size();
    }

    // Hotel Review Management
    public HotelReview addHotelReview(String userId, String hotelId, String comments, double rating) {
        try {
            String reviewId = IDGenerator.generateRandomString(10);
            HotelReview review = new HotelReview(reviewId, userId, hotelId, comments, rating);
            
            hotelReviews.put(reviewId, review);
            
            // Also add a rating entry
            addRating(userId, hotelId, rating, "HOTEL");
            
            saveHotelReviewsToFile();
            Logger.log("Hotel review added: " + reviewId + " for hotel " + hotelId);
            
            return review;
        } catch (Exception e) {
            Logger.error("Error adding hotel review: " + e.getMessage());
            return null;
        }
    }

    public void removeHotelReview(String reviewId) {
        try {
            HotelReview removed = hotelReviews.remove(reviewId);
            if (removed != null) {
                saveHotelReviewsToFile();
                Logger.log("Hotel review removed: " + reviewId);
            }
        } catch (Exception e) {
            Logger.error("Error removing hotel review: " + e.getMessage());
        }
    }

    public HotelReview getHotelReviewById(String reviewId) {
        return hotelReviews.get(reviewId);
    }

    public List<HotelReview> getAllHotelReviews() {
        return new ArrayList<>(hotelReviews.values());
    }

    public List<HotelReview> getHotelReviewsByHotel(String hotelId) {
        return hotelReviews.values().stream()
                .filter(review -> review.getHotelId().equals(hotelId))
                .collect(Collectors.toList());
    }

    public List<HotelReview> getHotelReviewsByUser(String userId) {
        return hotelReviews.values().stream()
                .filter(review -> review.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public double getAverageHotelRating(String hotelId) {
        List<HotelReview> reviews = getHotelReviewsByHotel(hotelId);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        
        return reviews.stream()
                .mapToDouble(HotelReview::getRating)
                .average()
                .orElse(0.0);
    }

    public int getHotelReviewCount(String hotelId) {
        return getHotelReviewsByHotel(hotelId).size();
    }

    // Rating Management
    public Rating addRating(String userId, String entityId, double ratingValue, String entityType) {
        try {
            String ratingId = IDGenerator.generateRandomString(10);
            Rating rating = new Rating(ratingId, userId, entityId, ratingValue, entityType);
            
            ratings.put(ratingId, rating);
            saveRatingsToFile();
            
            Logger.log("Rating added: " + ratingId + " for " + entityType + " " + entityId);
            return rating;
        } catch (Exception e) {
            Logger.error("Error adding rating: " + e.getMessage());
            return null;
        }
    }

    public void removeRating(String ratingId) {
        try {
            Rating removed = ratings.remove(ratingId);
            if (removed != null) {
                saveRatingsToFile();
                Logger.log("Rating removed: " + ratingId);
            }
        } catch (Exception e) {
            Logger.error("Error removing rating: " + e.getMessage());
        }
    }

    public Rating getRatingById(String ratingId) {
        return ratings.get(ratingId);
    }

    public List<Rating> getAllRatings() {
        return new ArrayList<>(ratings.values());
    }

    public List<Rating> getRatingsByEntity(String entityId, String entityType) {
        return ratings.values().stream()
                .filter(rating -> rating.getEntityId().equals(entityId) && 
                                rating.getEntityType().equals(entityType))
                .collect(Collectors.toList());
    }

    public List<Rating> getRatingsByUser(String userId) {
        return ratings.values().stream()
                .filter(rating -> rating.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    // Review Analysis and Statistics
    public Map<Integer, Integer> getPackageRatingDistribution(String packageId) {
        Map<Integer, Integer> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, 0);
        }
        
        List<PackageReview> reviews = getPackageReviewsByPackage(packageId);
        for (PackageReview review : reviews) {
            int ratingLevel = (int) Math.round(review.getRating());
            if (ratingLevel >= 1 && ratingLevel <= 5) {
                distribution.put(ratingLevel, distribution.get(ratingLevel) + 1);
            }
        }
        
        return distribution;
    }

    public Map<Integer, Integer> getHotelRatingDistribution(String hotelId) {
        Map<Integer, Integer> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, 0);
        }
        
        List<HotelReview> reviews = getHotelReviewsByHotel(hotelId);
        for (HotelReview review : reviews) {
            int ratingLevel = (int) Math.round(review.getRating());
            if (ratingLevel >= 1 && ratingLevel <= 5) {
                distribution.put(ratingLevel, distribution.get(ratingLevel) + 1);
            }
        }
        
        return distribution;
    }

    public List<PackageReview> getTopRatedPackageReviews(int limit) {
        return packageReviews.values().stream()
                .sorted(Comparator.comparing(PackageReview::getRating).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<HotelReview> getTopRatedHotelReviews(int limit) {
        return hotelReviews.values().stream()
                .sorted(Comparator.comparing(HotelReview::getRating).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<PackageReview> getRecentPackageReviews(int limit) {
        return packageReviews.values().stream()
                .sorted(Comparator.comparing(PackageReview::getReviewDate).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<HotelReview> getRecentHotelReviews(int limit) {
        return hotelReviews.values().stream()
                .sorted(Comparator.comparing(HotelReview::getReviewDate).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public boolean hasUserReviewedPackage(String userId, String packageId) {
        return packageReviews.values().stream()
                .anyMatch(review -> review.getUserId().equals(userId) && 
                                  review.getPackageId().equals(packageId));
    }

    public boolean hasUserReviewedHotel(String userId, String hotelId) {
        return hotelReviews.values().stream()
                .anyMatch(review -> review.getUserId().equals(userId) && 
                                  review.getHotelId().equals(hotelId));
    }

    // Search and Filter Methods
    public List<PackageReview> searchPackageReviews(String keyword) {
        return packageReviews.values().stream()
                .filter(review -> review.getReviewText() != null && 
                                review.getReviewText().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<HotelReview> searchHotelReviews(String keyword) {
        return hotelReviews.values().stream()
                .filter(review -> review.getComments() != null && 
                                review.getComments().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<PackageReview> getPackageReviewsByRatingRange(double minRating, double maxRating) {
        return packageReviews.values().stream()
                .filter(review -> review.getRating() >= minRating && review.getRating() <= maxRating)
                .collect(Collectors.toList());
    }

    public List<HotelReview> getHotelReviewsByRatingRange(double minRating, double maxRating) {
        return hotelReviews.values().stream()
                .filter(review -> review.getRating() >= minRating && review.getRating() <= maxRating)
                .collect(Collectors.toList());
    }

    // Statistics Methods
    public int getTotalPackageReviews() {
        return packageReviews.size();
    }

    public int getTotalHotelReviews() {
        return hotelReviews.size();
    }

    public int getTotalRatings() {
        return ratings.size();
    }

    public double getOverallAveragePackageRating() {
        return packageReviews.values().stream()
                .mapToDouble(PackageReview::getRating)
                .average()
                .orElse(0.0);
    }

    public double getOverallAverageHotelRating() {
        return hotelReviews.values().stream()
                .mapToDouble(HotelReview::getRating)
                .average()
                .orElse(0.0);
    }

    // File Operations
    private void loadReviewsFromFile() {
        loadPackageReviewsFromFile();
        loadHotelReviewsFromFile();
        loadRatingsFromFile();
    }

    private void loadPackageReviewsFromFile() {
        try {
            List<String> lines = FileHandler.readFromFile(PACKAGE_REVIEWS_FILE);
            for (String line : lines) {
                PackageReview review = parsePackageReviewFromString(line);
                if (review != null) {
                    packageReviews.put(review.getReviewId(), review);
                }
            }
        } catch (Exception e) {
            Logger.error("Failed to load package reviews from file: " + e.getMessage());
        }
    }

    private void savePackageReviewsToFile() {
        try {
            FileHandler.clearFile(PACKAGE_REVIEWS_FILE);
            for (PackageReview review : packageReviews.values()) {
                String reviewString = convertPackageReviewToString(review);
                FileHandler.writeToFile(PACKAGE_REVIEWS_FILE, reviewString);
            }
        } catch (Exception e) {
            Logger.error("Failed to save package reviews to file: " + e.getMessage());
        }
    }

    private void loadHotelReviewsFromFile() {
        try {
            List<String> lines = FileHandler.readFromFile(HOTEL_REVIEWS_FILE);
            for (String line : lines) {
                HotelReview review = parseHotelReviewFromString(line);
                if (review != null) {
                    hotelReviews.put(review.getReviewId(), review);
                }
            }
        } catch (Exception e) {
            Logger.error("Failed to load hotel reviews from file: " + e.getMessage());
        }
    }

    private void saveHotelReviewsToFile() {
        try {
            FileHandler.clearFile(HOTEL_REVIEWS_FILE);
            for (HotelReview review : hotelReviews.values()) {
                String reviewString = convertHotelReviewToString(review);
                FileHandler.writeToFile(HOTEL_REVIEWS_FILE, reviewString);
            }
        } catch (Exception e) {
            Logger.error("Failed to save hotel reviews to file: " + e.getMessage());
        }
    }

    private void loadRatingsFromFile() {
        try {
            List<String> lines = FileHandler.readFromFile(RATINGS_FILE);
            for (String line : lines) {
                Rating rating = parseRatingFromString(line);
                if (rating != null) {
                    ratings.put(rating.getRatingId(), rating);
                }
            }
        } catch (Exception e) {
            Logger.error("Failed to load ratings from file: " + e.getMessage());
        }
    }

    private void saveRatingsToFile() {
        try {
            FileHandler.clearFile(RATINGS_FILE);
            for (Rating rating : ratings.values()) {
                String ratingString = convertRatingToString(rating);
                FileHandler.writeToFile(RATINGS_FILE, ratingString);
            }
        } catch (Exception e) {
            Logger.error("Failed to save ratings to file: " + e.getMessage());
        }
    }

    // Fixed Parsing Methods
    private PackageReview parsePackageReviewFromString(String reviewString) {
        try {
            String[] parts = reviewString.split("\\|");
            if (parts.length >= 5) {
                return new PackageReview(parts[0], parts[1], parts[2], parts[3], Double.parseDouble(parts[4]));
            }
        } catch (Exception e) {
            Logger.error("Failed to parse package review: " + e.getMessage());
        }
        return null;
    }

    private String convertPackageReviewToString(PackageReview review) {
        return String.join("|",
            review.getReviewId(), review.getUserId(), review.getPackageId(),
            review.getReviewText(), String.valueOf(review.getRating()));
    }

    private HotelReview parseHotelReviewFromString(String reviewString) {
        try {
            String[] parts = reviewString.split("\\|");
            if (parts.length >= 5) {
                return new HotelReview(parts[0], parts[1], parts[2], parts[3], Double.parseDouble(parts[4]));
            }
        } catch (Exception e) {
            Logger.error("Failed to parse hotel review: " + e.getMessage());
        }
        return null;
    }

    private String convertHotelReviewToString(HotelReview review) {
        return String.join("|",
            review.getReviewId(), review.getUserId(), review.getHotelId(),
            review.getComments(), String.valueOf(review.getRating()));
    }

    private Rating parseRatingFromString(String ratingString) {
        try {
            String[] parts = ratingString.split("\\|");
            if (parts.length >= 5) {
                return new Rating(parts[0], parts[1], parts[2], Double.parseDouble(parts[3]), parts[4]);
            }
        } catch (Exception e) {
            Logger.error("Failed to parse rating: " + e.getMessage());
        }
        return null;
    }

    private String convertRatingToString(Rating rating) {
        return String.join("|",
            rating.getRatingId(), rating.getUserId(), rating.getEntityId(),
            String.valueOf(rating.getRatingValue()), rating.getEntityType());
    }

    // Additional utility methods
    public void updatePackageReview(PackageReview review) {
        if (review != null && packageReviews.containsKey(review.getReviewId())) {
            packageReviews.put(review.getReviewId(), review);
            savePackageReviewsToFile();
            Logger.log("Package review updated: " + review.getReviewId());
        }
    }

    public void updateHotelReview(HotelReview review) {
        if (review != null && hotelReviews.containsKey(review.getReviewId())) {
            hotelReviews.put(review.getReviewId(), review);
            saveHotelReviewsToFile();
            Logger.log("Hotel review updated: " + review.getReviewId());
        }
    }

    public void updateRating(Rating rating) {
        if (rating != null && ratings.containsKey(rating.getRatingId())) {
            ratings.put(rating.getRatingId(), rating);
            saveRatingsToFile();
            Logger.log("Rating updated: " + rating.getRatingId());
        }
    }

    // Bulk operations
    public void removeAllReviewsByUser(String userId) {
        try {
            packageReviews.entrySet().removeIf(entry -> entry.getValue().getUserId().equals(userId));
            hotelReviews.entrySet().removeIf(entry -> entry.getValue().getUserId().equals(userId));
            ratings.entrySet().removeIf(entry -> entry.getValue().getUserId().equals(userId));
            
            savePackageReviewsToFile();
            saveHotelReviewsToFile();
            saveRatingsToFile();
            
            Logger.log("All reviews removed for user: " + userId);
        } catch (Exception e) {
            Logger.error("Error removing all reviews for user: " + e.getMessage());
        }
    }

    public void removeAllReviewsForPackage(String packageId) {
        try {
            packageReviews.entrySet().removeIf(entry -> entry.getValue().getPackageId().equals(packageId));
            ratings.entrySet().removeIf(entry -> 
                entry.getValue().getEntityId().equals(packageId) && 
                "PACKAGE".equals(entry.getValue().getEntityType()));
            
            savePackageReviewsToFile();
            saveRatingsToFile();
            
            Logger.log("All reviews removed for package: " + packageId);
        } catch (Exception e) {
            Logger.error("Error removing all reviews for package: " + e.getMessage());
        }
    }

    public void removeAllReviewsForHotel(String hotelId) {
        try {
            hotelReviews.entrySet().removeIf(entry -> entry.getValue().getHotelId().equals(hotelId));
            ratings.entrySet().removeIf(entry -> 
                entry.getValue().getEntityId().equals(hotelId) && 
                "HOTEL".equals(entry.getValue().getEntityType()));
            
            saveHotelReviewsToFile();
            saveRatingsToFile();
            
            Logger.log("All reviews removed for hotel: " + hotelId);
        } catch (Exception e) {
            Logger.error("Error removing all reviews for hotel: " + e.getMessage());
        }
    }
}
