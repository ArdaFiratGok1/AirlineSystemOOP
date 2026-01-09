package ScenariosModule;

import FlightManagementModule.Plane;
import FlightManagementModule.Seat;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Scenario1 {

    //GUI için Simülasyon Metodu

    public static void runSimulationForGUI(JTextArea logArea, boolean isSynchronized) {
        
        //Her test için sıfır, temiz bir uçak oluşturuyoruz
        Plane simulationPlane = new Plane("SIM-TEST", "Boeing 737", 180);
        int passengerCount = 90; //90 Yolcu saldıracak

        SwingUtilities.invokeLater(() -> {
            logArea.setText(""); //Ekranı temizle
            logArea.append(">> SİMÜLASYON BAŞLATILIYOR...\n");
            logArea.append(">> MOD: " + (isSynchronized ? "SAFE (GÜVENLİ - SYNCHRONIZED)" : "UNSAFE (GÜVENSİZ - RACE CONDITION)") + "\n");
            logArea.append(">> Yolcu Sayısı: " + passengerCount + " | Hedef: Rastgele Koltuk Kapmaca\n\n");
        });

        ExecutorService executor = Executors.newFixedThreadPool(passengerCount);

        for (int i = 0; i < passengerCount; i++) {
            final int pId = i + 1;
            executor.execute(() -> {
                boolean seated = false;
                Random random = new Random();

                //Yolcu oturana kadar (veya yer bulamayana kadar) dener                
                while (!seated) {
                    try {
                        //koltuk seçimi dokümandaki gibi rastgele
                        String seatNum = (random.nextInt(30) + 1) + "" + (char)('A' + random.nextInt(6));
                        
                        
                        if (isSynchronized) {
                            
                            synchronized (simulationPlane) {
                                seated = tryBookSeat(simulationPlane, seatNum, pId, logArea, false);
                            }
                        } else {
                            
                            seated = tryBookSeat(simulationPlane, seatNum, pId, logArea, true);
                        }
                        
                        
                        if (!seated) Thread.sleep(10); 

                    } catch (Exception e) {}
                }
            });
        }

        executor.shutdown();
        
        
        new Thread(() -> {
            try {
                executor.awaitTermination(10, TimeUnit.SECONDS);
                
                
                long totalOccupied = simulationPlane.getSeats().values().stream().filter(Seat::isReserved).count();
                
                SwingUtilities.invokeLater(() -> {
                    logArea.append("\n--------------------------------\n");
                    logArea.append("🏁 SİMÜLASYON BİTTİ!\n");
                    logArea.append("Beklenen Doluluk: " + passengerCount + "\n");
                    logArea.append("Gerçekleşen Doluluk: " + totalOccupied + "\n");
                    
                    if (totalOccupied == passengerCount) {
                        logArea.append("✅ SONUÇ: BAŞARILI (Veri kaybı yok)\n");
                    } else {
                        logArea.append("❌ SONUÇ: HATALI (Veri kaybı / Çakışma var!)\n");
                        logArea.append(">> " + (passengerCount - totalOccupied) + " yolcu bilet aldığını sandı ama alamadı.\n");
                    }
                });
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

   
    private static boolean tryBookSeat(Plane plane, String seatNum, int pId, JTextArea logArea, boolean addDelay) {
        Seat seat = plane.getSeat(seatNum);
        
        
        if (seat != null && !seat.isReserved()) {
            
            
            if (addDelay) {
                try { Thread.sleep(5); } catch (InterruptedException e) {}
            }

           
            seat.setReserveStatus(true);
            
            SwingUtilities.invokeLater(() -> logArea.append("Yolcu " + pId + " -> " + seatNum + " koltuğunu aldı.\n"));
            return true;
        }
        return false;
    }
}