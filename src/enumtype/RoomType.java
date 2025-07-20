package enumtype;

public enum RoomType {
    STANDARD("Standard Room", 1500.0),
    DELUXE("Deluxe Room", 2500.0),
    SUITE("Suite Room", 4000.0);

    private final String displayName;
    private final double basePrice;

    RoomType(String displayName, double basePrice) {
        this.displayName = displayName;
        this.basePrice = basePrice;
    }

    public String getDisplayName() { return displayName; }
    public double getBasePrice() { return basePrice; }
}
