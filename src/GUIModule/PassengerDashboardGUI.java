package GUIModule;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Map;

import FlightManagementModule.Flight;
import FlightManagementModule.Seat;
import ReservationAndTicketingModule.Passenger;
import ReservationAndTicketingModule.Reservation;
import ServiceAndManagersModule.FlightManager;
import ServiceAndManagersModule.ReservationManager;
import ServiceAndManagersModule.SeatManager;

public class PassengerDashboardGUI extends JFrame {

    private User currentUser;
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

        // TEMA AYARLARI
        setupTheme();

        setTitle("✈️ Yolcu Paneli - Hoşgeldiniz, " + user.getUsername());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        // --- 1. ÜST HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80)); // Koyu Lacivert
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel lblTitle = new JLabel("SkyTech Yolcu İşlemleri");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        
        JLabel lblUser = new JLabel("👤 " + currentUser.getUsername());
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblUser.setForeground(new Color(200, 200, 200));

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblUser, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- 2. SEKMELER ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        tabbedPane.addTab("  🔍 Uçuş Ara & Bilet Al  ", createBookingPanel());
        tabbedPane.addTab("  🎫 Rezervasyonlarım  ", createMyReservationsPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // ÇIKIŞ BUTONU
        JButton btnLogout = new JButton("ÇIKIŞ YAP");
        btnLogout.setBackground(new Color(231, 76, 60));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginGUI();
        });
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(btnLogout);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void setupTheme() {
        try {
            FlatLightLaf.setup();
            UIManager.put("Component.arc", 12);
            UIManager.put("Button.arc", 12);
            UIManager.put("Table.rowHeight", 30);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.selectionBackground", new Color(52, 152, 219));
            UIManager.put("Table.selectionForeground", Color.WHITE);
        } catch (Exception e) {}
    }

    // --- PANEL 1: UÇUŞ LİSTESİ VE REZERVASYON ---
    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setOpaque(false);

        // Arama Çubuğu (Bonus Özellik)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtSearch = new JTextField(20);
        txtSearch.putClientProperty("JTextField.placeholderText", "Şehir veya Uçuş No ara...");
        searchPanel.add(new JLabel("🔍 Filtrele: "));
        searchPanel.add(txtSearch);
        panel.add(searchPanel, BorderLayout.NORTH);

        // 1. Uçuş Tablosu
        String[] cols = {"Uçuş No", "Rota", "Tarih", "Saat", "Boş Koltuk"};
        flightTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(flightTableModel);
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        
        loadFlights(); // Verileri yükle

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Canlı Arama Mantığı
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(flightTableModel);
        table.setRowSorter(sorter);
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() {
                String text = txtSearch.getText();
                if (text.trim().length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        // 2. Alt Kısım (Satın Alma İşlemi - Modernize Edildi)
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBackground(new Color(235, 245, 251)); // Açık Mavi Arka Plan
        bottomPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(52, 152, 219)), " Rezervasyon İşlemleri "
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtSeatSelect = new JTextField(8);
        chkBaggage = new JCheckBox("Ekstra Bagaj (+500 TL)");
        chkBaggage.setOpaque(false);
        
        JButton btnShowSeats = new JButton("👁️ Koltuk Durumlarını Gör");
        btnShowSeats.setBackground(new Color(241, 196, 15)); // Sarı
        btnShowSeats.setForeground(Color.BLACK);

        JButton btnBook = new JButton("REZERVASYONU TAMAMLA");
        btnBook.setBackground(new Color(39, 174, 96)); // Yeşil
        btnBook.setForeground(Color.WHITE);
        btnBook.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Layout Yerleşimi
        gbc.gridx = 0; gbc.gridy = 0;
        bottomPanel.add(new JLabel("İstenen Koltuk (Örn: 1A):"), gbc);
        
        gbc.gridx = 1;
        bottomPanel.add(txtSeatSelect, gbc);
        
        gbc.gridx = 2;
        bottomPanel.add(btnShowSeats, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        bottomPanel.add(chkBaggage, gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        bottomPanel.add(btnBook, gbc);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        // --- AKSİYONLAR ---
        
        // Koltuk Haritasını Göster Butonu
        btnShowSeats.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Önce listeden bir uçuş seçiniz!");
                return;
            }
            int modelRow = table.convertRowIndexToModel(selectedRow);
            String flightNum = (String) flightTableModel.getValueAt(modelRow, 0);
            showSeatMap(flightNum);
        });

        // Rezervasyon Yap Butonu
        btnBook.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen tablodan bir uçuş seçin!");
                return;
            }
            int modelRow = table.convertRowIndexToModel(selectedRow);
            String flightNum = (String) flightTableModel.getValueAt(modelRow, 0);
            String seatNum = txtSeatSelect.getText().trim().toUpperCase();

            handleBooking(flightNum, seatNum);
        });

        return panel;
    }

    // --- PANEL 2: REZERVASYONLARIM ---
    private JPanel createMyReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setOpaque(false);

        String[] cols = {"Rezervasyon Kodu", "Uçuş", "Koltuk", "Durum"};
        resTableModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(resTableModel);
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        
        loadMyReservations();

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // İptal Butonu
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("SEÇİLİ REZERVASYONU İPTAL ET");
        btnCancel.setBackground(new Color(231, 76, 60)); // Kırmızı
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 12));

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
    
    // --- YENİ ÖZELLİK: GÖRSEL KOLTUK HARİTASI ---
    private void showSeatMap(String flightNum) {
        Flight flight = flightManager.getFlightByNum(flightNum);
        if (flight == null) return;
        
        // 1. AYARLAR
        int businessRows = 5; // İlk 5 sıra Business olsun (Sarı)
        
        // 2. PANEL YAPISI (7 Sütun: 3 Koltuk + 1 Boşluk + 3 Koltuk)
        JPanel seatPanel = new JPanel(new GridLayout(0, 7, 5, 5)); 
        seatPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Kenar boşluğu
        
        Map<String, Seat> seats = flight.getPlane().getSeats();
        int rows = flight.getPlane().getCapacity() / 6; 
        
        for (int i = 1; i <= rows; i++) {
            // --- SOL TARA (A, B, C) ---
            addSeatToPanel(seatPanel, i, 'A', seats, businessRows);
            addSeatToPanel(seatPanel, i, 'B', seats, businessRows);
            addSeatToPanel(seatPanel, i, 'C', seats, businessRows);
            
            // --- KORİDOR BOŞLUĞU (Görünmez Panel) ---
            JLabel aisle = new JLabel("", SwingConstants.CENTER);
            aisle.setPreferredSize(new Dimension(30, 40)); // Boşluk genişliği
            // İstersen buraya sıra numarasını yazdırabilirsin:
            // aisle.setText(String.valueOf(i)); 
            seatPanel.add(aisle);
            
            // --- SAĞ TARAF (D, E, F) ---
            addSeatToPanel(seatPanel, i, 'D', seats, businessRows);
            addSeatToPanel(seatPanel, i, 'E', seats, businessRows);
            addSeatToPanel(seatPanel, i, 'F', seats, businessRows);
        }
        
        // 3. SCROLL PANE (Aşağıyı görebilmek için kaydırma çubuğu)
        JScrollPane scrollPane = new JScrollPane(seatPanel);
        scrollPane.setPreferredSize(new Dimension(500, 500)); // Pencere boyutu sabitlenir
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Tekerlek hızı artırıldı
        scrollPane.setBorder(null); // Çerçeve kirliliğini kaldır

        // 4. GÖSTER
        JOptionPane.showMessageDialog(this, scrollPane, flightNum + " Detaylı Koltuk Haritası", JOptionPane.PLAIN_MESSAGE);
    }
    
    private void addSeatToPanel(JPanel panel, int row, char col, Map<String, Seat> seats, int businessRowLimit) {
        String seatCode = row + "" + col;
        Seat seat = seats.get(seatCode);
        
        JButton seatBtn = new JButton(seatCode);
        seatBtn.setPreferredSize(new Dimension(55, 45));
        seatBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        seatBtn.setFocusPainted(false); // Tıklayınca oluşan çirkin çerçeveyi kaldır
        
        if (seat != null && seat.isReserved()) {
            // --- DOLU KOLTUK (Kırmızı) ---
            seatBtn.setBackground(new Color(231, 76, 60)); 
            seatBtn.setForeground(Color.WHITE);
            seatBtn.setEnabled(false); // Tıklanamaz
        } else {
            // --- BOŞ KOLTUK ---
            if (row <= businessRowLimit) {
                // BUSINESS (Sarı)
                seatBtn.setBackground(new Color(241, 196, 15)); // Altın Sarısı
                seatBtn.setForeground(Color.BLACK);
                seatBtn.setToolTipText("Business Class");
            } else {
                // EKONOMİ (Yeşil)
                seatBtn.setBackground(new Color(46, 204, 113)); // Zümrüt Yeşili
                seatBtn.setForeground(Color.WHITE);
                seatBtn.setToolTipText("Economy Class");
            }
            
            // Tıklayınca kutuya yazma aksiyonu
            seatBtn.addActionListener(e -> txtSeatSelect.setText(seatCode));
        }
        panel.add(seatBtn);
    }

    // --- MANTIK METODLARI (ESKİ KODUN AYNISI) ---

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
            // İsim eşleşmesi (Basit yöntem)
            if (r.getPassenger().getName().equalsIgnoreCase(currentUser.getUsername())) {
                Object[] row = {
                    r.getReservationCode(),
                    r.getFlight().getFlightNum(),
                    (r.getSeat() != null ? r.getSeat().getSeatNum() : "Yok"),
                    (r.isActive() ? "AKTİF" : "İPTAL")
                };
                resTableModel.addRow(row);
            }
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
            
            if (seat == null) {
                throw new IllegalArgumentException("Hata: Koltuk bulunamadı -> " + seatNum);
            }

            if (seat.isReserved()) {
                JOptionPane.showMessageDialog(this, "Bu koltuk maalesef DOLU.");
                return;
            }

            Passenger p = new Passenger("11111111111", currentUser.getUsername(), "Yolcu", "email@test.com");
            Reservation res = resManager.makeReservation(flight, p, seat);
            
            if (res != null) {
                flightManager.updateFlight(flight);
                JOptionPane.showMessageDialog(this, "Rezervasyon Başarılı! Koltuk: " + seatNum);
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
        // ARTIK BURAYA "flightManager" NESNESİNİ DE GÖNDERİYORUZ:
        boolean success = resManager.cancelReservation(resCode, flightManager);
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Rezervasyon iptal edildi ve koltuk boşa çıkarıldı.");
            
            // Listeleri Yenile
            loadMyReservations(); // Durum "İPTAL" olacak
            loadFlights();        // Boş koltuk sayısı 1 artacak
        } else {
            JOptionPane.showMessageDialog(this, "İptal işlemi başarısız oldu.");
        }
    }
}