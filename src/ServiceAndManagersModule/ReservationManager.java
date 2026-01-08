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
    
    /**
     * Uçuş bilgileri (ID, Saat, Tarih vs.) değiştiğinde,
     * bu uçuştur yapılmış tüm rezervasyonları da günceller.
     * * @param oldFlightNum - Uçuşun eski numarası (Rezervasyonları bulmak için)
     * @param newFlightData - Yeni uçuş nesnesi (Güncellemek için)
     */
    public void updateFlightInfoInReservations(String oldFlightNum, Flight newFlightData) {
        boolean updated = false;
        
        for (Reservation res : reservations) {
            // Rezervasyondaki uçuşun numarası, değiştirilen uçuşun eski numarasıyla eşleşiyor mu?
            if (res.getFlight().getFlightNum().equals(oldFlightNum)) {
                
                // Eşleşiyorsa, rezervasyonun içindeki Flight nesnesini yenisiyle değiştir
                res.setFlight(newFlightData);
                
                // Eğer koltuk nesnesi de Flight'a bağlıysa, onu da yeni uçaktan çekmek gerekebilir.
                // Ancak basitlik adına Flight nesnesini değiştirmek genelde yeterlidir (tarih/saat için).
                // Daha gelişmiş versiyonda koltuk referansını da yenileyebilirsin:
                // Seat newSeat = newFlightData.getPlane().getSeat(res.getSeat().getSeatNum());
                // res.setSeat(newSeat);
                
                updated = true;
            }
        }

        if (updated) {
            FileManager.saveData(FILE_NAME, this.reservations); // Dosyayı kaydet
            System.out.println("İlgili rezervasyonlar yeni uçuş bilgileriyle güncellendi.");
        }
    }
    
    public Reservation makeReservation(Flight f, Passenger p, Seat s) {
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
    public boolean cancelReservation(String resCode, FlightManager flightManager) {
        for (Reservation res : reservations) {
            if (res.getReservationCode().equals(resCode)) {
                // 1. Rezervasyonu pasif yap
                res.setActive(false);
                
                // 2. KOLTUĞU BOŞA ÇIKAR (Kritik Kısım Burası!)
                // Rezervasyonun içindeki uçuş/koltuk bilgisi eski olabilir.
                // Bu yüzden FlightManager'dan güncel "Master" veriyi çekiyoruz.
                String flightNum = res.getFlight().getFlightNum();
                String seatNum = res.getSeat().getSeatNum();
                
                Flight masterFlight = flightManager.getFlightByNum(flightNum);
                if (masterFlight != null) {
                    Seat masterSeat = masterFlight.getPlane().getSeat(seatNum);
                    if (masterSeat != null) {
                        masterSeat.setReserveStatus(false); // Koltuğu BOŞ yap
                        
                        // Uçuş dosyasını güncelle (flights.dat güncellenir)
                        flightManager.updateFlight(masterFlight); 
                        System.out.println("Koltuk boşa çıkarıldı: " + seatNum);
                    }
                }
                
                // 3. Rezervasyon dosyasını kaydet
                FileManager.saveData(FILE_NAME, this.reservations);
                return true;
            }
        }
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
