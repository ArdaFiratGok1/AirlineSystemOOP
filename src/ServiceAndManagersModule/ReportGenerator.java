package ServiceAndManagersModule;

import java.util.List;
import FlightManagementModule.Flight;
import FlightManagementModule.Seat;

// PDF Kaynak 29: Raporlama işlemi ayrı bir Thread içinde (Runnable) çalışmalıdır.
public class ReportGenerator implements Runnable {   //                                    ######### SENARYO 2 ICIN ######### 

    private FlightManager flightManager;

    public ReportGenerator(FlightManager flightManager) {
        this.flightManager = flightManager;
    }

    @Override
    public void run() {
        // PDF Kaynak 30: İşlem sırasında kullanıcıya bilgi verilmeli (Konsol simülasyonu)
        System.out.println("\n[Rapor Thread] >> Rapor hazırlanıyor... (Bu işlem 3 saniye sürecek)");

        try {
            // PDF Kaynak 29: Uzun süren işlemi simüle etmek için Thread uyutulur
            Thread.sleep(3000); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // PDF Kaynak 28: Doluluk oranlarını hesaplama mantığı
        List<Flight> flights = flightManager.getAllFlights();
        StringBuilder report = new StringBuilder();
        
        report.append("\n========================================\n");
        report.append("          UÇUŞ DOLULUK RAPORU           \n");
        report.append("========================================\n");
        
        if (flights.isEmpty()) {
            report.append("Sistemde kayıtlı uçuş bulunmamaktadır.\n");
        } else {
            for (Flight f : flights) {
                int capacity = f.getPlane().getCapacity();
                int occupied = 0;
                
                // Dolu koltukları say
                for (Seat s : f.getPlane().getSeats().values()) {
                    if (s.isReserved()) {
                        occupied++;
                    }
                }
                
                double ratio = (capacity > 0) ? ((double) occupied / capacity) * 100 : 0.0;
                
                report.append(String.format("Uçuş: %-7s | Dolu: %-3d / %-3d | Oran: %%%.2f\n", 
                        f.getFlightNum(), occupied, capacity, ratio));
            }
        }
        report.append("========================================\n");
        
        // PDF Kaynak 30: İşlem bitince sonucu asenkron olarak basıyoruz
        System.out.println(report.toString());
        System.out.println("[Rapor Thread] >> Rapor işlemi tamamlandı.");
    }
}