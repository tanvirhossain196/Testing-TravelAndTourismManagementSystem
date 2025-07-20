package enumtype;

public enum RoleType {
    ADMIN("Administrator", "Full system access"),
    TOURIST("Tourist", "Browse and book packages"),
    AGENT("Travel Agent", "Manage packages and itineraries");

    private final String displayName;
    private final String description;

    RoleType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
