package model;

import enumtype.TransportType;
import util.DateUtil;

public class Transport {
    private String transportId;
    private TransportType type;
    private String departure;
    private String arrival;
    private String departureTime;
    private String arrivalTime;
    private double fare;
    private int totalSeats;
    private int availableSeats;
    private String operatorName;
    private boolean isActive;

    public Transport(String transportId, TransportType type, String departure, String arrival) {
        this.transportId = transportId;
        this.type = type;
        this.departure = departure;
        this.arrival = arrival;
        this.fare = type.getBaseFare();
        this.isActive = true;
        setDefaultSeats();
    }

    private void setDefaultSeats() {
        switch (type) {
            case CAR:
                this.totalSeats = 4;
                break;
            case BUS:
                this.totalSeats = 40;
                break;
            case TRAIN:
                this.totalSeats = 72;
                break;
            case FLIGHT:
                this.totalSeats = 180;
                break;
        }
        this.availableSeats = this.totalSeats;
    }

    public boolean bookSeat() {
        if (availableSeats > 0) {
            availableSeats--;
            return true;
        }
        return false;
    }

    public void cancelSeat() {
        if (availableSeats < totalSeats) {
            availableSeats++;
        }
    }

    // Getters and Setters
    public String getTransportId() { return transportId; }
    public void setTransportId(String transportId) { this.transportId = transportId; }
    public TransportType getType() { return type; }
    public void setType(TransportType type) { this.type = type; }
    public String getDeparture() { return departure; }
    public void setDeparture(String departure) { this.departure = departure; }
    public String getArrival() { return arrival; }
    public void setArrival(String arrival) { this.arrival = arrival; }
    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
    public double getFare() { return fare; }
    public void setFare(double fare) { this.fare = fare; }
    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return "Transport{" +
                "type=" + type +
                ", departure='" + departure + '\'' +
                ", arrival='" + arrival + '\'' +
                ", fare=" + fare +
                ", availableSeats=" + availableSeats +
                '}';
    }
}
