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

    // GUI için Simülasyon Metodu
    // isSynchronized: TRUE ise Güvenli (Synchronized), FALSE ise Güvensiz (Race Condition) çalışır.
    public static void runSimulationForGUI(JTextArea logArea, boolean isSynchronized) {
        
        // Her test için sıfır, temiz bir uçak oluşturuyoruz
        Plane simulationPlane = new Plane("SIM-TEST", "Boeing 737", 180);
        int passengerCount = 90; // 90 Yolcu saldıracak

        SwingUtilities.invokeLater(() -> {
            logArea.setText(""); // Ekranı temizle
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

                // Yolcu oturana kadar (veya yer bulamayana kadar) dener
                // Amaç: Race condition yaratmak için aynı anda saldırmalarını sağlamak
                while (!seated) {
                    try {
                        // Rastgele koltuk seç: 1A ... 30F arası
                        String seatNum = (random.nextInt(30) + 1) + "" + (char)('A' + random.nextInt(6));
                        
                        // --- KRİTİK NOKTA ---
                        if (isSynchronized) {
                            // GÜVENLİ MOD: Uçağı kilitliyoruz (Lock)
                            synchronized (simulationPlane) {
                                seated = tryBookSeat(simulationPlane, seatNum, pId, logArea, false);
                            }
                        } else {
                            // GÜVENSİZ MOD: Kilitleme yok, herkes aynı anda erişiyor
                            // Hata oluşsun diye yapay gecikme (sleep) ekliyoruz
                            seated = tryBookSeat(simulationPlane, seatNum, pId, logArea, true);
                        }
                        
                        // Eğer oturamadıysa döngü devam eder, başka koltuk dener...
                        // Sonsuz döngüye girmemesi için basit bir fren:
                        if (!seated) Thread.sleep(10); 

                    } catch (Exception e) {}
                }
            });
        }

        executor.shutdown();
        
        // Sonuçları Bekle ve Yazdır
        new Thread(() -> {
            try {
                executor.awaitTermination(10, TimeUnit.SECONDS);
                
                // Toplam dolu koltuk sayısını hesapla
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

    // Yardımcı Metod: Koltuk Rezerve Etme Denemesi
    private static boolean tryBookSeat(Plane plane, String seatNum, int pId, JTextArea logArea, boolean addDelay) {
        Seat seat = plane.getSeat(seatNum);
        
        // 1. Kontrol (Check)
        if (seat != null && !seat.isReserved()) {
            
            // Unsafe modda hatayı garantilemek için araya yapay gecikme sokuyoruz
            // Bu sırada başka bir thread de buraya girip koltuğu boş sanacak!
            if (addDelay) {
                try { Thread.sleep(5); } catch (InterruptedException e) {}
            }

            // 2. İşlem (Act)
            seat.setReserveStatus(true);
            
            SwingUtilities.invokeLater(() -> logArea.append("Yolcu " + pId + " -> " + seatNum + " koltuğunu aldı.\n"));
            return true;
        }
        return false;
    }
}