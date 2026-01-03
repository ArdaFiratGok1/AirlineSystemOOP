package ReservationAndTicketingModule;

import java.io.Serializable;

public class Ticket implements Serializable {
    private String ticketID;
    private Reservation reservation;
    private double price;
    private double baggageAllowance;
    
    // Constructor
    public Ticket(String ticketID, Reservation reservation, double price, double baggageAllowance) {
        this.ticketID = ticketID;
        this.reservation = reservation;
        this.price = price;
        this.baggageAllowance = baggageAllowance;
    }
    
    // Methods
    public String printTicket() {
        String passengerName = "Bilinmiyor";
        String flightNum = "Bilinmiyor";
        String seatNum = "Yok";

        // Null check yaparak rezervasyon detaylarını çekiyoruz
        if (reservation != null) {
            if (reservation.getPassenger() != null) {
                passengerName = reservation.getPassenger().getName() + " " + reservation.getPassenger().getSurname();
            }
            if (reservation.getFlight() != null) {
                flightNum = reservation.getFlight().getFlightNum();
            }
            if (reservation.getSeat() != null) {
                seatNum = reservation.getSeat().getSeatNum();
            }
        }

        return String.format(
            "========================================\n" +
            "               UÇUŞ BİLETİ              \n" +
            "========================================\n" +
            "Bilet ID    : %s\n" +
            "Yolcu       : %s\n" +
            "Uçuş No     : %s\n" +
            "Koltuk      : %s\n" +
            "Bagaj Hakkı : %.1f kg\n" +
            "Tutar       : %.2f TL\n" +
            "========================================",
            ticketID, passengerName, flightNum, seatNum, baggageAllowance, price
        );
    }

    /**
     * Terminalde liste halinde görürken kısa özet geçmek için.
     */
    @Override
    public String toString() {
        return "Bilet (" + ticketID + ") - Fiyat: " + price + " TL";
    }

    // --- GETTER & SETTER METODLARI ---

    public String getTicketID() {
        return ticketID;
    }

    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getBaggageAllowance() {
        return baggageAllowance;
    }

    public void setBaggageAllowance(double baggageAllowance) {
        this.baggageAllowance = baggageAllowance;
    }
}
