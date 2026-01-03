package ReservationAndTicketingModule;

import java.io.Serializable;

public class Passenger implements Serializable{

	private String passengerID;
	private String name;
	private String surname;
	private String contactInfo;
	
	public Passenger(String passengerID, String name, String surname, String contactInfo)
	{
		this.contactInfo = contactInfo;
		this.name = name;
		this.surname = surname;
		this.passengerID = passengerID;
	}
	
	public String getPassengerInfo()
	{
		return String.format("%s %s (ID: %s) - İletişim: %s", 
                name, surname, passengerID, contactInfo);
	}
	
	@Override
    public String toString() {
        return getPassengerInfo();
    }
	
	public String getPassengerID() {
		return passengerID;
	}

	public void setPassengerID(String passengerID) {
		this.passengerID = passengerID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	
}
