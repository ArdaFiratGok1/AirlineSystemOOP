package ServiceAndManagersModule;

import java.util.List;

import FlightManagementModule.Flight;
import FlightManagementModule.Seat;
import ReservationAndTicketingModule.Passenger;
import ReservationAndTicketingModule.Reservation;

import java.util.ArrayList;

public class ReservationManager {
    private List<Reservation> reservations;
    // Rezervasyonların tutulacağı dosya adı
    private final String FILE_NAME = "reservations.dat";

    public ReservationManager() {
        // 1. Program başlarken eski rezervasyonları yükle
        Object data = FileManager.loadData(FILE_NAME);
        
        if (data != null) {
            this.reservations = (List<Reservation>) data;
        } else {
            this.reservations = new ArrayList<>();
        }
    }

    public Reservation makeReservation(Flight f, Passenger p, Seat s) {
        // Not: Burada henüz logic yok, sadece kayıt mantığını gösteriyorum
        
        // Örnek iskelet:
        // Reservation newRes = new Reservation(..., f, p, s, ...);
        // reservations.add(newRes);
        
        // 2. Yeni rezervasyonu dosyaya kaydet
        FileManager.saveData(FILE_NAME, this.reservations);
        
        return null; // Şimdilik null
    }

    public void cancelReservation(String resCode) {
        // İptal mantığı...
        
        // 3. İptal sonrası dosyayı güncelle
        FileManager.saveData(FILE_NAME, this.reservations);
    }
}
