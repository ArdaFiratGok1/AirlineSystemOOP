package GUIModule;

import javax.swing.*;
import java.awt.*;

public class LoginGUI extends JFrame {
    
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;
    private UserManager userManager;

    public LoginGUI() {
        userManager = new UserManager(); 

        setTitle("Giriş Ekranı - Havayolu Sistemi");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setLayout(new BorderLayout());

        // --- Başlık ---
        JLabel titleLabel = new JLabel("HOŞGELDİNİZ", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // --- Form Paneli ---
        // GridLayout(Satır, Sütun, YatayBoşluk, DikeyBoşluk)
        JPanel formPanel = new JPanel(new GridLayout(3, 1, 10, 10)); // Düzeni değiştirdim, daha düzgün durur
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        // 1. Kullanıcı Adı
        formPanel.add(new JLabel("Kullanıcı Adı:"));
        userField = new JTextField();
        formPanel.add(userField); // <-- BU SATIR EKSİKTİ, EKLENDİ!
        
        // 2. Şifre
        formPanel.add(new JLabel("Şifre:"));
        passField = new JPasswordField();
        formPanel.add(passField); // <-- BU SATIR EKSİKTİ, EKLENDİ!

        add(formPanel, BorderLayout.CENTER);

        // --- Buton Paneli ---
        JPanel buttonPanel = new JPanel();
        loginButton = new JButton("GİRİŞ YAP");
        loginButton.setPreferredSize(new Dimension(150, 40));
        loginButton.setBackground(new Color(70, 130, 180)); 
        loginButton.setForeground(Color.WHITE);
        
        buttonPanel.add(loginButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(buttonPanel, BorderLayout.SOUTH);

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
            JOptionPane.showMessageDialog(this, 
                "Giriş Başarılı!\nHoşgeldin: " + user.getUsername() + "\nRol: " + user.getRole());
            
            // Kullanıcıyı içeri alıp Login ekranını kapatıyoruz
            this.dispose(); 
            
            // Rolüne göre ilgili paneli aç
            if (user.getRole().equalsIgnoreCase("ADMIN")) {
                 new AdminDashboardGUI(); // Birazdan yazacağız
            } else {
                 new PassengerDashboardGUI(user); // Birazdan yazacağız
            }
            
        } else {
            JOptionPane.showMessageDialog(this, 
                "Hatalı Kullanıcı Adı veya Şifre!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginGUI());
    }
}

//Dolu ve boş koltuklar bir panelde gözükmesi