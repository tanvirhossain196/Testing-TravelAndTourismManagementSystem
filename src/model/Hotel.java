package model;

import java.util.List;
import java.util.ArrayList;

public class Hotel {
    private String hotelId;
    private String name;
    private String location;
    private double rating;
    private List<Room> roomList;
    private String address;
    private String phone;
    private String email;
    private List<String> amenities;
    private String description;
    private boolean isActive;

    public Hotel(String hotelId, String name, String location, double rating) {
        this.hotelId = hotelId;
        this.name = name;
        this.location = location;
        this.rating = rating;
        this.roomList = new ArrayList<>();
        this.amenities = new ArrayList<>();
        this.isActive = true;
    }

    public void addRoom(Room room) {
        roomList.add(room);
    }

    public void removeRoom(String roomId) {
        roomList.removeIf(room -> room.getRoomId().equals(roomId));
    }

    public Room getRoomById(String roomId) {
        return roomList.stream()
                .filter(room -> room.getRoomId().equals(roomId))
                .findFirst()
                .orElse(null);
    }

    public List<Room> getAvailableRooms() {
        return roomList.stream()
                .filter(Room::isAvailable)
                .collect(ArrayList::new, (list, room) -> list.add(room), ArrayList::addAll);
    }

    // Getters and Setters
    public String getHotelId() { return hotelId; }
    public void setHotelId(String hotelId) { this.hotelId = hotelId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public List<Room> getRoomList() { return roomList; }
    public void setRoomList(List<Room> roomList) { this.roomList = roomList; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<String> getAmenities() { return amenities; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return "Hotel{" +
                "name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", rating=" + rating +
                ", rooms=" + roomList.size() +
                '}';
    }
}
