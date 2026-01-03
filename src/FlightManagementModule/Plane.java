package FlightManagementModule;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Plane implements Serializable {
    private String planeID;
    private String planeModel;
    private int capacity;
    
    // DEĞİŞİKLİK: Matris yerine Map kullanıyoruz.
    // Key: "1A" (Koltuk No), Value: Seat Nesnesi
    private Map<String, Seat> seats; 

    public Plane(String planeID, String planeModel, int capacity) {
        this.planeID = planeID;
        this.planeModel = planeModel;
        this.capacity = capacity;
        this.seats = new HashMap<>(); // Boş harita oluşturuluyor
        
        // Constructor çalıştığında koltukları dolduruyoruz
        initializeSeats(); 
    }

    /**
     * Döküman Kaynak 9 ve 15.
     * Koltukları oluşturup Map'e ekler.
     */
    public void initializeSeats() {
        // Dökümanda 30 sıra ve 6 sütun (A-F) örneği verilmişti.
        // Ancak Map kullandığımız için ileride burası dinamik parametre de alabilir.
        char[] colLetters = {'A', 'B', 'C', 'D', 'E', 'F'};
        int rows = 30; 

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < 6; col++) {
                String seatNum = (row + 1) + "" + colLetters[col];//String oldugunu anlasın diye "" koydum buraya
                
                // İlk 5 sıra Business, kalanı Economy
                SeatType type = (row < 5) ? SeatType.BUSINESS : SeatType.ECONOMY;
                double price = (row < 5) ? 5000.0 : 1500.0;

                Seat seat = new Seat(seatNum, type, price);
                
                // Matris yerine Map'e "put" işlemi yapıyoruz
                seats.put(seatNum, seat);
            }
        }
    }

    /**
     * Array döngüsü yerine doğrudan Key ile erişim. Çok daha hızlıdır.
     */
    public Seat getSeat(String seatNum) {
        return seats.get(seatNum); // Varsa döner, yoksa null döner
    }
    
    // Map'i döndüren getter (Manager'lar için lazım olacak)
    public Map<String, Seat> getSeats() { return seats; }
    
    public String getPlaneID() { return planeID; }
    public int getCapacity() { return capacity; }
    
    @Override
    public String toString() {
        return planeModel + " (" + planeID + ")";
    }
}