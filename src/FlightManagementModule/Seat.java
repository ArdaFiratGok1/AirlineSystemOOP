package FlightManagementModule;

import java.io.Serializable;

public class Seat implements Serializable {
    // Attributes (UML'den)
    private String seatNum;
    private SeatType seatClass;
    private double price;
    private boolean reserveStatus;

    // Constructor
    public Seat(String seatNum, SeatType seatClass, double price) {
        this.seatNum = seatNum;
        this.seatClass = seatClass;
        this.price = price;
        this.reserveStatus = false; // Başlangıçta boş
    }

    // Methods (UML'den)
    public boolean isAvailable() {
        return !reserveStatus; // Basit bir kontrol ekledim
    }

    public void setReserveStatus(boolean status) {
        this.reserveStatus = status;
    }

    // Getter methods
    public String getSeatNum() { return seatNum; }
    public SeatType getSeatClass() { return seatClass; }
    public double getPrice() { return price; }
}