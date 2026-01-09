package ReservationAndTicketingModule;
import java.io.Serializable;

public class Baggage implements Serializable {
    
    private double weight;
    private String ticketID; //UML'de Ticket ile ilişki kurmak için eklemiştik

    
    public Baggage(double weight, String ticketID) {
        this.weight = weight;
        this.ticketID = ticketID;
    }
    
    
    public boolean checkWeightLimit(double allowance) {
        return weight <= allowance;//if elsesiz kullanım zaten bool döndürüyorz
    }
    
    
    @Override
    public String toString() {
        return String.format("Bagaj: %.1f kg [Bilet ID: %s]", weight, ticketID);
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
