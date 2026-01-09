package GUIModule;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Random; // Random ID için gerekli

public class LoginGUI extends JFrame {

    private JTextField userField;
    private JPasswordField passField;
    private UserManager userManager;

    public LoginGUI() {
        userManager = new UserManager();
        
        // --- MODERN TEMA (FlatLaf) ---
        try {
            FlatLightLaf.setup();
            UIManager.put("Component.arc", 15);
            UIManager.put("Button.arc", 15);
            UIManager.put("TextComponent.arc", 15);
        } catch (Exception e) {}

        setTitle("SkyTech - Giriş Ekranı");
        setSize(450, 580); // Yüksekliği biraz artırdık (Register butonu için)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Arka Plan Rengi
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(245, 247, 250)); 
        setContentPane(mainPanel);

        // --- ORTA KART (BEYAZ KUTU) ---
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(40, 40, 40, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; 

        // 1. Logo ve Başlık
        JLabel lblIcon = new JLabel("✈️", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 50));
        gbc.gridy = 0;
        cardPanel.add(lblIcon, gbc);

        JLabel lblTitle = new JLabel("SKYTECH GİRİŞ", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(44, 62, 80));
        gbc.gridy = 1;
        cardPanel.add(lblTitle, gbc);

        // 2. Kullanıcı Adı
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 0, 5, 0);
        JLabel lblUser = new JLabel("Kullanıcı Adı");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(Color.GRAY);
        cardPanel.add(lblUser, gbc);

        userField = new JTextField(15);
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userField.putClientProperty("JTextField.placeholderText", "Örn: yolcu1");
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 10, 0);
        cardPanel.add(userField, gbc);

        // 3. Şifre
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 0, 5, 0);
        JLabel lblPass = new JLabel("Şifre");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(Color.GRAY);
        cardPanel.add(lblPass, gbc);

        passField = new JPasswordField(15);
        passField.putClientProperty("JTextField.placeholderText", "******");
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 20, 0);
        cardPanel.add(passField, gbc);

        // 4. Giriş Butonu
        JButton loginButton = new JButton("GÜVENLİ GİRİŞ YAP");
        loginButton.setPreferredSize(new Dimension(200, 40));
        loginButton.setBackground(new Color(52, 152, 219)); // SkyTech Mavi
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginButton.setFocusPainted(false);
        
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 10, 0); // Altına boşluk bırak
        cardPanel.add(loginButton, gbc);

        // --- 5. KAYIT OL BUTONU (YENİ) ---
        JButton registerButton = new JButton("HESAP OLUŞTUR (KAYIT OL)");
        registerButton.setPreferredSize(new Dimension(200, 40));
        registerButton.setBackground(new Color(39, 174, 96)); // Yeşil (Register Rengi)
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        registerButton.setFocusPainted(false);

        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 0, 0);
        cardPanel.add(registerButton, gbc);

        mainPanel.add(cardPanel);

        // --- Aksiyonlar ---
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> showRegisterDialog()); // Register Metodu
        
        getRootPane().setDefaultButton(loginButton);

        setVisible(true);
    }

    private void handleLogin() {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        User user = userManager.login(username, password);

        if (user != null) {
            this.dispose(); 
            
            // Rol Kontrolü
            String role = user.getRole().toUpperCase();
            if (role.equals("ADMIN") || role.equals("STAFF")) {
                 new AdminDashboardGUI(user); 
            } else {
                 new PassengerDashboardGUI(user); 
            }
            
        } else {
            JOptionPane.showMessageDialog(this, 
                "Hatalı Kullanıcı Adı veya Şifre!", "Giriş Hatası", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- YENİ KAYIT OLMA METODU ---
    private void showRegisterDialog() {
        // Modern bir giriş paneli oluştur
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        JTextField txtNewUser = new JTextField();
        JPasswordField txtNewPass = new JPasswordField();
        
        panel.add(new JLabel("Yeni Kullanıcı Adı:"));
        panel.add(txtNewUser);
        panel.add(new JLabel("Yeni Şifre:"));
        panel.add(txtNewPass);

        int result = JOptionPane.showConfirmDialog(this, panel, 
                "SkyTech - Yeni Hesap Oluştur", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String newUser = txtNewUser.getText().trim();
            String newPass = new String(txtNewPass.getPassword()).trim();

            if (newUser.isEmpty() || newPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Kullanıcı adı veya şifre boş olamaz!", "Hata", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Kullanıcı adı daha önce alınmış mı kontrol et
            if (isUsernameTaken(newUser)) {
                JOptionPane.showMessageDialog(this, "Bu kullanıcı adı zaten kullanılıyor!", "Hata", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Yeni Kullanıcıyı Oluştur (Rol otomatik olarak PASSENGER)
            // ID Random üretilir (10000 - 99999 arası)
            int randomId = new Random().nextInt(90000) + 10000;
            User newPassenger = new User(randomId, newUser, newPass, "PASSENGER");

            // Manager'a ekle (Dosyaya da kaydeder)
            userManager.addUser(newPassenger);

            JOptionPane.showMessageDialog(this, "Kayıt Başarılı! Şimdi giriş yapabilirsiniz.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
            
            // Kolaylık olsun diye alanları dolduralım
            userField.setText(newUser);
            passField.setText("");
            passField.requestFocus();
        }
    }

    // Yardımcı Metod: Kullanıcı adı kontrolü
    private boolean isUsernameTaken(String username) {
        for (User u : userManager.getAllUsers()) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginGUI());
    }
}