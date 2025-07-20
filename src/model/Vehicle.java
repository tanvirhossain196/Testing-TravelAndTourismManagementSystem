package model;

import enumtype.TransportType;
import java.util.List;
import java.util.ArrayList;

public class Vehicle {
    private String vehicleId;
    private String model;
    private String make;
    private String year;
    private TransportType vehicleType;
    private int seatsAvailable;
    private String licensePlate;
    private boolean isOperational;
    private String driverName;
    private String driverLicense;
    private List<String> features;
    private double fuelCapacity;
    private String insuranceDetails;

    public Vehicle(String vehicleId, String model, TransportType vehicleType, int seatsAvailable) {
        this.vehicleId = vehicleId;
        this.model = model;
        this.vehicleType = vehicleType;
        this.seatsAvailable = seatsAvailable;
        this.isOperational = true;
        this.features = new ArrayList<>();
    }

    public void addFeature(String feature) {
        features.add(feature);
    }

    public void removeFeature(String feature) {
        features.remove(feature);
    }

    // Getters and Setters
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    public TransportType getVehicleType() { return vehicleType; }
    public void setVehicleType(TransportType vehicleType) { this.vehicleType = vehicleType; }
    public int getSeatsAvailable() { return seatsAvailable; }
    public void setSeatsAvailable(int seatsAvailable) { this.seatsAvailable = seatsAvailable; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public boolean isOperational() { return isOperational; }
    public void setOperational(boolean operational) { isOperational = operational; }
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    public String getDriverLicense() { return driverLicense; }
    public void setDriverLicense(String driverLicense) { this.driverLicense = driverLicense; }
    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }
    public double getFuelCapacity() { return fuelCapacity; }
    public void setFuelCapacity(double fuelCapacity) { this.fuelCapacity = fuelCapacity; }
    public String getInsuranceDetails() { return insuranceDetails; }
    public void setInsuranceDetails(String insuranceDetails) { this.insuranceDetails = insuranceDetails; }
}
