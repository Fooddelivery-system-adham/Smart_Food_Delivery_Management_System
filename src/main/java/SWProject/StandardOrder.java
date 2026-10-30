package SWProject;

import java.util.List;

public class StandardOrder extends Order {

    public StandardOrder(int orderId, int customerId, int restaurantId, String status, 
                         String deliveryAddress, double deliveryDistanceKm, double subtotal, 
                         double deliveryFee, double discountAmount, double total, List<OrderItem> items) {
        super(orderId, customerId, restaurantId, "STANDARD", status, deliveryAddress, 
              deliveryDistanceKm, subtotal, deliveryFee, discountAmount, total, items);
    }

    @Override
    public void applyTypeRules() {
        // Standard orders have no extra fees and are delivered ASAP
        this.additionalTypeFee = 0.0;
        this.scheduledTime = "ASAP";
    }
}