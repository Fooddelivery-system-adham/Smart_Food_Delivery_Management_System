package SWProject;

import java.util.List;

public class ExpressOrder extends Order {

    public ExpressOrder(int orderId, int customerId, int restaurantId, String status, 
                        String deliveryAddress, double deliveryDistanceKm, double subtotal, 
                        double deliveryFee, double discountAmount, double total, List<OrderItem> items) {
        super(orderId, customerId, restaurantId, "EXPRESS", status, deliveryAddress, 
              deliveryDistanceKm, subtotal, deliveryFee, discountAmount, total, items);
    }

    @Override
    public void applyTypeRules() {
        // Express orders require an additional urgency fee (US5.2)
        this.additionalTypeFee = 5.00;
        this.scheduledTime = "ASAP (PRIORITY)";
    }
    
}