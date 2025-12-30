package FlightManagementModule;

import java.io.Serializable;

public class Plane implements Serializable {
    // Attributes (UML'den)
    private String planeID;
    private String planeModel;
    private int capacity;
    private Seat[][] seatMatrix; // Dokümandaki gereksinim

    // Constructor
    public Plane(String planeID, String planeModel, int capacity) {
        this.planeID = planeID;
        this.planeModel = planeModel;
        this.capacity = capacity;
        this.seatMatrix = new Seat[30][6]; // Örnek boyut, initializeSeats ile doldurulacak
    }

    // Methods (UML'den)
    public Seat getSeat(String seatNum) {
        return null; // TODO: Implement logic later
    }

    public void initializeSeats() {
        // TODO: Implement logic to fill seatMatrix with Seat objects
    }
    
    // Getter methods
    public Seat[][] getSeatMatrix() { return seatMatrix; }
    public String getPlaneID() { return planeID; }
}