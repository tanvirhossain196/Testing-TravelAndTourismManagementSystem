package manager;

import model.Room;
import enumtype.RoomType;
import util.Logger;
import util.FileHandler;
import java.util.*;
import java.util.stream.Collectors;

public class RoomManager {
    private Map<String, Room> rooms;
    private Map<String, List<String>> hotelRooms; // hotelId -> List of roomIds
    private static final String ROOMS_FILE = "rooms.dat";

    public RoomManager() {
        this.rooms = new HashMap<>();
        this.hotelRooms = new HashMap<>();
        loadRoomsFromFile();
    }

    public void addRoom(Room room) {
        if (room != null && !rooms.containsKey(room.getRoomId())) {
            rooms.put(room.getRoomId(), room);
            
            // Add to hotel rooms mapping
            String hotelId = room.getHotelId();
            hotelRooms.computeIfAbsent(hotelId, k -> new ArrayList<>()).add(room.getRoomId());
            
            saveRoomsToFile();
            Logger.log("Room added: " + room.getRoomId() + " in hotel " + hotelId);
        }
    }

    public void removeRoom(String roomId) {
        Room room = rooms.remove(roomId);
        if (room != null) {
            // Remove from hotel rooms mapping
            String hotelId = room.getHotelId();
            List<String> roomIds = hotelRooms.get(hotelId);
            if (roomIds != null) {
                roomIds.remove(roomId);
                if (roomIds.isEmpty()) {
                    hotelRooms.remove(hotelId);
                }
            }
            
            saveRoomsToFile();
            Logger.log("Room removed: " + roomId);
        }
    }

    public Room getRoomById(String roomId) {
        return rooms.get(roomId);
    }

    public void updateRoom(Room room) {
        if (room != null && rooms.containsKey(room.getRoomId())) {
            rooms.put(room.getRoomId(), room);
            saveRoomsToFile();
            Logger.log("Room updated: " + room.getRoomId());
        }
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public List<Room> getRoomsByHotel(String hotelId) {
        List<String> roomIds = hotelRooms.getOrDefault(hotelId, new ArrayList<>());
        return roomIds.stream()
                .map(rooms::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Room> getAvailableRooms() {
        return rooms.values().stream()
                .filter(Room::isAvailable)
                .collect(Collectors.toList());
    }

    public List<Room> getAvailableRoomsByHotel(String hotelId) {
        return getRoomsByHotel(hotelId).stream()
                .filter(Room::isAvailable)
                .collect(Collectors.toList());
    }

    public List<Room> getRoomsByType(RoomType roomType) {
        return rooms.values().stream()
                .filter(room -> room.getRoomType() == roomType)
                .collect(Collectors.toList());
    }

    public List<Room> getAvailableRoomsByType(RoomType roomType) {
        return rooms.values().stream()
                .filter(room -> room.getRoomType() == roomType && room.isAvailable())
                .collect(Collectors.toList());
    }

    public List<Room> getRoomsByPriceRange(double minPrice, double maxPrice) {
        return rooms.values().stream()
                .filter(room -> room.getPrice() >= minPrice && room.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    public boolean assignRoomToBooking(String roomId, String bookingId) {
        Room room = getRoomById(roomId);
        if (room != null && room.isAvailable()) {
            room.bookRoom();
            updateRoom(room);
            Logger.log("Room assigned to booking: " + roomId + " -> " + bookingId);
            return true;
        }
        return false;
    }

    public boolean releaseRoomFromBooking(String roomId) {
        Room room = getRoomById(roomId);
        if (room != null && !room.isAvailable()) {
            room.checkOut();
            updateRoom(room);
            Logger.log("Room released from booking: " + roomId);
            return true;
        }
        return false;
    }

    public void checkRoomStatus(String roomId) {
        Room room = getRoomById(roomId);
        if (room != null) {
            System.out.println("Room Status for " + roomId + ":");
            System.out.println("  Available: " + room.isAvailable());
            System.out.println("  Type: " + room.getRoomType().getDisplayName());
            System.out.println("  Price: $" + room.getPrice());
            System.out.println("  Capacity: " + room.getCapacity());
        } else {
            System.out.println("Room not found: " + roomId);
        }
    }

    public void blockRoom(String roomId, String reason) {
        Room room = getRoomById(roomId);
        if (room != null) {
            room.setAvailability(false);
            updateRoom(room);
            Logger.log("Room blocked: " + roomId + " - Reason: " + reason);
        }
    }

    public void unblockRoom(String roomId) {
        Room room = getRoomById(roomId);
        if (room != null) {
            room.setAvailability(true);
            updateRoom(room);
            Logger.log("Room unblocked: " + roomId);
        }
    }

    public int getTotalRooms() {
        return rooms.size();
    }

    public int getAvailableRoomsCount() {
        return (int) rooms.values().stream().filter(Room::isAvailable).count();
    }

    public int getOccupiedRoomsCount() {
        return getTotalRooms() - getAvailableRoomsCount();
    }

    public double getOccupancyRate() {
        int totalRooms = getTotalRooms();
        return totalRooms > 0 ? (double) getOccupiedRoomsCount() / totalRooms * 100 : 0.0;
    }

    public Map<RoomType, Long> getRoomCountByType() {
        return rooms.values().stream()
                .collect(Collectors.groupingBy(Room::getRoomType, Collectors.counting()));
    }

    public double getAverageRoomPrice() {
        return rooms.values().stream()
                .mapToDouble(Room::getPrice)
                .average()
                .orElse(0.0);
    }

    public Room getMostExpensiveRoom() {
        return rooms.values().stream()
                .max(Comparator.comparing(Room::getPrice))
                .orElse(null);
    }

    public Room getCheapestRoom() {
        return rooms.values().stream()
                .min(Comparator.comparing(Room::getPrice))
                .orElse(null);
    }

    public List<Room> searchRooms(String keyword) {
        return rooms.values().stream()
                .filter(room -> room.getRoomId().toLowerCase().contains(keyword.toLowerCase()) ||
                               room.getRoomNumber().toLowerCase().contains(keyword.toLowerCase()) ||
                               room.getRoomType().getDisplayName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    private void loadRoomsFromFile() {
        try {
            List<String> lines = FileHandler.readFromFile(ROOMS_FILE);
            for (String line : lines) {
                Room room = parseRoomFromString(line);
                if (room != null) {
                    rooms.put(room.getRoomId(), room);
                    
                    // Update hotel rooms mapping
                    String hotelId = room.getHotelId();
                    hotelRooms.computeIfAbsent(hotelId, k -> new ArrayList<>()).add(room.getRoomId());
                }
            }
        } catch (Exception e) {
            Logger.error("Failed to load rooms from file: " + e.getMessage());
        }
    }

    private void saveRoomsToFile() {
        try {
            FileHandler.clearFile(ROOMS_FILE);
            for (Room room : rooms.values()) {
                String roomString = convertRoomToString(room);
                FileHandler.writeToFile(ROOMS_FILE, roomString);
            }
        } catch (Exception e) {
            Logger.error("Failed to save rooms to file: " + e.getMessage());
        }
    }

    private Room parseRoomFromString(String roomString) {
        try {
            String[] parts = roomString.split("\\|");
            if (parts.length >= 5) {
                Room room = new Room(parts[0], parts[1], parts[2], 
                                   RoomType.valueOf(parts[3]), Double.parseDouble(parts[4]));
                if (parts.length > 5) {
                    room.setAvailability(Boolean.parseBoolean(parts[5]));
                }
                if (parts.length > 6) {
                    room.setCapacity(Integer.parseInt(parts[6]));
                }
                return room;
            }
        } catch (Exception e) {
            Logger.error("Failed to parse room: " + e.getMessage());
        }
        return null;
    }

    private String convertRoomToString(Room room) {
        return String.join("|",
            room.getRoomId(), room.getHotelId(), room.getRoomNumber(),
            room.getRoomType().name(), String.valueOf(room.getPrice()),
            String.valueOf(room.isAvailable()), String.valueOf(room.getCapacity()));
    }
}
