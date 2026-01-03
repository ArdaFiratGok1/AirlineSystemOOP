package UnitTestsModule;

import org.junit.jupiter.api.Test;

import FlightManagementModule.Flight;
import FlightManagementModule.Plane;
import FlightManagementModule.Route;
import ServiceAndManagersModule.FlightSearchEngine;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;


public class FlightSearchEngineTest {

    @Test
    public void testRouteFiltering() {
        FlightSearchEngine engine = new FlightSearchEngine();
        List<Flight> flights = new ArrayList<>();
        
        // Test için sahte uçak ve rota oluşturuyoruz
        Plane dummyPlane = new Plane("P1", "Boeing 737", 180); 
        // Not: Plane constructor'ın parametrelerine göre burası değişebilir.
        
        // Test verisi: İstanbul -> Ankara
        Route route1 = new Route("Istanbul", "Ankara", "IST");
        flights.add(new Flight("TK01", route1, "10-01-2026", "10:00", "1h", dummyPlane));

        // Test verisi: İzmir -> Antalya
        Route route2 = new Route("Izmir", "Antalya", "ADB");
        flights.add(new Flight("TK02", route2, "10-01-2026", "12:00", "1h", dummyPlane));

        // Metodu Çalıştır
        List<Flight> results = engine.searchFlights(flights, "Istanbul", "Ankara");

        // Kontrol Et (Assertion)
        assertEquals(1, results.size(), "Filtreleme sonucu 1 uçuş dönmeliydi.");
        assertEquals("TK01", results.get(0).getFlightNum(), "Dönen uçuş TK01 olmalıydı.");
    }

    @Test
    public void testExpiredFlightElimination() {
        FlightSearchEngine engine = new FlightSearchEngine();
        List<Flight> flights = new ArrayList<>();
        Plane dummyPlane = new Plane("P1", "TestPlane", 100);

        // Tarihi geçmiş uçuş (2020)
        Flight oldFlight = new Flight("OLD01", new Route("A", "B", "X"), "01-01-2020", "10:00", "1h", dummyPlane);
        
        // Gelecek uçuş (2030)
        Flight futureFlight = new Flight("NEW01", new Route("A", "B", "X"), "01-01-2030", "10:00", "1h", dummyPlane);

        flights.add(oldFlight);
        flights.add(futureFlight);

        // Metodu Çalıştır
        List<Flight> activeFlights = engine.filterActiveFlights(flights);

        // Kontrol Et
        assertEquals(1, activeFlights.size(), "Sadece 1 aktif uçuş kalmalıydı.");
        assertEquals("NEW01", activeFlights.get(0).getFlightNum(), "Gelecek uçuş listede olmalıydı.");
    }
}