package SWProject;

import java.util.List;

public class ScheduledOrder extends Order {

    private final String requestedTime;

    public ScheduledOrder(int orderId, int customerId, int restaurantId, String status, 
                          String deliveryAddress, double deliveryDistanceKm, double subtotal, 
                          double deliveryFee, double discountAmount, double total, 
                          List<OrderItem> items, String requestedTime) {
        super(orderId, customerId, restaurantId, "SCHEDULED", status, deliveryAddress, 
              deliveryDistanceKm, subtotal, deliveryFee, discountAmount, total, items);
        this.requestedTime = requestedTime;
    }

    @Override
    public void applyTypeRules() {
        // Scheduled orders have no extra fee but enforce a future time
        this.additionalTypeFee = 0.0;
        this.scheduledTime = this.requestedTime;
    }
}