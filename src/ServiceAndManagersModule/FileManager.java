package ServiceAndManagersModule;

import java.io.*;

public class FileManager {

    // Verilen veriyi (List, Object vb.) dosyaya kaydeder
    public static void saveData(String fileName, Object data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(data);
            System.out.println("Veri kaydedildi: " + fileName);
        } catch (IOException e) {
            System.err.println("Kaydetme hatası (" + fileName + "): " + e.getMessage());
        }
    }

    // Dosyadan veriyi okur ve Object olarak döndürür
    public static Object loadData(String fileName) {
        File file = new File(fileName);
        
        // Eğer dosya henüz yoksa (ilk çalıştırış), null döndür
        if (!file.exists()) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Okuma hatası (" + fileName + "): " + e.getMessage());
            return null;
        }
    }
}
