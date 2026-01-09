package FlightManagementModule;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Flight implements Serializable {
    
    private String flightNum;
    private Route route;
    private String date;      //"01-01-2026"
    private String hour;      //"14:30"
    private String duration;
    private Plane plane;

    
    public Flight(String flightNum, Route route, String date, String hour, String duration, Plane plane) {
        this.flightNum = flightNum;
        this.route = route;
        this.date = date;
        this.hour = hour;
        this.duration = duration;
        this.plane = plane;
    }

    
    
    public boolean isExpired()
    {
    	try
    	{
    		String dateTimeString = this.date+ " " + this.hour; //tarih ve saat birlestirmesi	
    		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"); 
    		LocalDateTime flightDateTime = LocalDateTime.parse(dateTimeString, formatter);
    		return flightDateTime.isBefore(LocalDateTime.now()); //bu karsilastirma saate de bakiyor
    	} catch (DateTimeParseException e)
    	{
    		System.err.println("Tarih/Saat formatı hatası (" + flightNum + "): " + this.date + " " + this.hour);
    		return false; //hata varsa gecmemis sayma
    	}
    }
    
   
    public String getFlightDetails() {
        String routeInfo = (route != null) ? route.getDeparturePlace() + " -> " + route.getArrivalPlace() : "Rota Yok";
        return String.format("Uçuş: %s | %s | Tarih: %s %s | Süre: %s", 
                flightNum, routeInfo, date, hour, duration);
    }

    public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public void setFlightNum(String flightNum) {
		this.flightNum = flightNum;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}
	
	public String getTime() {
        return hour;
    }

	public void setPlane(Plane plane) {
		this.plane = plane;
	}

	
    @Override
    public String toString() {
        return getFlightDetails();
    }

    
    public String getFlightNum() { return flightNum; }
    public Route getRoute() { return route; }
    public Plane getPlane() { return plane; }
    public String getDate() { return date; }
    public String getHour() { return hour; } //Bunu da ekledim, lazım olabilir
}