package enumtype;

public enum TourType {
    FAMILY("Family Tour", "Suitable for families with children"),
    ADVENTURE("Adventure Tour", "Exciting outdoor activities"),
    HISTORICAL("Historical Tour", "Cultural and historical sites"),
    COUPLE("Couple Tour", "Romantic getaway packages");

    private final String displayName;
    private final String description;

    TourType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
