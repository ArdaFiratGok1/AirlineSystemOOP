package ScenariosModule;

import FlightManagementModule.Plane;
import FlightManagementModule.Seat;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Scenario1 {

    // Uçağımız (Ortak Kaynak)
    private static Plane plane = new Plane("SIM-001", "Boeing 737", 180);
    
    // Senaryo Ayarları
    private static final int PASSENGER_COUNT = 90; // 90 Yolcu saldıracak
    private static final boolean IS_SYNCHRONIZED = false; // TRUE yaparsan düzgün çalışır, FALSE yaparsan patlar

    public static void main(String[] args) {
        System.out.println("=== THREAD SİMÜLASYONU BAŞLIYOR ===");
        System.out.println("Mod: " + (IS_SYNCHRONIZED ? "SENKRONİZE (Güvenli)" : "ASENKRON (Güvensiz)"));
        System.out.println("Yolcu Sayısı: " + PASSENGER_COUNT);
        System.out.println("Toplam Koltuk: 180");
        System.out.println("-----------------------------------");

        // 90 tane Thread'i yönetecek bir havuz oluşturuyoruz
        ExecutorService executor = Executors.newFixedThreadPool(PASSENGER_COUNT);

        long startTime = System.currentTimeMillis();

        // 90 Tane Yolcu Görevi Başlat
        for (int i = 0; i < PASSENGER_COUNT; i++) {
            executor.execute(new PassengerTask(i + 1));
        }

        // Havuzu kapat ve bitmesini bekle
        executor.shutdown();
        try {
            // Tüm işlemlerin bitmesi için en fazla 10 saniye bekle
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();

        // --- SONUÇLARI SAY ---
        int occupiedCount = 0;
        // Plane sınıfını Map yaptığımız için values() ile dönüyoruz
        for (Seat s : plane.getSeats().values()) {
            if (s.isReserved()) {
                occupiedCount++;
            }
        }

        System.out.println("-----------------------------------");
        System.out.println("SİMÜLASYON BİTTİ!");
        System.out.println("Geçen Süre: " + (endTime - startTime) + " ms");
        System.out.println("Beklenen Dolu Koltuk: " + PASSENGER_COUNT);
        System.out.println("Gerçekleşen Dolu Koltuk: " + occupiedCount);

        if (occupiedCount == PASSENGER_COUNT) {
            System.out.println("SONUÇ:  BAŞARILI (Veri kaybı yok)");
        } else {
            System.out.println("SONUÇ:  HATALI (Race Condition oluştu!)");
            System.out.println("Fark: " + (PASSENGER_COUNT - occupiedCount) + " yolcu koltuğa oturduğunu sandı ama oturamadı.");
        }
    }

    // --- YOLCU GÖREVİ (THREAD CLASS) ---
    static class PassengerTask implements Runnable {
        private int passengerId;
        private Random random = new Random();

        public PassengerTask(int id) {
            this.passengerId = id;
        }

        @Override
        public void run() {
            boolean seated = false;

            // Yolcu oturana kadar denesin (veya pes etsin)
            // Döngü kuruyoruz çünkü rastgele seçtiği koltuk dolu olabilir
            while (!seated) {
                // 1. RASTGELE KOLTUK SEÇİMİ (Random)
                int row = random.nextInt(30) + 1; // 1-30 arası
                char col = (char) ('A' + random.nextInt(6)); // A-F arası
                String seatNum = row + "" + col; // Örn: "5C"

                // 2. THREAD MANTIĞI (Sync vs Async)
                if (IS_SYNCHRONIZED) {
                    // KİLİTLEME: Sadece tek bir thread bu bloğa girebilir
                    synchronized (plane) {
                        seated = tryToBook(seatNum);
                    }
                } else {
                    // KİLİTLEME YOK: Herkes aynı anda saldırır
                    seated = tryToBook(seatNum);
                }
                
                // Eğer oturamadıysa döngü başa döner, yeni random koltuk seçer
            }
        }

        // Koltuk kapma işlemi
        private boolean tryToBook(String seatNum) {
            Seat seat = plane.getSeat(seatNum);
            
            // Eğer koltuk varsa ve boşsa
            if (seat != null && !seat.isReserved()) {
                
                // ASENKRON HATAYI TETİKLEMEK İÇİN GECİKME
                if (!IS_SYNCHRONIZED) {
                    try { Thread.sleep(1); } catch (InterruptedException e) {}//rezerve etme 1 milisaniye geciktiriyoruz, bu surede koltuk hala bos gozukuyor
                }
                
                seat.setReserveStatus(true); // Rezerve et
                // System.out.println("Yolcu " + passengerId + " -> " + seatNum + " koltuğunu kaptı.");
                return true;
            }
            return false; // Doluysa veya yoksa başarısız
        }
    }
}