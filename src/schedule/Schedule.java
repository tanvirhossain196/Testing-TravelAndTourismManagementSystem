package schedule;

import util.DateUtil;
import util.IDGenerator;
import enumtype.BookingStatus;
import java.util.List;
import java.util.ArrayList;

public class Schedule {
    private String scheduleId;
    private String bookingId;
    private String userId;
    private String packageId;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String status;
    private List<ScheduleItem> scheduleItems;
    private String notes;
    private boolean isConfirmed;
    private String createdDate;
    private String lastModified;

    public Schedule(String scheduleId, String bookingId, String userId, String packageId) {
        this.scheduleId = scheduleId;
        this.bookingId = bookingId;
        this.userId = userId;
        this.packageId = packageId;
        this.scheduleItems = new ArrayList<>();
        this.status = "DRAFT";
        this.isConfirmed = false;
        this.createdDate = DateUtil.getCurrentDateTime();
        this.lastModified = DateUtil.getCurrentDateTime();
    }

    public void addScheduleItem(String title, String description, String date, String time, String location) {
        ScheduleItem item = new ScheduleItem(
            IDGenerator.generateRandomString(8),
            title, description, date, time, location
        );
        scheduleItems.add(item);
        updateLastModified();
    }

    public void removeScheduleItem(String itemId) {
        scheduleItems.removeIf(item -> item.getItemId().equals(itemId));
        updateLastModified();
    }

    public void confirmSchedule() {
        this.isConfirmed = true;
        this.status = "CONFIRMED";
        updateLastModified();
    }

    public void cancelSchedule() {
        this.status = "CANCELLED";
        updateLastModified();
    }

    public void completeSchedule() {
        this.status = "COMPLETED";
        updateLastModified();
    }

    public void updateScheduleItem(String itemId, String title, String description, String date, String time, String location) {
        for (ScheduleItem item : scheduleItems) {
            if (item.getItemId().equals(itemId)) {
                item.setTitle(title);
                item.setDescription(description);
                item.setDate(date);
                item.setTime(time);
                item.setLocation(location);
                updateLastModified();
                break;
            }
        }
    }

    public List<ScheduleItem> getScheduleItemsByDate(String date) {
        List<ScheduleItem> itemsForDate = new ArrayList<>();
        for (ScheduleItem item : scheduleItems) {
            if (item.getDate().equals(date)) {
                itemsForDate.add(item);
            }
        }
        return itemsForDate;
    }

    public boolean hasConflictWithTime(String date, String startTime, String endTime) {
        for (ScheduleItem item : scheduleItems) {
            if (item.getDate().equals(date)) {
                // Simple time conflict check (would need proper time parsing in real implementation)
                if (item.getTime().equals(startTime)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String generateScheduleReport() {
        StringBuilder report = new StringBuilder();
        report.append("=================== SCHEDULE REPORT ===================\n");
        report.append("Schedule ID: ").append(scheduleId).append("\n");
        report.append("Booking ID: ").append(bookingId).append("\n");
        report.append("Package ID: ").append(packageId).append("\n");
        report.append("Period: ").append(startDate).append(" to ").append(endDate).append("\n");
        report.append("Status: ").append(status).append("\n");
        report.append("Confirmed: ").append(isConfirmed ? "Yes" : "No").append("\n");
        report.append("========================================================\n");
        
        if (scheduleItems.isEmpty()) {
            report.append("No schedule items found.\n");
        } else {
            report.append("SCHEDULE ITEMS:\n");
            for (int i = 0; i < scheduleItems.size(); i++) {
                ScheduleItem item = scheduleItems.get(i);
                report.append(String.format("%d. %s\n", i + 1, item.toString()));
            }
        }
        
        report.append("========================================================\n");
        return report.toString();
    }

    private void updateLastModified() {
        this.lastModified = DateUtil.getCurrentDateTime();
    }

    // Inner class for schedule items
    public static class ScheduleItem {
        private String itemId;
        private String title;
        private String description;
        private String date;
        private String time;
        private String location;
        private boolean isCompleted;

        public ScheduleItem(String itemId, String title, String description, String date, String time, String location) {
            this.itemId = itemId;
            this.title = title;
            this.description = description;
            this.date = date;
            this.time = time;
            this.location = location;
            this.isCompleted = false;
        }

        // Getters and Setters
        public String getItemId() { return itemId; }
        public void setItemId(String itemId) { this.itemId = itemId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public boolean isCompleted() { return isCompleted; }
        public void setCompleted(boolean completed) { isCompleted = completed; }

        @Override
        public String toString() {
            return String.format("[%s] %s at %s, %s - %s (%s)",
                date, title, time, location, description, isCompleted ? "Completed" : "Pending");
        }
    }

    // Getters and Setters
    public String getScheduleId() { return scheduleId; }
    public void setScheduleId(String scheduleId) { this.scheduleId = scheduleId; }
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getPackageId() { return packageId; }
    public void setPackageId(String packageId) { this.packageId = packageId; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<ScheduleItem> getScheduleItems() { return scheduleItems; }
    public void setScheduleItems(List<ScheduleItem> scheduleItems) { this.scheduleItems = scheduleItems; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public boolean isConfirmed() { return isConfirmed; }
    public void setConfirmed(boolean confirmed) { isConfirmed = confirmed; }
    public String getCreatedDate() { return createdDate; }
    public String getLastModified() { return lastModified; }

    @Override
    public String toString() {
        return "Schedule{" +
                "scheduleId='" + scheduleId + '\'' +
                ", bookingId='" + bookingId + '\'' +
                ", status='" + status + '\'' +
                ", confirmed=" + isConfirmed +
                ", items=" + scheduleItems.size() +
                '}';
    }
}
