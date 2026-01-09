package GUIModule;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Random;

import FlightManagementModule.Flight;
import FlightManagementModule.Plane;
import FlightManagementModule.Route;
import FlightManagementModule.Seat;
import ServiceAndManagersModule.FlightManager;

public class AdminDashboardGUI extends JFrame {

    private FlightManager flightManager;
    private UserManager userManager;
    private User currentUser; 
    
    private ServiceAndManagersModule.ReservationManager resManager;
    
    private DefaultTableModel flightTableModel;
    private DefaultTableModel userTableModel;
    
    private JTextArea simulationLogArea;

    public AdminDashboardGUI(User currentUser) {
        this.currentUser = currentUser;
        
        flightManager = new FlightManager();
        userManager = new UserManager();
        resManager = new ServiceAndManagersModule.ReservationManager();
        
        // --- MODERN TEMA KURULUMU ---
        setupTheme();

        setTitle("✈️ SkyTech Yönetim Paneli - " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        setSize(1150, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Ana Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        // --- 1. ÜST BAŞLIK (HEADER) ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 20));
        headerPanel.setBackground(new Color(44, 62, 80)); // Koyu Lacivert
        
        JLabel lblHeader = new JLabel("Yönetici Kontrol Merkezi");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblHeader.setForeground(Color.WHITE);
        
        headerPanel.add(lblHeader);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- 2. ORTA KISIM (SEKMELER) ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        // SEKME 1: Uçuş İşlemleri (HERKES GÖREBİLİR)
        tabbedPane.addTab("  🛫 Uçuş İşlemleri  ", createFlightPanel());

        // SEKME 2 ve 3: SADECE "ADMIN" GÖREBİLİR
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            tabbedPane.addTab("  👥 Personel Yönetimi  ", createStaffPanel());
            tabbedPane.addTab("  ⚙️ Sistem Testleri & Rapor  ", createSimulationPanel());
        }

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // --- 3. ALT PANEL (ÇIKIŞ BUTONU) - YENİ EKLENDİ ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        
        JButton btnLogout = new JButton("GÜVENLİ ÇIKIŞ");
        btnLogout.setBackground(new Color(231, 76, 60)); // Kırmızı
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setFocusPainted(false);

        // Çıkış Aksiyonu
        btnLogout.addActionListener(e -> {
            dispose(); // Paneli kapat
            new LoginGUI(); // Giriş ekranına dön
        });

        bottomPanel.add(btnLogout);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        // --------------------------------------------------

        setVisible(true);
    }

    // --- TEMA AYARLARI ---
    private void setupTheme() {
        try {
            FlatLightLaf.setup(); 

            UIManager.put("Component.arc", 12);
            UIManager.put("Button.arc", 12);
            UIManager.put("TextComponent.arc", 12);
            
            Color accentBlue = new Color(52, 152, 219);
            Color bgLight = new Color(245, 247, 250);
            Color white = Color.WHITE;

            UIManager.put("Panel.background", bgLight);
            UIManager.put("Viewport.background", bgLight);
            
            UIManager.put("Table.background", white);
            UIManager.put("Table.selectionBackground", accentBlue);
            UIManager.put("Table.selectionForeground", white);
            UIManager.put("Table.gridColor", new Color(230, 230, 230));
            UIManager.put("Table.rowHeight", 35);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.stripeColor", new Color(250, 251, 252));

            UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 13));

        } catch (Exception ex) {}
    }

 // =========================================================================
    // SEKME 1: UÇUŞ YÖNETİMİ (GÜNCELLENDİ)
    // =========================================================================
    private JPanel createFlightPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setOpaque(false);

        // --- 1. ARAMA ÇUBUĞU ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        JLabel lblSearch = new JLabel("🔍 Uçuş Ara:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JTextField txtSearch = new JTextField(20);
        txtSearch.putClientProperty("JTextField.placeholderText", "Uçuş No, Şehir vb. yazın..."); 
        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        panel.add(searchPanel, BorderLayout.NORTH);

        // --- 2. TABLO (Süre Kolonu Eklendi) ---
        String[] columns = {"Uçuş No", "Nereden", "Nereye", "Tarih", "Saat", "Süre", "Uçak", "Kapasite"};
        flightTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        JTable table = new JTable(flightTableModel);
        
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        // Sütun genişliklerini ayarla (Estetik için)
        table.getColumnModel().getColumn(0).setPreferredWidth(70); // Uçuş No
        table.getColumnModel().getColumn(5).setPreferredWidth(50); // Süre
        table.getColumnModel().getColumn(7).setPreferredWidth(60); // Kapasite
        
        loadFlightsToTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        // Arama Mantığı
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

        // --- 3. ALT FORM VE BUTONLAR (YENİ ALANLAR EKLENDİ) ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(235, 245, 251)); 
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(52, 152, 219)),
            " Uçuş İşlemleri ", 0, 0, new Font("Segoe UI", Font.BOLD, 14), new Color(41, 128, 185)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Daha sıkı yerleşim
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form Bileşenleri
        JTextField txtNum = new JTextField(8);
        JTextField txtDep = new JTextField(8);
        JTextField txtArr = new JTextField(8);
        JTextField txtDate = new JTextField(8);
        JTextField txtTime = new JTextField(6);
        
        // YENİ ALANLAR:
        JTextField txtDuration = new JTextField(6);
        JTextField txtPlaneModel = new JTextField(10);
        JTextField txtCapacity = new JTextField(6);

        // Grid Yerleşimi (2 Kolonlu Form)
        // Sol Taraf
        addFormRow(formPanel, gbc, 0, 0, "Uçuş No:", txtNum);
        addFormRow(formPanel, gbc, 0, 1, "Kalkış:", txtDep);
        addFormRow(formPanel, gbc, 0, 2, "Varış:", txtArr);
        addFormRow(formPanel, gbc, 0, 3, "Tarih:", txtDate);
        
        // Sağ Taraf
        addFormRow(formPanel, gbc, 2, 0, "Saat:", txtTime);
        addFormRow(formPanel, gbc, 2, 1, "Süre:", txtDuration);
        addFormRow(formPanel, gbc, 2, 2, "Uçak Tipi:", txtPlaneModel);
        addFormRow(formPanel, gbc, 2, 3, "Kapasite:", txtCapacity);

        // --- BUTONLAR ---
        JButton btnAdd = new JButton("EKLE");
        btnAdd.setBackground(new Color(39, 174, 96)); // Yeşil
        btnAdd.setForeground(Color.WHITE);
        
        JButton btnUpdate = new JButton("GÜNCELLE");
        btnUpdate.setBackground(new Color(243, 156, 18)); // Turuncu
        btnUpdate.setForeground(Color.WHITE);

        JButton btnDelete = new JButton("SİL");
        btnDelete.setBackground(new Color(231, 76, 60)); // Kırmızı
        btnDelete.setForeground(Color.WHITE);
        
        JButton btnClear = new JButton("TEMİZLE");
        btnClear.setBackground(Color.GRAY);
        btnClear.setForeground(Color.WHITE);

        JPanel btnGroup = new JPanel(new GridLayout(1, 4, 10, 0));
        btnGroup.setOpaque(false);
        btnGroup.add(btnAdd);
        btnGroup.add(btnUpdate);
        btnGroup.add(btnDelete);
        btnGroup.add(btnClear);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4; // Tüm genişliği kaplasın
        gbc.insets = new Insets(15, 5, 5, 5);
        formPanel.add(btnGroup, gbc);

        // --- OLAYLAR (LISTENERS) ---

        // 1. Tablo Seçim Olayı
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int selectedRow = table.getSelectedRow();
                int modelRow = table.convertRowIndexToModel(selectedRow);
                
                txtNum.setText(flightTableModel.getValueAt(modelRow, 0).toString());
                txtDep.setText(flightTableModel.getValueAt(modelRow, 1).toString());
                txtArr.setText(flightTableModel.getValueAt(modelRow, 2).toString());
                txtDate.setText(flightTableModel.getValueAt(modelRow, 3).toString());
                txtTime.setText(flightTableModel.getValueAt(modelRow, 4).toString());
                txtDuration.setText(flightTableModel.getValueAt(modelRow, 5).toString());
                txtPlaneModel.setText(flightTableModel.getValueAt(modelRow, 6).toString());
                txtCapacity.setText(flightTableModel.getValueAt(modelRow, 7).toString());
            }
        });

        // 2. Ekle Butonu
        btnAdd.addActionListener(e -> {
            addNewFlight(
                txtNum.getText(), txtDep.getText(), txtArr.getText(), 
                txtDate.getText(), txtTime.getText(), 
                txtDuration.getText(), txtPlaneModel.getText(), txtCapacity.getText()
            );
            clearFields(txtNum, txtDep, txtArr, txtDate, txtTime, txtDuration, txtPlaneModel, txtCapacity);
        });

        // 3. Güncelle Butonu
        btnUpdate.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen güncellenecek uçuşu tablodan seçiniz!");
                return;
            }
            
            // Veri Doğrulama (Kapasite Sayı mı?)
            int cap = 0;
            try {
                cap = Integer.parseInt(txtCapacity.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Kapasite bir sayı olmalıdır!");
                return;
            }

            int modelRow = table.convertRowIndexToModel(selectedRow);
            String originalNum = flightTableModel.getValueAt(modelRow, 0).toString();
            
            // Uçak nesnesini güncelle
            // Not: Mevcut ID'yi korumak istersen flightManager'dan çekmen lazım, 
            // burada basitlik için random ID veriyoruz veya sabit bırakıyoruz.
            Plane p = new Plane("PL-" + new Random().nextInt(9999), txtPlaneModel.getText(), cap); 
            
            Route r = new Route(txtDep.getText(), txtArr.getText(), "GENEL");
            Flight updatedFlight = new Flight(txtNum.getText(), r, txtDate.getText(), txtTime.getText(), txtDuration.getText(), p);
            
            flightManager.updateFlight(originalNum, updatedFlight);
            resManager.updateFlightInfoInReservations(originalNum, updatedFlight);
            
            loadFlightsToTable(); 
            JOptionPane.showMessageDialog(this, "Uçuş başarıyla güncellendi!");
            clearFields(txtNum, txtDep, txtArr, txtDate, txtTime, txtDuration, txtPlaneModel, txtCapacity);
        });
        
        // 4. Sil Butonu
        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen silinecek uçuşu seçiniz!");
                return;
            }
            int modelRow = table.convertRowIndexToModel(selectedRow);
            String fNum = (String) flightTableModel.getValueAt(modelRow, 0);
            if (JOptionPane.showConfirmDialog(this, fNum + " silinsin mi?", "Onay", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                flightManager.removeFlight(fNum);
                loadFlightsToTable();
                clearFields(txtNum, txtDep, txtArr, txtDate, txtTime, txtDuration, txtPlaneModel, txtCapacity);
                JOptionPane.showMessageDialog(this, "Uçuş silindi.");
            }
        });
        
        // 5. Temizle Butonu
        btnClear.addActionListener(e -> {
            table.clearSelection();
            clearFields(txtNum, txtDep, txtArr, txtDate, txtTime, txtDuration, txtPlaneModel, txtCapacity);
        });

        panel.add(formPanel, BorderLayout.SOUTH);
        return panel;
    }

    // YENİ YARDIMCI METOD (GridBagLayout için satır ekleme)
    private void addFormRow(JPanel p, GridBagConstraints gbc, int x, int y, String label, Component cmp) {
        gbc.gridx = x; 
        gbc.gridy = y; 
        gbc.weightx = 0;
        p.add(new JLabel(label), gbc);
        
        gbc.gridx = x + 1; 
        gbc.weightx = 1.0;
        p.add(cmp, gbc);
    }
    // =========================================================================
    // SEKME 2: PERSONEL YÖNETİMİ
    // =========================================================================
    private JPanel createStaffPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setOpaque(false);

        String[] columns = {"ID", "Kullanıcı Adı", "Şifre", "Rol"};
        userTableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(userTableModel);
        
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        loadUsersToTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("❌ Bu Personeli Sil");
        popupMenu.add(deleteItem);
        table.setComponentPopupMenu(popupMenu);

        deleteItem.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row != -1) {
                int userId = (int) userTableModel.getValueAt(row, 0);
                if (JOptionPane.showConfirmDialog(this, "Kullanıcı ID: " + userId + " silinsin mi?", "Onay", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    userManager.removeUser(userId);
                    loadUsersToTable();
                    JOptionPane.showMessageDialog(this, "Personel silindi.");
                }
            }
        });

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        formPanel.setBackground(new Color(235, 245, 251));
        formPanel.setBorder(BorderFactory.createTitledBorder(" Yeni Personel Ekle "));

        JTextField txtUser = new JTextField(10);
        JTextField txtPass = new JTextField(10);
        JComboBox<String> cmbRole = new JComboBox<>(new String[]{"ADMIN", "STAFF", "PASSENGER"});

        JButton btnAddUser = new JButton("KULLANICI EKLE");
        btnAddUser.setBackground(new Color(39, 174, 96));
        btnAddUser.setForeground(Color.WHITE);

        formPanel.add(new JLabel("Kullanıcı Adı:")); formPanel.add(txtUser);
        formPanel.add(new JLabel("Şifre:")); formPanel.add(txtPass);
        formPanel.add(new JLabel("Rol:")); formPanel.add(cmbRole);
        formPanel.add(btnAddUser);

        btnAddUser.addActionListener(e -> {
            String uName = txtUser.getText();
            String uPass = txtPass.getText();
            String uRole = (String) cmbRole.getSelectedItem();

            if (!uName.isEmpty() && !uPass.isEmpty()) {
                int newId = new Random().nextInt(10000) + 1000; 
                GUIModule.User newUser = new GUIModule.User(newId, uName, uPass, uRole);
                userManager.addUser(newUser);
                loadUsersToTable();
                JOptionPane.showMessageDialog(this, "Personel eklendi: " + uName);
                txtUser.setText(""); txtPass.setText("");
            }
        });

        panel.add(formPanel, BorderLayout.SOUTH);
        return panel;
    }

    // =========================================================================
    // SEKME 3: SİMÜLASYONLAR
    // =========================================================================
    private JPanel createSimulationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        JPanel controlPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        controlPanel.setOpaque(false);
        controlPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JPanel sc1Panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        sc1Panel.setOpaque(false);
        sc1Panel.setBorder(BorderFactory.createTitledBorder("Scenario 1: Concurrency (Race Condition) Testi"));
        
        JButton btnUnsafe = new JButton("☠️ UNSAFE START (Güvensiz)");
        btnUnsafe.setBackground(new Color(231, 76, 60));
        btnUnsafe.setForeground(Color.WHITE);
        
        JButton btnSafe = new JButton("🛡️ SAFE START (Güvenli)");
        btnSafe.setBackground(new Color(39, 174, 96));
        btnSafe.setForeground(Color.WHITE);
        
        sc1Panel.add(btnUnsafe);
        sc1Panel.add(btnSafe);

        JPanel sc2Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sc2Panel.setOpaque(false);
        sc2Panel.setBorder(BorderFactory.createTitledBorder("Scenario 2: Asynchronous GUI Task"));
        
        JButton btnReport = new JButton("📊 Detaylı Rapor Al (Asenkron)");
        btnReport.setBackground(new Color(52, 152, 219));
        btnReport.setForeground(Color.WHITE);
        sc2Panel.add(btnReport);

        controlPanel.add(sc1Panel);
        controlPanel.add(sc2Panel);
        panel.add(controlPanel, BorderLayout.NORTH);

        simulationLogArea = new JTextArea();
        simulationLogArea.setEditable(false);
        simulationLogArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        simulationLogArea.setBackground(new Color(44, 62, 80)); 
        simulationLogArea.setForeground(new Color(236, 240, 241)); 
        simulationLogArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(new JScrollPane(simulationLogArea), BorderLayout.CENTER);

        btnUnsafe.addActionListener(e -> runScenario1InGUI(false)); 
        btnSafe.addActionListener(e -> runScenario1InGUI(true));   
        btnReport.addActionListener(e -> {
            simulationLogArea.setText(">> [Scenario 2] Rapor isteği alındı...\n>> GUI kilitlenmeden arka planda hesaplama yapılıyor...\n");
            generateReportAsync();
        });

        return panel;
    }

    // =========================================================================
    // YARDIMCI METODLAR
    // =========================================================================

    private void addFormRow(JPanel p, GridBagConstraints gbc, int row, String label, Component cmp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        p.add(lbl, gbc);
        gbc.gridx = 1; 
        p.add(cmp, gbc);
    }

    private void loadFlightsToTable() {
        flightTableModel.setRowCount(0);
        for (Flight f : flightManager.getAllFlights()) {
            flightTableModel.addRow(new Object[]{
                f.getFlightNum(), 
                f.getRoute().getDeparturePlace(), 
                f.getRoute().getArrivalPlace(),
                f.getDate(), 
                f.getTime(), 
                f.getDuration(), 
                f.getPlane().getPlaneModel(), 
                f.getPlane().getCapacity()
            });
        }
    }
    
    private void loadUsersToTable() {
        userTableModel.setRowCount(0);
        for (User u : userManager.getAllUsers()) {
            userTableModel.addRow(new Object[]{
                u.getUserID(), u.getUsername(), u.getPassword(), u.getRole()
            });
        }
    }

    private void addNewFlight(String num, String dep, String arr, String date, String time, String duration, String model, String capacityStr) {
        if(num.isEmpty() || dep.isEmpty() || arr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen temel bilgileri doldurunuz!");
            return;
        }
        int capacity = 0;
        try {
            capacity = Integer.parseInt(capacityStr);
            if (capacity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Kapasite geçerli bir pozitif sayı olmalıdır!");
            return;
        }
        // Girilen bilgilerle Uçak oluştur
        Plane p = new Plane("PL-" + new Random().nextInt(9999), model, capacity);
        
        // Girilen bilgilerle Rota oluştur
        Route r = new Route(dep, arr, "GENEL");
        
        // Uçuş oluştur
        Flight f = new Flight(num, r, date, time, duration, p);
        
        flightManager.addFlight(f);
        loadFlightsToTable();
        JOptionPane.showMessageDialog(this, "Uçuş başarıyla eklendi.");
    }

    private void generateReportAsync() {
        new Thread(() -> {
            try {
                Thread.sleep(3000); 
                StringBuilder sb = new StringBuilder();
                sb.append("\n=== DOLULUK RAPORU SONUCU ===\n");
                sb.append("------------------------------\n");
                for (Flight f : flightManager.getAllFlights()) {
                    long occupied = f.getPlane().getSeats().values().stream().filter(Seat::isReserved).count();
                    sb.append(String.format("Uçuş: %-6s | Dolu: %d / %d\n", f.getFlightNum(), occupied, f.getPlane().getCapacity()));
                }
                sb.append("------------------------------\n");
                SwingUtilities.invokeLater(() -> simulationLogArea.append(sb.toString() + ">> Rapor tamamlandı.\n"));
            } catch (Exception ex) { ex.printStackTrace(); }
        }).start();
    }
    
    private void clearFields(JTextField... fields) {
        for (JTextField f : fields) {
            f.setText("");
        }
    }

    private void runScenario1InGUI(boolean isSafe) {
        new Thread(() -> {
            try {
                ScenariosModule.Scenario1.runSimulationForGUI(simulationLogArea, isSafe);
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> simulationLogArea.append("HATA: Scenario1.runSimulationForGUI metodu bulunamadı!\n"));
                e.printStackTrace();
            }
        }).start();
    }
}