import java.util.List;
import java.util.Scanner;

import FlightManagementModule.Flight;
import FlightManagementModule.Plane;
import FlightManagementModule.Route;
import FlightManagementModule.Seat;
import ReservationAndTicketingModule.Passenger;
import ReservationAndTicketingModule.Reservation;
import ReservationAndTicketingModule.Ticket;
import ServiceAndManagersModule.CalculatePrice;
import ServiceAndManagersModule.FlightManager;
import ServiceAndManagersModule.ReservationManager;
import ServiceAndManagersModule.SeatManager;
import ServiceAndManagersModule.ReportGenerator;

import java.util.Random;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    
    // Yöneticiler (Managers)
    private static FlightManager flightManager = new FlightManager();
    private static ReservationManager reservationManager = new ReservationManager();
    private static SeatManager seatManager = new SeatManager();

    public static void main(String[] args) {
        // İlk açılışta hiç uçuş yoksa örnek verileri yükle
        initializeSystem();

        boolean isRunning = true;
        while (isRunning) {
            printMenu();
            System.out.print("Seçiminiz: ");
            int choice = -1;
            
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Lütfen sayı giriniz!");
                continue;
            }

            switch (choice) {
                case 1:
                    listActiveFlights();
                    break;
                case 2:
                    makeNewReservation();
                    break;
                case 3:
                    cancelReservation();
                    break;
                case 4:
                    listAllReservations();
                    break;
                case 5:
                    adminAddFlight();
                    break;
                case 6:
                    listPastFlights(); // Yeni case
                    break;
                case 7:
                    generateReportAsync(); 
                    break;
                case 0:
                    System.out.println("Sistemden çıkılıyor. İyi günler!");
                    isRunning = false;
                    break;
                default:
                    System.out.println("Geçersiz seçim, tekrar deneyin.");
            }
        }
    }

    // --- MENÜ VE İŞLEMLER ---

    private static void printMenu() {
        System.out.println("\n==========================================");
        System.out.println("      UÇUŞ REZERVASYON SİSTEMİ  ");
        System.out.println("==========================================");
        System.out.println("1. Aktif Uçuşları Listele (Bilet Alınabilir)");
        System.out.println("2. Bilet Al / Rezervasyon Yap");
        System.out.println("3. Rezervasyon İptal Et");
        System.out.println("4. Tüm Rezervasyonları Listele (Rapor)");
        System.out.println("5. Yeni Uçuş Ekle (Admin)");
        System.out.println("6. Geçmiş Uçuşları Listele (Arşiv)"); // YENİ
        System.out.println("0. Çıkış");
        System.out.println("==========================================");
    }

    private static void listActiveFlights() {
        System.out.println("\n--- ✈️ AKTİF UÇUŞLAR ---");
        List<Flight> flights = flightManager.getActiveFlights();
        
        if (flights.isEmpty()) {
            System.out.println("Sistemde aktif uçuş bulunmamaktadır.");
            return;
        }
        for (Flight f : flights) {
            int emptySeats = seatManager.getAvailableSeatCount(f);
            System.out.println(f.getFlightDetails());
            System.out.println("   -> Boş Koltuk: " + emptySeats + " / " + f.getPlane().getCapacity());
            System.out.println("------------------------------------------");
        }
    }

    private static void listPastFlights() {
        System.out.println("\n--- 🕰️ GEÇMİŞ UÇUŞLAR (ARŞİV) ---");
        List<Flight> flights = flightManager.getPastFlights();
        if (flights.isEmpty()) {
            System.out.println("Geçmiş uçuş kaydı yok.");
            return;
        }
        for (Flight f : flights) {
            // Geçmiş uçuşlarda boş koltuk sayısının bir önemi yoktur, sadece detay basıyoruz
            System.out.println(f.getFlightDetails());
            System.out.println("   -> DURUM: TAMAMLANDI");
            System.out.println("------------------------------------------");
        }
    }

    private static void makeNewReservation() {
        System.out.println("\n---  REZERVASYON YAP ---");
        
        // 1. Uçuş Seçimi
        System.out.print("Uçuş Numarasını Girin (Örn: TK101): ");
        String flightNum = scanner.nextLine().trim();
        
        Flight selectedFlight = flightManager.getFlightByNum(flightNum);
        if (selectedFlight == null) {
            System.out.println("HATA: Böyle bir uçuş bulunamadı!");
            return;
        }
        if (selectedFlight.isExpired()) {
            System.out.println("HATA: Bu uçuşun süresi geçmiş, bilet alamazsınız!");
            return;
        }

        // 2. Koltuk Durumunu Göster ve Seçim Yap
        System.out.println("Seçilen Uçuş: " + selectedFlight.getRoute().getDeparturePlace() + " -> " + selectedFlight.getRoute().getArrivalPlace());
        System.out.print("İstenen Koltuk No (Örn: 1A, 2B): ");
        String seatNum = scanner.nextLine().trim().toUpperCase();

        Seat selectedSeat = selectedFlight.getPlane().getSeat(seatNum);
        
        // Validasyonlar
        if (selectedSeat == null) {
            System.out.println("HATA: Böyle bir koltuk numarası yok.");
            return;
        }
        if (selectedSeat.isReserved()) {
            System.out.println("HATA: Bu koltuk dolu.");
            return;
        }

        // 3. Yolcu Bilgileri
        System.out.print("Yolcu Adı: ");
        String name = scanner.nextLine();
        System.out.print("Yolcu Soyadı: ");
        String surname = scanner.nextLine();
        System.out.print("TC/Pasaport No: ");
        String id = scanner.nextLine();
        System.out.print("İletişim (Tel/Email): ");
        String contact = scanner.nextLine();

        Passenger passenger = new Passenger(id, name, surname, contact);

        // 4. Rezervasyonu Gerçekleştir
        Reservation res = reservationManager.makeReservation(selectedFlight, passenger, selectedSeat);

        if (res != null) {
            // ÖNEMLİ: Koltuk durumu değiştiği için Uçuş Dosyasını güncelle!
            flightManager.updateFlight(selectedFlight);

            // 5. Fiyat Hesapla ve Bileti Bas
            System.out.print("Bagaj var mı? (E/H): ");
            String bagajSecim = scanner.nextLine();
            boolean hasBaggage = bagajSecim.equalsIgnoreCase("E");

            CalculatePrice calculator = new CalculatePrice();
            double price = calculator.calculate(selectedSeat.getSeatClass(), hasBaggage);
            
            // Bileti Oluştur
            Ticket ticket = new Ticket("BLT-" + res.getReservationCode(), res, price, hasBaggage ? 20.0 : 8.0);
            
            System.out.println("\n İŞLEM BAŞARILI! BİLETİNİZ HAZIR:");
            System.out.println(ticket.printTicket());
        } else {
            System.out.println("İşlem başarısız oldu.");
        }
    }

    private static void cancelReservation() {
        System.out.println("\n---  REZERVASYON İPTALİ ---");
        System.out.print("İptal edilecek Rezervasyon Kodu (Örn: REZ-1234): ");
        String code = scanner.nextLine().trim();

        // 1. Önce rezervasyon nesnesini bulalım (Detaylara ihtiyacımız var)
        Reservation targetRes = null;
        for (Reservation r : reservationManager.getAllReservations()) {
            if (r.getReservationCode().equals(code)) {
                targetRes = r;
                break;
            }
        }

        if (targetRes == null) {
            System.out.println("HATA: Bu kodla bir rezervasyon bulunamadı.");
            return;
        }

        if (!targetRes.isActive()) {
            System.out.println("HATA: Bu rezervasyon zaten iptal edilmiş.");
            return;
        }

        // 2. Rezervasyonu İptal Et (reservation.dat güncellenir)
        boolean success = reservationManager.cancelReservation(code);

        // 3. SENKRONİZASYON: Uçuş dosyasındaki koltuğu da boşa çıkarmamız lazım!
        if (success) {
            String flightNum = targetRes.getFlight().getFlightNum();
            String seatNum = targetRes.getSeat().getSeatNum();

            // FlightManager'dan 'Canlı' uçuşu çek
            Flight realFlight = flightManager.getFlightByNum(flightNum);
            
            if (realFlight != null) {
                // Canlı uçuşun koltuğunu bul ve boşalt
                Seat realSeat = realFlight.getPlane().getSeat(seatNum);
                if (realSeat != null) {
                    realSeat.setReserveStatus(false); // Koltuğu BOŞA düşür
                    
                    // 4. Uçuş dosyasını güncelle (flights.dat güncellenir)
                    flightManager.updateFlight(realFlight);
                    System.out.println(">> Veritabanı Güncellendi: " + flightNum + " seferili uçuşta " + seatNum + " nolu koltuk boşa çıkarıldı.");
                }
            }
            System.out.println(" Rezervasyon başarıyla iptal edildi.");
        } else {
            System.out.println(" İptal işlemi sırasında bir hata oluştu.");
        }
    }

    private static void listAllReservations() {
        System.out.println("\n---  TÜM REZERVASYONLAR ---");
        List<Reservation> list = reservationManager.getAllReservations();
        if (list.isEmpty()) {
            System.out.println("Henüz hiç rezervasyon yok.");
        } else {
            for (Reservation r : list) {
                System.out.println(r);
            }
        }
    }
    
    private static void generateReportAsync() {
        // Create the task using the ReportGenerator class
        ReportGenerator task = new ReportGenerator(flightManager);
        
        // Run it in a separate thread so it doesn't block the main menu
        Thread thread = new Thread(task);
        thread.start();
        
        System.out.println(">> (Main Thread) Rapor isteği alındı. Siz menüyü kullanmaya devam edebilirsiniz.");
    }

    private static void adminAddFlight() {
        System.out.println("\n--- ➕ YENİ UÇUŞ EKLE (ADMIN) ---");
        System.out.print("Uçuş No (Örn: TK999): ");
        String fNum = scanner.nextLine();
        
        System.out.print("Kalkış Yeri: ");
        String dep = scanner.nextLine();
        
        System.out.print("Varış Yeri: ");
        String arr = scanner.nextLine();
        
        System.out.print("Tarih (dd-MM-yyyy): ");
        String date = scanner.nextLine();
        
        // Otomatik nesneler
        Plane p = new Plane("PL-" + new Random().nextInt(1000), "Boeing 737", 180);
        Route r = new Route(dep, arr, "GENEL");
        Flight f = new Flight(fNum, r, date, "12:00", "2h", p);
        
        flightManager.addFlight(f);
        System.out.println("Yeni uçuş sisteme eklendi!");
    }

    // --- BAŞLANGIÇ VERİSİ ---
    private static void initializeSystem() {
        if (flightManager.getAllFlights().isEmpty()) {
            System.out.println(">> İlk kurulum: Test verileri yükleniyor...");
            Plane p1 = new Plane("PL-001", "Airbus A320", 180);
            Route r1 = new Route("Istanbul", "Ankara", "ESB");
            Flight f1 = new Flight("TK101", r1, "01-01-2026", "09:00", "1h", p1);

            Plane p2 = new Plane("PL-002", "Boeing 737", 180);
            Route r2 = new Route("Izmir", "Antalya", "AYT");
            Flight f2 = new Flight("TK202", r2, "02-01-2026", "14:30", "50m", p2);

            flightManager.addFlight(f1);
            flightManager.addFlight(f2);
        }
    }
}