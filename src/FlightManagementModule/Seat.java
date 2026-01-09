package FlightManagementModule;

import java.io.Serializable;

public class Seat implements Serializable {
    
    private String seatNum;       
    private SeatType seatClass;   
    private double price;
    private boolean reserveStatus;

    
    public Seat(String seatNum, SeatType seatClass, double price) {
        this.seatNum = seatNum;
        this.seatClass = seatClass;
        this.price = price;
        this.reserveStatus = false; 
    }

    
    public boolean isAvailable() {
        return !reserveStatus; 
    }

    public void setReserveStatus(boolean status) {
        this.reserveStatus = status;
    }
    
    //managerda kullanmak için(isavailable'in tersi)
    public boolean isReserved() {
        return reserveStatus;
    }

    
    @Override
    public String toString() {
        String statusMark = reserveStatus ? "[DOLU]" : "[BOŞ]";
        return String.format("Koltuk %s (%s) - %.2f TL %s", seatNum, seatClass, price, statusMark);
    }

    
    public String getSeatNum() { return seatNum; }
    public SeatType getSeatClass() { return seatClass; }
    public double getPrice() { return price; }
}