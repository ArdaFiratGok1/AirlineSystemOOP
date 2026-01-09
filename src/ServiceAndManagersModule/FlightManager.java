package ServiceAndManagersModule;

import java.util.List;
import FlightManagementModule.Flight;
import java.util.stream.Collectors;
import java.util.ArrayList;

public class FlightManager {
    private List<Flight> flights; //burası thread unsafe   
    private final String FILE_NAME = "flights.dat"; 

    @SuppressWarnings("unchecked")
    public FlightManager() {
        Object data = FileManager.loadData(FILE_NAME);
        
        // eger dosya varsa listeyi ona esitle, yoksa bos liste olustur
        if (data != null) {
            this.flights = (List<Flight>) data;
        } else {
            this.flights = new ArrayList<>();
        }
    }

    public void addFlight(Flight f) {
        this.flights.add(f);
        // her eklemede dosyayı güncelle
        FileManager.saveData(FILE_NAME, this.flights);
        System.out.println("Uçuş eklendi: " + f.getFlightNum());
    }

    public void removeFlight(String flightNum) {
        
    	boolean removed = flights.removeIf(f -> f.getFlightNum().equals(flightNum));
        if (removed) {
            //  silme basariliysa dosyayi guncelle 
            FileManager.saveData(FILE_NAME, this.flights);
            System.out.println("Uçuş silindi: " + flightNum);
        } else {
            System.out.println("Silinecek uçuş bulunamadı: " + flightNum);
        }
    }
    
    // AdminDashboardGUI burayi kullaniyo.
    public void updateFlight(Flight flightToUpdate) {
        // aslinda alttaki 2 parametreli metodu kendi ID'siyle çagirarak isi cozebiliriz.
        // bu sayede kod tekrari olmaz.
        updateFlight(flightToUpdate.getFlightNum(), flightToUpdate);
    }

    public void updateFlight(String originalFlightNum, Flight newFlightData) {
        boolean found = false;
        
        for (int i = 0; i < flights.size(); i++) {
            Flight current = flights.get(i);
            
            // listede orijinal numarayi (eski ID) ariyoruz
            if (current.getFlightNum().equals(originalFlightNum)) {
                flights.set(i, newFlightData); // eski nesneyi yenisiyle degistir
                found = true;
                break; 
            }
        }

        if (found) {
            FileManager.saveData(FILE_NAME, this.flights); // dosyayı guncelle
            System.out.println("Uçuş ve dosya güncellendi: " + originalFlightNum);
        } else {
            System.out.println("Hata: Güncellenecek uçuş bulunamadı (" + originalFlightNum + ")");
        }
    }

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
            // isExpired false ise (süresi geçmemisse) listeye ekle
            if (!f.isExpired()) {
                activeList.add(f);
            }
        }
        return activeList;
    }

    public List<Flight> getPastFlights() {
        List<Flight> pastList = new ArrayList<>();
        for (Flight f : flights) {
            // isExpired true ise (süresi geçmisse) listeye ekle 
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
