package FlightManagementModule;

import java.io.Serializable;

public class Flight implements Serializable {
    // Attributes (UML'den)
    private String flightNum;
    private Route route;
    private String date;
    private String hour;
    private String duration;
    private Plane plane;

    // Constructor
    public Flight(String flightNum, Route route, String date, String hour, String duration, Plane plane) {
        this.flightNum = flightNum;
        this.route = route;
        this.date = date;
        this.hour = hour;
        this.duration = duration;
        this.plane = plane;
    }

    // Methods (UML'den)
    public boolean isExpired() {
        return false; // TODO: Implement date comparison logic later
    }

    public String getFlightDetails() {
        return null; // TODO: Implement string formatting later
    }

    // Getter methods (Erişim için gerekli)
    public String getFlightNum() { return flightNum; }
    public Route getRoute() { return route; }
    public Plane getPlane() { return plane; }
    public String getDate() { return date; }
}