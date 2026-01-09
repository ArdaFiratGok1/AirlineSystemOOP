package FlightManagementModule;

import java.io.Serializable;

public class Route implements Serializable {
    
    private String departurePlace;
    private String arrivalPlace;
    private String airportCode;

    
    public Route(String departurePlace, String arrivalPlace, String airportCode) {
        this.departurePlace = departurePlace;
        this.arrivalPlace = arrivalPlace;
        this.airportCode = airportCode;
    }

    

    
    public String getRouteDetails() {
        return departurePlace + " -> " + arrivalPlace + " [" + airportCode + "]";
    }

    
    @Override
    public String toString() {
        return getRouteDetails();
    }

    
    public String getDeparturePlace() { return departurePlace; }
    public String getArrivalPlace() { return arrivalPlace; }
    public String getAirportCode() { return airportCode; }
}