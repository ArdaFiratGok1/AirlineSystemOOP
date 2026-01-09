package ServiceAndManagersModule;

import java.util.Map;

import FlightManagementModule.Flight;
import FlightManagementModule.Plane;
import FlightManagementModule.Seat;

//Paket ve diğer sınıf importları silindi (Aynı dizindeler)

public class SeatManager {

	
	
	                   //BU KISIM SILINDI CUNKU PLANE'IN CONSTRUCTERI BUNU ZATEN YAPIYO
 /*public void createSeatingArrangement(Plane p) {
     if (p != null) {
         p.initializeSeats(); 
     }
 }
 
 */    


 public int getAvailableSeatCount(Flight f) {
     if (f == null || f.getPlane() == null) return 0;

     // Plane icinden mapi al
     Map<String, Seat> seats = f.getPlane().getSeats();
     int count = 0;

     for (Seat s : seats.values()) {
         if (!s.isReserved()) { // Eğer rezerve değilse say
             count++; 
         }
     }
     return count;
 }
 // koltuk dogru mu yanlis mi girdisi
 public boolean bookSeat(Flight f, String seatNum) {
     if (f == null || f.getPlane() == null) return false;
     Seat seat = f.getPlane().getSeat(seatNum);
     if (seat == null) {  
         //Unit Test icin exception fırlatiyoruz.
         throw new IllegalArgumentException("Koltuk bulunamadı: " + seatNum);        		
     }

     if (seat.isReserved()) {
         System.out.println("Hata: Koltuk zaten dolu -> " + seatNum);
         return false;
     }

     seat.setReserveStatus(true);
     return true;
 }
}