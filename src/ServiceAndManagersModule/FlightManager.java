package ServiceAndManagersModule;

//Dosya: src/FlightManager.java
import java.util.List;

import FlightManagementModule.Flight;

import java.util.ArrayList;

public class FlightManager {
 private List<Flight> flights;
 // Verilerin tutulacağı dosya adı
 private final String FILE_NAME = "flights.dat"; 

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
 }

 public void removeFlight(String flightNum) {
     // İskelet: Silme mantığı (removeIf Java 8+ özelliğidir)
     flights.removeIf(f -> f.getFlightNum().equals(flightNum));
     
     // 4. Silme yapınca dosyayı güncelle
     FileManager.saveData(FILE_NAME, this.flights);
 }

 public void updateFlight(Flight f) {
     // İskelet: Güncelleme mantığı buraya gelecek
     // ...
     
     // 5. Değişiklik olunca dosyayı güncelle
     FileManager.saveData(FILE_NAME, this.flights);
 }

 public List<Flight> getAllFlights() {
     return flights;
 }
}
