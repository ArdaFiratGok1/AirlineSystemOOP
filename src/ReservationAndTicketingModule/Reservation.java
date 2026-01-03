package ReservationAndTicketingModule;

import java.io.Serializable;

import FlightManagementModule.Flight;
import FlightManagementModule.Seat;

public class Reservation implements Serializable {
    // Attributes (Kaynak 12'ye uygun hale getirildi)
    private String reservationCode;
    private Flight flight;
    private Passenger passenger; // EKLENDİ (Plane yerine Passenger geldi)
    private Seat seat;
    private String dateOfReservation;
    private boolean isActive;

    // Constructor
    public Reservation(String reservationCode, Flight flight, Passenger passenger, Seat seat, String dateOfReservation) {
        this.reservationCode = reservationCode;
        this.flight = flight;
        this.passenger = passenger;
        this.seat = seat;
        this.dateOfReservation = dateOfReservation;
        this.isActive = true; // Yeni oluşturulan rezervasyon varsayılan olarak aktiftir
    }

    // Methods
    public void cancel() {
        this.isActive = false;
        // Not: Koltuğun rezervasyon durumunu "boş"a çekme işlemi Manager sınıfında yapılacak.
    }

    /**
     * Terminal çıktısı için özet bilgi.
     * Örn: [REZ-001] Arda Gök -> TK01 (1A) - AKTİF
     */
    @Override
    public String toString() {
        String status = isActive ? "AKTİF" : "İPTAL";
        String passengerName = (passenger != null) ? passenger.getName() + " " + passenger.getSurname() : "Bilinmiyor";
        String flightNum = (flight != null) ? flight.getFlightNum() : "Bilinmiyor";
        String seatNum = (seat != null) ? seat.getSeatNum() : "Yok";

        return String.format("[%s] %s -> Uçuş: %s, Koltuk: %s (%s)", 
                reservationCode, passengerName, flightNum, seatNum, status);
    }

    // --- GETTER & SETTER METODLARI ---

    public String getReservationCode() { return reservationCode; }
    public void setReservationCode(String reservationCode) { this.reservationCode = reservationCode; }

    public Flight getFlight() { return flight; }
    public void setFlight(Flight flight) { this.flight = flight; }

    public Passenger getPassenger() { return passenger; } // Yeni getter
    public void setPassenger(Passenger passenger) { this.passenger = passenger; } // Yeni setter

    public Seat getSeat() { return seat; }
    public void setSeat(Seat seat) { this.seat = seat; }

    public String getDateOfReservation() { return dateOfReservation; }
    public void setDateOfReservation(String dateOfReservation) { this.dateOfReservation = dateOfReservation; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean isActive) { this.isActive = isActive; }
}