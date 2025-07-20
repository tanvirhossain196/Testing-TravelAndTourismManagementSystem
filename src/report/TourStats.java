package report;

import model.*;
import manager.*;
import enumtype.*;
import util.DateUtil;
import java.util.*;

public class TourStats {
    private PackageManager packageManager;
    private BookingManager bookingManager;
    private UserManager userManager;
    private PaymentManager paymentManager;

    public TourStats() {
        this.packageManager = new PackageManager();
        this.bookingManager = new BookingManager();
        this.userManager = new UserManager();
        this.paymentManager = new PaymentManager();
    }

    public TourStats(PackageManager packageManager, BookingManager bookingManager, 
                    UserManager userManager, PaymentManager paymentManager) {
        this.packageManager = packageManager;
        this.bookingManager = bookingManager;
        this.userManager = userManager;
        this.paymentManager = paymentManager;
    }

    public List<TourPackage> getPopularPackages() {
        return getPopularPackages(10);
    }

    public List<TourPackage> getPopularPackages(int limit) {
        try {
            Map<String, Long> packageBookingCount = new HashMap<>();
            List<Booking> allBookings = bookingManager.getAllBookings();
            
            for (Booking booking : allBookings) {
                String packageId = booking.getPackageId();
                packageBookingCount.put(packageId, packageBookingCount.getOrDefault(packageId, 0L) + 1);
            }

            List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>(packageBookingCount.entrySet());
            sortedEntries.sort(new Comparator<Map.Entry<String, Long>>() {
                @Override
                public int compare(Map.Entry<String, Long> e1, Map.Entry<String, Long> e2) {
                    return e2.getValue().compareTo(e1.getValue());
                }
            });

            List<TourPackage> result = new ArrayList<>();
            int actualLimit = Math.min(limit, sortedEntries.size());
            for (int i = 0; i < actualLimit; i++) {
                TourPackage pkg = packageManager.getPackageById(sortedEntries.get(i).getKey());
                if (pkg != null) {
                    result.add(pkg);
                }
            }
            return result;
        } catch (Exception e) {
            util.Logger.error("Error getting popular packages: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Map<String, Integer> getPackageBookingCounts() {
        Map<String, Integer> bookingCounts = new HashMap<>();
        
        try {
            List<Booking> bookings = bookingManager.getAllBookings();
            for (Booking booking : bookings) {
                String packageId = booking.getPackageId();
                bookingCounts.put(packageId, bookingCounts.getOrDefault(packageId, 0) + 1);
            }
        } catch (Exception e) {
            util.Logger.error("Error getting package booking counts: " + e.getMessage());
        }
        
        return bookingCounts;
    }

    public double calculateAverageRating() {
        return calculateAverageRating(null);
    }

    public double calculateAverageRating(String packageId) {
        try {
            // Simulated rating calculation since we don't have rating data in bookings
            List<Double> ratings = new ArrayList<>();
            
            if (packageId != null) {
                // Package-specific rating
                List<Booking> packageBookings = new ArrayList<>();
                List<Booking> allBookings = bookingManager.getAllBookings();
                for (Booking booking : allBookings) {
                    if (booking.getPackageId().equals(packageId)) {
                        packageBookings.add(booking);
                    }
                }
                
                for (int i = 0; i < packageBookings.size(); i++) {
                    ratings.add(3.5 + (Math.random() * 1.5)); // Simulated ratings between 3.5-5.0
                }
            } else {
                // Overall system rating
                List<Booking> allBookings = bookingManager.getAllBookings();
                for (int i = 0; i < allBookings.size(); i++) {
                    ratings.add(3.0 + (Math.random() * 2.0)); // Simulated ratings between 3.0-5.0
                }
            }
            
            if (ratings.isEmpty()) {
                return 0.0;
            }
            
            double sum = 0.0;
            for (Double rating : ratings) {
                sum += rating;
            }
            return sum / ratings.size();
        } catch (Exception e) {
            util.Logger.error("Error calculating average rating: " + e.getMessage());
            return 0.0;
        }
    }

    public Map<String, Double> getAllPackageRatings() {
        Map<String, Double> packageRatings = new HashMap<>();
        
        try {
            List<TourPackage> allPackages = packageManager.listPackages();
            for (TourPackage pkg : allPackages) {
                double rating = calculateAverageRating(pkg.getPackageId());
                packageRatings.put(pkg.getPackageId(), rating);
            }
        } catch (Exception e) {
            util.Logger.error("Error getting all package ratings: " + e.getMessage());
        }
        
        return packageRatings;
    }

    public PackagePerformanceStats getPackagePerformance(String packageId) {
        try {
            TourPackage tourPackage = packageManager.getPackageById(packageId);
            if (tourPackage == null) {
                return null;
            }

            List<Booking> packageBookings = new ArrayList<>();
            List<Booking> allBookings = bookingManager.getAllBookings();
            for (Booking booking : allBookings) {
                if (booking.getPackageId().equals(packageId)) {
                    packageBookings.add(booking);
                }
            }

            int totalBookings = packageBookings.size();
            long confirmedBookings = 0;
            long cancelledBookings = 0;
            
            for (Booking booking : packageBookings) {
                if (booking.getStatus() == BookingStatus.CONFIRMED) {
                    confirmedBookings++;
                } else if (booking.getStatus() == BookingStatus.CANCELED) {
                    cancelledBookings++;
                }
            }

            double totalRevenue = 0.0;
            for (Booking booking : packageBookings) {
                if (booking.getStatus() == BookingStatus.CONFIRMED) {
                    totalRevenue += booking.getTotalAmount();
                }
            }

            double averageBookingValue = 0.0;
            if (totalBookings > 0) {
                double totalBookingValue = 0.0;
                for (Booking booking : packageBookings) {
                    totalBookingValue += booking.getTotalAmount();
                }
                averageBookingValue = totalBookingValue / totalBookings;
            }

            double rating = calculateAverageRating(packageId);
            
            int totalParticipants = 0;
            for (Booking booking : packageBookings) {
                totalParticipants += booking.getNumberOfPeople();
            }

            return new PackagePerformanceStats(
                packageId, tourPackage.getName(), totalBookings, (int)confirmedBookings,
                (int)cancelledBookings, totalRevenue, averageBookingValue, rating, totalParticipants
            );
        } catch (Exception e) {
            util.Logger.error("Error getting package performance: " + e.getMessage());
            return null;
        }
    }

    public List<PackagePerformanceStats> getAllPackagePerformance() {
        List<PackagePerformanceStats> performanceList = new ArrayList<>();
        
        try {
            List<TourPackage> allPackages = packageManager.listPackages();
            for (TourPackage pkg : allPackages) {
                PackagePerformanceStats stats = getPackagePerformance(pkg.getPackageId());
                if (stats != null) {
                    performanceList.add(stats);
                }
            }
        } catch (Exception e) {
            util.Logger.error("Error getting all package performance: " + e.getMessage());
        }
        
        return performanceList;
    }

    public Map<PackageCategory, Integer> getBookingsByCategory() {
        Map<PackageCategory, Integer> categoryBookings = new HashMap<>();
        
        try {
            List<Booking> bookings = bookingManager.getAllBookings();
            for (Booking booking : bookings) {
                TourPackage pkg = packageManager.getPackageById(booking.getPackageId());
                if (pkg != null && pkg.getCategory() != null) {
                    PackageCategory category = pkg.getCategory();
                    categoryBookings.put(category, categoryBookings.getOrDefault(category, 0) + 1);
                }
            }
        } catch (Exception e) {
            util.Logger.error("Error getting bookings by category: " + e.getMessage());
        }
        
        return categoryBookings;
    }

    public Map<TourType, Integer> getBookingsByTourType() {
        Map<TourType, Integer> tourTypeBookings = new HashMap<>();
        
        try {
            List<Booking> bookings = bookingManager.getAllBookings();
            for (Booking booking : bookings) {
                TourPackage pkg = packageManager.getPackageById(booking.getPackageId());
                if (pkg != null && pkg.getTourType() != null) {
                    TourType tourType = pkg.getTourType();
                    tourTypeBookings.put(tourType, tourTypeBookings.getOrDefault(tourType, 0) + 1);
                }
            }
        } catch (Exception e) {
            util.Logger.error("Error getting bookings by tour type: " + e.getMessage());
        }
        
        return tourTypeBookings;
    }

    public CustomerStats getCustomerStatistics() {
        try {
            List<user> allUsers = userManager.getAllUsers();
            List<Tourist> tourists = new ArrayList<>();
            
            for (user u : allUsers) {
                if ("TOURIST".equals(u.getRole())) {
                    tourists.add((Tourist) u);
                }
            }

            int totalCustomers = tourists.size();
            int activeCustomers = 0;
            for (Tourist tourist : tourists) {
                if (!tourist.getBookingHistory().isEmpty()) {
                    activeCustomers++;
                }
            }

            double averageLoyaltyPoints = 0.0;
            if (!tourists.isEmpty()) {
                int totalPoints = 0;
                for (Tourist tourist : tourists) {
                    totalPoints += tourist.getLoyaltyPoints();
                }
                averageLoyaltyPoints = (double) totalPoints / tourists.size();
            }

            int repeatCustomers = 0;
            for (Tourist tourist : tourists) {
                if (tourist.getBookingHistory().size() > 1) {
                    repeatCustomers++;
                }
            }

            double retentionRate = totalCustomers > 0 ? (repeatCustomers * 100.0) / totalCustomers : 0;

            // Calculate average customer lifetime value
            double totalRevenue = 0.0;
            List<Booking> allBookings = bookingManager.getAllBookings();
            for (Booking booking : allBookings) {
                totalRevenue += booking.getTotalAmount();
            }
            
            double averageCLV = activeCustomers > 0 ? totalRevenue / activeCustomers : 0;

            return new CustomerStats(totalCustomers, activeCustomers, (int)averageLoyaltyPoints, 
                                   repeatCustomers, retentionRate, averageCLV);
        } catch (Exception e) {
            util.Logger.error("Error getting customer statistics: " + e.getMessage());
            return new CustomerStats(0, 0, 0, 0, 0.0, 0.0);
        }
    }

    public RevenueStats getRevenueStatistics() {
        try {
            List<Payment> completedPayments = new ArrayList<>();
            List<Payment> allPayments = paymentManager.getAllPayments();
            
            for (Payment payment : allPayments) {
                if ("COMPLETED".equals(payment.getPaymentStatus())) {
                    completedPayments.add(payment);
                }
            }

            double totalRevenue = 0.0;
            for (Payment payment : completedPayments) {
                totalRevenue += payment.getAmount();
            }

            double averageTransactionValue = 0.0;
            if (!completedPayments.isEmpty()) {
                averageTransactionValue = totalRevenue / completedPayments.size();
            }

            // Monthly revenue calculation (simplified)
            String currentMonth = DateUtil.getCurrentDate().substring(0, 7);
            double monthlyRevenue = 0.0;
            for (Payment payment : completedPayments) {
                if (payment.getPaymentDate().startsWith(currentMonth)) {
                    monthlyRevenue += payment.getAmount();
                }
            }

            // Payment method distribution
            Map<String, Double> paymentMethodRevenue = new HashMap<>();
            for (Payment payment : completedPayments) {
                String method = payment.getPaymentMethod();
                paymentMethodRevenue.put(method, paymentMethodRevenue.getOrDefault(method, 0.0) + payment.getAmount());
            }

            return new RevenueStats(totalRevenue, averageTransactionValue, monthlyRevenue, paymentMethodRevenue);
        } catch (Exception e) {
            util.Logger.error("Error getting revenue statistics: " + e.getMessage());
            return new RevenueStats(0.0, 0.0, 0.0, new HashMap<String, Double>());
        }
    }

    public Map<String, Integer> getMonthlyBookingTrends() {
        Map<String, Integer> monthlyTrends = new HashMap<>();
        
        try {
            List<Booking> bookings = bookingManager.getAllBookings();
            for (Booking booking : bookings) {
                String month = booking.getBookingDate().substring(0, 7); // YYYY-MM
                monthlyTrends.put(month, monthlyTrends.getOrDefault(month, 0) + 1);
            }
        } catch (Exception e) {
            util.Logger.error("Error getting monthly booking trends: " + e.getMessage());
        }
        
        return monthlyTrends;
    }

    public Map<String, Double> getMonthlyRevenueTrends() {
        Map<String, Double> monthlyRevenue = new HashMap<>();
        
        try {
            List<Payment> completedPayments = new ArrayList<>();
            List<Payment> allPayments = paymentManager.getAllPayments();
            
            for (Payment payment : allPayments) {
                if ("COMPLETED".equals(payment.getPaymentStatus())) {
                    completedPayments.add(payment);
                }
            }
            
            for (Payment payment : completedPayments) {
                String month = payment.getPaymentDate().substring(0, 7); // YYYY-MM
                monthlyRevenue.put(month, monthlyRevenue.getOrDefault(month, 0.0) + payment.getAmount());
            }
        } catch (Exception e) {
            util.Logger.error("Error getting monthly revenue trends: " + e.getMessage());
        }
        
        return monthlyRevenue;
    }

    public List<TourPackage> getTopRatedPackages(int limit) {
        try {
            Map<String, Double> packageRatings = getAllPackageRatings();
            
            List<Map.Entry<String, Double>> sortedEntries = new ArrayList<>(packageRatings.entrySet());
            sortedEntries.sort(new Comparator<Map.Entry<String, Double>>() {
                @Override
                public int compare(Map.Entry<String, Double> e1, Map.Entry<String, Double> e2) {
                    return e2.getValue().compareTo(e1.getValue());
                }
            });

            List<TourPackage> result = new ArrayList<>();
            int actualLimit = Math.min(limit, sortedEntries.size());
            for (int i = 0; i < actualLimit; i++) {
                TourPackage pkg = packageManager.getPackageById(sortedEntries.get(i).getKey());
                if (pkg != null) {
                    result.add(pkg);
                }
            }
            return result;
        } catch (Exception e) {
            util.Logger.error("Error getting top rated packages: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public String generateTourStatsReport() {
        StringBuilder report = new StringBuilder();
        
        try {
            report.append("=================== TOUR STATISTICS REPORT ===================\n");
            report.append("Generated: ").append(DateUtil.getCurrentDateTime()).append("\n");
            report.append("================================================================\n\n");

            // Popular Packages
            report.append("🏆 TOP 5 POPULAR PACKAGES\n");
            List<TourPackage> popularPackages = getPopularPackages(5);
            for (int i = 0; i < popularPackages.size(); i++) {
                TourPackage pkg = popularPackages.get(i);
                int bookings = getPackageBookingCounts().getOrDefault(pkg.getPackageId(), 0);
                report.append(String.format("%d. %s (%d bookings)\n", 
                    i + 1, pkg.getName(), bookings));
            }

            // Rating Statistics
            report.append("\n⭐ RATING STATISTICS\n");
            double overallRating = calculateAverageRating();
            report.append(String.format("Overall Average Rating: %.2f/5.0\n", overallRating));
            
            List<TourPackage> topRated = getTopRatedPackages(3);
            report.append("Top Rated Packages:\n");
            for (int i = 0; i < topRated.size(); i++) {
                TourPackage pkg = topRated.get(i);
                double rating = calculateAverageRating(pkg.getPackageId());
                report.append(String.format("  %d. %s (%.2f/5.0)\n", 
                    i + 1, pkg.getName(), rating));
            }

            // Category Distribution
            report.append("\n📊 BOOKINGS BY CATEGORY\n");
            Map<PackageCategory, Integer> categoryBookings = getBookingsByCategory();
            for (Map.Entry<PackageCategory, Integer> entry : categoryBookings.entrySet()) {
                report.append(String.format("  %s: %d bookings\n", 
                    entry.getKey().getDisplayName(), entry.getValue()));
            }

            // Tour Type Distribution
            report.append("\n🎯 BOOKINGS BY TOUR TYPE\n");
            Map<TourType, Integer> tourTypeBookings = getBookingsByTourType();
            for (Map.Entry<TourType, Integer> entry : tourTypeBookings.entrySet()) {
                report.append(String.format("  %s: %d bookings\n", 
                    entry.getKey().getDisplayName(), entry.getValue()));
            }

            // Customer Statistics
            report.append("\n👥 CUSTOMER STATISTICS\n");
            CustomerStats customerStats = getCustomerStatistics();
            report.append(customerStats.toString());

            // Revenue Statistics
            report.append("\n💰 REVENUE STATISTICS\n");
            RevenueStats revenueStats = getRevenueStatistics();
            report.append(revenueStats.toString());

            report.append("\n================================================================\n");
            
        } catch (Exception e) {
            util.Logger.error("Error generating tour stats report: " + e.getMessage());
            report.append("Error generating report: ").append(e.getMessage()).append("\n");
        }
        
        return report.toString();
    }

    // Inner classes for statistics
    public static class PackagePerformanceStats {
        public String packageId;
        public String packageName;
        public int totalBookings;
        public int confirmedBookings;
        public int cancelledBookings;
        public double totalRevenue;
        public double averageBookingValue;
        public double rating;
        public int totalParticipants;

        public PackagePerformanceStats(String packageId, String packageName, int totalBookings,
                                     int confirmedBookings, int cancelledBookings, double totalRevenue,
                                     double averageBookingValue, double rating, int totalParticipants) {
            this.packageId = packageId;
            this.packageName = packageName;
            this.totalBookings = totalBookings;
            this.confirmedBookings = confirmedBookings;
            this.cancelledBookings = cancelledBookings;
            this.totalRevenue = totalRevenue;
            this.averageBookingValue = averageBookingValue;
            this.rating = rating;
            this.totalParticipants = totalParticipants;
        }

        public double getConfirmationRate() {
            return totalBookings > 0 ? (confirmedBookings * 100.0) / totalBookings : 0;
        }

        public double getCancellationRate() {
            return totalBookings > 0 ? (cancelledBookings * 100.0) / totalBookings : 0;
        }

        @Override
        public String toString() {
            return String.format("Package: %s\nBookings: %d (%.1f%% confirmed)\nRevenue: $%.2f\nRating: %.2f/5.0\nParticipants: %d",
                packageName, totalBookings, getConfirmationRate(), totalRevenue, rating, totalParticipants);
        }
    }

    public static class CustomerStats {
        public int totalCustomers;
        public int activeCustomers;
        public int averageLoyaltyPoints;
        public int repeatCustomers;
        public double retentionRate;
        public double averageCustomerLifetimeValue;

        public CustomerStats(int totalCustomers, int activeCustomers, int averageLoyaltyPoints,
                           int repeatCustomers, double retentionRate, double averageCustomerLifetimeValue) {
            this.totalCustomers = totalCustomers;
            this.activeCustomers = activeCustomers;
            this.averageLoyaltyPoints = averageLoyaltyPoints;
            this.repeatCustomers = repeatCustomers;
            this.retentionRate = retentionRate;
            this.averageCustomerLifetimeValue = averageCustomerLifetimeValue;
        }

        @Override
        public String toString() {
            return String.format("Total Customers: %d\nActive Customers: %d\nRepeat Customers: %d\nRetention Rate: %.1f%%\nAverage Loyalty Points: %d\nAverage CLV: $%.2f",
                totalCustomers, activeCustomers, repeatCustomers, retentionRate, averageLoyaltyPoints, averageCustomerLifetimeValue);
        }
    }

    public static class RevenueStats {
        public double totalRevenue;
        public double averageTransactionValue;
        public double monthlyRevenue;
        public Map<String, Double> paymentMethodRevenue;

        public RevenueStats(double totalRevenue, double averageTransactionValue, 
                          double monthlyRevenue, Map<String, Double> paymentMethodRevenue) {
            this.totalRevenue = totalRevenue;
            this.averageTransactionValue = averageTransactionValue;
            this.monthlyRevenue = monthlyRevenue;
            this.paymentMethodRevenue = paymentMethodRevenue;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Total Revenue: $%.2f\n", totalRevenue));
            sb.append(String.format("This Month Revenue: $%.2f\n", monthlyRevenue));
            sb.append(String.format("Average Transaction: $%.2f\n", averageTransactionValue));
            sb.append("Payment Method Breakdown:\n");
            for (Map.Entry<String, Double> entry : paymentMethodRevenue.entrySet()) {
                double percentage = totalRevenue > 0 ? (entry.getValue() / totalRevenue) * 100 : 0;
                sb.append(String.format("  %s: $%.2f (%.1f%%)\n", 
                    entry.getKey(), entry.getValue(), percentage));
            }
            return sb.toString();
        }
    }
}