package ServiceAndManagersModule;

import java.util.List;
import FlightManagementModule.Flight;
import java.util.stream.Collectors;
import java.util.ArrayList;

public class FlightManager {
    private List<Flight> flights;//burası thread unsafe
    // Verilerin tutulacağı dosya adı
    private final String FILE_NAME = "flights.dat"; 

    @SuppressWarnings("unchecked")
    public FlightManager() {
        // 1. Program açıldığında önce dosyadan veriyi çek
        Object data = FileManager.loadData(FILE_NAME);
        
        // 2. Eğer dosya varsa listeyi ona eşitle, yoksa boş liste oluştur
        if (data != null) {
            this.flights = (List<Flight>) data;
        } else {
            this.flights = new ArrayList<>();
        }
    }

    public void addFlight(Flight f) {
        this.flights.add(f);
        // 3. Ekleme yapınca dosyayı güncelle
        FileManager.saveData(FILE_NAME, this.flights);
        System.out.println("Uçuş eklendi: " + f.getFlightNum());
    }

    public void removeFlight(String flightNum) {
        // RemoveIf: Eğer uçuş numarası eşleşiyorsa listeden sil
        boolean removed = flights.removeIf(f -> f.getFlightNum().equals(flightNum));
        
        if (removed) {
            // 4. Silme başarılıysa dosyayı güncelle
            FileManager.saveData(FILE_NAME, this.flights);
            System.out.println("Uçuş silindi: " + flightNum);
        } else {
            System.out.println("Silinecek uçuş bulunamadı: " + flightNum);
        }
    }

 // =================================================================
    // 1. METOD: Sadece rezervasyon yapıldığında çalışır (ID değişmez)
    // PassengerDashboardGUI burayı kullanır.
    // =================================================================
    public void updateFlight(Flight flightToUpdate) {
        // Aslında alttaki 2 parametreli metodu kendi ID'siyle çağırarak işi çözebiliriz.
        // Bu sayede kod tekrarı olmaz.
        updateFlight(flightToUpdate.getFlightNum(), flightToUpdate);
    }

    // =================================================================
    // 2. METOD: Admin panelinden uçuş bilgileri (ID dahil) değişirse çalışır
    // AdminDashboardGUI burayı kullanır.
    // =================================================================
    public void updateFlight(String originalFlightNum, Flight newFlightData) {
        boolean found = false;
        
        for (int i = 0; i < flights.size(); i++) {
            Flight current = flights.get(i);
            
            // Listede orijinal numarayı (Eski ID) arıyoruz
            if (current.getFlightNum().equals(originalFlightNum)) {
                flights.set(i, newFlightData); // Eski nesneyi yenisiyle değiştir
                found = true;
                break; // Bulduk, döngüden çık
            }
        }

        if (found) {
            FileManager.saveData(FILE_NAME, this.flights); // Dosyayı güncelle
            System.out.println("Uçuş ve dosya güncellendi: " + originalFlightNum);
        } else {
            System.out.println("Hata: Güncellenecek uçuş bulunamadı (" + originalFlightNum + ")");
        }
    }

    /**
     * ID'ye göre uçuş arama (Gerekli olabilir)
     */
    public Flight getFlightByNum(String flightNum) {
        for (Flight f : flights) {
            if (f.getFlightNum().equals(flightNum)) {
                return f;
            }
        }
        return null;
    }
    
    public List<Flight> getActiveFlights() {
        List<Flight> activeList = new ArrayList<>();
        for (Flight f : flights) {
            // isExpired false ise (süresi geçmemişse) listeye ekle
            if (!f.isExpired()) {
                activeList.add(f);
            }
        }
        return activeList;
    }

    public List<Flight> getPastFlights() {
        List<Flight> pastList = new ArrayList<>();
        for (Flight f : flights) {
            // isExpired true ise (süresi geçmişse) listeye ekle 
            if (f.isExpired()) {
                pastList.add(f);
            }
        }
        return pastList;
    }
    
    public List<Flight> getAllFlights() {
        return flights;
    }
}
