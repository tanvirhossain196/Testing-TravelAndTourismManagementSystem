package ui;

import util.InputHandler;
import util.IDGenerator;
import util.DateUtil;
import util.Logger;
import util.CurrencyFormatter;
import manager.*;
import model.*;
import enumtype.BookingStatus;
import java.util.List;

public class BookingUI {
    private user currentUser;
    private UserManager userManager;
    private BookingManager bookingManager;
    private PackageManager packageManager;
    private PaymentManager paymentManager;

    public BookingUI(user currentUser, UserManager userManager) {
        this.currentUser = currentUser;
        this.userManager = userManager;
        this.bookingManager = new BookingManager();
        this.packageManager = new PackageManager();
        this.paymentManager = new PaymentManager();
    }

    public void createNewBooking() {
        clearScreen();
        System.out.println("CREATE NEW BOOKING");
        System.out.println("=======================================================");
        
        try {
            // Show available packages
            List<TourPackage> packages = packageManager.listActivePackages();
            if (packages.isEmpty()) {
                System.out.println("No packages available for booking.");
                InputHandler.pressEnterToContinue();
                return;
            }
            
            displayPackageList(packages);
            
            // Get package selection
            int packageChoice = InputHandler.getInt("\nEnter Package Number (1-" + packages.size() + "): ");
            
            // Validate package choice
            if (packageChoice < 1 || packageChoice > packages.size()) {
                System.out.println("Invalid package number!");
                InputHandler.pressEnterToContinue();
                return;
            }
            
            // Get selected package
            TourPackage selectedPackage = packages.get(packageChoice - 1);
            
            if (!selectedPackage.isActive() || !selectedPackage.isAvailable()) {
                System.out.println("Package is not available for booking!");
                InputHandler.pressEnterToContinue();
                return;
            }
            
            // Show selected package details
            System.out.println("\nSELECTED PACKAGE DETAILS");
            System.out.println("=======================================================");
            System.out.println("Package: " + selectedPackage.getName());
            System.out.println("Location: " + selectedPackage.getLocation());
            System.out.println("Duration: " + selectedPackage.getDuration() + " days");
            System.out.println("Price per person: " + CurrencyFormatter.formatBDT(selectedPackage.getBasePrice()));
            System.out.println("=======================================================");
            
            // Confirm selection
            if (!InputHandler.getBoolean("Do you want to book this package?")) {
                System.out.println("Booking cancelled.");
                InputHandler.pressEnterToContinue();
                return;
            }
            
            // Get booking details
            String travelDate = getTravelDate();
            int numberOfPeople = getNumberOfPeople();
            
            // Calculate total amount
            double totalAmount = calculateTotalAmount(selectedPackage, numberOfPeople);
            
            // Create booking
            String bookingId = IDGenerator.generateBookingId();
            Booking newBooking = new Booking(bookingId, currentUser.getId(), selectedPackage.getPackageId(), travelDate, numberOfPeople);
            newBooking.setTotalAmount(totalAmount);
            
            // Get special requests
            String specialRequests = InputHandler.getString("Any special requests (optional): ");
            if (!specialRequests.trim().isEmpty()) {
                newBooking.setSpecialRequests(specialRequests);
            }
            
            // Show booking summary
            showBookingSummary(newBooking, selectedPackage);
            
            if (InputHandler.getBoolean("Confirm booking?")) {
                bookingManager.addBooking(newBooking);
                selectedPackage.addBooking();
                packageManager.updatePackage(selectedPackage);
                
                // Add to user's booking history
                if (currentUser instanceof Tourist) {
                    ((Tourist) currentUser).getBookingHistory().add(selectedPackage.getPackageId());
                }
                
                System.out.println("Booking created successfully!");
                System.out.println("Booking ID: " + bookingId);
                System.out.println("Please proceed to payment...");
                
                Logger.log("New booking created: " + bookingId + " by " + currentUser.getEmail());
                
                // Proceed to payment
                PaymentUI paymentUI = new PaymentUI(currentUser, userManager);
                paymentUI.processPayment(newBooking);
                
            } else {
                System.out.println("Booking cancelled.");
            }
            
        } catch (Exception e) {
            System.out.println("Error creating booking: " + e.getMessage());
            Logger.error("Booking creation error: " + e.getMessage());
        }
        
        InputHandler.pressEnterToContinue();
    }

    private void displayPackageList(List<TourPackage> packages) {
        System.out.println("AVAILABLE TOUR PACKAGES");
        System.out.println("=======================================================");
        System.out.printf("%-4s %-30s %-15s %-12s %-6s%n", 
            "NO.", "PACKAGE NAME", "LOCATION", "PRICE", "DAYS");
        System.out.println("-------------------------------------------------------");
        
        for (int i = 0; i < packages.size(); i++) {
            TourPackage pkg = packages.get(i);
            String packageName = pkg.getName().length() > 29 ? 
                pkg.getName().substring(0, 26) + "..." : pkg.getName();
            String location = pkg.getLocation().length() > 14 ? 
                pkg.getLocation().substring(0, 11) + "..." : pkg.getLocation();
            
            System.out.printf("%-4d %-30s %-15s %-12s %-6d%n",
                (i + 1), packageName, location,
                CurrencyFormatter.formatBDT(pkg.getBasePrice()),
                pkg.getDuration());
        }
        
        System.out.println("=======================================================");
        System.out.println("Total Packages Available: " + packages.size());
    }

    public void viewUserBookings() {
        clearScreen();
        System.out.println("MY BOOKING HISTORY");
        System.out.println("=======================================================");
        
        List<Booking> userBookings = bookingManager.getBookingsByUser(currentUser.getId());
        
        if (userBookings.isEmpty()) {
            System.out.println("You have no bookings yet.");
            System.out.println("Browse our packages and make your first booking!");
        } else {
            System.out.printf("%-12s %-12s %-12s %-6s %-10s %-10s%n",
                "BOOKING ID", "PACKAGE ID", "TRAVEL DATE", "PEOPLE", "AMOUNT", "STATUS");
            System.out.println("-------------------------------------------------------");
            
            for (Booking booking : userBookings) {
                System.out.printf("%-12s %-12s %-12s %-6d %-10s %-10s%n",
                    booking.getBookingId(),
                    booking.getPackageId(),
                    booking.getTravelDate(),
                    booking.getNumberOfPeople(),
                    CurrencyFormatter.formatBDT(booking.getTotalAmount()),
                    booking.getStatus().getDisplayName());
            }
            
            System.out.println("\nTotal Bookings: " + userBookings.size());
            
            double totalPaid = 0.0;
            for (Booking booking : userBookings) {
                if (booking.getStatus() == BookingStatus.CONFIRMED) {
                    totalPaid += booking.getTotalAmount();
                }
            }
            
            System.out.println("Total Amount Spent: " + CurrencyFormatter.formatBDT(totalPaid));
            
            // Option to view booking details
            if (InputHandler.getBoolean("View details of a specific booking?")) {
                viewBookingDetails();
            }
        }
        
        InputHandler.pressEnterToContinue();
    }

    private void viewBookingDetails() {
        String bookingId = InputHandler.getString("Enter Booking ID: ");
        Booking booking = bookingManager.getBookingById(bookingId);
        
        if (booking == null || !booking.getUserId().equals(currentUser.getId())) {
            System.out.println("Booking not found or access denied!");
            return;
        }
        
        clearScreen();
        System.out.println("BOOKING DETAILS");
        System.out.println("=======================================================");
        displayBookingDetails(booking);
        
        // Options based on booking status
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            System.out.println("\nOptions:");
            System.out.println("1. Cancel Booking");
            System.out.println("2. Download Ticket");
            
            int choice = InputHandler.getInt("Enter choice (1-2, 0 to skip): ");
            switch (choice) {
                case 1:
                    cancelBooking(booking);
                    break;
                case 2:
                    downloadTicket(booking);
                    break;
            }
        }
    }

    private void cancelBooking(Booking booking) {
        System.out.println("CANCEL BOOKING");
        System.out.println("=======================================================");
        
        if (booking.getStatus() == BookingStatus.COMPLETED || booking.getStatus() == BookingStatus.CANCELED) {
            System.out.println("Cannot cancel this booking!");
            return;
        }
        
        long daysUntilTravel = DateUtil.daysBetween(DateUtil.getCurrentDate(), booking.getTravelDate());
        double cancellationFee = calculateCancellationFee(booking, daysUntilTravel);
        double refundAmount = booking.getTotalAmount() - cancellationFee;
        
        System.out.println("Cancellation Details:");
        System.out.println("Original Amount: " + CurrencyFormatter.formatBDT(booking.getTotalAmount()));
        System.out.println("Cancellation Fee: " + CurrencyFormatter.formatBDT(cancellationFee));
        System.out.println("Refund Amount: " + CurrencyFormatter.formatBDT(refundAmount));
        
        String reason = InputHandler.getString("Reason for cancellation: ");
        
        if (InputHandler.getBoolean("Confirm cancellation?")) {
            booking.setStatus(BookingStatus.CANCELED);
            bookingManager.updateBooking(booking);
            
            TourPackage tourPackage = packageManager.getPackageById(booking.getPackageId());
            if (tourPackage != null) {
                tourPackage.cancelBooking();
                packageManager.updatePackage(tourPackage);
            }
            
            System.out.println("Booking cancelled successfully!");
            System.out.println("Refund amount: " + CurrencyFormatter.formatBDT(refundAmount));
            
            Logger.log("Booking cancelled: " + booking.getBookingId() + " - Reason: " + reason);
        }
    }

    private double calculateCancellationFee(Booking booking, long daysUntilTravel) {
        double originalAmount = booking.getTotalAmount();
        double feePercentage;
        
        if (daysUntilTravel >= 30) {
            feePercentage = 0.05; // 5% fee
        } else if (daysUntilTravel >= 15) {
            feePercentage = 0.15; // 15% fee
        } else if (daysUntilTravel >= 7) {
            feePercentage = 0.25; // 25% fee
        } else {
            feePercentage = 0.50; // 50% fee
        }
        
        return originalAmount * feePercentage;
    }

    private void downloadTicket(Booking booking) {
        if (!booking.isPaid()) {
            System.out.println("Payment required to download ticket!");
            return;
        }
        
        String ticketId = IDGenerator.generateTicketId();
        TourPackage pkg = packageManager.getPackageById(booking.getPackageId());
        
        System.out.println("TICKET GENERATED");
        System.out.println("=======================================================");
        System.out.println("Ticket ID: " + ticketId);
        System.out.println("Booking ID: " + booking.getBookingId());
        System.out.println("Passenger: " + currentUser.getName());
        System.out.println("Package: " + (pkg != null ? pkg.getName() : "Unknown Package"));
        System.out.println("Travel Date: " + booking.getTravelDate());
        System.out.println("Number of People: " + booking.getNumberOfPeople());
        System.out.println("Amount Paid: " + CurrencyFormatter.formatBDT(booking.getTotalAmount()));
        System.out.println("=======================================================");
        System.out.println("Contact: +880-2-123456789 for any assistance.");
        
        Logger.log("Ticket downloaded: " + ticketId + " for booking " + booking.getBookingId());
    }

    private String getTravelDate() {
        while (true) {
            String date = InputHandler.getString("Enter travel date (YYYY-MM-DD): ");
            if (DateUtil.isValidDate(date)) {
                long daysFromNow = DateUtil.daysBetween(DateUtil.getCurrentDate(), date);
                if (daysFromNow >= 1) {
                    return date;
                } else {
                    System.out.println("Travel date must be at least 1 day from today!");
                }
            } else {
                System.out.println("Invalid date format! Please use YYYY-MM-DD.");
            }
        }
    }

    private int getNumberOfPeople() {
        while (true) {
            int people = InputHandler.getInt("Number of people: ");
            if (people >= 1 && people <= 20) {
                return people;
            } else {
                System.out.println("Number of people must be between 1 and 20!");
            }
        }
    }

    private double calculateTotalAmount(TourPackage tourPackage, int numberOfPeople) {
        double baseAmount = tourPackage.getBasePrice() * numberOfPeople;
        double discount = 0.0;
        
        if (currentUser instanceof Tourist) {
            Tourist tourist = (Tourist) currentUser;
            if (tourist.getLoyaltyPoints() >= 1000) {
                discount = 0.10; // 10% discount for loyal customers
            }
        }
        
        if (numberOfPeople >= 4) {
            discount = Math.max(discount, 0.08); // 8% group discount
        }
        
        double finalAmount = baseAmount * (1 - discount);
        
        if (discount > 0) {
            System.out.println("Discount applied: " + (discount * 100) + "%");
            System.out.println("Final amount: " + CurrencyFormatter.formatBDT(finalAmount));
        }
        
        return finalAmount;
    }

    private void showBookingSummary(Booking booking, TourPackage tourPackage) {
        System.out.println("\nBOOKING SUMMARY");
        System.out.println("=======================================================");
        System.out.println("Booking ID: " + booking.getBookingId());
        System.out.println("Package: " + tourPackage.getName());
        System.out.println("Location: " + tourPackage.getLocation());
        System.out.println("Duration: " + tourPackage.getDuration() + " days");
        System.out.println("Travel Date: " + booking.getTravelDate());
        System.out.println("Number of People: " + booking.getNumberOfPeople());
        System.out.println("Total Amount: " + CurrencyFormatter.formatBDT(booking.getTotalAmount()));
        if (booking.getSpecialRequests() != null && !booking.getSpecialRequests().trim().isEmpty()) {
            System.out.println("Special Requests: " + booking.getSpecialRequests());
        }
        System.out.println("=======================================================");
    }

    private void displayBookingDetails(Booking booking) {
        TourPackage pkg = packageManager.getPackageById(booking.getPackageId());
        
        System.out.println("Booking ID: " + booking.getBookingId());
        System.out.println("Package: " + (pkg != null ? pkg.getName() : "Unknown"));
        System.out.println("Location: " + (pkg != null ? pkg.getLocation() : "Unknown"));
        System.out.println("Booking Date: " + booking.getBookingDate());
        System.out.println("Travel Date: " + booking.getTravelDate());
        System.out.println("Number of People: " + booking.getNumberOfPeople());
        System.out.println("Total Amount: " + CurrencyFormatter.formatBDT(booking.getTotalAmount()));
        System.out.println("Status: " + booking.getStatus().getDisplayName());
        System.out.println("Payment Status: " + (booking.isPaid() ? "Paid" : "Pending"));
        
        if (booking.getSpecialRequests() != null && !booking.getSpecialRequests().trim().isEmpty()) {
            System.out.println("Special Requests: " + booking.getSpecialRequests());
        }
    }

    public void viewBookingHistory() {
        clearScreen();
        System.out.println("COMPLETE BOOKING HISTORY");
        System.out.println("=======================================================");
        
        List<Booking> userBookings = bookingManager.getBookingsByUser(currentUser.getId());
        
        if (userBookings.isEmpty()) {
            System.out.println("No booking history found.");
        } else {
            for (Booking booking : userBookings) {
                System.out.println("-------------------------------------------------------");
                displayBookingDetails(booking);
            }
            System.out.println("-------------------------------------------------------");
            System.out.println("Total Bookings: " + userBookings.size());
        }
        
        InputHandler.pressEnterToContinue();
    }

    private void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}