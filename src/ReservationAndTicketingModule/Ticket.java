package ReservationAndTicketingModule;

public class Ticket {
	private String ticketID;
	private Reservation reservation;
	private double price;
	private double baggageAllowance;
	
	public Ticket(String ticketID, Reservation reservation, double price, double baggageAllowance)
	{
		this.baggageAllowance = baggageAllowance;
		this.price = price;
		this.reservation = reservation;
		this.ticketID = ticketID;
	}
	
	
	public String printTicket()
	{
		String ticket = "Ticket ID: " + ticketID + "\nReservation: " + reservation + "\nPrice: " + price + "\nBaggage Allowance: " + baggageAllowance;
		return ticket;
	}


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
