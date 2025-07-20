package model;

import java.util.List;
import java.util.ArrayList;

public class TravelAgent extends user {
    private String agencyName;
    private String licenseNumber;
    private List<String> managedPackages;
    private double commissionRate;

    public TravelAgent(String id, String name, String email, String password, String phone) {
        super(id, name, email, password, phone);
        this.managedPackages = new ArrayList<>();
        this.commissionRate = 0.10;
    }

    @Override
    public String getRole() {
        return "AGENT";
    }

    // Getters and Setters
    public String getAgencyName() { return agencyName; }
    public void setAgencyName(String agencyName) { this.agencyName = agencyName; }
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public List<String> getManagedPackages() { return managedPackages; }
    public double getCommissionRate() { return commissionRate; }
    public void setCommissionRate(double commissionRate) { this.commissionRate = commissionRate; }
}
