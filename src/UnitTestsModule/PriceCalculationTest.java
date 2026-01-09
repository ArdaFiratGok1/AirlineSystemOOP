package UnitTestsModule;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import FlightManagementModule.SeatType;      // Ana pakette olduğu için import gerekebilir veya package ayarı yapılmalı
import ServiceAndManagersModule.CalculatePrice;

public class PriceCalculationTest {

    @Test
    public void testEconomyPrice() {
        CalculatePrice calculator = new CalculatePrice();
        //Örnek: Ekonomi taban fiyat 1500, bagaj yok.
        double price = calculator.calculate(SeatType.ECONOMY, false);
        
        //Beklenen: 1500.0 (Bunu CalculatePrice sınıfında kodlayacağız)
        assertEquals(1500.0, price, "Ekonomi fiyatı hatalı!");
    }
    
    

    @Test
    public void testBusinessPrice() {
        CalculatePrice calculator = new CalculatePrice();
        //Örnek: Business taban fiyat 5000, bagaj var (+500 TL diyelim).
        double price = calculator.calculate(SeatType.BUSINESS, true);
        
        //Beklenen: 5500.0
        assertEquals(5500.0, price, "Business fiyatı hatalı!");
    }
}