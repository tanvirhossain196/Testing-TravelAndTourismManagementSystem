package ui;

import util.InputHandler;
import util.IDGenerator;
import util.Logger;
import util.CurrencyFormatter;
import util.NotificationSender;
import manager.*;
import model.*;
import java.util.List;

public class PaymentUI {
    private user currentUser;
    private UserManager userManager;
    private PaymentManager paymentManager;
    private BookingManager bookingManager;

    public PaymentUI(user currentUser, UserManager userManager) {
        this.currentUser = currentUser;
        this.userManager = userManager;
        this.paymentManager = new PaymentManager();
        this.bookingManager = new BookingManager();
    }

    public void processPayment(Booking booking) {
        clearScreen();
        System.out.println("PAYMENT PROCESSING");
        System.out.println("=======================================================");
        
        System.out.println("Booking ID: " + booking.getBookingId());
        System.out.println("Amount to Pay: " + CurrencyFormatter.formatBDT(booking.getTotalAmount()));
        System.out.println();
        
        String paymentMethod = selectPaymentMethod();
        
        if (paymentMethod == null) {
            System.out.println("Payment cancelled.");
            return;
        }
        
        String paymentId = IDGenerator.generatePaymentId();
        Payment payment = new Payment(paymentId, booking.getBookingId(), 
            booking.getTotalAmount(), paymentMethod);
        
        // Process payment based on method
        boolean paymentSuccess = processPaymentByMethod(payment, paymentMethod);
        
        if (paymentSuccess) {
            payment.processPayment();
            paymentManager.addPayment(payment);
            
            // Update booking status
            booking.setPaid(true);
            booking.confirmBooking();
            bookingManager.updateBooking(booking);
            
            // Add loyalty points for tourists
            if (currentUser instanceof Tourist) {
                int loyaltyPoints = (int) (booking.getTotalAmount() / 100); // 1 point per $100
                ((Tourist) currentUser).addLoyaltyPoints(loyaltyPoints);
                userManager.updateUser(currentUser);
                System.out.println("You earned " + loyaltyPoints + " loyalty points!");
            }
            
            // Generate invoice
            generateInvoice(booking, payment);
            
            // Send confirmation
            NotificationSender.sendPaymentConfirmation(currentUser.getEmail(), 
                currentUser.getName(), paymentId, booking.getTotalAmount());
            
            System.out.println("Payment processed successfully!");
            System.out.println("Payment ID: " + paymentId);
            System.out.println("Your booking is now confirmed!");
            
            Logger.log("Payment processed: " + paymentId + " for booking " + booking.getBookingId());
            
        } else {
            payment.failPayment();
            paymentManager.addPayment(payment);
            
            System.out.println("Payment failed! Please try again.");
            System.out.println("Payment ID: " + paymentId + " (Failed)");
            
            Logger.log("Payment failed: " + paymentId);
        }
        
        InputHandler.pressEnterToContinue();
    }

    public void viewUserPayments() {
        clearScreen();
        System.out.println("MY PAYMENT HISTORY");
        System.out.println("=======================================================");
        
        List<Booking> userBookings = bookingManager.getBookingsByUser(currentUser.getId());
        List<Payment> userPayments = paymentManager.getPaymentsByUser(userBookings);
        
        if (userPayments.isEmpty()) {
            System.out.println("No payment history found.");
        } else {
            System.out.printf("%-12s %-12s %-10s %-15s %-12s%n",
                "PAYMENT ID", "BOOKING ID", "AMOUNT", "METHOD", "STATUS");
            System.out.println("-------------------------------------------------------");
            
            for (Payment payment : userPayments) {
                System.out.printf("%-12s %-12s $%-9.2f %-15s %-12s%n",
                    payment.getPaymentId(),
                    payment.getBookingId(),
                    payment.getAmount(),
                    payment.getPaymentMethod(),
                    payment.getPaymentStatus());
            }
            
            System.out.println("\nTotal Payments: " + userPayments.size());
            
            double totalPaid = 0.0;
            for (Payment payment : userPayments) {
                if ("COMPLETED".equals(payment.getPaymentStatus())) {
                    totalPaid += payment.getAmount();
                }
            }
            
            System.out.println("Total Amount Paid: " + CurrencyFormatter.formatBDT(totalPaid));
        }
        
        InputHandler.pressEnterToContinue();
    }

    private String selectPaymentMethod() {
        System.out.println("SELECT PAYMENT METHOD");
        System.out.println("=======================================================");
        System.out.println("1. Credit/Debit Card");
        System.out.println("2. Mobile Banking (bKash/Nagad)");
        System.out.println("3. Bank Transfer");
        System.out.println("4. Cash (Pay at Office)");
        System.out.println("5. Cancel Payment");
        System.out.println();
        
        int choice = InputHandler.getInt("Enter choice (1-5): ");
        
        switch (choice) {
            case 1:
                return "CARD";
            case 2:
                return "MOBILE_BANKING";
            case 3:
                return "BANK_TRANSFER";
            case 4:
                return "CASH";
            case 5:
                return null;
            default:
                System.out.println("Invalid choice!");
                return selectPaymentMethod();
        }
    }

    private boolean processPaymentByMethod(Payment payment, String method) {
        switch (method) {
            case "CARD":
                return processCardPayment(payment);
            case "MOBILE_BANKING":
                return processMobileBankingPayment(payment);
            case "BANK_TRANSFER":
                return processBankTransferPayment(payment);
            case "CASH":
                return processCashPayment(payment);
            default:
                return false;
        }
    }

    private boolean processCardPayment(Payment payment) {
        System.out.println("\nCARD PAYMENT");
        System.out.println("=======================================================");
        
        String cardNumber = InputHandler.getString("Enter card number (16 digits): ");
        if (cardNumber.length() != 16 || !cardNumber.matches("\\d+")) {
            System.out.println("Invalid card number! Must be 16 digits.");
            return false;
        }
        
        String expiryDate = InputHandler.getString("Enter expiry date (MM/YY): ");
        if (!expiryDate.matches("\\d{2}/\\d{2}")) {
            System.out.println("Invalid expiry date format! Use MM/YY.");
            return false;
        }
        
        String cvv = InputHandler.getString("Enter CVV (3 digits): ");
        if (cvv.length() != 3 || !cvv.matches("\\d+")) {
            System.out.println("Invalid CVV! Must be 3 digits.");
            return false;
        }
        
        String cardHolderName = InputHandler.getString("Enter cardholder name: ");
        if (cardHolderName.trim().length() < 2) {
            System.out.println("Invalid cardholder name!");
            return false;
        }
        
        // Store last 4 digits for security
        payment.setCardLastFour(cardNumber.substring(12));
        
        // Simulate payment processing
        System.out.println("Processing payment...");
        try {
            Thread.sleep(2000); // Simulate processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate success (90% success rate)
        boolean success = Math.random() < 0.9;
        
        if (success) {
            payment.setTransactionId("TXN" + IDGenerator.generateRandomString(10));
            System.out.println("Card payment successful!");
            System.out.println("Transaction ID: " + payment.getTransactionId());
        } else {
            System.out.println("Card payment failed! Please check your card details.");
        }
        
        return success;
    }

    private boolean processMobileBankingPayment(Payment payment) {
        System.out.println("\nMOBILE BANKING PAYMENT");
        System.out.println("=======================================================");
        
        System.out.println("Select your mobile banking service:");
        System.out.println("1. bKash");
        System.out.println("2. Nagad");
        System.out.println("3. Rocket");
        System.out.println();
        
        int choice = InputHandler.getInt("Enter choice (1-3): ");
        String service;
        
        switch (choice) {
            case 1: service = "bKash"; break;
            case 2: service = "Nagad"; break;
            case 3: service = "Rocket"; break;
            default:
                System.out.println("Invalid choice!");
                return false;
        }
        
        String mobileNumber = InputHandler.getString("Enter your " + service + " number: ");
        if (!mobileNumber.matches("\\+?880\\d{10}|\\d{11}")) {
            System.out.println("Invalid mobile number format!");
            return false;
        }
        
        String pin = InputHandler.getString("Enter your " + service + " PIN: ");
        if (pin.length() < 4 || pin.length() > 6 || !pin.matches("\\d+")) {
            System.out.println("Invalid PIN format!");
            return false;
        }
        
        payment.setBankName(service);
        
        // Simulate payment processing
        System.out.println("Sending payment request to " + service + "...");
        System.out.println("Please check your phone for confirmation.");
        
        try {
            Thread.sleep(3000); // Simulate processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate success (85% success rate)
        boolean success = Math.random() < 0.85;
        
        if (success) {
            payment.setTransactionId(service.toUpperCase() + IDGenerator.generateRandomString(8));
            System.out.println(service + " payment successful!");
            System.out.println("Transaction ID: " + payment.getTransactionId());
        } else {
            System.out.println(service + " payment failed! Please try again.");
        }
        
        return success;
    }

    private boolean processBankTransferPayment(Payment payment) {
        System.out.println("\nBANK TRANSFER PAYMENT");
        System.out.println("=======================================================");
        
        System.out.println("Please transfer the amount to our bank account:");
        System.out.println("-------------------------------------------------------");
        System.out.println("Bank: Dutch-Bangla Bank Limited");
        System.out.println("Account Name: TourBD Limited");
        System.out.println("Account Number: 1234567890");
        System.out.println("Routing Number: 090271234");
        System.out.println("Amount: " + CurrencyFormatter.formatBDT(payment.getAmount()));
        System.out.println("Reference: " + payment.getBookingId());
        System.out.println("-------------------------------------------------------");
        System.out.println();
        
        String transactionRef = InputHandler.getString("Enter bank transaction reference: ");
        if (transactionRef.trim().length() < 5) {
            System.out.println("Invalid transaction reference!");
            return false;
        }
        
        payment.setTransactionId(transactionRef);
        payment.setBankName("Manual Verification Required");
        
        System.out.println("Bank transfer details recorded!");
        System.out.println("Your payment will be verified within 24 hours.");
        
        return true; // Always return true for bank transfer (manual verification)
    }

    private boolean processCashPayment(Payment payment) {
        System.out.println("\nCASH PAYMENT");
        System.out.println("=======================================================");
        
        System.out.println("Please visit our office to make cash payment:");
        System.out.println("-------------------------------------------------------");
        System.out.println("Address: 123 Tourism Street, Gulshan-2");
        System.out.println("         Dhaka-1212, Bangladesh");
        System.out.println("Phone: +880-2-123456789");
        System.out.println("Office Hours: 9:00 AM - 6:00 PM (Saturday-Thursday)");
        System.out.println("Amount to Pay: " + CurrencyFormatter.formatBDT(payment.getAmount()));
        System.out.println("-------------------------------------------------------");
        System.out.println();
        System.out.println("Please bring this booking reference: " + payment.getBookingId());
        System.out.println("Please bring a valid photo ID");
        System.out.println();
        
        payment.setTransactionId("CASH_PENDING");
        
        System.out.println("Cash payment option selected!");
        System.out.println("Your booking is reserved for 48 hours.");
        
        return true; // Return true but payment status will be pending
    }

    public void viewPaymentMethods() {
        clearScreen();
        System.out.println("AVAILABLE PAYMENT METHODS");
        System.out.println("=======================================================");
        
        System.out.println("1. CREDIT/DEBIT CARDS");
        System.out.println("   - Visa, MasterCard, American Express");
        System.out.println("   - Instant processing");
        System.out.println("   - Secure SSL encryption");
        System.out.println("   - Processing fee: 2.5%");
        System.out.println();
        
        System.out.println("2. MOBILE BANKING");
        System.out.println("   - bKash, Nagad, Rocket");
        System.out.println("   - Quick and convenient");
        System.out.println("   - Available 24/7");
        System.out.println("   - Processing fee: 1.5%");
        System.out.println();
        
        System.out.println("3. BANK TRANSFER");
        System.out.println("   - Direct bank transfer");
        System.out.println("   - No processing fee");
        System.out.println("   - Manual verification required");
        System.out.println("   - Processing time: 24 hours");
        System.out.println();
        
        System.out.println("4. CASH PAYMENT");
        System.out.println("   - Pay at our office");
        System.out.println("   - No processing fee");
        System.out.println("   - Instant confirmation");
        System.out.println("   - Office hours only");
        System.out.println();
        
        InputHandler.pressEnterToContinue();
    }

    public void requestRefund(String paymentId) {
        clearScreen();
        System.out.println("REQUEST REFUND");
        System.out.println("=======================================================");
        
        Payment payment = paymentManager.getPaymentById(paymentId);
        if (payment == null) {
            System.out.println("Payment not found!");
            InputHandler.pressEnterToContinue();
            return;
        }
        
        if (!"COMPLETED".equals(payment.getPaymentStatus())) {
            System.out.println("Cannot refund incomplete payment!");
            InputHandler.pressEnterToContinue();
            return;
        }
        
        System.out.println("Payment Details:");
        System.out.println("Payment ID: " + payment.getPaymentId());
        System.out.println("Amount: " + CurrencyFormatter.formatBDT(payment.getAmount()));
        System.out.println("Method: " + payment.getPaymentMethod());
        System.out.println("Date: " + payment.getPaymentDate());
        System.out.println();
        
        String reason = InputHandler.getString("Reason for refund: ");
        
        if (InputHandler.getBoolean("Confirm refund request?")) {
            // Create refund request
            String refundId = IDGenerator.generateRefundId();
            
            System.out.println("Refund request submitted!");
            System.out.println("Refund ID: " + refundId);
            System.out.println("Processing time: 5-7 business days");
            System.out.println("You will receive email confirmation shortly.");
            
            Logger.log("Refund requested: " + refundId + " for payment " + paymentId + " - Reason: " + reason);
        } else {
            System.out.println("Refund request cancelled.");
        }
        
        InputHandler.pressEnterToContinue();
    }

    private void generateInvoice(Booking booking, Payment payment) {
        String invoiceId = IDGenerator.generateInvoiceId();
        Invoice invoice = new Invoice(invoiceId, booking.getBookingId(), currentUser.getId());
        
        // Add package as invoice item
        PackageManager packageManager = new PackageManager();
        TourPackage pkg = packageManager.getPackageById(booking.getPackageId());
        if (pkg != null) {
            invoice.addItem(pkg.getName() + " (" + pkg.getLocation() + ")", 
                pkg.getBasePrice(), booking.getNumberOfPeople());
        }
        
        // Add tax (5%)
        double taxAmount = booking.getTotalAmount() * 0.05;
        invoice.setTax(taxAmount);
        invoice.calculateTotal();
        
        System.out.println("\nINVOICE GENERATED");
        System.out.println("=======================================================");
        System.out.println(invoice.generateInvoiceText());
        
        Logger.log("Invoice generated: " + invoiceId + " for booking " + booking.getBookingId());
    }

    public void checkPaymentStatus(String paymentId) {
        clearScreen();
        System.out.println("PAYMENT STATUS");
        System.out.println("=======================================================");
        
        Payment payment = paymentManager.getPaymentById(paymentId);
        if (payment == null) {
            System.out.println("Payment not found with ID: " + paymentId);
        } else {
            System.out.println("Payment ID: " + payment.getPaymentId());
            System.out.println("Booking ID: " + payment.getBookingId());
            System.out.println("Amount: " + CurrencyFormatter.formatBDT(payment.getAmount()));
            System.out.println("Method: " + payment.getPaymentMethod());
            System.out.println("Status: " + payment.getPaymentStatus());
            System.out.println("Date: " + payment.getPaymentDate());
            
            if (payment.getTransactionId() != null) {
                System.out.println("Transaction ID: " + payment.getTransactionId());
            }
            
            if (payment.getCardLastFour() != null) {
                System.out.println("Card: ****-****-****-" + payment.getCardLastFour());
            }
            
            if (payment.getBankName() != null) {
                System.out.println("Bank/Service: " + payment.getBankName());
            }
        }
        
        InputHandler.pressEnterToContinue();
    }

    private void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}