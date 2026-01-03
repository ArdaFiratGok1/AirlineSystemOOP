package GUIModule;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import FlightManagementModule.Flight;
import FlightManagementModule.Seat;
import ReservationAndTicketingModule.Passenger;
import ReservationAndTicketingModule.Reservation;
import ServiceAndManagersModule.CalculatePrice;
import ServiceAndManagersModule.FlightManager;
import ServiceAndManagersModule.ReservationManager;
import ServiceAndManagersModule.SeatManager;

public class PassengerDashboardGUI extends JFrame {

    private User currentUser; // Giriş yapan kullanıcı
    private FlightManager flightManager;
    private ReservationManager resManager;
    private SeatManager seatManager;

    // Tablo Modelleri
    private DefaultTableModel flightTableModel;
    private DefaultTableModel resTableModel;

    // Seçim Alanları
    private JTextField txtSeatSelect;
    private JCheckBox chkBaggage;

    public PassengerDashboardGUI(User user) {
        this.currentUser = user;
        flightManager = new FlightManager();
        resManager = new ReservationManager();
        seatManager = new SeatManager();

        setTitle("YOLCU PANELİ - Hoşgeldin " + user.getUsername());
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- SEKMELİ YAPI (Tabs) ---
        JTabbedPane tabbedPane = new JTabbedPane();

        // SEKME 1: Bilet Alma
        JPanel bookingPanel = createBookingPanel();
        tabbedPane.addTab("Uçuş Ara & Bilet Al", bookingPanel);

        // SEKME 2: Rezervasyonlarım
        JPanel myResPanel = createMyReservationsPanel();
        tabbedPane.addTab("Rezervasyonlarım / İptal", myResPanel);

        add(tabbedPane);
        setVisible(true);
    }

    // --- PANEL 1: UÇUŞ LİSTESİ VE REZERVASYON ---
    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 1. Uçuş Tablosu
        String[] cols = {"Uçuş No", "Rota", "Tarih", "Saat", "Boş Koltuk"};
        flightTableModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(flightTableModel);
        loadFlights(); // Verileri yükle

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // 2. Alt Kısım (Satın Alma İşlemi)
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Rezervasyon Yap"));

        txtSeatSelect = new JTextField(5);
        chkBaggage = new JCheckBox("Ekstra Bagaj (+500 TL)");
        JButton btnBook = new JButton("Seçili Uçuşa Rezervasyon Yap");
        btnBook.setBackground(new Color(70, 130, 180));
        btnBook.setForeground(Color.WHITE);

        bottomPanel.add(new JLabel("İstenen Koltuk (Örn: 1A):"));
        bottomPanel.add(txtSeatSelect);
        bottomPanel.add(chkBaggage);
        bottomPanel.add(btnBook);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        // Buton Aksiyonu
        btnBook.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen tablodan bir uçuş seçin!");
                return;
            }

            String flightNum = (String) flightTableModel.getValueAt(selectedRow, 0);
            String seatNum = txtSeatSelect.getText().trim().toUpperCase();

            handleBooking(flightNum, seatNum);
        });

        return panel;
    }

    // --- PANEL 2: REZERVASYONLARIM ---
    private JPanel createMyReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] cols = {"Rezervasyon Kodu", "Uçuş", "Koltuk", "Durum"};
        resTableModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(resTableModel);
        loadMyReservations();

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // İptal Butonu
        JPanel bottomPanel = new JPanel();
        JButton btnCancel = new JButton("Seçili Rezervasyonu İptal Et");
        btnCancel.setBackground(Color.RED);
        btnCancel.setForeground(Color.WHITE);

        bottomPanel.add(btnCancel);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // İptal Aksiyonu
        btnCancel.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "İptal etmek için listeden seçim yapın.");
                return;
            }
            String resCode = (String) resTableModel.getValueAt(selectedRow, 0);
            handleCancel(resCode);
        });

        return panel;
    }

    // --- MANTIK METODLARI ---

    private void loadFlights() {
        flightTableModel.setRowCount(0);
        for (Flight f : flightManager.getAllFlights()) {
            int empty = seatManager.getAvailableSeatCount(f);
            Object[] row = {
                f.getFlightNum(),
                f.getRoute().getDeparturePlace() + " -> " + f.getRoute().getArrivalPlace(),
                f.getDate(),
                f.getTime(),
                empty + " / " + f.getPlane().getCapacity()
            };
            flightTableModel.addRow(row);
        }
    }

    private void loadMyReservations() {
        resTableModel.setRowCount(0);
        for (Reservation r : resManager.getAllReservations()) {
            // Sadece şu anki kullanıcıya ait olanları göster (İsim eşleşmesi - Basit yöntem)
            // Gerçek projede UserID ile eşleşmek daha doğru olur.
            // Burada User login ismini Passenger soyadına vs eşliyoruz varsayalım.
            // Test için hepsini gösteriyorum veya User içindeki isme göre filtreleyebilirsin.
            
            // Filtreleme mantığı:
            // if (r.getPassenger().getName().equals(currentUser.getUsername())) ...
            
            Object[] row = {
                r.getReservationCode(),
                r.getFlight().getFlightNum(),
                (r.getSeat() != null ? r.getSeat().getSeatNum() : "Yok"),
                (r.isActive() ? "AKTİF" : "İPTAL")
            };
            resTableModel.addRow(row);
        }
    }

   private void handleBooking(String flightNum, String seatNum) {
        Flight flight = flightManager.getFlightByNum(flightNum);
        
        if (flight == null) {
            JOptionPane.showMessageDialog(this, "Uçuş bulunamadı!");
            return;
        }

        try {
            Seat seat = flight.getPlane().getSeat(seatNum);
            
            // 1. Koltuk Var mı Kontrolü (Exception Fırlatmalı Manager Testi için)
            if (seat == null) {
                 // SeatManager'daki hatayı simüle etmek için ya manager'ı çağırırız
                 // ya da manuel hata fırlatırız.
                 // Doğrusu: SeatManager.bookSeat kullanmadan validasyon yapmaktır.
                 throw new IllegalArgumentException("Hata: Koltuk bulunamadı -> " + seatNum);
            }

            // 2. Koltuk Dolu mu Kontrolü (Sadece okuma yapıyoruz, yazma değil!)
            if (seat.isReserved()) {
                JOptionPane.showMessageDialog(this, "Bu koltuk maalesef DOLU.");
                return;
            }

            // --- KRİTİK DÜZELTME: seatManager.bookSeat() ÇAĞIRMIYORUZ! ---
            // Çünkü onu çağırırsak koltuğu doldurur, ReservationManager "Dolu" sanıp kaydetmez.

            // 3. Yolcu Nesnesi Oluştur
            // Not: ID ve Soyad şimdilik dummy (rastgele), gerçek projede User nesnesinden gelmeli.
            Passenger p = new Passenger("11111111111", currentUser.getUsername(), "Yolcu", "email@test.com");
            
            // 4. Rezervasyonu Yap (Bu metod hem koltuğu kapatacak hem dosyaya yazacak)
            Reservation res = resManager.makeReservation(flight, p, seat);
            
            if (res != null) {
                // 5. Uçuş Dosyasını Güncelle (Koltuk artık dolu olduğu için)
                flightManager.updateFlight(flight);
                
                JOptionPane.showMessageDialog(this, "Rezervasyon Başarılı! Koltuk: " + seatNum);
                
                // Tabloları Yenile
                loadFlights();
                loadMyReservations();
            } else {
                JOptionPane.showMessageDialog(this, "Rezervasyon oluşturulamadı (Manager Hatası).");
            }

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Beklenmedik Hata: " + ex.getMessage());
        }
    }

    private void handleCancel(String resCode) {
        boolean success = resManager.cancelReservation(resCode);
        if (success) {
            // Uçuş dosyasını da güncellemek gerekir (Main'deki mantık)
            // Kolaylık olsun diye tüm uçuşları refresh edebilirsin
            // Detaylısı Main.java'daki gibi yapılabilir.
            JOptionPane.showMessageDialog(this, "İptal Edildi.");
            loadMyReservations();
            loadFlights(); // Koltuk sayısı artsın diye
        } else {
            JOptionPane.showMessageDialog(this, "İptal edilemedi.");
        }
    }
}