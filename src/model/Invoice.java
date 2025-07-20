package model;

import util.DateUtil;
import util.CurrencyFormatter;
import java.util.List;
import java.util.ArrayList;

public class Invoice {
    private String invoiceId;
    private String bookingId;
    private String userId;
    private double subtotal;
    private double tax;
    private double discount;
    private double totalAmount;
    private String issueDate;
    private String dueDate;
    private String status;
    private List<InvoiceItem> items;

    public Invoice(String invoiceId, String bookingId, String userId) {
        this.invoiceId = invoiceId;
        this.bookingId = bookingId;
        this.userId = userId;
        this.issueDate = DateUtil.getCurrentDate();
        this.status = "GENERATED";
        this.items = new ArrayList<>();
        this.tax = 0.0;
        this.discount = 0.0;
    }

    public void addItem(String description, double amount, int quantity) {
        items.add(new InvoiceItem(description, amount, quantity));
        calculateTotal();
    }

    public void calculateTotal() {
        subtotal = items.stream().mapToDouble(item -> item.amount * item.quantity).sum();
        totalAmount = subtotal + tax - discount;
    }

    public String generateInvoiceText() {
        StringBuilder invoice = new StringBuilder();
        invoice.append("================== INVOICE ==================\n");
        invoice.append("Invoice ID: ").append(invoiceId).append("\n");
        invoice.append("Booking ID: ").append(bookingId).append("\n");
        invoice.append("Issue Date: ").append(issueDate).append("\n");
        invoice.append("==============================================\n");
        invoice.append("ITEMS:\n");
        
        for (InvoiceItem item : items) {
            invoice.append(String.format("%-30s %2d x %10s = %10s\n", 
                item.description, item.quantity, 
                CurrencyFormatter.formatBDT(item.amount),
                CurrencyFormatter.formatBDT(item.amount * item.quantity)));
        }
        
        invoice.append("==============================================\n");
        invoice.append(String.format("Subtotal: %20s\n", CurrencyFormatter.formatBDT(subtotal)));
        invoice.append(String.format("Tax: %25s\n", CurrencyFormatter.formatBDT(tax)));
        invoice.append(String.format("Discount: %20s\n", CurrencyFormatter.formatBDT(discount)));
        invoice.append("==============================================\n");
        invoice.append(String.format("TOTAL: %23s\n", CurrencyFormatter.formatBDT(totalAmount)));
        invoice.append("==============================================\n");
        
        return invoice.toString();
    }

    // Inner class for invoice items
    public static class InvoiceItem {
        public String description;
        public double amount;
        public int quantity;

        public InvoiceItem(String description, double amount, int quantity) {
            this.description = description;
            this.amount = amount;
            this.quantity = quantity;
        }
    }

    // Getters and Setters
    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }
    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getIssueDate() { return issueDate; }
    public void setIssueDate(String issueDate) { this.issueDate = issueDate; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<InvoiceItem> getItems() { return items; }
}
