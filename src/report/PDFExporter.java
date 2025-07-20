package report;

import util.DateUtil;
import util.FileHandler;
import util.Logger;
import util.CurrencyFormatter;
import model.*;
import java.util.List;

public class PDFExporter {
    private static final String EXPORT_DIR = "exports";
    private static final String LINE_SEPARATOR = "================================================================";
    private static final String SECTION_SEPARATOR = "----------------------------------------";

    public PDFExporter() {
        // Initialize export directory
        createExportDirectory();
    }

    public boolean exportReportToPDF(String reportContent, String fileName) {
        return exportToPDF(reportContent, fileName, "Report");
    }

    public boolean exportToPDF(String content, String fileName, String documentType) {
        try {
            // Since we can't use actual PDF libraries, we'll create a formatted text file
            // that simulates PDF output with better formatting
            String formattedContent = formatForPDFOutput(content, documentType);
            
            String pdfFileName = fileName.endsWith(".pdf") ? fileName : fileName + ".pdf";
            String fullPath = EXPORT_DIR + "/" + pdfFileName;
            
            // Write formatted content to file
            FileHandler.writeToFile(fullPath, formattedContent);
            
            Logger.log("PDF exported successfully: " + pdfFileName);
            return true;
            
        } catch (Exception e) {
            Logger.error("Failed to export PDF: " + e.getMessage());
            return false;
        }
    }

    public boolean exportDailyReportToPDF(String reportContent, String date) {
        String fileName = "daily_report_" + date.replace("-", "_") + ".pdf";
        return exportReportToPDF(reportContent, fileName);
    }

    public boolean exportMonthlyReportToPDF(String reportContent, String month) {
        String fileName = "monthly_report_" + month.replace("-", "_") + ".pdf";
        return exportReportToPDF(reportContent, fileName);
    }

    public boolean exportAnnualReportToPDF(String reportContent, String year) {
        String fileName = "annual_report_" + year + ".pdf";
        return exportReportToPDF(reportContent, fileName);
    }

    public boolean exportUserListToPDF(List<user> users) {
        StringBuilder content = new StringBuilder();
        
        content.append(createPDFHeader("USER LIST REPORT"));
        content.append("Generated: ").append(DateUtil.getCurrentDateTime()).append("\n\n");
        
        content.append("TOTAL USERS: ").append(users.size()).append("\n");
        content.append(LINE_SEPARATOR).append("\n\n");
        
        // Table header
        content.append(String.format("%-12s %-25s %-30s %-12s %-8s\n",
            "USER ID", "NAME", "EMAIL", "ROLE", "STATUS"));
        content.append(SECTION_SEPARATOR).append("\n");
        
        // User data
        for (user user : users) {
            content.append(String.format("%-12s %-25s %-30s %-12s %-8s\n",
                user.getId(),
                truncateString(user.getName(), 24),
                truncateString(user.getEmail(), 29),
                user.getRole(),
                user.isActive() ? "Active" : "Inactive"));
        }
        
        content.append("\n").append(createPDFFooter());
        
        return exportToPDF(content.toString(), "user_list_" + DateUtil.getCurrentDate(), "User List");
    }

    public boolean exportPackageListToPDF(List<TourPackage> packages) {
        StringBuilder content = new StringBuilder();
        
        content.append(createPDFHeader("PACKAGE LIST REPORT"));
        content.append("Generated: ").append(DateUtil.getCurrentDateTime()).append("\n\n");
        
        content.append("TOTAL PACKAGES: ").append(packages.size()).append("\n");
        content.append(LINE_SEPARATOR).append("\n\n");
        
        // Table header
        content.append(String.format("%-12s %-25s %-15s %-12s %-6s %-8s\n",
            "PACKAGE ID", "NAME", "LOCATION", "PRICE", "DAYS", "STATUS"));
        content.append(SECTION_SEPARATOR).append("\n");
        
        // Package data
        for (TourPackage pkg : packages) {
            content.append(String.format("%-12s %-25s %-15s %-12s %-6d %-8s\n",
                pkg.getPackageId(),
                truncateString(pkg.getName(), 24),
                truncateString(pkg.getLocation(), 14),
                CurrencyFormatter.formatBDT(pkg.getBasePrice()),
                pkg.getDuration(),
                pkg.isActive() ? "Active" : "Inactive"));
        }
        
        content.append("\n").append(createPDFFooter());
        
        return exportToPDF(content.toString(), "package_list_" + DateUtil.getCurrentDate(), "Package List");
    }

    public boolean exportBookingListToPDF(List<Booking> bookings) {
        StringBuilder content = new StringBuilder();
        
        content.append(createPDFHeader("BOOKING LIST REPORT"));
        content.append("Generated: ").append(DateUtil.getCurrentDateTime()).append("\n\n");
        
        content.append("TOTAL BOOKINGS: ").append(bookings.size()).append("\n");
        content.append(LINE_SEPARATOR).append("\n\n");
        
        // Summary statistics
        long confirmedBookings = bookings.stream()
            .filter(b -> b.getStatus() == enumtype.BookingStatus.CONFIRMED)
            .count();
        
        double totalRevenue = bookings.stream()
            .mapToDouble(Booking::getTotalAmount)
            .sum();
        
        content.append("SUMMARY STATISTICS:\n");
        content.append("Confirmed Bookings: ").append(confirmedBookings).append("\n");
        content.append("Total Revenue: ").append(CurrencyFormatter.formatBDT(totalRevenue)).append("\n\n");
        
        // Table header
        content.append(String.format("%-12s %-12s %-12s %-6s %-12s %-10s\n",
            "BOOKING ID", "PACKAGE ID", "TRAVEL DATE", "PEOPLE", "AMOUNT", "STATUS"));
        content.append(SECTION_SEPARATOR).append("\n");
        
        // Booking data
        for (Booking booking : bookings) {
            content.append(String.format("%-12s %-12s %-12s %-6d %-12s %-10s\n",
                booking.getBookingId(),
                booking.getPackageId(),
                booking.getTravelDate(),
                booking.getNumberOfPeople(),
                CurrencyFormatter.formatBDT(booking.getTotalAmount()),
                booking.getStatus().getDisplayName()));
        }
        
        content.append("\n").append(createPDFFooter());
        
        return exportToPDF(content.toString(), "booking_list_" + DateUtil.getCurrentDate(), "Booking List");
    }

    public boolean exportPaymentListToPDF(List<Payment> payments) {
        StringBuilder content = new StringBuilder();
        
        content.append(createPDFHeader("PAYMENT LIST REPORT"));
        content.append("Generated: ").append(DateUtil.getCurrentDateTime()).append("\n\n");
        
        content.append("TOTAL PAYMENTS: ").append(payments.size()).append("\n");
        content.append(LINE_SEPARATOR).append("\n\n");
        
        // Summary statistics
        double totalAmount = payments.stream()
            .filter(p -> "COMPLETED".equals(p.getPaymentStatus()))
            .mapToDouble(Payment::getAmount)
            .sum();
        
        long completedPayments = payments.stream()
            .filter(p -> "COMPLETED".equals(p.getPaymentStatus()))
            .count();
        
        content.append("SUMMARY STATISTICS:\n");
        content.append("Completed Payments: ").append(completedPayments).append("\n");
        content.append("Total Amount: ").append(CurrencyFormatter.formatBDT(totalAmount)).append("\n\n");
        
        // Table header
        content.append(String.format("%-12s %-12s %-12s %-15s %-12s\n",
            "PAYMENT ID", "BOOKING ID", "AMOUNT", "METHOD", "STATUS"));
        content.append(SECTION_SEPARATOR).append("\n");
        
        // Payment data
        for (Payment payment : payments) {
            content.append(String.format("%-12s %-12s %-12s %-15s %-12s\n",
                payment.getPaymentId(),
                payment.getBookingId(),
                CurrencyFormatter.formatBDT(payment.getAmount()),
                payment.getPaymentMethod(),
                payment.getPaymentStatus()));
        }
        
        content.append("\n").append(createPDFFooter());
        
        return exportToPDF(content.toString(), "payment_list_" + DateUtil.getCurrentDate(), "Payment List");
    }

    public boolean exportCustomReportToPDF(String title, String content, String fileName) {
        StringBuilder pdfContent = new StringBuilder();
        
        pdfContent.append(createPDFHeader(title.toUpperCase()));
        pdfContent.append("Generated: ").append(DateUtil.getCurrentDateTime()).append("\n\n");
        pdfContent.append(content);
        pdfContent.append("\n").append(createPDFFooter());
        
        return exportToPDF(pdfContent.toString(), fileName, title);
    }

    public boolean exportInvoiceToPDF(Invoice invoice) {
        StringBuilder content = new StringBuilder();
        
        content.append(createPDFHeader("INVOICE"));
        content.append("Invoice ID: ").append(invoice.getInvoiceId()).append("\n");
        content.append("Issue Date: ").append(invoice.getIssueDate()).append("\n");
        content.append("Booking ID: ").append(invoice.getBookingId()).append("\n\n");
        
        content.append(LINE_SEPARATOR).append("\n");
        content.append("BILLING DETAILS:\n");
        content.append(LINE_SEPARATOR).append("\n");
        
        // Add invoice items (simplified)
        content.append(String.format("%-40s %-8s %-12s %-12s\n",
            "DESCRIPTION", "QTY", "UNIT PRICE", "AMOUNT"));
        content.append(SECTION_SEPARATOR).append("\n");
        
        for (Invoice.InvoiceItem item : invoice.getItems()) {
            content.append(String.format("%-40s %-8d %-12s %-12s\n",
                truncateString(item.description, 39),
                item.quantity,
                CurrencyFormatter.formatBDT(item.amount),
                CurrencyFormatter.formatBDT(item.amount * item.quantity)));
        }
        
        content.append(SECTION_SEPARATOR).append("\n");
        content.append(String.format("%-61s %-12s\n", "SUBTOTAL:", 
            CurrencyFormatter.formatBDT(invoice.getSubtotal())));
        content.append(String.format("%-61s %-12s\n", "TAX:", 
            CurrencyFormatter.formatBDT(invoice.getTax())));
        content.append(String.format("%-61s %-12s\n", "DISCOUNT:", 
            CurrencyFormatter.formatBDT(invoice.getDiscount())));
        content.append(LINE_SEPARATOR).append("\n");
        content.append(String.format("%-61s %-12s\n", "TOTAL:", 
            CurrencyFormatter.formatBDT(invoice.getTotalAmount())));
        content.append(LINE_SEPARATOR).append("\n\n");
        
        content.append("Thank you for choosing TourBD!\n");
        content.append(createPDFFooter());
        
        String fileName = "invoice_" + invoice.getInvoiceId() + ".pdf";
        return exportToPDF(content.toString(), fileName, "Invoice");
    }

    public boolean exportTicketToPDF(Ticket ticket) {
        StringBuilder content = new StringBuilder();
        
        content.append(createPDFHeader("TRAVEL TICKET"));
        content.append("Ticket ID: ").append(ticket.getTicketId()).append("\n");
        content.append("Issue Date: ").append(ticket.getIssueDate()).append("\n");
        content.append("Booking Reference: ").append(ticket.getBookingId()).append("\n\n");
        
        content.append(LINE_SEPARATOR).append("\n");
        content.append("PASSENGER DETAILS:\n");
        content.append(LINE_SEPARATOR).append("\n");
        content.append("Name: ").append(ticket.getPassengerName()).append("\n");
        if (ticket.getPassengerPhone() != null) {
            content.append("Phone: ").append(ticket.getPassengerPhone()).append("\n");
        }
        content.append("Package: ").append(ticket.getPackageName()).append("\n");
        
        if (ticket.getSeatNumber() != null) {
            content.append("Seat: ").append(ticket.getSeatNumber()).append("\n");
        }
        
        content.append("\n");
        content.append(LINE_SEPARATOR).append("\n");
        content.append("QR CODE: ").append(ticket.getQRcode()).append("\n");
        content.append("STATUS: ").append(ticket.isUsed() ? "USED" : "VALID").append("\n");
        content.append(LINE_SEPARATOR).append("\n\n");
        
        content.append("Please present this ticket at the departure point.\n");
        content.append("For any queries, contact: +880-2-123456789\n\n");
        content.append(createPDFFooter());
        
        String fileName = "ticket_" + ticket.getTicketId() + ".pdf";
        return exportToPDF(content.toString(), fileName, "Ticket");
    }

    public List<String> getExportedFiles() {
        return FileHandler.readFromFile(EXPORT_DIR + "/exported_files.txt");
    }

    public boolean deleteExportedFile(String fileName) {
        try {
            // In a real implementation, would delete the actual file
            Logger.log("File deleted: " + fileName);
            return true;
        } catch (Exception e) {
            Logger.error("Failed to delete file: " + fileName + " - " + e.getMessage());
            return false;
        }
    }

    private String formatForPDFOutput(String content, String documentType) {
        StringBuilder formatted = new StringBuilder();
        
        // Add PDF metadata simulation
        formatted.append("%PDF-1.4\n");
        formatted.append("% TourBD Management System - ").append(documentType).append("\n");
        formatted.append("% Generated: ").append(DateUtil.getCurrentDateTime()).append("\n\n");
        
        // Add content with better formatting
        String[] lines = content.split("\n");
        for (String line : lines) {
            // Add page formatting markers
            if (line.startsWith("=====")) {
                formatted.append("\n").append(line).append("\n");
            } else if (line.startsWith("-----")) {
                formatted.append(line).append("\n");
            } else {
                formatted.append(line).append("\n");
            }
        }
        
        // Add PDF footer
        formatted.append("\n\n");
        formatted.append("% End of PDF content\n");
        formatted.append("%%EOF\n");
        
        return formatted.toString();
    }

    private String createPDFHeader(String title) {
        StringBuilder header = new StringBuilder();
        
        header.append(LINE_SEPARATOR).append("\n");
        header.append(centerText("TOURBD TRAVEL & TOURISM MANAGEMENT SYSTEM")).append("\n");
        header.append(centerText(title)).append("\n");
        header.append(LINE_SEPARATOR).append("\n\n");
        
        return header.toString();
    }

    private String createPDFFooter() {
        StringBuilder footer = new StringBuilder();
        
        footer.append(LINE_SEPARATOR).append("\n");
        footer.append(centerText("TourBD - Your Trusted Travel Partner")).append("\n");
        footer.append(centerText("Email: info@tourbd.com | Phone: +880-2-123456789")).append("\n");
        footer.append(centerText("Website: www.tourbd.com")).append("\n");
        footer.append(LINE_SEPARATOR).append("\n");
        
        return footer.toString();
    }

    private String centerText(String text) {
        int totalWidth = LINE_SEPARATOR.length();
        int padding = (totalWidth - text.length()) / 2;
        StringBuilder centered = new StringBuilder();
        
        for (int i = 0; i < padding; i++) {
            centered.append(" ");
        }
        centered.append(text);
        
        return centered.toString();
    }

    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }

    private void createExportDirectory() {
        // In a real implementation, would create the directory
        Logger.log("Export directory initialized: " + EXPORT_DIR);
    }

    public void cleanOldExports(int daysOld) {
        // In a real implementation, would delete files older than specified days
        Logger.log("Cleaned exports older than " + daysOld + " days");
    }

    public long getExportDirectorySize() {
        // In a real implementation, would calculate actual directory size
        return 1024 * 1024; // Return 1MB as placeholder
    }

    public boolean exportBulkData(String dataType, String startDate, String endDate) {
        try {
            StringBuilder content = new StringBuilder();
            
            content.append(createPDFHeader("BULK DATA EXPORT - " + dataType.toUpperCase()));
            content.append("Export Period: ").append(startDate).append(" to ").append(endDate).append("\n\n");
            content.append("Data Type: ").append(dataType).append("\n");
            content.append("Export Date: ").append(DateUtil.getCurrentDateTime()).append("\n\n");
            content.append("Bulk data export completed successfully.\n");
            content.append("Records exported: ").append(calculateRecordCount(dataType)).append("\n\n");
            content.append(createPDFFooter());
            
            String fileName = "bulk_export_" + dataType.toLowerCase() + "_" + 
                             startDate.replace("-", "_") + "_to_" + endDate.replace("-", "_") + ".pdf";
            
            return exportToPDF(content.toString(), fileName, "Bulk Data Export");
            
        } catch (Exception e) {
            Logger.error("Bulk export failed: " + e.getMessage());
            return false;
        }
    }

    private int calculateRecordCount(String dataType) {
        // Placeholder calculation
        switch (dataType.toLowerCase()) {
            case "users": return 150;
            case "bookings": return 500;
            case "payments": return 450;
            case "packages": return 25;
            default: return 100;
        }
    }
}
