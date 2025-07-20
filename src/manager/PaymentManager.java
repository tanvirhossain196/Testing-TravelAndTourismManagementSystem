package manager;

import model.Payment;
import model.Booking;
import util.Logger;
import util.FileHandler;
import util.DateUtil;
import java.util.*;
import java.util.stream.Collectors;

public class PaymentManager {
    private Map<String, Payment> payments;
    private static final String PAYMENTS_FILE = "payments.dat";

    public PaymentManager() {
        this.payments = new HashMap<>();
        loadPaymentsFromFile();
    }

    public void addPayment(Payment payment) {
        if (payment != null && !payments.containsKey(payment.getPaymentId())) {
            payments.put(payment.getPaymentId(), payment);
            savePaymentsToFile();
            Logger.log("Payment added: " + payment.getPaymentId() + " - Amount: $" + payment.getAmount());
        }
    }

    public void removePayment(String paymentId) {
        Payment removed = payments.remove(paymentId);
        if (removed != null) {
            savePaymentsToFile();
            Logger.log("Payment removed: " + paymentId);
        }
    }

    public Payment getPaymentById(String paymentId) {
        return payments.get(paymentId);
    }

    public void updatePayment(Payment payment) {
        if (payment != null && payments.containsKey(payment.getPaymentId())) {
            payments.put(payment.getPaymentId(), payment);
            savePaymentsToFile();
            Logger.log("Payment updated: " + payment.getPaymentId());
        }
    }

    public List<Payment> getAllPayments() {
        return new ArrayList<>(payments.values());
    }

    public List<Payment> getPaymentsByBooking(String bookingId) {
        return payments.values().stream()
                .filter(payment -> payment.getBookingId().equals(bookingId))
                .collect(Collectors.toList());
    }

    public List<Payment> getPaymentsByUser(List<Booking> userBookings) {
        Set<String> bookingIds = userBookings.stream()
                .map(Booking::getBookingId)
                .collect(Collectors.toSet());
        
        return payments.values().stream()
                .filter(payment -> bookingIds.contains(payment.getBookingId()))
                .collect(Collectors.toList());
    }

    public List<Payment> getPaymentsByStatus(String status) {
        return payments.values().stream()
                .filter(payment -> payment.getPaymentStatus().equals(status))
                .collect(Collectors.toList());
    }

    public List<Payment> getCompletedPayments() {
        return getPaymentsByStatus("COMPLETED");
    }

    public List<Payment> getPendingPayments() {
        return getPaymentsByStatus("PENDING");
    }

    public List<Payment> getFailedPayments() {
        return getPaymentsByStatus("FAILED");
    }

    public List<Payment> getRefundedPayments() {
        return getPaymentsByStatus("REFUNDED");
    }

    public List<Payment> getPaymentsByMethod(String paymentMethod) {
        return payments.values().stream()
                .filter(payment -> payment.getPaymentMethod().equals(paymentMethod))
                .collect(Collectors.toList());
    }

    public List<Payment> getPaymentsByDate(String date) {
        return payments.values().stream()
                .filter(payment -> payment.getPaymentDate().startsWith(date))
                .collect(Collectors.toList());
    }

    public List<Payment> getPaymentsInDateRange(String startDate, String endDate) {
        return payments.values().stream()
                .filter(payment -> {
                    String paymentDate = payment.getPaymentDate().substring(0, 10); // Extract date part
                    return paymentDate.compareTo(startDate) >= 0 && 
                           paymentDate.compareTo(endDate) <= 0;
                })
                .collect(Collectors.toList());
    }

    public List<Payment> getPaymentsByAmountRange(double minAmount, double maxAmount) {
        return payments.values().stream()
                .filter(payment -> payment.getAmount() >= minAmount && payment.getAmount() <= maxAmount)
                .collect(Collectors.toList());
    }

    public boolean processPayment(String paymentId) {
        Payment payment = getPaymentById(paymentId);
        if (payment != null && "PENDING".equals(payment.getPaymentStatus())) {
            payment.processPayment();
            updatePayment(payment);
            Logger.log("Payment processed: " + paymentId);
            return true;
        }
        return false;
    }

    public boolean failPayment(String paymentId, String reason) {
        Payment payment = getPaymentById(paymentId);
        if (payment != null && "PENDING".equals(payment.getPaymentStatus())) {
            payment.failPayment();
            updatePayment(payment);
            Logger.log("Payment failed: " + paymentId + " - Reason: " + reason);
            return true;
        }
        return false;
    }

    public boolean refundPayment(String paymentId) {
        Payment payment = getPaymentById(paymentId);
        if (payment != null && "COMPLETED".equals(payment.getPaymentStatus())) {
            payment.refundPayment();
            updatePayment(payment);
            Logger.log("Payment refunded: " + paymentId);
            return true;
        }
        return false;
    }

    public double getTotalRevenue() {
        return getCompletedPayments().stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public double getRevenueByDate(String date) {
        return getPaymentsByDate(date).stream()
                .filter(payment -> "COMPLETED".equals(payment.getPaymentStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public double getRevenueInDateRange(String startDate, String endDate) {
        return getPaymentsInDateRange(startDate, endDate).stream()
                .filter(payment -> "COMPLETED".equals(payment.getPaymentStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public double getRevenueByPaymentMethod(String paymentMethod) {
        return getPaymentsByMethod(paymentMethod).stream()
                .filter(payment -> "COMPLETED".equals(payment.getPaymentStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public int getTotalPayments() {
        return payments.size();
    }

    public int getPaymentCountByStatus(String status) {
        return (int) payments.values().stream()
                .filter(payment -> payment.getPaymentStatus().equals(status))
                .count();
    }

    public double getAveragePaymentAmount() {
        return payments.values().stream()
                .mapToDouble(Payment::getAmount)
                .average()
                .orElse(0.0);
    }

    public Payment getHighestPayment() {
        return payments.values().stream()
                .max(Comparator.comparing(Payment::getAmount))
                .orElse(null);
    }

    public Payment getLowestPayment() {
        return payments.values().stream()
                .min(Comparator.comparing(Payment::getAmount))
                .orElse(null);
    }

    public Map<String, Double> getRevenueByPaymentMethod() {
        Map<String, Double> revenueByMethod = new HashMap<>();
        
        for (Payment payment : getCompletedPayments()) {
            String method = payment.getPaymentMethod();
            revenueByMethod.put(method, revenueByMethod.getOrDefault(method, 0.0) + payment.getAmount());
        }
        
        return revenueByMethod;
    }

    public Map<String, Integer> getPaymentCountByMethod() {
        return payments.values().stream()
                .collect(Collectors.groupingBy(Payment::getPaymentMethod, 
                        Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)));
    }

    public Map<String, Double> getDailyRevenue() {
        Map<String, Double> dailyRevenue = new HashMap<>();
        
        for (Payment payment : getCompletedPayments()) {
            String date = payment.getPaymentDate().substring(0, 10); // Extract date part
            dailyRevenue.put(date, dailyRevenue.getOrDefault(date, 0.0) + payment.getAmount());
        }
        
        return dailyRevenue;
    }

    public Map<String, Double> getMonthlyRevenue() {
        Map<String, Double> monthlyRevenue = new HashMap<>();
        
        for (Payment payment : getCompletedPayments()) {
            String month = payment.getPaymentDate().substring(0, 7); // Extract YYYY-MM
            monthlyRevenue.put(month, monthlyRevenue.getOrDefault(month, 0.0) + payment.getAmount());
        }
        
        return monthlyRevenue;
    }

    public double getSuccessRate() {
        int totalPayments = getTotalPayments();
        if (totalPayments == 0) return 0.0;
        
        int successfulPayments = getPaymentCountByStatus("COMPLETED");
        return (double) successfulPayments / totalPayments * 100;
    }

    public double getFailureRate() {
        int totalPayments = getTotalPayments();
        if (totalPayments == 0) return 0.0;
        
        int failedPayments = getPaymentCountByStatus("FAILED");
        return (double) failedPayments / totalPayments * 100;
    }

    public List<Payment> getTodaysPayments() {
        String today = DateUtil.getCurrentDate();
        return getPaymentsByDate(today);
    }

    public double getTodaysRevenue() {
        String today = DateUtil.getCurrentDate();
        return getRevenueByDate(today);
    }

    public List<Payment> searchPayments(String keyword) {
        return payments.values().stream()
                .filter(payment -> payment.getPaymentId().toLowerCase().contains(keyword.toLowerCase()) ||
                                 payment.getBookingId().toLowerCase().contains(keyword.toLowerCase()) ||
                                 payment.getPaymentMethod().toLowerCase().contains(keyword.toLowerCase()) ||
                                 payment.getTransactionId().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    private void loadPaymentsFromFile() {
        try {
            List<String> lines = FileHandler.readFromFile(PAYMENTS_FILE);
            for (String line : lines) {
                Payment payment = parsePaymentFromString(line);
                if (payment != null) {
                    payments.put(payment.getPaymentId(), payment);
                }
            }
        } catch (Exception e) {
            Logger.error("Failed to load payments from file: " + e.getMessage());
        }
    }

    private void savePaymentsToFile() {
        try {
            FileHandler.clearFile(PAYMENTS_FILE);
            for (Payment payment : payments.values()) {
                String paymentString = convertPaymentToString(payment);
                FileHandler.writeToFile(PAYMENTS_FILE, paymentString);
            }
        } catch (Exception e) {
            Logger.error("Failed to save payments to file: " + e.getMessage());
        }
    }

    private Payment parsePaymentFromString(String paymentString) {
        try {
            String[] parts = paymentString.split("\\|");
            if (parts.length >= 4) {
                Payment payment = new Payment(parts[0], parts[1], Double.parseDouble(parts[2]), parts[3]);
                if (parts.length > 4) {
                    payment.setPaymentStatus(parts[4]);
                }
                if (parts.length > 5) {
                    payment.setTransactionId(parts[5]);
                }
                return payment;
            }
        } catch (Exception e) {
            Logger.error("Failed to parse payment: " + e.getMessage());
        }
        return null;
    }

    private String convertPaymentToString(Payment payment) {
        return String.join("|",
            payment.getPaymentId(), payment.getBookingId(), String.valueOf(payment.getAmount()),
            payment.getPaymentMethod(), payment.getPaymentStatus(),
            payment.getTransactionId() != null ? payment.getTransactionId() : "");
    }
}
