package model;

import enumtype.RoomType;

public class Room {
    private String roomId;
    private String hotelId;
    private String roomNumber;
    private RoomType roomType;
    private boolean availability;
    private double price;
    private int capacity;
    private String description;
    private boolean hasAC;
    private boolean hasWiFi;
    private boolean hasTV;
    private String bedType;

    public Room(String roomId, String hotelId, String roomNumber, RoomType roomType, double price) {
        this.roomId = roomId;
        this.hotelId = hotelId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.price = price;
        this.availability = true;
        this.capacity = 2; // Default capacity
    }

    public boolean isAvailable() {
        return availability;
    }

    public void bookRoom() {
        this.availability = false;
    }

    public void checkOut() {
        this.availability = true;
    }

    // Getters and Setters
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getHotelId() { return hotelId; }
    public void setHotelId(String hotelId) { this.hotelId = hotelId; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }
    public boolean isAvailability() { return availability; }
    public void setAvailability(boolean availability) { this.availability = availability; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isHasAC() { return hasAC; }
    public void setHasAC(boolean hasAC) { this.hasAC = hasAC; }
    public boolean isHasWiFi() { return hasWiFi; }
    public void setHasWiFi(boolean hasWiFi) { this.hasWiFi = hasWiFi; }
    public boolean isHasTV() { return hasTV; }
    public void setHasTV(boolean hasTV) { this.hasTV = hasTV; }
    public String getBedType() { return bedType; }
    public void setBedType(String bedType) { this.bedType = bedType; }

    @Override
    public String toString() {
        return "Room{" +
                "roomNumber='" + roomNumber + '\'' +
                ", roomType=" + roomType +
                ", price=" + price +
                ", available=" + availability +
                '}';
    }
}
