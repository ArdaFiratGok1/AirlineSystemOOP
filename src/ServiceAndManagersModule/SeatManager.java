package ServiceAndManagersModule;

import FlightManagementModule.Flight;
import FlightManagementModule.Plane;

public class SeatManager {
    // Methods (UML'den - Kaynak: 15)
    
    public void createSeatingArrangement(Plane p) {
        // TODO: Trigger plane.initializeSeats()
    }

    public int getAvailableSeatCount(Flight f) {
        return 0; // TODO: Iterate seats and count non-reserved ones
    }

    public boolean bookSeat(Flight f, String seatNum) {
        return false; // TODO: Check availability and set reserved status
    }
}
