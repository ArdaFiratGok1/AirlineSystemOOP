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

    // --- DOLDURULAN KISIMLAR ---

    /**
     * Rota bilgisini formatlı bir şekilde döndürür.
     * Örn: "Istanbul -> Ankara [IST]"
     */
    public String getRouteDetails() {
        return departurePlace + " -> " + arrivalPlace + " [" + airportCode + "]";
    }

    /**
     * Terminalde nesneyi yazdırınca anlamlı çıktı verir.
     */
    @Override
    public String toString() {
        return getRouteDetails();
    }

    // --- GETTER METODLARI ---
    public String getDeparturePlace() { return departurePlace; }
    public String getArrivalPlace() { return arrivalPlace; }
    public String getAirportCode() { return airportCode; }
}