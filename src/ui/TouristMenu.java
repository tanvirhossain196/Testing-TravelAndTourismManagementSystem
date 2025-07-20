
package ui;

import util.InputHandler;
import util.Logger;
import manager.*;
import model.user;
import model.Tourist;
import model.Booking;
import enumtype.BookingStatus;
import feedback.PackageReview;
import java.util.List;

public class TouristMenu {
    private Tourist tourist;
    private UserManager userManager;
    private PackageManager packageManager;
    private BookingManager bookingManager;
    private ReviewManager reviewManager;
    private boolean isRunning;

    public TouristMenu(user user, UserManager userManager) {
        this.tourist = (Tourist) user;
        this.userManager = userManager;
        this.packageManager = new PackageManager();
        this.bookingManager = new BookingManager();
        this.reviewManager = new ReviewManager();
        this.isRunning = true;
    }

    public void displayTouristMenu() {
        while (isRunning) {
            clearScreen();
            printTouristHeader();
            printTouristMenuOptions();
            
            int choice = InputHandler.getInt("Enter your choice: ");
            handleTouristMenuChoice(choice);
        }
    }

    private void printTouristHeader() {
        System.out.println("=======================================================");
        System.out.println("                TOURIST PORTAL");
        System.out.println("              Welcome, " + tourist.getName());
        System.out.println("              Loyalty Points: " + tourist.getLoyaltyPoints());
        System.out.println("=======================================================");
        System.out.println();
    }

    private void printTouristMenuOptions() {
        System.out.println("-------------------------------------------------------");
        System.out.println("               TOURIST FUNCTIONS");
        System.out.println("-------------------------------------------------------");
        System.out.println(" 1.  Browse Tour Packages");
        System.out.println(" 2.  Search Packages");
        System.out.println(" 3.  View Package Details");
        System.out.println(" 4.  Book a Package");
        System.out.println(" 5.  My Booking History");
        System.out.println(" 6.  My Payments");
        System.out.println(" 7.  Give Reviews");
        System.out.println(" 8.  My Profile");
        System.out.println(" 9.  Special Offers");
        System.out.println(" 10. Help & Support");
        System.out.println(" 11. Logout");
        System.out.println("-------------------------------------------------------");
        System.out.println();
    }

    private void handleTouristMenuChoice(int choice) {
        switch (choice) {
            case 1:
                browseTourPackages();
                break;
            case 2:
                searchPackages();
                break;
            case 3:
                viewPackageDetails();
                break;
            case 4:
                bookPackage();
                break;
            case 5:
                viewBookingHistory();
                break;
            case 6:
                viewPayments();
                break;
            case 7:
                giveReviews();
                break;
            case 8:
                viewProfile();
                break;
            case 9:
                viewSpecialOffers();
                break;
            case 10:
                showHelpAndSupport();
                break;
            case 11:
                logout();
                break;
            default:
                System.out.println("Invalid choice! Please select 1-11.");
                InputHandler.pressEnterToContinue();
        }
    }

    private void browseTourPackages() {
        PackageUI packageUI = new PackageUI(tourist, userManager);
        packageUI.browseTourPackages();
    }

    private void searchPackages() {
        PackageUI packageUI = new PackageUI(tourist, userManager);
        packageUI.searchPackages();
    }

    private void viewPackageDetails() {
        String packageId = InputHandler.getString("Enter Package ID: ");
        PackageUI packageUI = new PackageUI(tourist, userManager);
        packageUI.viewPackageDetails(packageId);
    }

    private void bookPackage() {
        BookingUI bookingUI = new BookingUI(tourist, userManager);
        bookingUI.createNewBooking();
    }

    private void viewBookingHistory() {
        BookingUI bookingUI = new BookingUI(tourist, userManager);
        bookingUI.viewUserBookings();
    }

    private void viewPayments() {
        PaymentUI paymentUI = new PaymentUI(tourist, userManager);
        paymentUI.viewUserPayments();
    }

    private void giveReviews() {
        clearScreen();
        System.out.println("GIVE REVIEWS");
        System.out.println("=======================================================");
        
        List<String> bookingHistory = tourist.getBookingHistory();
        if (bookingHistory.isEmpty()) {
            System.out.println("You haven't booked any packages yet.");
            System.out.println("Book a package first to give reviews!");
        } else {
            System.out.println("Your completed bookings:");
            System.out.println("-------------------------------------------------------");
            for (int i = 0; i < bookingHistory.size(); i++) {
                System.out.println((i + 1) + ". Package ID: " + bookingHistory.get(i));
            }
            System.out.println("-------------------------------------------------------");
            
            String packageId = InputHandler.getString("Enter Package ID to review: ");
            if (bookingHistory.contains(packageId)) {
                submitReview(packageId);
            } else {
                System.out.println("You haven't booked this package or it's not completed yet.");
            }
        }
        
        InputHandler.pressEnterToContinue();
    }

    private void submitReview(String packageId) {
        System.out.println("\nREVIEW SUBMISSION");
        System.out.println("=======================================================");
        
        int rating = 0;
        while (rating < 1 || rating > 5) {
            rating = InputHandler.getInt("Rate this package (1-5 stars): ");
            if (rating < 1 || rating > 5) {
                System.out.println("Please enter a rating between 1 and 5.");
            }
        }
        
        String reviewTitle = InputHandler.getString("Review title: ");
        String reviewText = InputHandler.getString("Write your detailed review: ");
        
        // Submit review through ReviewManager
        PackageReview packageReview = reviewManager.addPackageReview(tourist.getId(), packageId, reviewText, rating);
        if (packageReview != null) {
            packageReview.setReviewTitle(reviewTitle);
            packageReview.setUserName(tourist.getName());
            reviewManager.updatePackageReview(packageReview);
            
            // Add loyalty points for reviewing
            tourist.addLoyaltyPoints(50); // 50 points for each review
            userManager.updateUser(tourist);
            
            System.out.println("Thank you for your review!");
            System.out.println("Rating: " + rating + "/5 stars");
            System.out.println("Title: " + reviewTitle);
            System.out.println("Review: " + reviewText);
            System.out.println("You earned 50 loyalty points!");
            
            Logger.log("Review submitted by " + tourist.getEmail() + " for package " + packageId);
        } else {
            System.out.println("Failed to submit review. Please try again.");
        }
    }

    private void viewProfile() {
        clearScreen();
        System.out.println("MY PROFILE");
        System.out.println("=======================================================");
        System.out.println("Personal Information:");
        System.out.println("  Name: " + tourist.getName());
        System.out.println("  Email: " + tourist.getEmail());
        System.out.println("  Phone: " + tourist.getPhone());
        System.out.println("  Nationality: " + tourist.getNationality());
        System.out.println("  Member Since: " + tourist.getCreatedDate());
        System.out.println();
        System.out.println("Account Statistics:");
        System.out.println("  Loyalty Points: " + tourist.getLoyaltyPoints());
        System.out.println("  Total Bookings: " + tourist.getBookingHistory().size());
        System.out.println("  Account Status: " + (tourist.isActive() ? "Active" : "Inactive"));
        
        // Show loyalty tier
        String loyaltyTier = getLoyaltyTier(tourist.getLoyaltyPoints());
        System.out.println("  Loyalty Tier: " + loyaltyTier);
        
        System.out.println();
        System.out.println("Travel Preferences:");
        if (tourist.getPreferences().isEmpty()) {
            System.out.println("  No preferences set");
        } else {
            for (String pref : tourist.getPreferences()) {
                System.out.println("  - " + pref);
            }
        }
        
        System.out.println("\nProfile Options:");
        System.out.println("1. Update Profile");
        System.out.println("2. Change Password");
        System.out.println("3. Set Travel Preferences");
        System.out.println("4. View Loyalty Benefits");
        System.out.println("5. Back to Main Menu");
        
        int choice = InputHandler.getInt("Enter choice (1-5): ");
        switch (choice) {
            case 1:
                updateProfile();
                break;
            case 2:
                changePassword();
                break;
            case 3:
                setTravelPreferences();
                break;
            case 4:
                viewLoyaltyBenefits();
                break;
            case 5:
                return;
            default:
                System.out.println("Invalid choice!");
                InputHandler.pressEnterToContinue();
        }
    }

    private void updateProfile() {
        System.out.println("\nUPDATE PROFILE");
        System.out.println("=======================================================");
        
        String newPhone = InputHandler.getString("New Phone (current: " + tourist.getPhone() + "): ");
        if (!newPhone.trim().isEmpty() && util.Validator.validatePhone(newPhone)) {
            tourist.setPhone(newPhone);
            System.out.println("Phone updated successfully!");
        }
        
        String newNationality = InputHandler.getString("New Nationality (current: " + tourist.getNationality() + "): ");
        if (!newNationality.trim().isEmpty()) {
            tourist.setNationality(newNationality);
            System.out.println("Nationality updated successfully!");
        }
        
        userManager.updateUser(tourist);
        Logger.log("Profile updated by tourist: " + tourist.getEmail());
        
        InputHandler.pressEnterToContinue();
    }

    private void changePassword() {
        System.out.println("\nCHANGE PASSWORD");
        System.out.println("=======================================================");
        
        String currentPassword = InputHandler.getString("Enter current password: ");
        if (!tourist.getPassword().equals(currentPassword)) {
            System.out.println("Incorrect current password!");
            InputHandler.pressEnterToContinue();
            return;
        }
        
        String newPassword = InputHandler.getString("Enter new password (min 6 characters): ");
        if (!util.Validator.validatePassword(newPassword)) {
            System.out.println("Password must be at least 6 characters long!");
            InputHandler.pressEnterToContinue();
            return;
        }
        
        String confirmPassword = InputHandler.getString("Confirm new password: ");
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Passwords don't match!");
            InputHandler.pressEnterToContinue();
            return;
        }
        
        tourist.setPassword(newPassword);
        userManager.updateUser(tourist);
        
        System.out.println("Password changed successfully!");
        Logger.log("Password changed by tourist: " + tourist.getEmail());
        
        InputHandler.pressEnterToContinue();
    }

    private void setTravelPreferences() {
        System.out.println("\nSET TRAVEL PREFERENCES");
        System.out.println("=======================================================");
        
        System.out.println("Select your travel preferences:");
        System.out.println("1. Adventure Tours");
        System.out.println("2. Cultural Tours");
        System.out.println("3. Beach Holidays");
        System.out.println("4. Historical Sites");
        System.out.println("5. Family Packages");
        System.out.println("6. Romantic Getaways");
        System.out.println("7. Budget Travel");
        System.out.println("8. Luxury Travel");
        
        String preferences = InputHandler.getString("Enter preference numbers (comma separated, e.g., 1,3,5): ");
        String[] prefNumbers = preferences.split(",");
        
        tourist.getPreferences().clear();
        
        for (String prefNum : prefNumbers) {
            try {
                int num = Integer.parseInt(prefNum.trim());
                switch (num) {
                    case 1: tourist.getPreferences().add("Adventure Tours"); break;
                    case 2: tourist.getPreferences().add("Cultural Tours"); break;
                    case 3: tourist.getPreferences().add("Beach Holidays"); break;
                    case 4: tourist.getPreferences().add("Historical Sites"); break;
                    case 5: tourist.getPreferences().add("Family Packages"); break;
                    case 6: tourist.getPreferences().add("Romantic Getaways"); break;
                    case 7: tourist.getPreferences().add("Budget Travel"); break;
                    case 8: tourist.getPreferences().add("Luxury Travel"); break;
                }
            } catch (NumberFormatException e) {
                // Skip invalid numbers
            }
        }
        
        userManager.updateUser(tourist);
        System.out.println("Travel preferences updated successfully!");
        
        InputHandler.pressEnterToContinue();
    }

    private void viewLoyaltyBenefits() {
        clearScreen();
        System.out.println("LOYALTY BENEFITS");
        System.out.println("=======================================================");
        
        int points = tourist.getLoyaltyPoints();
        String tier = getLoyaltyTier(points);
        
        System.out.println("Current Status:");
        System.out.println("  Loyalty Points: " + points);
        System.out.println("  Current Tier: " + tier);
        System.out.println();
        
        System.out.println("Tier Benefits:");
        System.out.println("-------------------------------------------------------");
        System.out.println("BRONZE (0-499 points): 2% discount, Birthday offers");
        System.out.println("SILVER (500-999 points): 5% discount, Priority support");
        System.out.println("GOLD (1000-1999 points): 10% discount, Hotel upgrades");
        System.out.println("PLATINUM (2000+ points): 15% discount, VIP services");
        
        if (points < 2000) {
            int nextTierPoints = getNextTierPoints(points);
            System.out.println("\nNext Tier: " + nextTierPoints + " more points needed");
        }
        
        InputHandler.pressEnterToContinue();
    }

    private String getLoyaltyTier(int points) {
        if (points >= 2000) return "PLATINUM";
        if (points >= 1000) return "GOLD";
        if (points >= 500) return "SILVER";
        return "BRONZE";
    }

    private int getNextTierPoints(int currentPoints) {
        if (currentPoints < 500) return 500 - currentPoints;
        if (currentPoints < 1000) return 1000 - currentPoints;
        if (currentPoints < 2000) return 2000 - currentPoints;
        return 0;
    }

    private void viewSpecialOffers() {
        clearScreen();
        System.out.println("SPECIAL OFFERS");
        System.out.println("=======================================================");
        System.out.println("Personalized Offers for You:");
        System.out.println();
        
        if (tourist.getLoyaltyPoints() >= 1000) {
            System.out.println("VIP DISCOUNT: 15% off on all international packages!");
        }
        
        if (tourist.getBookingHistory().size() >= 3) {
            System.out.println("FREQUENT TRAVELER: 10% off on next booking!");
        }
        
        if (tourist.getBookingHistory().isEmpty()) {
            System.out.println("FIRST TIME VISITOR: 5% off on your first booking!");
        }
        
        // General offers
        System.out.println("\nCurrent Promotions:");
        System.out.println("SEASONAL OFFER: Cox's Bazar Special - 20% off!");
        System.out.println("EARLY BIRD: Book 30 days in advance and save 12%!");
        System.out.println("FAMILY PACKAGE: Book for 4+ and get child discount!");
        System.out.println("GROUP BOOKING: 15% off for groups of 10 or more!");
        
        InputHandler.pressEnterToContinue();
    }

    private void showHelpAndSupport() {
        clearScreen();
        System.out.println("HELP & SUPPORT");
        System.out.println("=======================================================");
        
        System.out.println("Frequently Asked Questions:");
        System.out.println("-------------------------------------------------------");
        System.out.println("Q: How do I cancel my booking?");
        System.out.println("A: Go to 'My Booking History' and select the booking to cancel.");
        System.out.println();
        System.out.println("Q: How do I earn loyalty points?");
        System.out.println("A: Book packages, write reviews, and refer friends!");
        System.out.println();
        System.out.println("Q: What payment methods do you accept?");
        System.out.println("A: We accept cards, mobile banking, bank transfer, and cash.");
        System.out.println();
        
        System.out.println("Contact Support:");
        System.out.println("-------------------------------------------------------");
        System.out.println("Phone: +880-2-123456789");
        System.out.println("Email: support@tourbd.com");
        System.out.println("Office Hours: 9:00 AM - 6:00 PM (Saturday-Thursday)");
        System.out.println("Emergency Contact: +880-1812345678");
        
        InputHandler.pressEnterToContinue();
    }

    private void logout() {
        clearScreen();
        System.out.println("=======================================================");
        System.out.println("                 TOURIST LOGOUT");
        System.out.println("=======================================================");
        System.out.println();
        System.out.println("Thank you for using TourBD, " + tourist.getName() + "!");
        System.out.println("Your current loyalty points: " + tourist.getLoyaltyPoints());
        System.out.println("Session ended at: " + util.DateUtil.getCurrentDateTime());
        System.out.println();
        
        Logger.log("Tourist logged out: " + tourist.getEmail());
        isRunning = false;
    }

    private void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}

