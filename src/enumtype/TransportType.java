package enumtype;

public enum TransportType {
    CAR("Car", 50.0),
    BUS("Bus", 25.0),
    TRAIN("Train", 30.0),
    FLIGHT("Flight", 200.0);

    private final String displayName;
    private final double baseFare;

    TransportType(String displayName, double baseFare) {
        this.displayName = displayName;
        this.baseFare = baseFare;
    }

    public String getDisplayName() { return displayName; }
    public double getBaseFare() { return baseFare; }
}
