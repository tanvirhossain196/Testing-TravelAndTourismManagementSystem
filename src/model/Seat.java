package model;

public class Seat {
    private String seatId;
    private String transportId;
    private String seatNumber;
    private boolean isAvailable;
    private String passengerName;
    private String passengerPhone;
    private String seatType; // Window, Aisle, Middle
    private double extraCharge;
    private boolean isBlocked;

    public Seat(String seatId, String transportId, String seatNumber) {
        this.seatId = seatId;
        this.transportId = transportId;
        this.seatNumber = seatNumber;
        this.isAvailable = true;
        this.extraCharge = 0.0;
        this.isBlocked = false;
    }

    public boolean bookSeat(String passengerName, String passengerPhone) {
        if (isAvailable && !isBlocked) {
            this.isAvailable = false;
            this.passengerName = passengerName;
            this.passengerPhone = passengerPhone;
            return true;
        }
        return false;
    }

    public void cancelSeat() {
        this.isAvailable = true;
        this.passengerName = null;
        this.passengerPhone = null;
    }

    // Getters and Setters
    public String getSeatId() { return seatId; }
    public void setSeatId(String seatId) { this.seatId = seatId; }
    public String getTransportId() { return transportId; }
    public void setTransportId(String transportId) { this.transportId = transportId; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public String getPassengerPhone() { return passengerPhone; }
    public void setPassengerPhone(String passengerPhone) { this.passengerPhone = passengerPhone; }
    public String getSeatType() { return seatType; }
    public void setSeatType(String seatType) { this.seatType = seatType; }
    public double getExtraCharge() { return extraCharge; }
    public void setExtraCharge(double extraCharge) { this.extraCharge = extraCharge; }
    public boolean isBlocked() { return isBlocked; }
    public void setBlocked(boolean blocked) { isBlocked = blocked; }

    @Override
    public String toString() {
        return "Seat{" +
                "seatNumber='" + seatNumber + '\'' +
                ", available=" + isAvailable +
                ", passenger='" + passengerName + '\'' +
                '}';
    }
}
