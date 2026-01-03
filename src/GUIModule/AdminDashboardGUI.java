package GUIModule;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Random;

import FlightManagementModule.Flight;
import FlightManagementModule.Plane;
import FlightManagementModule.Route;
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
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Programı kapatma, sadece pencereyi kapat
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. ÜST PANEL (Başlık) ---
        JLabel lblTitle = new JLabel("UÇUŞ YÖNETİM SİSTEMİ", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        add(lblTitle, BorderLayout.NORTH);

        // --- 2. ORTA PANEL (Tablo) ---
        // Tablo Sütunları
        String[] columns = {"Uçuş No", "Nereden", "Nereye", "Tarih", "Saat", "Uçak Modeli"};
        tableModel = new DefaultTableModel(columns, 0);
        flightTable = new JTable(tableModel);
        
        loadFlightsToTable(); // Verileri yükle

        add(new JScrollPane(flightTable), BorderLayout.CENTER);

        // --- 3. ALT PANEL (Ekleme Formu) ---
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Yeni Uçuş Ekle"));

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

        JButton btnAdd = new JButton("EKLE");
        btnAdd.setBackground(new Color(60, 179, 113)); // Yeşil
        btnAdd.setForeground(Color.WHITE);
        formPanel.add(btnAdd);

        bottomPanel.add(formPanel);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- BUTON AKSİYONU ---
        btnAdd.addActionListener(e -> addNewFlight());

        setVisible(true);
    }

    private void loadFlightsToTable() {
        tableModel.setRowCount(0); // Tabloyu temizle
        List<Flight> flights = flightManager.getAllFlights();
        
        for (Flight f : flights) {
            Object[] row = {
                f.getFlightNum(),
                f.getRoute().getDeparturePlace(),
                f.getRoute().getArrivalPlace(),
                f.getDate(),
                f.getTime(),//hour döndürüyoz burda
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

            // Basitlik adına Uçak ve Rota otomatik oluşturuluyor
            Plane p = new Plane("PL-" + new Random().nextInt(999), "Boeing 737", 180);
            Route r = new Route(dep, arr, "GENEL");
            
            Flight f = new Flight(num, r, date, time, "2h", p);
            
            flightManager.addFlight(f); // Dosyaya kaydeder
            loadFlightsToTable(); // Tabloyu güncelle
            
            JOptionPane.showMessageDialog(this, "Uçuş Başarıyla Eklendi!");
            
            // Alanları temizle
            txtFlightNum.setText(""); txtDep.setText(""); txtArr.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
        }
    }
}