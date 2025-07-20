package enumtype;

public enum BookingStatus {
    PENDING("Pending", "Booking is being processed"),
    CONFIRMED("Confirmed", "Booking is confirmed"),
    CANCELED("Canceled", "Booking has been canceled"),
    COMPLETED("Completed", "Tour has been completed");

    private final String displayName;
    private final String description;

    BookingStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
