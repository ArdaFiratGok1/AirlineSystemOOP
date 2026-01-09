package ServiceAndManagersModule;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import FlightManagementModule.Flight;
import FlightManagementModule.Seat;
import ReservationAndTicketingModule.Passenger;
import ReservationAndTicketingModule.Reservation;

public class ReservationManager {
    private List<Reservation> reservations;
    private final String FILE_NAME = "reservations.dat";

    @SuppressWarnings("unchecked")
    public ReservationManager() {
    	// once gecmis dosyayi yukle 
    	Object data = FileManager.loadData(FILE_NAME);
        
        if (data != null) {
            this.reservations = (List<Reservation>) data;
        } else {
            this.reservations = new ArrayList<>();
        }
    }
    
    public Reservation findReservationByDetails(String flightNum, String passengerID) {
        for (Reservation res : reservations) {
        	if (res.isActive() && 
                res.getFlight().getFlightNum().equalsIgnoreCase(flightNum) &&
                res.getPassenger().getPassengerID().equals(passengerID)) {
                return res;
            }
        }
        return null; 
    }

    // yeni bir rezervasyon olusturur, koltugu kapatir ve dosyaya kaydeder
    public void updateFlightInfoInReservations(String oldFlightNum, Flight newFlightData) {
        boolean updated = false;
        
        for (Reservation res : reservations) {

        	if (res.getFlight().getFlightNum().equals(oldFlightNum)) 
        	{
                res.setFlight(newFlightData);
                updated = true;
            }
        }

        if (updated) {
            FileManager.saveData(FILE_NAME, this.reservations); // Dosyayi kaydet
            System.out.println("İlgili rezervasyonlar yeni uçuş bilgileriyle güncellendi.");
        }
    }
    
    public Reservation makeReservation(Flight f, Passenger p, Seat s) {
        if (s.isReserved()) {
            System.out.println("Hata: Seçilen koltuk (" + s.getSeatNum() + ") zaten dolu!");
            return null;
        }
        // rastgele rezervasyon kodu uretme
        String resCode = "REZ-" + (1000 + new Random().nextInt(9000));
        
        // tarih
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        // rezervasyon yapilmasi
        Reservation newRes = new Reservation(resCode, f, p, s, date);       
        // koltugu dolu olarak isaretleme
        s.setReserveStatus(true);
        // listeye ekle
        reservations.add(newRes);
        FileManager.saveData(FILE_NAME, this.reservations);
        System.out.println("Rezervasyon Başarılı: " + resCode);
        return newRes;
    }
    
    public boolean cancelReservation(String resCode, FlightManager flightManager) {
        for (Reservation res : reservations) {
            if (res.getReservationCode().equals(resCode)) {
                //  dolu yapar
                res.setActive(false);
                // rezervasyonun icinde ucus/koltuk bilgisi eski olabilir.
                // bu yüzden FlightManager'dan güncel "Master" veriyi çekiyoruz.
                String flightNum = res.getFlight().getFlightNum();
                String seatNum = res.getSeat().getSeatNum();
                Flight masterFlight = flightManager.getFlightByNum(flightNum);
                if (masterFlight != null) 
                {
                    Seat masterSeat = masterFlight.getPlane().getSeat(seatNum);
                    if (masterSeat != null)
                    {
                        masterSeat.setReserveStatus(false); // Koltugu BOS yap
                        // Ucus dosyasini guncelle (flights.dat güncellenir)
                        flightManager.updateFlight(masterFlight); 
                        System.out.println("Koltuk boşa çıkarıldı: " + seatNum);
                    }
                }
                
                FileManager.saveData(FILE_NAME, this.reservations);
                return true;
            }
        }
        return false;
    }
   
    public List<Reservation> getAllReservations() {
        return reservations;
    }
    
    public List<Reservation> getReservationsByFlight(String flightNum) {
        List<Reservation> flightRes = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r.getFlight().getFlightNum().equals(flightNum)) {
                flightRes.add(r);
            }
        }
        return flightRes;
    }
}
