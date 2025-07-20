package manager;

import model.TourGuide;
import schedule.GuideAssignment;
import schedule.GuideSchedule;
import util.Logger;
import util.FileHandler;
import util.IDGenerator;
import java.util.*;
import java.util.stream.Collectors;

public class GuideManager {
    private Map<String, TourGuide> guides;
    private Map<String, GuideSchedule> guideSchedules;
    private Map<String, List<GuideAssignment>> guideAssignments;
    private static final String GUIDES_FILE = "guides.dat";
    private static final String ASSIGNMENTS_FILE = "guide_assignments.dat";

    public GuideManager() {
        this.guides = new HashMap<>();
        this.guideSchedules = new HashMap<>();
        this.guideAssignments = new HashMap<>();
        loadGuidesFromFile();
        loadAssignmentsFromFile();
        initializeSchedules();
    }

    public void addGuide(TourGuide guide) {
        if (guide != null && !guides.containsKey(guide.getGuideId())) {
            guides.put(guide.getGuideId(), guide);
            guideSchedules.put(guide.getGuideId(), new GuideSchedule(guide.getGuideId(), guide.getName()));
            guideAssignments.put(guide.getGuideId(), new ArrayList<>());
            saveGuidesToFile();
            Logger.log("Tour guide added: " + guide.getName());
        }
    }

    public void removeGuide(String guideId) {
        TourGuide removed = guides.remove(guideId);
        if (removed != null) {
            guideSchedules.remove(guideId);
            guideAssignments.remove(guideId);
            saveGuidesToFile();
            Logger.log("Tour guide removed: " + removed.getName());
        }
    }

    public TourGuide getGuideById(String guideId) {
        return guides.get(guideId);
    }

    public void updateGuide(TourGuide guide) {
        if (guide != null && guides.containsKey(guide.getGuideId())) {
            guides.put(guide.getGuideId(), guide);
            saveGuidesToFile();
            Logger.log("Tour guide updated: " + guide.getName());
        }
    }

    public List<TourGuide> getAllGuides() {
        return new ArrayList<>(guides.values());
    }

    public List<TourGuide> getAvailableGuides() {
        return guides.values().stream()
                .filter(TourGuide::isAvailable)
                .collect(Collectors.toList());
    }

    public List<TourGuide> getGuidesByLanguage(String language) {
        return guides.values().stream()
                .filter(guide -> guide.getLanguages().contains(language))
                .collect(Collectors.toList());
    }

    public List<TourGuide> getGuidesByRating(double minRating) {
        return guides.values().stream()
                .filter(guide -> guide.getRating() >= minRating)
                .sorted(Comparator.comparing(TourGuide::getRating).reversed())
                .collect(Collectors.toList());
    }

    public List<TourGuide> getGuidesBySpecialization(String specialization) {
        return guides.values().stream()
                .filter(guide -> guide.getSpecialization() != null && 
                               guide.getSpecialization().toLowerCase().contains(specialization.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<TourGuide> searchGuides(String keyword) {
        return guides.values().stream()
                .filter(guide -> guide.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                               (guide.getSpecialization() != null && 
                                guide.getSpecialization().toLowerCase().contains(keyword.toLowerCase())) ||
                               guide.getLanguages().stream().anyMatch(lang -> 
                                   lang.toLowerCase().contains(keyword.toLowerCase())))
                .collect(Collectors.toList());
    }

    public GuideAssignment assignGuideToTour(String guideId, String bookingId, String packageId, 
                                           String date, String startTime, String endTime) {
        TourGuide guide = getGuideById(guideId);
        if (guide == null || !guide.isAvailable()) {
            return null;
        }

        GuideSchedule schedule = guideSchedules.get(guideId);
        if (schedule != null && !schedule.isAvailableForTimeSlot(date, startTime, endTime)) {
            return null;
        }

        GuideAssignment assignment = new GuideAssignment(guideId, guide.getName(), bookingId, packageId);
        assignment.setDate(date);
        assignment.setStartTime(startTime);
        assignment.setEndTime(endTime);
        assignment.setGuideRate(guide.getDailyRate());

        // Add to assignments
        guideAssignments.get(guideId).add(assignment);
        
        // Update guide schedule
        if (schedule != null) {
            schedule.addAssignment(assignment);
        }

        // Assign tour to guide
        guide.assignTour(packageId);
        updateGuide(guide);

        saveAssignmentsToFile();
        Logger.log("Guide assigned to tour: " + guide.getName() + " -> " + packageId);
        
        return assignment;
    }

    public boolean unassignGuideFromTour(String guideId, String assignmentId) {
        List<GuideAssignment> assignments = guideAssignments.get(guideId);
        if (assignments != null) {
            GuideAssignment assignment = assignments.stream()
                    .filter(a -> a.getAssignmentId().equals(assignmentId))
                    .findFirst()
                    .orElse(null);

            if (assignment != null) {
                assignments.remove(assignment);
                
                // Update guide schedule
                GuideSchedule schedule = guideSchedules.get(guideId);
                if (schedule != null) {
                    schedule.removeAssignment(assignmentId);
                }

                // Complete tour for guide
                TourGuide guide = getGuideById(guideId);
                if (guide != null) {
                    guide.completeTour(assignment.getPackageId());
                    updateGuide(guide);
                }

                saveAssignmentsToFile();
                Logger.log("Guide unassigned from tour: " + guideId + " -> " + assignmentId);
                return true;
            }
        }
        return false;
    }

    public List<GuideAssignment> getAssignmentsForGuide(String guideId) {
        return guideAssignments.getOrDefault(guideId, new ArrayList<>());
    }

    public List<GuideAssignment> getActiveAssignments(String guideId) {
        return getAssignmentsForGuide(guideId).stream()
                .filter(GuideAssignment::isActive)
                .collect(Collectors.toList());
    }

    public List<GuideAssignment> getCompletedAssignments(String guideId) {
        return getAssignmentsForGuide(guideId).stream()
                .filter(GuideAssignment::isCompleted)
                .collect(Collectors.toList());
    }

    public GuideSchedule getGuideSchedule(String guideId) {
        return guideSchedules.get(guideId);
    }

    public List<TourGuide> getAvailableGuidesForDate(String date) {
        return guides.values().stream()
                .filter(guide -> {
                    GuideSchedule schedule = guideSchedules.get(guide.getGuideId());
                    return schedule != null && schedule.isAvailableForDate(date);
                })
                .collect(Collectors.toList());
    }

    public TourGuide findBestGuideForTour(String packageName, String language, String date) {
        return guides.values().stream()
                .filter(guide -> guide.isAvailable())
                .filter(guide -> language == null || guide.getLanguages().contains(language))
                .filter(guide -> {
                    GuideSchedule schedule = guideSchedules.get(guide.getGuideId());
                    return schedule != null && schedule.isAvailableForDate(date);
                })
                .max(Comparator.comparing(TourGuide::getRating))
                .orElse(null);
    }

    public void updateGuideRating(String guideId, double newRating) {
        TourGuide guide = getGuideById(guideId);
        if (guide != null) {
            guide.updateRating(newRating);
            updateGuide(guide);
            Logger.log("Guide rating updated: " + guide.getName() + " -> " + guide.getRating());
        }
    }

    public void markGuideUnavailable(String guideId, String date, String reason) {
        GuideSchedule schedule = guideSchedules.get(guideId);
        if (schedule != null) {
            schedule.markUnavailable(date, reason);
            Logger.log("Guide marked unavailable: " + guideId + " on " + date);
        }
    }

    public void markGuideAvailable(String guideId, String date) {
        GuideSchedule schedule = guideSchedules.get(guideId);
        if (schedule != null) {
            schedule.markAvailable(date);
            Logger.log("Guide marked available: " + guideId + " on " + date);
        }
    }

    public int getTotalGuides() {
        return guides.size();
    }

    public int getAvailableGuidesCount() {
        return (int) guides.values().stream().filter(TourGuide::isAvailable).count();
    }

    public double getAverageGuideRating() {
        return guides.values().stream()
                .mapToDouble(TourGuide::getRating)
                .average()
                .orElse(0.0);
    }

    public TourGuide getTopRatedGuide() {
        return guides.values().stream()
                .max(Comparator.comparing(TourGuide::getRating))
                .orElse(null);
    }

    public Map<String, Long> getGuideCountByLanguage() {
        Map<String, Long> languageCount = new HashMap<>();
        for (TourGuide guide : guides.values()) {
            for (String language : guide.getLanguages()) {
                languageCount.put(language, languageCount.getOrDefault(language, 0L) + 1);
            }
        }
        return languageCount;
    }

    public double getTotalGuideEarnings(String guideId) {
        return getCompletedAssignments(guideId).stream()
                .mapToDouble(GuideAssignment::calculateTotalEarnings)
                .sum();
    }

    public Map<String, Double> getAllGuideEarnings() {
        Map<String, Double> earnings = new HashMap<>();
        for (String guideId : guides.keySet()) {
            earnings.put(guideId, getTotalGuideEarnings(guideId));
        }
        return earnings;
    }

    private void initializeSchedules() {
        for (TourGuide guide : guides.values()) {
            if (!guideSchedules.containsKey(guide.getGuideId())) {
                guideSchedules.put(guide.getGuideId(), new GuideSchedule(guide.getGuideId(), guide.getName()));
            }
            if (!guideAssignments.containsKey(guide.getGuideId())) {
                guideAssignments.put(guide.getGuideId(), new ArrayList<>());
            }
        }
    }

    private void loadGuidesFromFile() {
        try {
            List<String> lines = FileHandler.readFromFile(GUIDES_FILE);
            for (String line : lines) {
                TourGuide guide = parseGuideFromString(line);
                if (guide != null) {
                    guides.put(guide.getGuideId(), guide);
                }
            }
        } catch (Exception e) {
            Logger.error("Failed to load guides from file: " + e.getMessage());
        }
    }

    private void saveGuidesToFile() {
        try {
            FileHandler.clearFile(GUIDES_FILE);
            for (TourGuide guide : guides.values()) {
                String guideString = convertGuideToString(guide);
                FileHandler.writeToFile(GUIDES_FILE, guideString);
            }
        } catch (Exception e) {
            Logger.error("Failed to save guides to file: " + e.getMessage());
        }
    }

    private void loadAssignmentsFromFile() {
        try {
            List<String> lines = FileHandler.readFromFile(ASSIGNMENTS_FILE);
            for (String line : lines) {
                GuideAssignment assignment = parseAssignmentFromString(line);
                if (assignment != null) {
                    guideAssignments.computeIfAbsent(assignment.getGuideId(), k -> new ArrayList<>()).add(assignment);
                }
            }
        } catch (Exception e) {
            Logger.error("Failed to load assignments from file: " + e.getMessage());
        }
    }

    private void saveAssignmentsToFile() {
        try {
            FileHandler.clearFile(ASSIGNMENTS_FILE);
            for (List<GuideAssignment> assignments : guideAssignments.values()) {
                for (GuideAssignment assignment : assignments) {
                    String assignmentString = convertAssignmentToString(assignment);
                    FileHandler.writeToFile(ASSIGNMENTS_FILE, assignmentString);
                }
            }
        } catch (Exception e) {
            Logger.error("Failed to save assignments to file: " + e.getMessage());
        }
    }

    private TourGuide parseGuideFromString(String guideString) {
        try {
            String[] parts = guideString.split("\\|");
            if (parts.length >= 4) {
                TourGuide guide = new TourGuide(parts[0], parts[1], parts[2], parts[3]);
                if (parts.length > 4) {
                    guide.setDailyRate(Double.parseDouble(parts[4]));
                }
                if (parts.length > 5) {
                    guide.setRating(Double.parseDouble(parts[5]));
                }
                if (parts.length > 6) {
                    guide.setAvailable(Boolean.parseBoolean(parts[6]));
                }
                return guide;
            }
        } catch (Exception e) {
            Logger.error("Failed to parse guide: " + e.getMessage());
        }
        return null;
    }

    private String convertGuideToString(TourGuide guide) {
        return String.join("|",
            guide.getGuideId(), guide.getName(), guide.getPhone(), guide.getEmail(),
            String.valueOf(guide.getDailyRate()), String.valueOf(guide.getRating()),
            String.valueOf(guide.isAvailable()));
    }

    private GuideAssignment parseAssignmentFromString(String assignmentString) {
        try {
            String[] parts = assignmentString.split("\\|");
            if (parts.length >= 4) {
                GuideAssignment assignment = new GuideAssignment(parts[0], parts[1], parts[2], parts[3]);
                if (parts.length > 4) {
                    assignment.setDate(parts[4]);
                }
                if (parts.length > 5) {
                    assignment.setStartTime(parts[5]);
                }
                if (parts.length > 6) {
                    assignment.setEndTime(parts[6]);
                }
                if (parts.length > 7) {
                    assignment.setStatus(parts[7]);
                }
                return assignment;
            }
        } catch (Exception e) {
            Logger.error("Failed to parse assignment: " + e.getMessage());
        }
        return null;
    }

    private String convertAssignmentToString(GuideAssignment assignment) {
        return String.join("|",
            assignment.getGuideId(), assignment.getGuideName(), assignment.getBookingId(),
            assignment.getPackageId(), assignment.getDate(), assignment.getStartTime(),
            assignment.getEndTime(), assignment.getStatus());
    }
}
