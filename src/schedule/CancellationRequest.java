package schedule;

import util.DateUtil;
import util.IDGenerator;
import enumtype.BookingStatus;

public class CancellationRequest {
    private String requestId;
    private String bookingId;
    private String userId;
    private String userName;
    private String packageId;
    private String packageName;
    private String reason;
    private String requestDate;
    private String cancellationDate;
    private String status;
    private String processedBy;
    private String processedDate;
    private double refundAmount;
    private double cancellationFee;
    private double originalAmount;
    private String refundMethod;
    private String refundStatus;
    private String adminNotes;
    private int daysBeforeTravel;
    private boolean isEmergency;
    private String supportingDocuments;

    public CancellationRequest(String bookingId, String userId, String reason) {
        this.requestId = IDGenerator.generateRandomString(12);
        this.bookingId = bookingId;
        this.userId = userId;
        this.reason = reason;
        this.requestDate = DateUtil.getCurrentDateTime();
        this.status = "PENDING";
        this.refundStatus = "NOT_PROCESSED";
        this.isEmergency = false;
        this.cancellationFee = 0.0;
        this.refundAmount = 0.0;
    }

    public void approve(String processedBy) {
        this.status = "APPROVED";
        this.processedBy = processedBy;
        this.processedDate = DateUtil.getCurrentDateTime();
        calculateRefundAmount();
    }

    public void reject(String processedBy, String adminNotes) {
        this.status = "REJECTED";
        this.processedBy = processedBy;
        this.processedDate = DateUtil.getCurrentDateTime();
        this.adminNotes = adminNotes;
        this.refundAmount = 0.0;
    }

    public void processRefund(String refundMethod) {
        if ("APPROVED".equals(status)) {
            this.refundMethod = refundMethod;
            this.refundStatus = "PROCESSING";
        }
    }

    public void completeRefund() {
        if ("PROCESSING".equals(refundStatus)) {
            this.refundStatus = "COMPLETED";
        }
    }

    public void markAsEmergency(String supportingDocuments) {
        this.isEmergency = true;
        this.supportingDocuments = supportingDocuments;
        // Emergency requests might have reduced cancellation fees
        if (daysBeforeTravel < 7) {
            this.cancellationFee *= 0.5; // 50% reduction in emergency cases
        }
    }

    public void calculateRefundAmount() {
        if (originalAmount <= 0) {
            return; // Can't calculate without original amount
        }
        
        // Calculate cancellation fee based on days before travel
        double feePercentage = calculateCancellationFeePercentage();
        this.cancellationFee = originalAmount * feePercentage;
        this.refundAmount = originalAmount - cancellationFee;
        
        // Ensure refund amount is not negative
        if (refundAmount < 0) {
            refundAmount = 0;
        }
    }

    private double calculateCancellationFeePercentage() {
        if (isEmergency) {
            return 0.10; // 10% fee for emergency cancellations
        }
        
        if (daysBeforeTravel >= 30) {
            return 0.05; // 5% fee for 30+ days before
        } else if (daysBeforeTravel >= 15) {
            return 0.15; // 15% fee for 15-29 days before
        } else if (daysBeforeTravel >= 7) {
            return 0.25; // 25% fee for 7-14 days before
        } else if (daysBeforeTravel >= 3) {
            return 0.50; // 50% fee for 3-6 days before
        } else {
            return 0.75; // 75% fee for less than 3 days before
        }
    }

    public boolean isEligibleForRefund() {
        return "APPROVED".equals(status) && refundAmount > 0;
    }

    public boolean isProcessingComplete() {
        return "COMPLETED".equals(refundStatus);
    }

    public String getRefundPercentage() {
        if (originalAmount > 0) {
            double percentage = (refundAmount / originalAmount) * 100;
            return String.format("%.1f%%", percentage);
        }
        return "0%";
    }

    public String getCancellationFeePercentage() {
        if (originalAmount > 0) {
            double percentage = (cancellationFee / originalAmount) * 100;
            return String.format("%.1f%%", percentage);
        }
        return "0%";
    }

    public void updateDaysBeforeTravel(String travelDate) {
        try {
            this.daysBeforeTravel = (int) DateUtil.daysBetween(
                DateUtil.getCurrentDate(), travelDate);
        } catch (Exception e) {
            this.daysBeforeTravel = 0;
        }
    }

    public String generateCancellationReport() {
        StringBuilder report = new StringBuilder();
        report.append("================ CANCELLATION REQUEST REPORT ================\n");
        report.append("Request ID: ").append(requestId).append("\n");
        report.append("Booking ID: ").append(bookingId).append("\n");
        report.append("Customer: ").append(userName != null ? userName : userId).append("\n");
        report.append("Package: ").append(packageName != null ? packageName : packageId).append("\n");
        report.append("Request Date: ").append(requestDate).append("\n");
        report.append("Status: ").append(status).append("\n");
        report.append("Days Before Travel: ").append(daysBeforeTravel).append("\n");
        report.append("Emergency Request: ").append(isEmergency ? "Yes" : "No").append("\n");
        report.append("==============================================================\n");
        
        report.append("CANCELLATION REASON:\n");
        report.append(reason).append("\n\n");
        
        if (originalAmount > 0) {
            report.append("FINANCIAL DETAILS:\n");
            report.append("Original Amount: $").append(String.format("%.2f", originalAmount)).append("\n");
            report.append("Cancellation Fee: $").append(String.format("%.2f", cancellationFee))
                  .append(" (").append(getCancellationFeePercentage()).append(")\n");
            report.append("Refund Amount: $").append(String.format("%.2f", refundAmount))
                  .append(" (").append(getRefundPercentage()).append(")\n");
            report.append("Refund Status: ").append(refundStatus).append("\n");
            
            if (refundMethod != null) {
                report.append("Refund Method: ").append(refundMethod).append("\n");
            }
        }
        
        if (processedBy != null) {
            report.append("\nPROCESSING DETAILS:\n");
            report.append("Processed By: ").append(processedBy).append("\n");
            report.append("Processed Date: ").append(processedDate).append("\n");
            
            if (adminNotes != null && !adminNotes.trim().isEmpty()) {
                report.append("Admin Notes: ").append(adminNotes).append("\n");
            }
        }
        
        if (supportingDocuments != null && !supportingDocuments.trim().isEmpty()) {
            report.append("\nSupporting Documents: ").append(supportingDocuments).append("\n");
        }
        
        report.append("==============================================================\n");
        return report.toString();
    }

    // Getters and Setters
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getPackageId() { return packageId; }
    public void setPackageId(String packageId) { this.packageId = packageId; }
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getRequestDate() { return requestDate; }
    public String getCancellationDate() { return cancellationDate; }
    public void setCancellationDate(String cancellationDate) { this.cancellationDate = cancellationDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getProcessedBy() { return processedBy; }
    public void setProcessedBy(String processedBy) { this.processedBy = processedBy; }
    public String getProcessedDate() { return processedDate; }
    public double getRefundAmount() { return refundAmount; }
    public void setRefundAmount(double refundAmount) { this.refundAmount = refundAmount; }
    public double getCancellationFee() { return cancellationFee; }
    public void setCancellationFee(double cancellationFee) { this.cancellationFee = cancellationFee; }
    public double getOriginalAmount() { return originalAmount; }
    public void setOriginalAmount(double originalAmount) { this.originalAmount = originalAmount; }
    public String getRefundMethod() { return refundMethod; }
    public void setRefundMethod(String refundMethod) { this.refundMethod = refundMethod; }
    public String getRefundStatus() { return refundStatus; }
    public void setRefundStatus(String refundStatus) { this.refundStatus = refundStatus; }
    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
    public int getDaysBeforeTravel() { return daysBeforeTravel; }
    public void setDaysBeforeTravel(int daysBeforeTravel) { this.daysBeforeTravel = daysBeforeTravel; }
    public boolean isEmergency() { return isEmergency; }
    public void setEmergency(boolean emergency) { isEmergency = emergency; }
    public String getSupportingDocuments() { return supportingDocuments; }
    public void setSupportingDocuments(String supportingDocuments) { this.supportingDocuments = supportingDocuments; }

    @Override
    public String toString() {
        return "CancellationRequest{" +
                "requestId='" + requestId + '\'' +
                ", bookingId='" + bookingId + '\'' +
                ", status='" + status + '\'' +
                ", refundAmount=" + refundAmount +
                ", requestDate='" + requestDate + '\'' +
                ", emergency=" + isEmergency +
                '}';
    }
}
