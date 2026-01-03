package FlightManagementModule;

import java.io.Serializable;

public class Seat implements Serializable {
    // Attributes (UML'den)
    private String seatNum;       // Örn: "1A"
    private SeatType seatClass;   // Enum: ECONOMY, BUSINESS
    private double price;
    private boolean reserveStatus;

    // Constructor
    public Seat(String seatNum, SeatType seatClass, double price) {
        this.seatNum = seatNum;
        this.seatClass = seatClass;
        this.price = price;
        this.reserveStatus = false; // Başlangıçta boş
    }

    // Methods
    public boolean isAvailable() {
        return !reserveStatus; 
    }

    public void setReserveStatus(boolean status) {
        this.reserveStatus = status;
    }
    
    // Okunabilirlik için yardımcı metod (Manager'lar kullanabilir)
    public boolean isReserved() {
        return reserveStatus;
    }

    /**
     * Terminalde koltuk bilgisini güzel yazdırmak için eklendi.
     * Örn: "Koltuk 1A (BUSINESS) - 5000.0 TL [BOŞ]"
     */
    @Override
    public String toString() {
        String statusMark = reserveStatus ? "[DOLU]" : "[BOŞ]";
        return String.format("Koltuk %s (%s) - %.2f TL %s", seatNum, seatClass, price, statusMark);
    }

    // Getter methods
    public String getSeatNum() { return seatNum; }
    public SeatType getSeatClass() { return seatClass; }
    public double getPrice() { return price; }
}