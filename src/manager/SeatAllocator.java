package manager;

import model.Seat;
import model.Transport;
import util.Logger;
import java.util.List;
import java.util.ArrayList;

public class SeatAllocator {
    
    public List<Seat> autoAssignSeats(Transport transport, int numberOfSeats) {
        List<Seat> assignedSeats = new ArrayList<>();
        
        // This would be implemented with actual seat allocation logic
        // For now, returning empty list as placeholder
        Logger.log("Auto-assigning " + numberOfSeats + " seats for transport " + transport.getTransportId());
        
        return assignedSeats;
    }
    
    public Seat findBestAvailableSeat(List<Seat> availableSeats, String preferredSeatType) {
        if (availableSeats.isEmpty()) {
            return null;
        }
        
        // Try to find preferred seat type first
        for (Seat seat : availableSeats) {
            if (seat.getSeatType().equals(preferredSeatType)) {
                return seat;
            }
        }
        
        // Return first available seat if preferred type not found
        return availableSeats.get(0);
    }
}
