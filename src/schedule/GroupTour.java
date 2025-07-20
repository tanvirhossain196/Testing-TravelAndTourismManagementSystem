package schedule;

import util.DateUtil;
import util.IDGenerator;
import model.Booking;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class GroupTour {
    private String groupTourId;
    private String packageId;
    private String packageName;
    private String startDate;
    private String endDate;
    private int minGroupSize;
    private int maxGroupSize;
    private int currentSize;
    private List<String> bookingIds;
    private Map<String, String> participantDetails;
    private String groupLeaderId;
    private String guideId;
    private String status;
    private double groupDiscountRate;
    private String specialArrangements;
    private String meetingPoint;
    private String contactPerson;
    private String contactPhone;
    private String notes;
    private String createdDate;
    private String confirmedDate;

    public GroupTour(String packageId, String packageName, String startDate, String endDate) {
        this.groupTourId = IDGenerator.generateRandomString(10);
        this.packageId = packageId;
        this.packageName = packageName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.bookingIds = new ArrayList<>();
        this.participantDetails = new HashMap<>();
        this.status = "FORMING";
        this.currentSize = 0;
        this.minGroupSize = 5;
        this.maxGroupSize = 25;
        this.groupDiscountRate = 0.10; // 10% group discount
        this.createdDate = DateUtil.getCurrentDate();
    }

    public boolean addBooking(String bookingId, String userId, String userName, int numberOfPeople) {
        if (currentSize + numberOfPeople > maxGroupSize) {
            return false; // Group would exceed maximum size
        }
        
        if (!bookingIds.contains(bookingId)) {
            bookingIds.add(bookingId);
            participantDetails.put(bookingId, userName + " (" + numberOfPeople + " people)");
            currentSize += numberOfPeople;
            
            if (groupLeaderId == null) {
                groupLeaderId = userId; // First person becomes group leader
                contactPerson = userName;
            }
            
            checkGroupStatus();
            return true;
        }
        
        return false;
    }

    public boolean removeBooking(String bookingId) {
        if (bookingIds.contains(bookingId)) {
            String participantInfo = participantDetails.get(bookingId);
            bookingIds.remove(bookingId);
            participantDetails.remove(bookingId);
            
            // Extract number of people from participant info
            try {
                int peopleCount = extractPeopleCount(participantInfo);
                currentSize -= peopleCount;
            } catch (Exception e) {
                currentSize = Math.max(0, currentSize - 1); // Fallback
            }
            
            checkGroupStatus();
            return true;
        }
        
        return false;
    }

    public void assignGuide(String guideId) {
        this.guideId = guideId;
    }

    public void confirmGroup() {
        if (isGroupViable()) {
            this.status = "CONFIRMED";
            this.confirmedDate = DateUtil.getCurrentDate();
        }
    }

    public void startTour() {
        if ("CONFIRMED".equals(status)) {
            this.status = "IN_PROGRESS";
        }
    }

    public void completeTour() {
        this.status = "COMPLETED";
    }

    public void cancelGroup(String reason) {
        this.status = "CANCELLED";
        this.notes = (notes != null ? notes + "\n" : "") + "Cancelled: " + reason;
    }

    public boolean isGroupViable() {
        return currentSize >= minGroupSize;
    }

    public boolean isFull() {
        return currentSize >= maxGroupSize;
    }

    public int getAvailableSpots() {
        return maxGroupSize - currentSize;
    }

    public double calculateGroupDiscount(double originalAmount) {
        if (isGroupViable()) {
            return originalAmount * groupDiscountRate;
        }
        return 0.0;
    }

    public double calculateDiscountedAmount(double originalAmount) {
        return originalAmount - calculateGroupDiscount(originalAmount);
    }

    public List<String> getParticipantNames() {
        List<String> names = new ArrayList<>();
        for (String participantInfo : participantDetails.values()) {
            String name = participantInfo.split(" \\(")[0]; // Extract name before (
            names.add(name);
        }
        return names;
    }

    public void updateMeetingPoint(String meetingPoint) {
        this.meetingPoint = meetingPoint;
    }

    public void addSpecialArrangement(String arrangement) {
        if (specialArrangements == null || specialArrangements.trim().isEmpty()) {
            specialArrangements = arrangement;
        } else {
            specialArrangements += "\n" + arrangement;
        }
    }

    public void setContactPerson(String contactPerson, String contactPhone) {
        this.contactPerson = contactPerson;
        this.contactPhone = contactPhone;
    }

    public String generateGroupReport() {
        StringBuilder report = new StringBuilder();
        report.append("==================== GROUP TOUR REPORT ====================\n");
        report.append("Group Tour ID: ").append(groupTourId).append("\n");
        report.append("Package: ").append(packageName).append(" (").append(packageId).append(")\n");
        report.append("Period: ").append(startDate).append(" to ").append(endDate).append("\n");
        report.append("Status: ").append(status).append("\n");
        report.append("Group Size: ").append(currentSize).append("/").append(maxGroupSize).append("\n");
        report.append("Minimum Required: ").append(minGroupSize).append("\n");
        report.append("Group Viable: ").append(isGroupViable() ? "Yes" : "No").append("\n");
        report.append("Group Discount: ").append(groupDiscountRate * 100).append("%\n");
        
        if (contactPerson != null) {
            report.append("Contact Person: ").append(contactPerson);
            if (contactPhone != null) {
                report.append(" (").append(contactPhone).append(")");
            }
            report.append("\n");
        }
        
        if (guideId != null) {
            report.append("Assigned Guide: ").append(guideId).append("\n");
        }
        
        if (meetingPoint != null) {
            report.append("Meeting Point: ").append(meetingPoint).append("\n");
        }
        
        report.append("============================================================\n");
        
        if (bookingIds.isEmpty()) {
            report.append("No bookings in this group.\n");
        } else {
            report.append("PARTICIPANTS:\n");
            int counter = 1;
            for (Map.Entry<String, String> entry : participantDetails.entrySet()) {
                report.append(String.format("%d. %s (Booking: %s)\n", 
                    counter++, entry.getValue(), entry.getKey()));
            }
        }
        
        if (specialArrangements != null && !specialArrangements.trim().isEmpty()) {
            report.append("\nSPECIAL ARRANGEMENTS:\n");
            report.append(specialArrangements).append("\n");
        }
        
        if (notes != null && !notes.trim().isEmpty()) {
            report.append("\nNOTES:\n");
            report.append(notes).append("\n");
        }
        
        report.append("============================================================\n");
        return report.toString();
    }

    private void checkGroupStatus() {
        if ("FORMING".equals(status)) {
            if (isGroupViable()) {
                status = "READY_TO_CONFIRM";
            }
        } else if ("READY_TO_CONFIRM".equals(status)) {
            if (!isGroupViable()) {
                status = "FORMING";
            }
        }
    }

    private int extractPeopleCount(String participantInfo) {
        if (participantInfo != null && participantInfo.contains("(") && participantInfo.contains("people)")) {
            String countStr = participantInfo.substring(
                participantInfo.indexOf("(") + 1, 
                participantInfo.indexOf(" people)")
            );
            return Integer.parseInt(countStr);
        }
        return 1; // Default to 1 person
    }

    // Getters and Setters
    public String getGroupTourId() { return groupTourId; }
    public void setGroupTourId(String groupTourId) { this.groupTourId = groupTourId; }
    public String getPackageId() { return packageId; }
    public void setPackageId(String packageId) { this.packageId = packageId; }
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public int getMinGroupSize() { return minGroupSize; }
    public void setMinGroupSize(int minGroupSize) { this.minGroupSize = minGroupSize; }
    public int getMaxGroupSize() { return maxGroupSize; }
    public void setMaxGroupSize(int maxGroupSize) { this.maxGroupSize = maxGroupSize; }
    public int getCurrentSize() { return currentSize; }
    public List<String> getBookingIds() { return bookingIds; }
    public Map<String, String> getParticipantDetails() { return participantDetails; }
    public String getGroupLeaderId() { return groupLeaderId; }
    public void setGroupLeaderId(String groupLeaderId) { this.groupLeaderId = groupLeaderId; }
    public String getGuideId() { return guideId; }
    public void setGuideId(String guideId) { this.guideId = guideId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getGroupDiscountRate() { return groupDiscountRate; }
    public void setGroupDiscountRate(double groupDiscountRate) { this.groupDiscountRate = groupDiscountRate; }
    public String getSpecialArrangements() { return specialArrangements; }
    public void setSpecialArrangements(String specialArrangements) { this.specialArrangements = specialArrangements; }
    public String getMeetingPoint() { return meetingPoint; }
    public void setMeetingPoint(String meetingPoint) { this.meetingPoint = meetingPoint; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getCreatedDate() { return createdDate; }
    public String getConfirmedDate() { return confirmedDate; }

    @Override
    public String toString() {
        return "GroupTour{" +
                "groupTourId='" + groupTourId + '\'' +
                ", packageName='" + packageName + '\'' +
                ", currentSize=" + currentSize +
                ", maxSize=" + maxGroupSize +
                ", status='" + status + '\'' +
                ", startDate='" + startDate + '\'' +
                '}';
    }
}
