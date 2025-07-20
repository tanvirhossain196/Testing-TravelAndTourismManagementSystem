package enumtype;

public enum PackageCategory {
    LOCAL("Local Package", "Domestic destinations"),
    INTERNATIONAL("International Package", "Foreign destinations"),
    SEASONAL("Seasonal Package", "Special seasonal offers");

    private final String displayName;
    private final String description;

    PackageCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
