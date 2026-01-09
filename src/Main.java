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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.format.DateTimeParseException;

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
        System.out.println("6. Geçmiş Uçuşları Listele (Arşiv)");
        System.out.println("7. Rapor Al (Asenkron - Scenario 2)");
        System.out.println("0. Çıkış");
        System.out.println("==========================================");
    }

    private static void listActiveFlights() {
        System.out.println("\n---  AKTİF UÇUŞLAR ---");
        List<Flight> flights = flightManager.getActiveFlights();
        
        if (flights.isEmpty()) {
            System.out.println("Sistemde aktif uçuş bulunmamaktadır.");
            return;
        }
        for (Flight f : flights) {
        	if(f.isExpired() == false)
        	{
        		int emptySeats = seatManager.getAvailableSeatCount(f);
                System.out.println(f.getFlightDetails());
                System.out.println("   -> Boş Koltuk: " + emptySeats + " / " + f.getPlane().getCapacity());
                System.out.println("------------------------------------------");
        	}
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
        System.out.print("İptal edilecek uçuşun numarası (Örn: TK586): ");
        String code = scanner.nextLine().trim();

        // 1. Önce rezervasyon nesnesini bulalım (Detaylara ihtiyacımız var)
        System.out.print("Yolcu TC/Pasaport No Girin: ");
        String passengerID = scanner.nextLine().trim();

        Reservation targetRes = reservationManager.findReservationByDetails(code, passengerID);
        
        if (targetRes == null) {
            System.out.println("HATA: Bu bilgilere ait aktif bir rezervasyon bulunamadı.");
            return;
        }

        System.out.println(">> Rezervasyon Bulundu: " + targetRes.getReservationCode());
        System.out.println(">> Yolcu: " + targetRes.getPassenger().getName() + " " + targetRes.getPassenger().getSurname());
        System.out.println(">> Koltuk: " + targetRes.getSeat().getSeatNum());

        System.out.print("İptali onaylıyor musunuz? (E/H): ");
        String confirmation = scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("E")) {
            System.out.println("İşlem iptal edildi.");
            return;
        }

        // 4. Perform Cancellation
        boolean success = reservationManager.cancelReservation(targetRes.getReservationCode(), flightManager);

        if (success) {
            System.out.println("Rezervasyon başarıyla iptal edildi.");
            System.out.println("Koltuk boşa çıkarıldı ve dosyalar güncellendi.");
        } else {
            System.out.println("İptal işlemi sırasında bir hata oluştu.");
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
        System.out.println("\n--- YENİ UÇUŞ EKLE (ADMIN) ---");
        
        System.out.print("Uçuş No (Örn: TK999): ");
        String fNum = scanner.nextLine();
        
        System.out.print("Kalkış Yeri: ");
        String dep = scanner.nextLine();
        
        System.out.print("Varış Yeri: ");
        String arr = scanner.nextLine();
        
        // --- Tarih kontrolu (sadece takvimde var olan tarihler girilebiliyor) ---
        String date = "";
        boolean validDate = false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);
        while (!validDate) {
            System.out.print("Tarih (dd-MM-yyyy): ");
            date = scanner.nextLine().trim();
            try {
                LocalDate parsedDate = LocalDate.parse(date, formatter);
                if (parsedDate.isBefore(LocalDate.now())) {
                    System.out.println("HATA: Geçmişe uçuş ekleyemezsiniz!");
                } else {
                    validDate = true;
                }
            } catch (DateTimeParseException e) {
                System.out.println("HATA: Geçersiz tarih! Lütfen 'Gün-Ay-Yıl' formatında girin (Örn: 25-06-2026).");
            }
        }

        // 1. Uçuş Saati (Opsiyonel olarak eklendi, boş kalmasın diye)
        System.out.print("Uçuş Saati (Örn: 14:30): ");
        String hour = scanner.nextLine();

        // 2. Uçuş Süresi
        System.out.print("Uçuş Süresi (Örn: 2h 30m): ");
        String duration = scanner.nextLine();

        // 3. Uçak Modeli
        System.out.print("Uçak Modeli (Örn: Boeing 737, Airbus A320): ");
        String planeModel = scanner.nextLine();

        // 4. Kapasite (Sayı kontrolü yaparak)
        int capacity = 0;
        boolean validCap = false;
        while (!validCap) {
            System.out.print("Uçak Kapasitesi (Örn: 180): ");
            try {
                capacity = Integer.parseInt(scanner.nextLine());
                if (capacity > 0) {
                    validCap = true;
                } else {
                    System.out.println("Kapasite 0'dan büyük olmalıdır.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Lütfen geçerli bir sayı giriniz!");
            }
        }
        // Verilen girdilere gore ucagin olusturulmasi 
        Plane p = new Plane("PL-" + new Random().nextInt(10000), planeModel, capacity);    
        Route r = new Route(dep, arr, "GENEL");
        
        // Girilen bilgilerle uçuşu oluşturuyoruz
        Flight f = new Flight(fNum, r, date, hour, duration, p);
    
        flightManager.addFlight(f);
        System.out.println("Yeni uçuş ve uçak bilgileri sisteme başarıyla eklendi!");
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