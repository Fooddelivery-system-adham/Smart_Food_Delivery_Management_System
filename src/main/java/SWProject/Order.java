package SWProject;

import java.util.List;

public class Order {
    private int orderId;
    private final int customerId;
    private final int restaurantId;
    private Integer driverId;
    private final String orderType; // STANDARD, EXPRESS, SCHEDULED
    private String status;         // PENDING, CONFIRMED, etc.
    private final String deliveryAddress;
    private final double total;
    private final List<OrderItem> items;

    public Order(int orderId, int customerId, int restaurantId, String orderType,
                 String status, String deliveryAddress, double total, List<OrderItem> items) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.orderType = orderType;
        this.status = status;
        this.deliveryAddress = deliveryAddress;
        this.total = total;
        this.items = items;
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getCustomerId() { return customerId; }
    public int getRestaurantId() { return restaurantId; }
    public Integer getDriverId() { return driverId; }
    public String getOrderType() { return orderType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public double getTotal() { return total; }
    public List<OrderItem> getItems() { return items; }
}