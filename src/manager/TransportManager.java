package manager;

import model.Transport;
import model.Vehicle;
import model.Seat;
import enumtype.TransportType;
import util.Logger;
import util.FileHandler;
import java.util.*;
import java.util.stream.Collectors;

public class TransportManager {
    private Map<String, Transport> transports;
    private Map<String, Vehicle> vehicles;
    private Map<String, List<Seat>> transportSeats; // transportId -> List of seats
    private static final String TRANSPORTS_FILE = "transports.dat";
    private static final String VEHICLES_FILE = "vehicles.dat";

    public TransportManager() {
        this.transports = new HashMap<>();
        this.vehicles = new HashMap<>();
        this.transportSeats = new HashMap<>();
        loadTransportsFromFile();
        loadVehiclesFromFile();
    }

    public void addTransport(Transport transport) {
        if (transport != null && !transports.containsKey(transport.getTransportId())) {
            transports.put(transport.getTransportId(), transport);
            initializeSeatsForTransport(transport);
            saveTransportsToFile();
            Logger.log("Transport added: " + transport.getTransportId() + " (" + transport.getType() + ")");
        }
    }

    public void removeTransport(String transportId) {
        Transport removed = transports.remove(transportId);
        if (removed != null) {
            transportSeats.remove(transportId);
            saveTransportsToFile();
            Logger.log("Transport removed: " + transportId);
        }
    }

    public Transport getTransportById(String transportId) {
        return transports.get(transportId);
    }

    public void updateTransport(Transport transport) {
        if (transport != null && transports.containsKey(transport.getTransportId())) {
            transports.put(transport.getTransportId(), transport);
            saveTransportsToFile();
            Logger.log("Transport updated: " + transport.getTransportId());
        }
    }

    public List<Transport> getAllTransports() {
        return new ArrayList<>(transports.values());
    }

    public List<Transport> getActiveTransports() {
        return transports.values().stream()
                .filter(Transport::isActive)
                .collect(Collectors.toList());
    }

    public List<Transport> searchTransports(String departure, String arrival) {
        return transports.values().stream()
                .filter(transport -> transport.getDeparture().toLowerCase().contains(departure.toLowerCase()) &&
                                   transport.getArrival().toLowerCase().contains(arrival.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Transport> getTransportsByType(TransportType type) {
        return transports.values().stream()
                .filter(transport -> transport.getType() == type)
                .collect(Collectors.toList());
    }

    public List<Transport> getAvailableTransports() {
        return transports.values().stream()
                .filter(transport -> transport.getAvailableSeats() > 0)
                .collect(Collectors.toList());
    }

    public List<Transport> getTransportsByRoute(String departure, String arrival) {
        return transports.values().stream()
                .filter(transport -> transport.getDeparture().equalsIgnoreCase(departure) &&
                                   transport.getArrival().equalsIgnoreCase(arrival))
                .collect(Collectors.toList());
    }

    public boolean bookSeat(String transportId) {
        Transport transport = getTransportById(transportId);
        if (transport != null && transport.bookSeat()) {
            updateTransport(transport);
            Logger.log("Seat booked on transport: " + transportId);
            return true;
        }
        return false;
    }

    public boolean cancelSeat(String transportId) {
        Transport transport = getTransportById(transportId);
        if (transport != null) {
            transport.cancelSeat();
            updateTransport(transport);
            Logger.log("Seat cancelled on transport: " + transportId);
            return true;
        }
        return false;
    }

    public void addVehicle(Vehicle vehicle) {
        if (vehicle != null && !vehicles.containsKey(vehicle.getVehicleId())) {
            vehicles.put(vehicle.getVehicleId(), vehicle);
            saveVehiclesToFile();
            Logger.log("Vehicle added: " + vehicle.getVehicleId() + " (" + vehicle.getModel() + ")");
        }
    }

    public void removeVehicle(String vehicleId) {
        Vehicle removed = vehicles.remove(vehicleId);
        if (removed != null) {
            saveVehiclesToFile();
            Logger.log("Vehicle removed: " + vehicleId);
        }
    }

    public Vehicle getVehicleById(String vehicleId) {
        return vehicles.get(vehicleId);
    }

    public List<Vehicle> getAllVehicles() {
        return new ArrayList<>(vehicles.values());
    }

    public List<Vehicle> getOperationalVehicles() {
        return vehicles.values().stream()
                .filter(Vehicle::isOperational)
                .collect(Collectors.toList());
    }

    public List<Vehicle> getVehiclesByType(TransportType type) {
        return vehicles.values().stream()
                .filter(vehicle -> vehicle.getVehicleType() == type)
                .collect(Collectors.toList());
    }

    public List<Seat> getSeatsForTransport(String transportId) {
        return transportSeats.getOrDefault(transportId, new ArrayList<>());
    }

    public List<Seat> getAvailableSeats(String transportId) {
        return getSeatsForTransport(transportId).stream()
                .filter(Seat::isAvailable)
                .collect(Collectors.toList());
    }

    public boolean bookSpecificSeat(String transportId, String seatNumber, String passengerName, String passengerPhone) {
        List<Seat> seats = getSeatsForTransport(transportId);
        for (Seat seat : seats) {
            if (seat.getSeatNumber().equals(seatNumber) && seat.isAvailable()) {
                if (seat.bookSeat(passengerName, passengerPhone)) {
                    // Also update transport available seats count
                    Transport transport = getTransportById(transportId);
                    if (transport != null) {
                        transport.bookSeat();
                        updateTransport(transport);
                    }
                    Logger.log("Specific seat booked: " + seatNumber + " on transport " + transportId);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean cancelSpecificSeat(String transportId, String seatNumber) {
        List<Seat> seats = getSeatsForTransport(transportId);
        for (Seat seat : seats) {
            if (seat.getSeatNumber().equals(seatNumber) && !seat.isAvailable()) {
                seat.cancelSeat();
                // Also update transport available seats count
                Transport transport = getTransportById(transportId);
                if (transport != null) {
                    transport.cancelSeat();
                    updateTransport(transport);
                }
                Logger.log("Specific seat cancelled: " + seatNumber + " on transport " + transportId);
                return true;
            }
        }
        return false;
    }

    public int getTotalTransports() {
        return transports.size();
    }

    public int getActiveTransportsCount() {
        return (int) transports.values().stream().filter(Transport::isActive).count();
    }

    public int getTotalVehicles() {
        return vehicles.size();
    }

    public int getOperationalVehiclesCount() {
        return (int) vehicles.values().stream().filter(Vehicle::isOperational).count();
    }

    public Map<TransportType, Long> getTransportCountByType() {
        return transports.values().stream()
                .collect(Collectors.groupingBy(Transport::getType, Collectors.counting()));
    }

    public double getAverageOccupancyRate() {
        return transports.values().stream()
                .mapToDouble(transport -> {
                    double occupancyRate = (double) (transport.getTotalSeats() - transport.getAvailableSeats()) / transport.getTotalSeats() * 100;
                    return occupancyRate;
                })
                .average()
                .orElse(0.0);
    }

    public List<Transport> getFullyBookedTransports() {
        return transports.values().stream()
                .filter(transport -> transport.getAvailableSeats() == 0)
                .collect(Collectors.toList());
    }

    private void initializeSeatsForTransport(Transport transport) {
        List<Seat> seats = new ArrayList<>();
        int totalSeats = transport.getTotalSeats();
        
        for (int i = 1; i <= totalSeats; i++) {
            String seatNumber = generateSeatNumber(transport.getType(), i);
            Seat seat = new Seat(transport.getTransportId() + "_SEAT_" + i, 
                               transport.getTransportId(), seatNumber);
            
            // Set seat type based on position
            if (i % 6 == 1 || i % 6 == 0) { // Window seats
                seat.setSeatType("Window");
            } else if (i % 3 == 2) { // Aisle seats
                seat.setSeatType("Aisle");
            } else { // Middle seats
                seat.setSeatType("Middle");
            }
            
            seats.add(seat);
        }
        
        transportSeats.put(transport.getTransportId(), seats);
    }

    private String generateSeatNumber(TransportType type, int seatIndex) {
        switch (type) {
            case FLIGHT:
                int row = (seatIndex - 1) / 6 + 1;
                char seatLetter = (char) ('A' + (seatIndex - 1) % 6);
                return row + String.valueOf(seatLetter);
            case TRAIN:
                return "T" + seatIndex;
            case BUS:
                return "B" + seatIndex;
            case CAR:
                return "C" + seatIndex;
            default:
                return String.valueOf(seatIndex);
        }
    }

    private void loadTransportsFromFile() {
        try {
            List<String> lines = FileHandler.readFromFile(TRANSPORTS_FILE);
            for (String line : lines) {
                Transport transport = parseTransportFromString(line);
                if (transport != null) {
                    transports.put(transport.getTransportId(), transport);
                }
            }
        } catch (Exception e) {
            Logger.error("Failed to load transports from file: " + e.getMessage());
        }
    }

    private void saveTransportsToFile() {
        try {
            FileHandler.clearFile(TRANSPORTS_FILE);
            for (Transport transport : transports.values()) {
                String transportString = convertTransportToString(transport);
                FileHandler.writeToFile(TRANSPORTS_FILE, transportString);
            }
        } catch (Exception e) {
            Logger.error("Failed to save transports to file: " + e.getMessage());
        }
    }

    private void loadVehiclesFromFile() {
        try {
            List<String> lines = FileHandler.readFromFile(VEHICLES_FILE);
            for (String line : lines) {
                Vehicle vehicle = parseVehicleFromString(line);
                if (vehicle != null) {
                    vehicles.put(vehicle.getVehicleId(), vehicle);
                }
            }
        } catch (Exception e) {
            Logger.error("Failed to load vehicles from file: " + e.getMessage());
        }
    }

    private void saveVehiclesToFile() {
        try {
            FileHandler.clearFile(VEHICLES_FILE);
            for (Vehicle vehicle : vehicles.values()) {
                String vehicleString = convertVehicleToString(vehicle);
                FileHandler.writeToFile(VEHICLES_FILE, vehicleString);
            }
        } catch (Exception e) {
            Logger.error("Failed to save vehicles to file: " + e.getMessage());
        }
    }

    private Transport parseTransportFromString(String transportString) {
        try {
            String[] parts = transportString.split("\\|");
            if (parts.length >= 4) {
                Transport transport = new Transport(parts[0], TransportType.valueOf(parts[1]), 
                                                  parts[2], parts[3]);
                if (parts.length > 4) {
                    transport.setFare(Double.parseDouble(parts[4]));
                }
                if (parts.length > 5) {
                    transport.setAvailableSeats(Integer.parseInt(parts[5]));
                }
                return transport;
            }
        } catch (Exception e) {
            Logger.error("Failed to parse transport: " + e.getMessage());
        }
        return null;
    }

    private String convertTransportToString(Transport transport) {
        return String.join("|",
            transport.getTransportId(), transport.getType().name(),
            transport.getDeparture(), transport.getArrival(),
            String.valueOf(transport.getFare()), String.valueOf(transport.getAvailableSeats()));
    }

    private Vehicle parseVehicleFromString(String vehicleString) {
        try {
            String[] parts = vehicleString.split("\\|");
            if (parts.length >= 4) {
                Vehicle vehicle = new Vehicle(parts[0], parts[1], 
                                            TransportType.valueOf(parts[2]), Integer.parseInt(parts[3]));
                if (parts.length > 4) {
                    vehicle.setOperational(Boolean.parseBoolean(parts[4]));
                }
                return vehicle;
            }
        } catch (Exception e) {
            Logger.error("Failed to parse vehicle: " + e.getMessage());
        }
        return null;
    }

    private String convertVehicleToString(Vehicle vehicle) {
        return String.join("|",
            vehicle.getVehicleId(), vehicle.getModel(), vehicle.getVehicleType().name(),
            String.valueOf(vehicle.getSeatsAvailable()), String.valueOf(vehicle.isOperational()));
    }
}
