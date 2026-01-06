package ServiceAndManagersModule;

import FlightManagementModule.SeatType;

//Paket ve import gerekmez (Aynı dizindeler)

public class CalculatePrice {

 // Plane sınıfındaki mantıkla uyumlu taban fiyatlar
 private static final double BUSINESS_BASE_PRICE = 5000.0;
 private static final double ECONOMY_BASE_PRICE = 1500.0;
 private static final double BAGGAGE_FEE = 500.0; // Bagaj varsa eklenecek ücret

 /**
  * Koltuk tipi ve bagaj durumuna göre fiyat hesaplar.
  */
 public double calculate(SeatType type, boolean hasBaggage) {
     double finalPrice = 0.0;

     // 1. Taban Fiyatı Belirle
     if (type == SeatType.BUSINESS) {
         finalPrice = BUSINESS_BASE_PRICE;
     } else {
         finalPrice = ECONOMY_BASE_PRICE;
     }

     // 2. Bagaj Ücretini Ekle
     if (hasBaggage) {
         finalPrice += BAGGAGE_FEE;
     }

     return finalPrice;
 }

 /**
  * Fiyata %10 indirim uygular.
  */
 public double applyDiscount(double price) { // 25 YAS ALTINA UYGULANABILIR
     // %10 indirim = Fiyatın %90'ı
     return price * 0.90;
 }
}