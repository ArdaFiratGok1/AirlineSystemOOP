package GUIModule;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Random;

import FlightManagementModule.Flight;
import FlightManagementModule.Plane;
import FlightManagementModule.Route;
import FlightManagementModule.Seat; // Doluluk hesabı için gerekli
import ServiceAndManagersModule.FlightManager;

public class AdminDashboardGUI extends JFrame {

    private JTable flightTable;
    private DefaultTableModel tableModel;
    private FlightManager flightManager;

    // Input Alanları
    private JTextField txtFlightNum, txtDep, txtArr, txtDate, txtTime;

    public AdminDashboardGUI() {
        flightManager = new FlightManager();

        setTitle("YÖNETİCİ PANELİ - Uçuş Yönetimi");
        setSize(900, 650); // Rapor butonu sığsın diye biraz genişlettik
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. ÜST PANEL (Başlık) ---
        JLabel lblTitle = new JLabel("UÇUŞ YÖNETİM SİSTEMİ", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        add(lblTitle, BorderLayout.NORTH);

        // --- 2. ORTA PANEL (Tablo) ---
        String[] columns = {"Uçuş No", "Nereden", "Nereye", "Tarih", "Saat", "Uçak Modeli"};
        tableModel = new DefaultTableModel(columns, 0);
        flightTable = new JTable(tableModel);
        
        loadFlightsToTable(); // Verileri yükle

        add(new JScrollPane(flightTable), BorderLayout.CENTER);

        // --- 3. ALT PANEL (Ekleme Formu ve Rapor) ---
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("İşlemler"));

        // Form Paneli
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        txtFlightNum = new JTextField(5);
        txtDep = new JTextField(8);
        txtArr = new JTextField(8);
        txtDate = new JTextField(8);
        txtTime = new JTextField(5);

        formPanel.add(new JLabel("No:")); formPanel.add(txtFlightNum);
        formPanel.add(new JLabel("Kalkış:")); formPanel.add(txtDep);
        formPanel.add(new JLabel("Varış:")); formPanel.add(txtArr);
        formPanel.add(new JLabel("Tarih (GG-AA-YYYY):")); formPanel.add(txtDate);
        formPanel.add(new JLabel("Saat:")); formPanel.add(txtTime);

        // EKLE Butonu
        JButton btnAdd = new JButton("UÇUŞ EKLE");
        btnAdd.setBackground(new Color(60, 179, 113)); // Yeşil
        btnAdd.setForeground(Color.WHITE);
        formPanel.add(btnAdd);

        // --- YENİ EKLENEN KISIM: RAPOR BUTONU (SCENARIO 2) ---
        JButton btnReport = new JButton("DOLULUK RAPORU AL (Asenkron)");
        btnReport.setBackground(new Color(255, 140, 0)); // Turuncu
        btnReport.setForeground(Color.WHITE);
        formPanel.add(btnReport);
        // -----------------------------------------------------

        bottomPanel.add(formPanel);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- BUTON AKSİYONLARI ---
        btnAdd.addActionListener(e -> addNewFlight());
        
        // Rapor butonuna basınca asenkron metod çalışacak
        btnReport.addActionListener(e -> generateReportAsync());

        setVisible(true);
    }

    private void loadFlightsToTable() {
        tableModel.setRowCount(0); 
        List<Flight> flights = flightManager.getAllFlights();
        
        for (Flight f : flights) {
            Object[] row = {
                f.getFlightNum(),
                f.getRoute().getDeparturePlace(),
                f.getRoute().getArrivalPlace(),
                f.getDate(),
                f.getTime(),
                f.getPlane().getPlaneModel()
            };
            tableModel.addRow(row);
        }
    }

    private void addNewFlight() {
        try {
            String num = txtFlightNum.getText();
            String dep = txtDep.getText();
            String arr = txtArr.getText();
            String date = txtDate.getText();
            String time = txtTime.getText();

            if (num.isEmpty() || dep.isEmpty() || arr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!");
                return;
            }

            Plane p = new Plane("PL-" + new Random().nextInt(999), "Boeing 737", 180);
            Route r = new Route(dep, arr, "GENEL");
            
            Flight f = new Flight(num, r, date, time, "2h", p);
            
            flightManager.addFlight(f); 
            loadFlightsToTable(); 
            
            JOptionPane.showMessageDialog(this, "Uçuş Başarıyla Eklendi!");
            
            txtFlightNum.setText(""); txtDep.setText(""); txtArr.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
        }
    }

    /**
     * SCENARIO 2: Asynchronous GUI Task
     * Arayüzü dondurmadan arka planda rapor hazırlar.
     */
    private void generateReportAsync() {
        // 1. Kullanıcıya bilgi ver (Hemen çalışır)
        JOptionPane.showMessageDialog(this, 
            "Rapor hazırlanıyor... Lütfen bekleyiniz.\n(Arayüz donmayacak, işlem arkada sürüyor)", 
            "İşlem Başladı", JOptionPane.INFORMATION_MESSAGE);

        // 2. Thread başlat
        new Thread(() -> {
            try {
                // Uzun süren işlemi simüle et (3 saniye)
                Thread.sleep(3000);

                StringBuilder report = new StringBuilder();
                report.append("=== DETAYLI DOLULUK RAPORU ===\n\n");

                for (Flight f : flightManager.getAllFlights()) {
                    int capacity = f.getPlane().getCapacity();
                    // Stream ile dolu koltukları sayıyoruz
                    long occupied = f.getPlane().getSeats().values().stream()
                                     .filter(Seat::isReserved).count();
                    
                    double ratio = (capacity > 0) ? ((double) occupied / capacity * 100) : 0;

                    report.append(String.format("Uçuş: %-6s | Rota: %s -> %s | Dolu: %d/%d (%%.2f)\n", 
                        f.getFlightNum(), 
                        f.getRoute().getDeparturePlace(), 
                        f.getRoute().getArrivalPlace(),
                        occupied, capacity, ratio));
                }

                // 3. İşlem bitince sonucu ekrana bas (Swing Thread'ine dönmek için invokeLater şart)
                SwingUtilities.invokeLater(() -> {
                    JTextArea textArea = new JTextArea(report.toString());
                    textArea.setEditable(false);
                    textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                    
                    JScrollPane scrollPane = new JScrollPane(textArea);
                    scrollPane.setPreferredSize(new Dimension(500, 300));
                    
                    JOptionPane.showMessageDialog(AdminDashboardGUI.this, scrollPane, "Rapor Sonucu", JOptionPane.INFORMATION_MESSAGE);
                });

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}