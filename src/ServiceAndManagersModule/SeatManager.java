package ServiceAndManagersModule;

import java.util.Map;

import FlightManagementModule.Flight;
import FlightManagementModule.Plane;
import FlightManagementModule.Seat;

//Paket ve diğer sınıf importları silindi (Aynı dizindeler)

public class SeatManager {

 public void createSeatingArrangement(Plane p) {
     if (p != null) {
         p.initializeSeats(); 
     }
 }

 /**
  * Uçaktaki boş koltuk sayısını hesaplar.
  */
 public int getAvailableSeatCount(Flight f) {
     if (f == null || f.getPlane() == null) return 0;

     // Plane içindeki Map'i al
     Map<String, Seat> seats = f.getPlane().getSeats();
     int count = 0;

     // Sadece değerler (Seat nesneleri) üzerinde döngü kuruyoruz
     for (Seat s : seats.values()) {
         if (!s.isReserved()) { // Eğer rezerve değilse say
             count++; 
         }
     }
     return count;
 }

 /**
  * Koltuk rezerve etme işlemi.
  * Döküman gereği olmayan koltukta hata fırlatmalı veya false dönmeli.
  */
 public boolean bookSeat(Flight f, String seatNum) {
     if (f == null || f.getPlane() == null) return false;

     // Doğrudan getSeat ile çekiyoruz (HashMap avantajı)
     Seat seat = f.getPlane().getSeat(seatNum);

     // 1. Koltuk var mı?
     if (seat == null) {  
         //Unit Test için exception fırlatıyoruz.
         throw new IllegalArgumentException("Koltuk bulunamadı: " + seatNum);
         //return false; bunu acınca exceptionu kapatınca error veriyor
        		
     }

     // 2. Koltuk zaten dolu mu?
     if (seat.isReserved()) {
         System.out.println("Hata: Koltuk zaten dolu -> " + seatNum);
         return false;
     }

     // 3. Rezerve et
     seat.setReserveStatus(true);
     return true;
 }
}