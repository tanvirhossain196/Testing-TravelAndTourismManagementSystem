package model;

import util.DateUtil;

public class Payment {
    private String paymentId;
    private String bookingId;
    private double amount;
    private String paymentMethod;
    private String paymentStatus;
    private String paymentDate;
    private String transactionId;
    private String currency;
    private double exchangeRate;
    private String bankName;
    private String cardLastFour;

    public Payment(String paymentId, String bookingId, double amount, String paymentMethod) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = "PENDING";
        this.paymentDate = DateUtil.getCurrentDateTime();
        this.currency = "BDT";
        this.exchangeRate = 1.0;
    }

    public void processPayment() {
        this.paymentStatus = "COMPLETED";
    }

    public void failPayment() {
        this.paymentStatus = "FAILED";
    }

    public void refundPayment() {
        this.paymentStatus = "REFUNDED";
    }

    // Getters and Setters
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public double getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(double exchangeRate) { this.exchangeRate = exchangeRate; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getCardLastFour() { return cardLastFour; }
    public void setCardLastFour(String cardLastFour) { this.cardLastFour = cardLastFour; }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", amount=" + amount +
                ", method='" + paymentMethod + '\'' +
                ", status='" + paymentStatus + '\'' +
                ", date='" + paymentDate + '\'' +
                '}';
    }
}
