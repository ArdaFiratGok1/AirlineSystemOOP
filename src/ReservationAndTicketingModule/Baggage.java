package ReservationAndTicketingModule;

public class Baggage {
	private double weight;
	private String ticketID;

	public Baggage(double weight, String ticketID)
	{
		this.ticketID = ticketID;
		this.weight = weight;
	}
	
	public boolean checkWeightLimit(double allowance)
	{
		if(weight > allowance)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getTicketID() {
		return ticketID;
	}

	public void setTicketID(String ticketID) {
		this.ticketID = ticketID;
	}

	
	
}
