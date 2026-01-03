package GUIModule;

import java.util.ArrayList;
import java.util.List;
import ServiceAndManagersModule.FileManager;

public class UserManager {
    
    private List<User> users; // Değişken adını camelCase yaptım (standart)
    private final String FILE_NAME = "users.dat";
    
    @SuppressWarnings("unchecked")
    public UserManager() {
        Object data = FileManager.loadData(FILE_NAME);
        
        if (data != null) {
            this.users = (List<User>) data;
        } else {
            this.users = new ArrayList<>();
            // Dosya yoksa varsayılan kullanıcıları oluştur (Test kolaylığı için) 
            initializeDefaultUsers();
        }
    }
    
    private void initializeDefaultUsers() {
        // Admin Hesabı
        User admin = new User(1, "admin", "123", "ADMIN");
        // Yolcu Hesabı
        User passenger = new User(2, "user", "123", "PASSENGER");
        
        this.users.add(admin);
        this.users.add(passenger);
        
        saveUsers(); // Listeyi kaydet
        System.out.println(">> Varsayılan kullanıcılar oluşturuldu.");
    }
    
    // Login Kontrolü (LoginGUI için gerekli)
    public User login(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.checkPassword(password)) {
                return u; // Giriş başarılı, kullanıcıyı döndür
            }
        }
        return null; // Başarısız
    }

    public void addUser(User usr) {
        this.users.add(usr);
        
        // DÜZELTME: Sadece 'usr' değil, tüm listeyi kaydediyoruz!
        saveUsers(); 
        System.out.println(usr.getUsername() + " kullanıcısı başarıyla eklendi.");
    }
    
    public void removeUser(int userID) {
        // RemoveIf: Eğer ID eşleşiyorsa listeden sil
        boolean removed = users.removeIf(u -> u.getUserID() == userID);
        
        if (removed) {
            saveUsers();
            System.out.println("Kullanıcı silindi ID: " + userID);
        } else {
            System.out.println("Silinecek kullanıcı bulunamadı ID: " + userID);
        }
    }
    
    // Helper: Kaydetme işlemini tek bir yerden yönetmek daha güvenlidir
    private void saveUsers() {
        FileManager.saveData(FILE_NAME, this.users);
    }
    
    public List<User> getAllUsers() {
        return users;
    }
}