package SWProject;

import java.util.List;

public class Order {
    private int orderId;
    private final int customerId;
    private final int restaurantId;
    private Integer driverId;
    private final String orderType;
    private String status;
    private final String deliveryAddress;
    private final double deliveryDistanceKm;
    private final double subtotal;
    private final double deliveryFee;
    private final double discountAmount;
    private final double total;
    private final List<OrderItem> items;

    // Full 12-parameter constructor for Sprint 4 (Detailed Invoice)
    public Order(int orderId, int customerId, int restaurantId, String orderType,
                 String status, String deliveryAddress, double deliveryDistanceKm,
                 double subtotal, double deliveryFee, double discountAmount,
                 double total, List<OrderItem> items) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.orderType = orderType;
        this.status = status;
        this.deliveryAddress = deliveryAddress;
        this.deliveryDistanceKm = deliveryDistanceKm;
        this.subtotal = subtotal;
        this.deliveryFee = deliveryFee;
        this.discountAmount = discountAmount;
        this.total = total;
        this.items = items;
    }

    // Overloaded 8-parameter constructor for backward compatibility with simple queries
    public Order(int orderId, int customerId, int restaurantId, String orderType,
                 String status, String deliveryAddress, double total, List<OrderItem> items) {
        this(orderId, customerId, restaurantId, orderType, status, deliveryAddress, 0.0, total, 0.0, 0.0, total, items);
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getCustomerId() { return customerId; }
    public int getRestaurantId() { return restaurantId; }
    public Integer getDriverId() { return driverId; }
    public void setDriverId(Integer driverId) { this.driverId = driverId; }
    public String getOrderType() { return orderType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public double getDeliveryDistanceKm() { return deliveryDistanceKm; }
    public double getSubtotal() { return subtotal; }
    public double getDeliveryFee() { return deliveryFee; }
    public double getDiscountAmount() { return discountAmount; }
    public double getTotal() { return total; }
    public List<OrderItem> getItems() { return items; }
}
