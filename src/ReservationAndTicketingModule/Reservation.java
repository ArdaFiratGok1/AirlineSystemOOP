package ReservationAndTicketingModule;

import java.io.Serializable;

import FlightManagementModule.*;
//Tarih kutuphanesi eklenecek

public class Reservation implements Serializable {

	private String reservationCode;
	private Flight flight;
	private Plane plane;
	private Seat seat;
	private String dateOfReservation;
	private boolean isActive;

	
	public Reservation(String reservationCode, Flight flight, Plane plane, Seat seat, String dateOfReservation, boolean isActive) 
	{
		this.reservationCode = reservationCode;
		this.flight = flight;
		this.plane = plane;
		this.seat = seat;
		this.dateOfReservation = dateOfReservation;
		this.isActive = isActive;
	}

	
	public void cancel()
	{
		isActive = false;
	}


	public String getReservationCode() {
		return reservationCode;
	}


	public void setReservationCode(String reservationCode) {
		this.reservationCode = reservationCode;
	}


	public Flight getFlight() {
		return flight;
	}


	public void setFlight(Flight flight) {
		this.flight = flight;
	}


	public Plane getPlane() {
		return plane;
	}


	public void setPlane(Plane plane) {
		this.plane = plane;
	}


	public Seat getSeat() {
		return seat;
	}


	public void setSeat(Seat seat) {
		this.seat = seat;
	}


	public String getDateOfReservation() {
		return dateOfReservation;
	}


	public void setDateOfReservation(String dateOfReservation) {
		this.dateOfReservation = dateOfReservation;
	}


	public boolean isActive() {
		return isActive;
	}


	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
}
