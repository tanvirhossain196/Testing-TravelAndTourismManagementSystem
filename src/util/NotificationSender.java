package util;

public class NotificationSender {
    
    public static void sendWelcomeEmail(String email, String name) {
        System.out.println("=== EMAIL NOTIFICATION ===");
        System.out.println("To: " + email);
        System.out.println("Subject: Welcome to Travel Management System");
        System.out.println("Dear " + name + ",");
        System.out.println("Welcome to our Travel Management System!");
        System.out.println("Your account has been successfully created.");
        System.out.println("============================");
        Logger.log("Welcome email sent to: " + email);
    }

    public static void sendBookingConfirmation(String email, String name, String bookingId) {
        System.out.println("=== BOOKING CONFIRMATION ===");
        System.out.println("To: " + email);
        System.out.println("Subject: Booking Confirmation - " + bookingId);
        System.out.println("Dear " + name + ",");
        System.out.println("Your booking " + bookingId + " has been confirmed.");
        System.out.println("Thank you for choosing our services!");
        System.out.println("=============================");
        Logger.log("Booking confirmation sent to: " + email);
    }

    public static void sendPaymentConfirmation(String email, String name, String paymentId, double amount) {
        System.out.println("=== PAYMENT CONFIRMATION ===");
        System.out.println("To: " + email);
        System.out.println("Subject: Payment Received - " + paymentId);
        System.out.println("Dear " + name + ",");
        System.out.println("Payment of $" + amount + " has been received.");
        System.out.println("Payment ID: " + paymentId);
        System.out.println("=============================");
        Logger.log("Payment confirmation sent to: " + email);
    }

    public static void sendSMS(String phone, String message) {
        System.out.println("=== SMS NOTIFICATION ===");
        System.out.println("To: " + phone);
        System.out.println("Message: " + message);
        System.out.println("========================");
        Logger.log("SMS sent to: " + phone);
    }
}
