package manager;

import model.Hotel;
import model.Room;
import enumtype.RoomType;
import util.Logger;
import util.FileHandler;
import java.util.*;
import java.util.stream.Collectors;

public class HotelManager {
    private Map<String, Hotel> hotels;
    private static final String HOTELS_FILE = "hotels.dat";

    public HotelManager() {
        this.hotels = new HashMap<>();
        loadHotelsFromFile();
    }

    public void addHotel(Hotel hotel) {
        if (hotel != null && !hotels.containsKey(hotel.getHotelId())) {
            hotels.put(hotel.getHotelId(), hotel);
            saveHotelsToFile();
            Logger.log("Hotel added: " + hotel.getName());
        }
    }

    public void removeHotel(String hotelId) {
        Hotel removed = hotels.remove(hotelId);
        if (removed != null) {
            saveHotelsToFile();
            Logger.log("Hotel removed: " + removed.getName());
        }
    }

    public Hotel getHotelById(String hotelId) {
        return hotels.get(hotelId);
    }

    public void updateHotel(Hotel hotel) {
        if (hotel != null && hotels.containsKey(hotel.getHotelId())) {
            hotels.put(hotel.getHotelId(), hotel);
            saveHotelsToFile();
            Logger.log("Hotel updated: " + hotel.getName());
        }
    }

    public List<Hotel> getAllHotels() {
        return new ArrayList<>(hotels.values());
    }

    public List<Hotel> getActiveHotels() {
        return hotels.values().stream()
                .filter(Hotel::isActive)
                .collect(Collectors.toList());
    }

    public List<Hotel> searchHotels(String keyword) {
        return hotels.values().stream()
                .filter(hotel -> hotel.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                               hotel.getLocation().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Hotel> getHotelsByLocation(String location) {
        return hotels.values().stream()
                .filter(hotel -> hotel.getLocation().toLowerCase().contains(location.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Hotel> getHotelsByRating(double minRating) {
        return hotels.values().stream()
                .filter(hotel -> hotel.getRating() >= minRating)
                .sorted(Comparator.comparing(Hotel::getRating).reversed())
                .collect(Collectors.toList());
    }

    public List<Hotel> getHotelsWithAvailableRooms() {
        return hotels.values().stream()
                .filter(hotel -> !hotel.getAvailableRooms().isEmpty())
                .collect(Collectors.toList());
    }

    public List<Room> searchAvailableRooms(String location, RoomType roomType) {
        List<Room> availableRooms = new ArrayList<>();
        
        List<Hotel> hotelsInLocation = getHotelsByLocation(location);
        for (Hotel hotel : hotelsInLocation) {
            List<Room> rooms = hotel.getAvailableRooms().stream()
                    .filter(room -> roomType == null || room.getRoomType() == roomType)
                    .collect(Collectors.toList());
            availableRooms.addAll(rooms);
        }
        
        return availableRooms;
    }

    public boolean bookRoom(String hotelId, String roomId) {
        Hotel hotel = getHotelById(hotelId);
        if (hotel != null) {
            Room room = hotel.getRoomById(roomId);
            if (room != null && room.isAvailable()) {
                room.bookRoom();
                updateHotel(hotel);
                Logger.log("Room booked: " + roomId + " in hotel " + hotel.getName());
                return true;
            }
        }
        return false;
    }

    public boolean checkoutRoom(String hotelId, String roomId) {
        Hotel hotel = getHotelById(hotelId);
        if (hotel != null) {
            Room room = hotel.getRoomById(roomId);
            if (room != null && !room.isAvailable()) {
                room.checkOut();
                updateHotel(hotel);
                Logger.log("Room checkout: " + roomId + " in hotel " + hotel.getName());
                return true;
            }
        }
        return false;
    }

    public void addRoomToHotel(String hotelId, Room room) {
        Hotel hotel = getHotelById(hotelId);
        if (hotel != null) {
            hotel.addRoom(room);
            updateHotel(hotel);
            Logger.log("Room added to hotel: " + room.getRoomId() + " in " + hotel.getName());
        }
    }

    public void removeRoomFromHotel(String hotelId, String roomId) {
        Hotel hotel = getHotelById(hotelId);
        if (hotel != null) {
            hotel.removeRoom(roomId);
            updateHotel(hotel);
            Logger.log("Room removed from hotel: " + roomId + " in " + hotel.getName());
        }
    }

    public int getTotalHotels() {
        return hotels.size();
    }

    public int getActiveHotelsCount() {
        return (int) hotels.values().stream().filter(Hotel::isActive).count();
    }

    public double getAverageHotelRating() {
        return hotels.values().stream()
                .mapToDouble(Hotel::getRating)
                .average()
                .orElse(0.0);
    }

    public Hotel getHighestRatedHotel() {
        return hotels.values().stream()
                .max(Comparator.comparing(Hotel::getRating))
                .orElse(null);
    }

    public Map<String, Long> getHotelCountByLocation() {
        return hotels.values().stream()
                .collect(Collectors.groupingBy(Hotel::getLocation, Collectors.counting()));
    }

    public int getTotalRooms() {
        return hotels.values().stream()
                .mapToInt(hotel -> hotel.getRoomList().size())
                .sum();
    }

    public int getAvailableRoomsCount() {
        return hotels.values().stream()
                .mapToInt(hotel -> hotel.getAvailableRooms().size())
                .sum();
    }

    public double getOccupancyRate() {
        int totalRooms = getTotalRooms();
        int availableRooms = getAvailableRoomsCount();
        int occupiedRooms = totalRooms - availableRooms;
        
        return totalRooms > 0 ? (double) occupiedRooms / totalRooms * 100 : 0.0;
    }

    private void loadHotelsFromFile() {
        try {
            List<String> lines = FileHandler.readFromFile(HOTELS_FILE);
            for (String line : lines) {
                Hotel hotel = parseHotelFromString(line);
                if (hotel != null) {
                    hotels.put(hotel.getHotelId(), hotel);
                }
            }
        } catch (Exception e) {
            Logger.error("Failed to load hotels from file: " + e.getMessage());
        }
    }

    private void saveHotelsToFile() {
        try {
            FileHandler.clearFile(HOTELS_FILE);
            for (Hotel hotel : hotels.values()) {
                String hotelString = convertHotelToString(hotel);
                FileHandler.writeToFile(HOTELS_FILE, hotelString);
            }
        } catch (Exception e) {
            Logger.error("Failed to save hotels to file: " + e.getMessage());
        }
    }

    private Hotel parseHotelFromString(String hotelString) {
        try {
            String[] parts = hotelString.split("\\|");
            if (parts.length >= 4) {
                Hotel hotel = new Hotel(parts[0], parts[1], parts[2], Double.parseDouble(parts[3]));
                if (parts.length > 4) {
                    hotel.setActive(Boolean.parseBoolean(parts[4]));
                }
                return hotel;
            }
        } catch (Exception e) {
            Logger.error("Failed to parse hotel: " + e.getMessage());
        }
        return null;
    }

    private String convertHotelToString(Hotel hotel) {
        return String.join("|",
            hotel.getHotelId(), hotel.getName(), hotel.getLocation(),
            String.valueOf(hotel.getRating()), String.valueOf(hotel.isActive()));
    }
}
