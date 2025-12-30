package FlightManagementModule;

import java.io.Serializable;

public class Route implements Serializable {
    // Attributes (UML'den)
    private String departurePlace;
    private String arrivalPlace;
    private String airportCode;

    // Constructor
    public Route(String departurePlace, String arrivalPlace, String airportCode) {
        this.departurePlace = departurePlace;
        this.arrivalPlace = arrivalPlace;
        this.airportCode = airportCode;
    }

    // Methods (UML'den)
    public String getRouteDetails() {
        return null; // TODO: Implement logic later
    }

    // Getter methods (Gerekli olacağı için eklendi)
    public String getDeparturePlace() { return departurePlace; }
    public String getArrivalPlace() { return arrivalPlace; }
    public String getAirportCode() { return airportCode; }
}
