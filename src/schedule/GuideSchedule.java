package schedule;

import util.DateUtil;
import util.IDGenerator;
import model.TourGuide;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class GuideSchedule {
    private String scheduleId;
    private String guideId;
    private String guideName;
    private Map<String, List<GuideAssignment>> dailyAssignments;
    private List<String> unavailableDates;
    private String workingHoursStart;
    private String workingHoursEnd;
    private int maxToursPerDay;
    private String notes;
    private String createdDate;

    public GuideSchedule(String guideId, String guideName) {
        this.scheduleId = IDGenerator.generateRandomString(10);
        this.guideId = guideId;
        this.guideName = guideName;
        this.dailyAssignments = new HashMap<>();
        this.unavailableDates = new ArrayList<>();
        this.workingHoursStart = "08:00";
        this.workingHoursEnd = "18:00";
        this.maxToursPerDay = 2;
        this.createdDate = DateUtil.getCurrentDate();
    }

    public boolean isAvailableForDate(String date) {
        if (unavailableDates.contains(date)) {
            return false;
        }
        
        List<GuideAssignment> assignments = dailyAssignments.get(date);
        if (assignments == null) {
            return true;
        }
        
        return assignments.size() < maxToursPerDay;
    }

    public boolean isAvailableForTimeSlot(String date, String startTime, String endTime) {
        if (!isAvailableForDate(date)) {
            return false;
        }
        
        // Check if time is within working hours
        if (!isWithinWorkingHours(startTime, endTime)) {
            return false;
        }
        
        List<GuideAssignment> assignments = dailyAssignments.get(date);
        if (assignments == null) {
            return true;
        }
        
        // Check for time conflicts
        for (GuideAssignment assignment : assignments) {
            if (hasTimeConflict(startTime, endTime, assignment.getStartTime(), assignment.getEndTime())) {
                return false;
            }
        }
        
        return true;
    }

    public void addAssignment(GuideAssignment assignment) {
        String date = assignment.getDate();
        dailyAssignments.computeIfAbsent(date, k -> new ArrayList<>()).add(assignment);
    }

    public void removeAssignment(String assignmentId) {
        for (List<GuideAssignment> assignments : dailyAssignments.values()) {
            assignments.removeIf(assignment -> assignment.getAssignmentId().equals(assignmentId));
        }
    }

    public void markUnavailable(String date, String reason) {
        if (!unavailableDates.contains(date)) {
            unavailableDates.add(date);
        }
        
        // Cancel any existing assignments for this date
        List<GuideAssignment> assignments = dailyAssignments.get(date);
        if (assignments != null) {
            for (GuideAssignment assignment : assignments) {
                assignment.cancel("Guide unavailable: " + reason);
            }
        }
    }

    public void markAvailable(String date) {
        unavailableDates.remove(date);
    }

    public List<GuideAssignment> getAssignmentsForDate(String date) {
        return dailyAssignments.getOrDefault(date, new ArrayList<>());
    }

    public List<GuideAssignment> getAssignmentsForDateRange(String startDate, String endDate) {
        List<GuideAssignment> assignments = new ArrayList<>();
        
        for (Map.Entry<String, List<GuideAssignment>> entry : dailyAssignments.entrySet()) {
            String date = entry.getKey();
            if (isDateInRange(date, startDate, endDate)) {
                assignments.addAll(entry.getValue());
            }
        }
        
        return assignments;
    }

    public List<String> getAvailableDatesInRange(String startDate, String endDate) {
        List<String> availableDates = new ArrayList<>();
        
        // Simple implementation - in real scenario, would use proper date iteration
        String[] testDates = {startDate, endDate}; // Simplified for demo
        
        for (String date : testDates) {
            if (isAvailableForDate(date)) {
                availableDates.add(date);
            }
        }
        
        return availableDates;
    }

    public int getTotalAssignmentsForMonth(String month) {
        int count = 0;
        for (Map.Entry<String, List<GuideAssignment>> entry : dailyAssignments.entrySet()) {
            if (entry.getKey().startsWith(month)) { // Simple month check
                count += entry.getValue().size();
            }
        }
        return count;
    }

    public double getUtilizationRate(String startDate, String endDate) {
        int totalPossibleDays = calculateDaysBetween(startDate, endDate);
        int assignedDays = 0;
        
        for (Map.Entry<String, List<GuideAssignment>> entry : dailyAssignments.entrySet()) {
            String date = entry.getKey();
            if (isDateInRange(date, startDate, endDate) && !entry.getValue().isEmpty()) {
                assignedDays++;
            }
        }
        
        return totalPossibleDays > 0 ? (double) assignedDays / totalPossibleDays * 100 : 0.0;
    }

    public String generateScheduleReport(String startDate, String endDate) {
        StringBuilder report = new StringBuilder();
        report.append("================== GUIDE SCHEDULE REPORT ==================\n");
        report.append("Guide: ").append(guideName).append(" (").append(guideId).append(")\n");
        report.append("Period: ").append(startDate).append(" to ").append(endDate).append("\n");
        report.append("Working Hours: ").append(workingHoursStart).append(" - ").append(workingHoursEnd).append("\n");
        report.append("Max Tours/Day: ").append(maxToursPerDay).append("\n");
        report.append("=============================================================\n");
        
        List<GuideAssignment> assignments = getAssignmentsForDateRange(startDate, endDate);
        
        if (assignments.isEmpty()) {
            report.append("No assignments in this period.\n");
        } else {
            report.append("ASSIGNMENTS:\n");
            for (int i = 0; i < assignments.size(); i++) {
                GuideAssignment assignment = assignments.get(i);
                report.append(String.format("%d. %s\n", i + 1, assignment.toString()));
            }
        }
        
        report.append("\nUTILIZATION: ").append(String.format("%.1f", getUtilizationRate(startDate, endDate))).append("%\n");
        
        if (!unavailableDates.isEmpty()) {
            report.append("\nUNAVAILABLE DATES:\n");
            for (String date : unavailableDates) {
                if (isDateInRange(date, startDate, endDate)) {
                    report.append("  - ").append(date).append("\n");
                }
            }
        }
        
        report.append("=============================================================\n");
        return report.toString();
    }

    private boolean isWithinWorkingHours(String startTime, String endTime) {
        // Simple time comparison - in real implementation, would use proper time parsing
        return startTime.compareTo(workingHoursStart) >= 0 && endTime.compareTo(workingHoursEnd) <= 0;
    }

    private boolean hasTimeConflict(String start1, String end1, String start2, String end2) {
        // Simple time conflict check - in real implementation, would use proper time parsing
        return !(end1.compareTo(start2) <= 0 || start1.compareTo(end2) >= 0);
    }

    private boolean isDateInRange(String date, String startDate, String endDate) {
        return date.compareTo(startDate) >= 0 && date.compareTo(endDate) <= 0;
    }

    private int calculateDaysBetween(String startDate, String endDate) {
        // Simplified - in real implementation, would use DateUtil
        return 7; // Placeholder
    }

    // Getters and Setters
    public String getScheduleId() { return scheduleId; }
    public void setScheduleId(String scheduleId) { this.scheduleId = scheduleId; }
    public String getGuideId() { return guideId; }
    public void setGuideId(String guideId) { this.guideId = guideId; }
    public String getGuideName() { return guideName; }
    public void setGuideName(String guideName) { this.guideName = guideName; }
    public Map<String, List<GuideAssignment>> getDailyAssignments() { return dailyAssignments; }
    public List<String> getUnavailableDates() { return unavailableDates; }
    public String getWorkingHoursStart() { return workingHoursStart; }
    public void setWorkingHoursStart(String workingHoursStart) { this.workingHoursStart = workingHoursStart; }
    public String getWorkingHoursEnd() { return workingHoursEnd; }
    public void setWorkingHoursEnd(String workingHoursEnd) { this.workingHoursEnd = workingHoursEnd; }
    public int getMaxToursPerDay() { return maxToursPerDay; }
    public void setMaxToursPerDay(int maxToursPerDay) { this.maxToursPerDay = maxToursPerDay; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getCreatedDate() { return createdDate; }

    @Override
    public String toString() {
        return "GuideSchedule{" +
                "guideId='" + guideId + '\'' +
                ", guideName='" + guideName + '\'' +
                ", totalAssignments=" + dailyAssignments.size() +
                ", unavailableDates=" + unavailableDates.size() +
                '}';
    }
}
