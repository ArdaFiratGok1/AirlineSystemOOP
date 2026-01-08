package ServiceAndManagersModule;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import FlightManagementModule.Flight;


public class FlightSearchEngine implements Serializable {

    //normalde flightmanager içinde olabilir ama ayrıca testi yapıldığı için sınıfı da ayırdık ama normalde manager içinde olmalı.
	
	
    public List<Flight> searchFlights(List<Flight> flights, String dep, String arr) {
        List<Flight> result = new ArrayList<>();
        
        if (flights == null) return result;

        for (Flight f : flights) {
            // Null check önemli: Rota nesnesi olmayabilir
            if (f.getRoute() != null && 
                f.getRoute().getDeparturePlace().equalsIgnoreCase(dep) &&
                f.getRoute().getArrivalPlace().equalsIgnoreCase(arr)) {
                result.add(f);
            }
        }
        return result;
    }

    /**
     * Tarihi geçmiş uçuşları filtreler.
     * Sadece bugünün tarihine eşit veya gelecekteki uçuşları döndürür.
     */
    public List<Flight> filterActiveFlights(List<Flight> flights) {
        List<Flight> activeFlights = new ArrayList<>();
        
        if (flights == null) return activeFlights;

        // Tarih formatı: Gün-Ay-Yıl (Flight sınıfındaki formatla aynı olmalı)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate today = LocalDate.now();//BUNA BAKILACAK

        for (Flight f : flights) {
            try {
                // String tarihi LocalDate objesine çevir
                LocalDate flightDate = LocalDate.parse(f.getDate(), formatter);
                
                // Tarih kontrolü
                if (!flightDate.isBefore(today)) { // Bugün veya gelecek
                    activeFlights.add(f);
                }
            } catch (Exception e) {
                System.err.println("Tarih formatı hatası (" + f.getFlightNum() + "): " + f.getDate());
            }
        }
        return activeFlights;
    }
}
