package UnitTestsModule;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import FlightManagementModule.Flight;
import FlightManagementModule.Plane;
import FlightManagementModule.Route;
import ServiceAndManagersModule.SeatManager;

public class SeatManagerTest {

    
    @Test
    public void testAvailableSeatCountDecrease() {
        SeatManager seatManager = new SeatManager();
        
        //Test ortamı hazırla
        Plane plane = new Plane("TEST-P", "TestModel", 180); // 180 koltuklu
        Route route = new Route("A", "B", "CODE");
        Flight flight = new Flight("TK01", route, "01-01-2026", "10:00", "2h", plane);
        
        //Başlangıçta hepsi boş olmalı (180)
        int initialCount = seatManager.getAvailableSeatCount(flight);
        assertEquals(180, initialCount, "Başlangıçta 180 boş koltuk olmalı.");

        //Bir koltuk rezerve et (Örn: 1A)
        seatManager.bookSeat(flight, "1A");

        //Sayının 179'a düşüp düşmediğini kontrol et
        int newCount = seatManager.getAvailableSeatCount(flight);
        assertEquals(179, newCount, "Rezervasyon sonrası sayı 1 azalmalı.");
    }

   
    @Test
    public void testExceptionOnInvalidSeat() {
        SeatManager seatManager = new SeatManager();
        
        Plane plane = new Plane("TEST-P", "TestModel", 180);
        Route route = new Route("A", "B", "CODE");
        Flight flight = new Flight("TK01", route, "01-01-2026", "10:00", "2h", plane);

        //"99Z" diye bir koltuk yok. Kodun IllegalArgumentException atmasını bekliyoruz.
        assertThrows(IllegalArgumentException.class, () -> {
            seatManager.bookSeat(flight, "99Z");
        }, "Olmayan koltuk için Exception fırlatılmalıydı!");
    }
}