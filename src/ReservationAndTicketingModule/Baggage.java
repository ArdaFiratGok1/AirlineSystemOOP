package ReservationAndTicketingModule;
import java.io.Serializable;

public class Baggage implements Serializable {
    // Attributes (UML'den - Kaynak: 12)
    private double weight;
    private String ticketID; // UML'de Ticket ile ilişki kurmak için eklemiştik

    // Constructor
    public Baggage(double weight, String ticketID) {
        this.weight = weight;
        this.ticketID = ticketID;
    }
    
    // Methods
    /**
     * Bagajın verilen hakkı (allowance) aşıp aşmadığını kontrol eder.
     * @param allowance Biletin tanıdığı hak (örn: 20kg)
     * @return Limit dahilindeyse true, aşıyorsa false döner.
     */
    public boolean checkWeightLimit(double allowance) {
        // if-else yerine mantıksal ifadeyi doğrudan döndürmek daha temizdir
        return weight <= allowance;
    }
    
    /**
     * Terminalde test ederken bagaj bilgisini görmek için.
     */
    @Override
    public String toString() {
        return String.format("Bagaj: %.1f kg [Bilet ID: %s]", weight, ticketID);
    }
    
    // --- GETTER & SETTER METODLARI ---

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
