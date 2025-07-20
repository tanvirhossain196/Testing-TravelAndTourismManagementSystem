package schedule;

import util.DateUtil;
import util.IDGenerator;

public class GuideAssignment {
    private String assignmentId;
    private String guideId;
    private String guideName;
    private String bookingId;
    private String packageId;
    private String packageName;
    private String userId;
    private String userName;
    private String date;
    private String startTime;
    private String endTime;
    private String meetingPoint;
    private String specialInstructions;
    private String status;
    private double guideRate;
    private String assignedDate;
    private String assignedBy;
    private String completedDate;
    private String cancellationReason;
    private int numberOfTourists;

    public GuideAssignment(String guideId, String guideName, String bookingId, String packageId) {
        this.assignmentId = IDGenerator.generateRandomString(12);
        this.guideId = guideId;
        this.guideName = guideName;
        this.bookingId = bookingId;
        this.packageId = packageId;
        this.status = "ASSIGNED";
        this.assignedDate = DateUtil.getCurrentDateTime();
        this.guideRate = 0.0;
        this.numberOfTourists = 1;
    }

    public void confirm() {
        this.status = "CONFIRMED";
    }

    public void start() {
        this.status = "IN_PROGRESS";
    }

    public void complete() {
        this.status = "COMPLETED";
        this.completedDate = DateUtil.getCurrentDateTime();
    }

    public void cancel(String reason) {
        this.status = "CANCELLED";
        this.cancellationReason = reason;
    }

    public void reassign(String newGuideId, String newGuideName) {
        this.guideId = newGuideId;
        this.guideName = newGuideName;
        this.status = "REASSIGNED";
        this.assignedDate = DateUtil.getCurrentDateTime();
    }

    public boolean isActive() {
        return "ASSIGNED".equals(status) || "CONFIRMED".equals(status) || "IN_PROGRESS".equals(status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }

    public double calculateTotalEarnings() {
        if ("COMPLETED".equals(status)) {
            return guideRate * numberOfTourists;
        }
        return 0.0;
    }

    public String getDurationInHours() {
        if (startTime != null && endTime != null) {
            // Simple calculation - in real implementation, would use proper time parsing
            try {
                String[] start = startTime.split(":");
                String[] end = endTime.split(":");
                int startMinutes = Integer.parseInt(start[0]) * 60 + Integer.parseInt(start[1]);
                int endMinutes = Integer.parseInt(end[0]) * 60 + Integer.parseInt(end[1]);
                int durationMinutes = endMinutes - startMinutes;
                return String.format("%.1f hours", durationMinutes / 60.0);
            } catch (Exception e) {
                return "Unknown";
            }
        }
        return "Not set";
    }

    public boolean hasTimeConflictWith(GuideAssignment other) {
        if (!this.date.equals(other.date)) {
            return false;
        }
        
        // Simple time conflict check
        return !(this.endTime.compareTo(other.startTime) <= 0 || 
                this.startTime.compareTo(other.endTime) >= 0);
    }

    public String generateAssignmentDetails() {
        StringBuilder details = new StringBuilder();
        details.append("================ GUIDE ASSIGNMENT DETAILS ================\n");
        details.append("Assignment ID: ").append(assignmentId).append("\n");
        details.append("Guide: ").append(guideName).append(" (").append(guideId).append(")\n");
        details.append("Booking ID: ").append(bookingId).append("\n");
        details.append("Package: ").append(packageName != null ? packageName : packageId).append("\n");
        details.append("Tourist: ").append(userName != null ? userName : userId).append("\n");
        details.append("Date: ").append(date).append("\n");
        details.append("Time: ").append(startTime).append(" - ").append(endTime).append("\n");
        details.append("Duration: ").append(getDurationInHours()).append("\n");
        details.append("Meeting Point: ").append(meetingPoint != null ? meetingPoint : "TBD").append("\n");
        details.append("Number of Tourists: ").append(numberOfTourists).append("\n");
        details.append("Guide Rate: $").append(guideRate).append("\n");
        details.append("Total Earnings: $").append(calculateTotalEarnings()).append("\n");
        details.append("Status: ").append(status).append("\n");
        details.append("Assigned Date: ").append(assignedDate).append("\n");
        
        if (specialInstructions != null && !specialInstructions.trim().isEmpty()) {
            details.append("Special Instructions:\n").append(specialInstructions).append("\n");
        }
        
        if (completedDate != null) {
            details.append("Completed Date: ").append(completedDate).append("\n");
        }
        
        if (cancellationReason != null) {
            details.append("Cancellation Reason: ").append(cancellationReason).append("\n");
        }
        
        details.append("===========================================================\n");
        return details.toString();
    }

    // Getters and Setters
    public String getAssignmentId() { return assignmentId; }
    public void setAssignmentId(String assignmentId) { this.assignmentId = assignmentId; }
    public String getGuideId() { return guideId; }
    public void setGuideId(String guideId) { this.guideId = guideId; }
    public String getGuideName() { return guideName; }
    public void setGuideName(String guideName) { this.guideName = guideName; }
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public String getPackageId() { return packageId; }
    public void setPackageId(String packageId) { this.packageId = packageId; }
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public String getMeetingPoint() { return meetingPoint; }
    public void setMeetingPoint(String meetingPoint) { this.meetingPoint = meetingPoint; }
    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getGuideRate() { return guideRate; }
    public void setGuideRate(double guideRate) { this.guideRate = guideRate; }
    public String getAssignedDate() { return assignedDate; }
    public void setAssignedDate(String assignedDate) { this.assignedDate = assignedDate; }
    public String getAssignedBy() { return assignedBy; }
    public void setAssignedBy(String assignedBy) { this.assignedBy = assignedBy; }
    public String getCompletedDate() { return completedDate; }
    public String getCancellationReason() { return cancellationReason; }
    public int getNumberOfTourists() { return numberOfTourists; }
    public void setNumberOfTourists(int numberOfTourists) { this.numberOfTourists = numberOfTourists; }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s to %s (Guide: %s, Status: %s)", 
            date, packageName != null ? packageName : packageId, 
            startTime, endTime, guideName, status);
    }
}
