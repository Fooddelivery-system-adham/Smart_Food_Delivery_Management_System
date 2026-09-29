package SWProject;

public class Restaurant {
    private final int restaurantId;
    private final String name;
    private final String status;
    private final double minOrderValue;
    private final double maxDeliveryRadiusKm;

    public Restaurant(int restaurantId, String name, String status, double minOrderValue, double maxDeliveryRadiusKm) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.status = status;
        this.minOrderValue = minOrderValue;
        this.maxDeliveryRadiusKm = maxDeliveryRadiusKm;
    }

    public int getRestaurantId() { return restaurantId; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public double getMinOrderValue() { return minOrderValue; }
    public double getMaxDeliveryRadiusKm() { return maxDeliveryRadiusKm; }

    public boolean isOpen() {
        return "OPEN".equalsIgnoreCase(status);
    }
}