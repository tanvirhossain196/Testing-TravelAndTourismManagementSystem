package manager;

import model.*;
import enumtype.BookingStatus;
import util.Logger;
import util.FileHandler;
import util.DateUtil;
import java.util.*;
import java.util.stream.Collectors;

public class BookingManager {
    private Map<String, Booking> bookings;
    private static final String BOOKINGS_FILE = "bookings.dat";

    public BookingManager() {
        this.bookings = new HashMap<>();
        loadBookingsFromFile();
    }

    public void addBooking(Booking booking) {
        if (booking != null && !bookings.containsKey(booking.getBookingId())) {
            bookings.put(booking.getBookingId(), booking);
            saveBookingsToFile();
            Logger.log("Booking added: " + booking.getBookingId());
        }
    }

    public void removeBooking(String bookingId) {
        Booking removed = bookings.remove(bookingId);
        if (removed != null) {
            saveBookingsToFile();
            Logger.log("Booking removed: " + bookingId);
        }
    }

    public Booking getBookingById(String bookingId) {
        return bookings.get(bookingId);
    }

    public void updateBooking(Booking booking) {
        if (booking != null && bookings.containsKey(booking.getBookingId())) {
            bookings.put(booking.getBookingId(), booking);
            saveBookingsToFile();
            Logger.log("Booking updated: " + booking.getBookingId());
        }
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings.values());
    }

    public List<Booking> getBookingsByUser(String userId) {
        return bookings.values().stream()
                .filter(booking -> booking.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Booking> getBookingsByPackage(String packageId) {
        return bookings.values().stream()
                .filter(booking -> booking.getPackageId().equals(packageId))
                .collect(Collectors.toList());
    }

    public List<Booking> getBookingsByStatus(BookingStatus status) {
        return bookings.values().stream()
                .filter(booking -> booking.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Booking> getBookingsByDate(String date) {
        return bookings.values().stream()
                .filter(booking -> booking.getBookingDate().equals(date))
                .collect(Collectors.toList());
    }

    public List<Booking> getBookingsByTravelDate(String travelDate) {
        return bookings.values().stream()
                .filter(booking -> booking.getTravelDate().equals(travelDate))
                .collect(Collectors.toList());
    }

    public List<Booking> getBookingsInDateRange(String startDate, String endDate) {
        return bookings.values().stream()
                .filter(booking -> {
                    String bookingDate = booking.getBookingDate();
                    return bookingDate.compareTo(startDate) >= 0 && 
                           bookingDate.compareTo(endDate) <= 0;
                })
                .collect(Collectors.toList());
    }

    public List<Booking> getPendingBookings() {
        return getBookingsByStatus(BookingStatus.PENDING);
    }

    public List<Booking> getConfirmedBookings() {
        return getBookingsByStatus(BookingStatus.CONFIRMED);
    }

    public List<Booking> getCancelledBookings() {
        return getBookingsByStatus(BookingStatus.CANCELED);
    }

    public boolean cancelBooking(String bookingId, String reason) {
        Booking booking = getBookingById(bookingId);
        if (booking != null && booking.getStatus() != BookingStatus.COMPLETED) {
            booking.cancelBooking();
            updateBooking(booking);
            Logger.log("Booking cancelled: " + bookingId + " - Reason: " + reason);
            return true;
        }
        return false;
    }

    public boolean confirmBooking(String bookingId) {
        Booking booking = getBookingById(bookingId);
        if (booking != null && booking.getStatus() == BookingStatus.PENDING) {
            booking.confirmBooking();
            updateBooking(booking);
            Logger.log("Booking confirmed: " + bookingId);
            return true;
        }
        return false;
    }

    public boolean completeBooking(String bookingId) {
        Booking booking = getBookingById(bookingId);
        if (booking != null && booking.getStatus() == BookingStatus.CONFIRMED) {
            booking.completeBooking();
            updateBooking(booking);
            Logger.log("Booking completed: " + bookingId);
            return true;
        }
        return false;
    }

    public double getTotalRevenue() {
        return bookings.values().stream()
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED || 
                                 booking.getStatus() == BookingStatus.COMPLETED)
                .mapToDouble(Booking::getTotalAmount)
                .sum();
    }

    public double getRevenueByPackage(String packageId) {
        return bookings.values().stream()
                .filter(booking -> booking.getPackageId().equals(packageId) &&
                                 (booking.getStatus() == BookingStatus.CONFIRMED || 
                                  booking.getStatus() == BookingStatus.COMPLETED))
                .mapToDouble(Booking::getTotalAmount)
                .sum();
    }

    public int getTotalBookings() {
        return bookings.size();
    }

    public int getBookingsCountByStatus(BookingStatus status) {
        return (int) bookings.values().stream()
                .filter(booking -> booking.getStatus() == status)
                .count();
    }

    public List<Booking> getUpcomingBookings() {
        String today = DateUtil.getCurrentDate();
        return bookings.values().stream()
                .filter(booking -> booking.getTravelDate().compareTo(today) > 0 &&
                                 booking.getStatus() == BookingStatus.CONFIRMED)
                .sorted(Comparator.comparing(Booking::getTravelDate))
                .collect(Collectors.toList());
    }

    public List<Booking> getTodaysBookings() {
        String today = DateUtil.getCurrentDate();
        return getBookingsByTravelDate(today);
    }

    public Map<String, Integer> getBookingCountsByMonth() {
        Map<String, Integer> monthlyCount = new HashMap<>();
        for (Booking booking : bookings.values()) {
            String month = booking.getBookingDate().substring(0, 7); // YYYY-MM
            monthlyCount.put(month, monthlyCount.getOrDefault(month, 0) + 1);
        }
        return monthlyCount;
    }

    public List<Booking> searchBookings(String keyword) {
        return bookings.values().stream()
                .filter(booking -> booking.getBookingId().toLowerCase().contains(keyword.toLowerCase()) ||
                                 booking.getPackageId().toLowerCase().contains(keyword.toLowerCase()) ||
                                 booking.getUserId().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    private void loadBookingsFromFile() {
        try {
            List<String> lines = FileHandler.readFromFile(BOOKINGS_FILE);
            for (String line : lines) {
                Booking booking = parseBookingFromString(line);
                if (booking != null) {
                    bookings.put(booking.getBookingId(), booking);
                }
            }
        } catch (Exception e) {
            Logger.error("Failed to load bookings from file: " + e.getMessage());
        }
    }

    private void saveBookingsToFile() {
        try {
            FileHandler.clearFile(BOOKINGS_FILE);
            for (Booking booking : bookings.values()) {
                String bookingString = convertBookingToString(booking);
                FileHandler.writeToFile(BOOKINGS_FILE, bookingString);
            }
        } catch (Exception e) {
            Logger.error("Failed to save bookings to file: " + e.getMessage());
        }
    }

    private Booking parseBookingFromString(String bookingString) {
        try {
            String[] parts = bookingString.split("\\|");
            if (parts.length >= 8) {
                Booking booking = new Booking(parts[0], parts[1], parts[2], parts[3], 
                                            Integer.parseInt(parts[4]));
                booking.setTotalAmount(Double.parseDouble(parts[5]));
                booking.setStatus(BookingStatus.valueOf(parts[6]));
                booking.setPaid(Boolean.parseBoolean(parts[7]));
                return booking;
            }
        } catch (Exception e) {
            Logger.error("Failed to parse booking: " + e.getMessage());
        }
        return null;
    }

    private String convertBookingToString(Booking booking) {
        return String.join("|",
            booking.getBookingId(), booking.getUserId(), booking.getPackageId(),
            booking.getTravelDate(), String.valueOf(booking.getNumberOfPeople()),
            String.valueOf(booking.getTotalAmount()), booking.getStatus().name(),
            String.valueOf(booking.isPaid()));
    }
}
