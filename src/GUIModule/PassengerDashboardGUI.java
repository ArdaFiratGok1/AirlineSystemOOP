package GUIModule;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
    private JComboBox<String> cmbFlightFilter;
    
    // Fiyat Göstergesi
    private JLabel lblTotalPrice;

    public PassengerDashboardGUI(User user) {
        this.currentUser = user;
        flightManager = new FlightManager();
        resManager = new ReservationManager();
        seatManager = new SeatManager();

        // TEMA AYARLARI
        setupTheme();

        setTitle("✈️ Yolcu Paneli - Hoşgeldiniz, " + user.getUsername());
        setSize(1050, 750); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // Minimum boyut ekleyelim ki çok küçültülürse bozulmasın
        setMinimumSize(new Dimension(800, 600));

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
            // Scrollbar genişliği ve hissi
            UIManager.put("ScrollBar.width", 12);
            UIManager.put("ScrollBar.thumbArc", 999);
        } catch (Exception e) {}
    }

    // --- PANEL 1: UÇUŞ LİSTESİ VE REZERVASYON ---
    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setOpaque(false);

        // --- ARAMA ve FİLTRE PANELİ ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JTextField txtSearch = new JTextField(15);
        txtSearch.putClientProperty("JTextField.placeholderText", "Şehir veya Uçuş No ara...");
        searchPanel.add(new JLabel("🔍 Ara: "));
        searchPanel.add(txtSearch);

        searchPanel.add(new JLabel(" |  Göster: "));
        String[] filterOptions = {"Aktif Uçuşlar (Bilet Alınabilir)", "Geçmiş Uçuşlar", "Tüm Uçuşlar"};
        cmbFlightFilter = new JComboBox<>(filterOptions);
        cmbFlightFilter.setSelectedIndex(0); 
        searchPanel.add(cmbFlightFilter);

        panel.add(searchPanel, BorderLayout.NORTH);

        // --- TABLO ---
        String[] cols = {"Uçuş No", "Rota", "Tarih", "Saat", "Süre", "Uçak", "Boş Koltuk"};
        flightTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        
        JTable table = new JTable(flightTableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                int modelRow = convertRowIndexToModel(row);
                String dateStr = (String) getModel().getValueAt(modelRow, 2);
                String timeStr = (String) getModel().getValueAt(modelRow, 3);

                if (isExpired(dateStr, timeStr)) {
                    c.setForeground(Color.LIGHT_GRAY); 
                    c.setBackground(Color.WHITE);      
                } else {
                    if (isRowSelected(row)) {
                        c.setForeground(getSelectionForeground());
                        c.setBackground(getSelectionBackground());
                    } else {
                        c.setForeground(Color.BLACK);
                        c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        };

        // Tablo Ayarları
        table.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                if (index0 == index1) { 
                    if (index0 < table.getRowCount()) {
                        int modelRow = table.convertRowIndexToModel(index0);
                        String dateStr = (String) flightTableModel.getValueAt(modelRow, 2);
                        String timeStr = (String) flightTableModel.getValueAt(modelRow, 3);
                        if (isExpired(dateStr, timeStr)) { return; }
                    }
                }
                super.setSelectionInterval(index0, index1);
            }
        });

        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(4).setPreferredWidth(50);
        table.getColumnModel().getColumn(6).setPreferredWidth(80);

        loadFlights(); 

        // --- SCROLL PANE AYARLARI (Scroll Çubuğu Eklendi) ---
        JScrollPane scrollPane = new JScrollPane(table);
        // Veri az olsa bile scroll bar yolunu göster (Kullanıcı özelliğin var olduğunu anlasın)
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // --- OLAYLAR ---
        cmbFlightFilter.addActionListener(e -> {
            loadFlights(); 
            txtSearch.setText(""); 
        });

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

        // --- ALT PANEL (SATIN ALMA & FİYAT) ---
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBackground(new Color(235, 245, 251)); 
        bottomPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(52, 152, 219)), " Rezervasyon İşlemleri "
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtSeatSelect = new JTextField(8);
        chkBaggage = new JCheckBox("Ekstra Bagaj (+500 TL)");
        chkBaggage.setOpaque(false);
        
        // Fiyat Etiketi
        lblTotalPrice = new JLabel("Toplam Tutar: 0 TL");
        lblTotalPrice.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalPrice.setForeground(new Color(44, 62, 80));
        
        JButton btnShowSeats = new JButton("👁️ Koltuk Durumlarını Gör");
        btnShowSeats.setBackground(new Color(241, 196, 15)); 
        btnShowSeats.setForeground(Color.BLACK);

        JButton btnBook = new JButton("REZERVASYONU TAMAMLA");
        btnBook.setBackground(new Color(39, 174, 96)); 
        btnBook.setForeground(Color.WHITE);
        btnBook.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // 1. Satır
        gbc.gridx = 0; gbc.gridy = 0;
        bottomPanel.add(new JLabel("İstenen Koltuk (Örn: 1A):"), gbc);
        
        gbc.gridx = 1;
        bottomPanel.add(txtSeatSelect, gbc);
        
        gbc.gridx = 2;
        bottomPanel.add(btnShowSeats, gbc);
        
        // 2. Satır
        gbc.gridx = 0; gbc.gridy = 1;
        bottomPanel.add(chkBaggage, gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2; // Fiyatı sağa koyalım
        bottomPanel.add(lblTotalPrice, gbc);
        
        // 3. Satır (Buton)
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        gbc.insets = new Insets(15, 10, 10, 10); // Biraz boşluk
        bottomPanel.add(btnBook, gbc);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        // --- BUTON VE INPUT AKSİYONLARI ---
        
        chkBaggage.addActionListener(e -> updatePriceLabel());
        txtSeatSelect.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updatePriceLabel(); }
            public void removeUpdate(DocumentEvent e) { updatePriceLabel(); }
            public void changedUpdate(DocumentEvent e) { updatePriceLabel(); }
        });

        btnShowSeats.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Önce listeden AKTİF bir uçuş seçiniz!");
                return;
            }
            int modelRow = table.convertRowIndexToModel(selectedRow);
            String flightNum = (String) flightTableModel.getValueAt(modelRow, 0);
            showSeatMap(flightNum);
        });

        btnBook.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen tablodan AKTİF bir uçuş seçin!");
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

        String[] cols = {"Rezervasyon Kodu", "Uçuş", "Koltuk", "Tutar", "Durum"};
        resTableModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(resTableModel);
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        
        loadMyReservations();

        // --- SCROLL PANE AYARLARI (Scroll Çubuğu Eklendi) ---
        JScrollPane scrollPane = new JScrollPane(table);
        // Burada da scroll her zaman görünür olsun
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("SEÇİLİ REZERVASYONU İPTAL ET");
        btnCancel.setBackground(new Color(231, 76, 60)); // Kırmızı
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        bottomPanel.add(btnCancel);
        panel.add(bottomPanel, BorderLayout.SOUTH);

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
    
    // --- GÖRSEL KOLTUK HARİTASI ---
    private void showSeatMap(String flightNum) {
        Flight flight = flightManager.getFlightByNum(flightNum);
        if (flight == null) return;
        
        int businessRows = 5; 
        List<JButton> availableSeatButtons = new ArrayList<>();
        
        JPanel mainContainer = new JPanel(new BorderLayout());
        JPanel seatPanel = new JPanel(new GridLayout(0, 7, 5, 5)); 
        seatPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        
        Map<String, Seat> seats = flight.getPlane().getSeats();
        int rows = flight.getPlane().getCapacity() / 6; 
        
        for (int i = 1; i <= rows; i++) {
            addSeatToPanel(seatPanel, i, 'A', seats, businessRows, availableSeatButtons);
            addSeatToPanel(seatPanel, i, 'B', seats, businessRows, availableSeatButtons);
            addSeatToPanel(seatPanel, i, 'C', seats, businessRows, availableSeatButtons);
            
            JLabel aisle = new JLabel("", SwingConstants.CENTER);
            aisle.setPreferredSize(new Dimension(30, 40));
            seatPanel.add(aisle);
            
            addSeatToPanel(seatPanel, i, 'D', seats, businessRows, availableSeatButtons);
            addSeatToPanel(seatPanel, i, 'E', seats, businessRows, availableSeatButtons);
            addSeatToPanel(seatPanel, i, 'F', seats, businessRows, availableSeatButtons);
        }
        
        JScrollPane scrollPane = new JScrollPane(seatPanel);
        scrollPane.setPreferredSize(new Dimension(560, 450));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        legendPanel.setBackground(new Color(245, 247, 250));
        legendPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        
        legendPanel.add(createLegendItem(new Color(65, 105, 225), "Business (5.000 TL)"));
        legendPanel.add(createLegendItem(new Color(46, 204, 113), "Ekonomi (1.000 TL)"));
        legendPanel.add(createLegendItem(Color.GRAY, "Dolu")); // GRİ

        mainContainer.add(scrollPane, BorderLayout.CENTER);
        mainContainer.add(legendPanel, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(this, mainContainer, flightNum + " Koltuk Seçimi", JOptionPane.PLAIN_MESSAGE);
    }
    
    private JPanel createLegendItem(Color color, String text) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setOpaque(false);
        
        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(15, 15));
        colorBox.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        item.add(colorBox);
        item.add(lbl);
        return item;
    }
    
    private void addSeatToPanel(JPanel panel, int row, char col, Map<String, Seat> seats, int businessRowLimit, List<JButton> allButtons) {
        String seatCode = row + "" + col;
        Seat seat = seats.get(seatCode);
        
        JButton seatBtn = new JButton(seatCode);
        seatBtn.setPreferredSize(new Dimension(55, 45));
        seatBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        seatBtn.setFocusPainted(false);
        
        if (seat != null && seat.isReserved()) {
            seatBtn.setBackground(Color.GRAY); 
            seatBtn.setForeground(Color.WHITE);
            seatBtn.setEnabled(false);
        } else {
            if (row <= businessRowLimit) {
                seatBtn.setBackground(new Color(65, 105, 225)); 
                seatBtn.setForeground(Color.WHITE);
                seatBtn.setToolTipText("Business Class - 5.000 TL"); 
            } else {
                seatBtn.setBackground(new Color(46, 204, 113));
                seatBtn.setForeground(Color.WHITE);
                seatBtn.setToolTipText("Economy Class - 1.000 TL");
            }
            
            allButtons.add(seatBtn);
            
            seatBtn.addActionListener(e -> {
                for (JButton btn : allButtons) {
                    btn.setBorder(UIManager.getBorder("Button.border"));
                }
                seatBtn.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 3));
                txtSeatSelect.setText(seatCode);
            });
        }
        panel.add(seatBtn);
    }

    // --- YARDIMCI METODLAR ---
    private void updatePriceLabel() {
        String seatNum = txtSeatSelect.getText().trim().toUpperCase();
        long price = 0;
        
        if (!seatNum.isEmpty()) {
            try {
                String rowStr = seatNum.replaceAll("[^0-9]", ""); 
                if (!rowStr.isEmpty()) {
                    int row = Integer.parseInt(rowStr);
                    if (row <= 5) price = 5000;
                    else price = 1500;
                }
            } catch (NumberFormatException e) {}
        }
        
        if (chkBaggage.isSelected() && price > 0) {
            price += 500;
        }
        
        lblTotalPrice.setText("Toplam Tutar: " + price + " TL");
        if (price > 0) lblTotalPrice.setForeground(new Color(39, 174, 96));
        else lblTotalPrice.setForeground(new Color(44, 62, 80));
    }

    private void loadFlights() {
        flightTableModel.setRowCount(0);
        String selectedFilter = (cmbFlightFilter != null) ? (String) cmbFlightFilter.getSelectedItem() : "Aktif Uçuşlar";
        java.util.List<Flight> flightsToShow;

        if (selectedFilter.contains("Geçmiş")) {
            flightsToShow = flightManager.getPastFlights();
        } else if (selectedFilter.contains("Tüm")) {
            flightsToShow = flightManager.getAllFlights();
        } else {
            flightsToShow = flightManager.getActiveFlights();
        }

        for (Flight f : flightsToShow) {
            int empty = seatManager.getAvailableSeatCount(f);
            Object[] row = {
                f.getFlightNum(),
                f.getRoute().getDeparturePlace() + " -> " + f.getRoute().getArrivalPlace(),
                f.getDate(),
                f.getTime(),
                f.getDuration(), 
                f.getPlane().getPlaneModel(),
                empty + " / " + f.getPlane().getCapacity()
            };
            flightTableModel.addRow(row);
        }
    }
    
    private void loadMyReservations() {
        resTableModel.setRowCount(0);
        for (Reservation r : resManager.getAllReservations()) {
            if (r.getPassenger().getName().equalsIgnoreCase(currentUser.getUsername())) {
                long price = 0;
                String seatNum = (r.getSeat() != null) ? r.getSeat().getSeatNum() : "";
                if (!seatNum.isEmpty()) {
                    try {
                        String rowStr = seatNum.replaceAll("[^0-9]", "");
                        int row = Integer.parseInt(rowStr);
                        price = (row <= 5) ? 5000 : 1000;
                    } catch(Exception e){}
                }

                Object[] row = {
                    r.getReservationCode(),
                    r.getFlight().getFlightNum(),
                    seatNum,
                    price + " TL",
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
                txtSeatSelect.setText(""); 
                chkBaggage.setSelected(false);
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

    private boolean isExpired(String dateStr, String timeStr) {
        try {
            String dt = dateStr + " " + timeStr;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            LocalDateTime flightTime = LocalDateTime.parse(dt, formatter);
            return flightTime.isBefore(LocalDateTime.now());
        } catch (Exception e) {
            return false; 
        }
    }
    
    private void handleCancel(String resCode) {
        boolean success = resManager.cancelReservation(resCode, flightManager);
        if (success) {
            JOptionPane.showMessageDialog(this, "Rezervasyon iptal edildi ve koltuk boşa çıkarıldı.");
            loadMyReservations(); 
            loadFlights();        
        } else {
            JOptionPane.showMessageDialog(this, "İptal işlemi başarısız oldu.");
        }
    }
}