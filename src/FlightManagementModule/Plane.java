package FlightManagementModule;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Plane implements Serializable {
    private String planeID;
    private String planeModel;
    private int capacity;
    private int maxCapacity;
    
    
    private Map<String, Seat> seats; 

    public Plane(String planeID, String planeModel, int maxCapacity) {
        this.planeID = planeID;
        this.planeModel = planeModel;
        this.maxCapacity = maxCapacity;
        int targetCapacity = (maxCapacity * 90) / 100;//burada direkt tüm kapasiteyi girmek yerine 90'ı aktiflesiyor sonra istersek yükseltsin admin diye
        int rows = targetCapacity / 6;
        this.capacity = rows * 6;
        this.seats = new HashMap<>(); //bos mape atıyoruz önce
  
        // Constructor çalıştığında koltukları dolduruyoruz
        initializeSeats(rows);  //CONSTRUCTERA MAXCAPACITY EKLEYIP ONUN %90INI CAPACITYE ATAYIP KAPASITE DOLDUKTAN SONRA KALAN BOS YERLERI ZAMLI FIYATTAN SATABILIRIZ

    }

    
    public void initializeSeats(int rowNum) {           
        
        char[] colLetters = {'A', 'B', 'C', 'D', 'E', 'F'};
        int r, col;
        int businessRowLimit = (rowNum * 15) / 100;
        boolean isBusiness;
        for (r = 0; r < rowNum; r++) {
            for (col = 0; col < 6; col++) {
                String seatNum = (r + 1) + "" + colLetters[col];//String oldugunu anlasın diye "" koydum buraya
                
                isBusiness = r < businessRowLimit;
                SeatType type = isBusiness ? SeatType.BUSINESS : SeatType.ECONOMY; //BURDA UCAGIN KAPASITESININ %15INI BUSINESS YAPMAYA CALISTIM
                double price = isBusiness ? 5000.0 : 1500.0;

                Seat seat = new Seat(seatNum, type, price);
                
                // Matris yerine Map'e put işlemi yapıyoruz
                seats.put(seatNum, seat);
            }
        }
    }

    public String getPlaneModel() {
		return planeModel;
	}

	public void setPlaneModel(String planeModel) {
		this.planeModel = planeModel;
	}

	public void setPlaneID(String planeID) {
		this.planeID = planeID;
	}

	public int getMaxCapacity()
	{
		return maxCapacity;
	}

	public void setSeats(Map<String, Seat> seats) {
		this.seats = seats;
	}

	
    public Seat getSeat(String seatNum) {
        return seats.get(seatNum); 
    }
    
    //Map'i döndüren getter (Managerlar için lazım olacak)
    public Map<String, Seat> getSeats() { return seats; }
    
    public String getPlaneID() { return planeID; }
    public int getCapacity() { return capacity; }
    
    @Override
    public String toString() {
        return planeModel + " (" + planeID + ")";
    }
}