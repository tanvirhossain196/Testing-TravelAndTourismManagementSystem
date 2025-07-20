package manager;

import model.Payment;
import schedule.CancellationRequest;
import util.Logger;
import util.FileHandler;
import util.DateUtil;
import util.IDGenerator;
import java.util.*;
import java.util.stream.Collectors;

public class RefundManager {
    private Map<String, CancellationRequest> refundRequests;
    private PaymentManager paymentManager;
    private static final String REFUNDS_FILE = "refunds.dat";

    public RefundManager() {
        this.refundRequests = new HashMap<>();
        this.paymentManager = new PaymentManager();
        loadRefundsFromFile();
    }

    public RefundManager(PaymentManager paymentManager) {
        this.refundRequests = new HashMap<>();
        this.paymentManager = paymentManager;
        loadRefundsFromFile();
    }

    public CancellationRequest createRefundRequest(String bookingId, String userId, String reason, double originalAmount) {
        String requestId = IDGenerator.generateRandomString(12);
        CancellationRequest request = new CancellationRequest(bookingId, userId, reason);
        request.setOriginalAmount(originalAmount);
        
        refundRequests.put(requestId, request);
        saveRefundsToFile();
        
        Logger.log("Refund request created: " + requestId + " for booking " + bookingId);
        return request;
    }

    public void removeRefundRequest(String requestId) {
        CancellationRequest removed = refundRequests.remove(requestId);
        if (removed != null) {
            saveRefundsToFile();
            Logger.log("Refund request removed: " + requestId);
        }
    }

    public CancellationRequest getRefundRequestById(String requestId) {
        return refundRequests.get(requestId);
    }

    public List<CancellationRequest> getAllRefundRequests() {
        return new ArrayList<>(refundRequests.values());
    }

    public List<CancellationRequest> getPendingRefundRequests() {
        return refundRequests.values().stream()
                .filter(request -> "PENDING".equals(request.getStatus()))
                .collect(Collectors.toList());
    }

    public List<CancellationRequest> getApprovedRefundRequests() {
        return refundRequests.values().stream()
                .filter(request -> "APPROVED".equals(request.getStatus()))
                .collect(Collectors.toList());
    }

    public List<CancellationRequest> getRejectedRefundRequests() {
        return refundRequests.values().stream()
                .filter(request -> "REJECTED".equals(request.getStatus()))
                .collect(Collectors.toList());
    }

    public List<CancellationRequest> getRefundRequestsByUser(String userId) {
        return refundRequests.values().stream()
                .filter(request -> request.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<CancellationRequest> getRefundRequestsByBooking(String bookingId) {
        return refundRequests.values().stream()
                .filter(request -> request.getBookingId().equals(bookingId))
                .collect(Collectors.toList());
    }

    public boolean approveRefundRequest(String requestId, String processedBy) {
        CancellationRequest request = getRefundRequestById(requestId);
        if (request != null && "PENDING".equals(request.getStatus())) {
            request.approve(processedBy);
            updateRefundRequest(request);
            
            // Process the actual refund
            processRefund(request);
            
            Logger.log("Refund request approved: " + requestId + " by " + processedBy);
            return true;
        }
        return false;
    }

    public boolean rejectRefundRequest(String requestId, String processedBy, String reason) {
        CancellationRequest request = getRefundRequestById(requestId);
        if (request != null && "PENDING".equals(request.getStatus())) {
            request.reject(processedBy, reason);
            updateRefundRequest(request);
            
            Logger.log("Refund request rejected: " + requestId + " by " + processedBy + " - Reason: " + reason);
            return true;
        }
        return false;
    }

    public boolean processRefund(CancellationRequest request) {
        if (!"APPROVED".equals(request.getStatus())) {
            return false;
        }

        // Find the original payment
        List<Payment> payments = paymentManager.getPaymentsByBooking(request.getBookingId());
        Payment originalPayment = payments.stream()
                .filter(p -> "COMPLETED".equals(p.getPaymentStatus()))
                .findFirst()
                .orElse(null);

        if (originalPayment == null) {
            Logger.error("Original payment not found for refund request: " + request.getRequestId());
            return false;
        }

        // Determine refund method based on original payment method
        String refundMethod = determineRefundMethod(originalPayment.getPaymentMethod());
        request.processRefund(refundMethod);

        // Process the refund based on method
        boolean refundProcessed = processRefundByMethod(request, originalPayment);

        if (refundProcessed) {
            request.completeRefund();
            updateRefundRequest(request);
            
            // Update original payment status to refunded
            paymentManager.refundPayment(originalPayment.getPaymentId());
            
            Logger.log("Refund processed successfully: " + request.getRequestId() + 
                      " - Amount: $" + request.getRefundAmount());
            return true;
        }

        return false;
    }

    private boolean processRefundByMethod(CancellationRequest request, Payment originalPayment) {
        String refundMethod = request.getRefundMethod();
        
        switch (refundMethod) {
            case "ORIGINAL_PAYMENT_METHOD":
                return processOriginalMethodRefund(request, originalPayment);
            case "BANK_TRANSFER":
                return processBankTransferRefund(request);
            case "CASH":
                return processCashRefund(request);
            case "MOBILE_BANKING":
                return processMobileBankingRefund(request);
            default:
                Logger.error("Unknown refund method: " + refundMethod);
                return false;
        }
    }

    private boolean processOriginalMethodRefund(CancellationRequest request, Payment originalPayment) {
        // Simulate refund to original payment method
        System.out.println("Processing refund to original " + originalPayment.getPaymentMethod());
        System.out.println("Refund amount: $" + request.getRefundAmount());
        
        if (originalPayment.getCardLastFour() != null) {
            System.out.println("Refunding to card ending in: " + originalPayment.getCardLastFour());
        }
        
        // Simulate processing time
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // Ignore
        }
        
        return true; // Simulate successful refund
    }

    private boolean processBankTransferRefund(CancellationRequest request) {
        System.out.println("Processing bank transfer refund");
        System.out.println("Refund amount: $" + request.getRefundAmount());
        System.out.println("Bank transfer details will be sent to customer");
        return true;
    }

    private boolean processCashRefund(CancellationRequest request) {
        System.out.println("Processing cash refund");
        System.out.println("Refund amount: $" + request.getRefundAmount());
        System.out.println("Customer can collect cash from office");
        return true;
    }

    private boolean processMobileBankingRefund(CancellationRequest request) {
        System.out.println("Processing mobile banking refund");
        System.out.println("Refund amount: $" + request.getRefundAmount());
        System.out.println("Mobile banking refund will be processed");
        return true;
    }

    private String determineRefundMethod(String originalPaymentMethod) {
        switch (originalPaymentMethod) {
            case "CARD":
                return "ORIGINAL_PAYMENT_METHOD";
            case "MOBILE_BANKING":
                return "MOBILE_BANKING";
            case "BANK_TRANSFER":
                return "BANK_TRANSFER";
            case "CASH":
                return "CASH";
            default:
                return "BANK_TRANSFER"; // Default to bank transfer
        }
    }

    public void updateRefundRequest(CancellationRequest request) {
        if (request != null && refundRequests.containsKey(request.getRequestId())) {
            refundRequests.put(request.getRequestId(), request);
            saveRefundsToFile();
        }
    }

    public double getTotalRefundAmount() {
        return refundRequests.values().stream()
                .filter(request -> request.isProcessingComplete())
                .mapToDouble(CancellationRequest::getRefundAmount)
                .sum();
    }

    public double getTotalRefundAmountByDate(String date) {
        return refundRequests.values().stream()
                .filter(request -> request.isProcessingComplete() && 
                                 request.getRequestDate().startsWith(date))
                .mapToDouble(CancellationRequest::getRefundAmount)
                .sum();
    }

    public int getTotalRefundRequests() {
        return refundRequests.size();
    }

    public int getPendingRefundCount() {
        return (int) refundRequests.values().stream()
                .filter(request -> "PENDING".equals(request.getStatus()))
                .count();
    }

    public int getApprovedRefundCount() {
        return (int) refundRequests.values().stream()
                .filter(request -> "APPROVED".equals(request.getStatus()))
                .count();
    }

    public double getRefundApprovalRate() {
        int totalRequests = getTotalRefundRequests();
        if (totalRequests == 0) return 0.0;
        
        int approvedRequests = getApprovedRefundCount();
        return (double) approvedRequests / totalRequests * 100;
    }

    public Map<String, Integer> getRefundRequestsByStatus() {
        Map<String, Integer> statusCount = new HashMap<>();
        
        for (CancellationRequest request : refundRequests.values()) {
            String status = request.getStatus();
            statusCount.put(status, statusCount.getOrDefault(status, 0) + 1);
        }
        
        return statusCount;
    }

    public List<CancellationRequest> getEmergencyRefundRequests() {
        return refundRequests.values().stream()
                .filter(CancellationRequest::isEmergency)
                .collect(Collectors.toList());
    }

    public List<CancellationRequest> searchRefundRequests(String keyword) {
        return refundRequests.values().stream()
                .filter(request -> request.getRequestId().toLowerCase().contains(keyword.toLowerCase()) ||
                                 request.getBookingId().toLowerCase().contains(keyword.toLowerCase()) ||
                                 request.getUserId().toLowerCase().contains(keyword.toLowerCase()) ||
                                 request.getReason().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    private void loadRefundsFromFile() {
        try {
            List<String> lines = FileHandler.readFromFile(REFUNDS_FILE);
            for (String line : lines) {
                CancellationRequest request = parseRefundFromString(line);
                if (request != null) {
                    refundRequests.put(request.getRequestId(), request);
                }
            }
        } catch (Exception e) {
            Logger.error("Failed to load refunds from file: " + e.getMessage());
        }
    }

    private void saveRefundsToFile() {
        try {
            FileHandler.clearFile(REFUNDS_FILE);
            for (CancellationRequest request : refundRequests.values()) {
                String refundString = convertRefundToString(request);
                FileHandler.writeToFile(REFUNDS_FILE, refundString);
            }
        } catch (Exception e) {
            Logger.error("Failed to save refunds to file: " + e.getMessage());
        }
    }

    private CancellationRequest parseRefundFromString(String refundString) {
        try {
            String[] parts = refundString.split("\\|");
            if (parts.length >= 3) {
                CancellationRequest request = new CancellationRequest(parts[0], parts[1], parts[2]);
                if (parts.length > 3) {
                    request.setStatus(parts[3]);
                }
                if (parts.length > 4) {
                    request.setOriginalAmount(Double.parseDouble(parts[4]));
                }
                if (parts.length > 5) {
                    request.setRefundAmount(Double.parseDouble(parts[5]));
                }
                return request;
            }
        } catch (Exception e) {
            Logger.error("Failed to parse refund request: " + e.getMessage());
        }
        return null;
    }

    private String convertRefundToString(CancellationRequest request) {
        return String.join("|",
            request.getBookingId(), request.getUserId(), request.getReason(),
            request.getStatus(), String.valueOf(request.getOriginalAmount()),
            String.valueOf(request.getRefundAmount()));
    }
}
