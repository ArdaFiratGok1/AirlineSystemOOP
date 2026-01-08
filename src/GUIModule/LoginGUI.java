package GUIModule;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import GUIModule.UserManager; 

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
        setSize(450, 520);
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
        loginButton.setPreferredSize(new Dimension(200, 45));
        loginButton.setBackground(new Color(52, 152, 219)); // SkyTech Mavi
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        
        gbc.gridy = 6;
        cardPanel.add(loginButton, gbc);

        mainPanel.add(cardPanel);

        // --- Aksiyonlar ---
        loginButton.addActionListener(e -> handleLogin());
        getRootPane().setDefaultButton(loginButton);

        setVisible(true);
    }

    private void handleLogin() {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        User user = userManager.login(username, password);

        if (user != null) {
            this.dispose(); 
            
            // Rol Kontrolü (Daha önce konuştuğumuz gibi)
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginGUI());
    }
}