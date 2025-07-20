package model;

import java.util.List;

public class Admin extends user {
    private String adminLevel;
    private List<String> permissions;

    public Admin(String id, String name, String email, String password, String phone) {
        super(id, name, email, password, phone);
        this.adminLevel = "SUPER_ADMIN";
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    public String getAdminLevel() { return adminLevel; }
    public void setAdminLevel(String adminLevel) { this.adminLevel = adminLevel; }
    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }
}
