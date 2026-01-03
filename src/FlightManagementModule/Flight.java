package FlightManagementModule;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Flight implements Serializable {
    // Attributes
    private String flightNum;
    private Route route;
    private String date;      // Format: "dd-MM-yyyy" (Örn: "01-01-2026")
    private String hour;
    private String duration;
    private Plane plane;

    // Constructor
    public Flight(String flightNum, Route route, String date, String hour, String duration, Plane plane) {
        this.flightNum = flightNum;
        this.route = route;
        this.date = date;
        this.hour = hour;
        this.duration = duration;
        this.plane = plane;
    }

    // --- DOLDURULAN KISIMLAR ---

    /**
     * Uçuş tarihini bugünün tarihiyle kıyaslar.
     * Eğer uçuş tarihi geçmişse true döndürür.
     */
    public boolean isExpired() {
        try {
            // Tarih formatını belirliyoruz (Gün-Ay-Yıl)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate flightDate = LocalDate.parse(this.date, formatter);
            
            // Uçuş tarihi bugünden önceyse süresi geçmiştir
            return flightDate.isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            System.err.println("Tarih formatı hatası (" + flightNum + "): " + this.date);
            return false; // Hata varsa varsayılan olarak geçmemiş sayalım
        }
    }

    /**
     * Uçuş bilgilerini okunabilir bir String olarak döndürür.
     */
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

	/**
     * Terminalde nesneyi yazdırınca (System.out.println) anlamlı çıktı verir.
     */
    @Override
    public String toString() {
        return getFlightDetails();
    }

    // --- GETTER METODLARI ---
    public String getFlightNum() { return flightNum; }
    public Route getRoute() { return route; }
    public Plane getPlane() { return plane; }
    public String getDate() { return date; }
    public String getHour() { return hour; } // Bunu da ekledim, lazım olabilir
}