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

    /**
     * Güncellenmiş uçuş nesnesini alır, listedeki eskisini bulup değiştirir.
     */
    public void updateFlight(Flight updatedFlight) {
        for (int i = 0; i < flights.size(); i++) {
            Flight current = flights.get(i);
            
            // Eğer ID'ler eşleşiyorsa (Doğru uçuşu bulduk)
            if (current.getFlightNum().equals(updatedFlight.getFlightNum())) {
                flights.set(i, updatedFlight); // Listeyi güncelle
                FileManager.saveData(FILE_NAME, this.flights); // Dosyayı güncelle
                System.out.println("Uçuş güncellendi: " + updatedFlight.getFlightNum());
                return;
            }
        }
        System.out.println("Güncellenecek uçuş bulunamadı!");
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
