package ServiceAndManagersModule;

import FlightManagementModule.SeatType;

public class CalculatePrice {

 private static final double BUSINESS_BASE_PRICE = 5000.0;
 private static final double ECONOMY_BASE_PRICE = 1500.0;
 private static final double BAGGAGE_FEE = 500.0; 


 //                    hem koltuga hem bagaja bakiyoz
 public double calculate(SeatType type, boolean hasBaggage) {
     double finalPrice = 0.0;

     if (type == SeatType.BUSINESS) {
         finalPrice = BUSINESS_BASE_PRICE;
     } else {
         finalPrice = ECONOMY_BASE_PRICE;
     }
     if (hasBaggage) {
         finalPrice += BAGGAGE_FEE;
     }

     return finalPrice;
 }
}