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
        // 1. Program başlarken eski rezervasyonları yükle
        Object data = FileManager.loadData(FILE_NAME);
        
        if (data != null) {
            this.reservations = (List<Reservation>) data;
        } else {
            this.reservations = new ArrayList<>();
        }
    }

    /**
     * Yeni bir rezervasyon oluşturur, koltuğu kapatır ve dosyaya kaydeder.
     */
    public Reservation makeReservation(Flight f, Passenger p, Seat s) {
        // 1. Validasyon: Koltuk zaten dolu mu?
        if (s.isReserved()) {
            System.out.println("Hata: Seçilen koltuk (" + s.getSeatNum() + ") zaten dolu!");
            return null;
        }

        // 2. Rezervasyon Kodu Üret (Örn: REZ-4821)
        String resCode = "REZ-" + (1000 + new Random().nextInt(9000));
        
        // 3. Tarih Al (Bugün)
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        // 4. Nesneyi Oluştur
        Reservation newRes = new Reservation(resCode, f, p, s, date);
        
        // 5. Koltuğu "DOLU" olarak işaretle (Kritik Adım)
        s.setReserveStatus(true);

        // 6. Listeye Ekle ve Kaydet
        reservations.add(newRes);
        FileManager.saveData(FILE_NAME, this.reservations);
        
        System.out.println("Rezervasyon Başarılı: " + resCode);
        return newRes;
    }

    /**
     * Rezervasyon koduna göre iptal işlemi yapar ve koltuğu boşa çıkarır.
     */
    public boolean cancelReservation(String resCode) {
        for (Reservation res : reservations) {
            // Kodu bul ve eğer zaten iptal edilmemişse işlem yap
            if (res.getReservationCode().equals(resCode) && res.isActive()) {
                
                // 1. Rezervasyonu pasife çek
                res.cancel();
                
                // 2. Koltuğu tekrar "BOŞ" yap (Kritik Adım)
                if (res.getSeat() != null) {
                    res.getSeat().setReserveStatus(false);
                }
                
                // 3. Dosyayı güncelle
                FileManager.saveData(FILE_NAME, this.reservations);
                System.out.println("Rezervasyon iptal edildi: " + resCode);
                return true;
            }
        }
        System.out.println("İptal edilecek aktif rezervasyon bulunamadı: " + resCode);
        return false;
    }
    
    /**
     * Tüm rezervasyonları döndürür.
     */
    public List<Reservation> getAllReservations() {
        return reservations;
    }
    
    /**
     * Belirli bir uçuşa ait rezervasyonları bulur (Raporlama için).
     */
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
