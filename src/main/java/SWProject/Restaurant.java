package SWProject;

public class Restaurant {
    private final int restaurantId;
    private final String name;
    private final String status;
    private final double minOrderValue;
    private final double maxDeliveryRadiusKm;
    private final double latitude;
    private final double longitude;

    public Restaurant(int restaurantId, String name, String status,
                      double minOrderValue, double maxDeliveryRadiusKm,
                      double latitude, double longitude) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.status = status;
        this.minOrderValue = minOrderValue;
        this.maxDeliveryRadiusKm = maxDeliveryRadiusKm;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Kept for compatibility with any older code that constructs Restaurant without coordinates.
    public Restaurant(int restaurantId, String name, String status,
                      double minOrderValue, double maxDeliveryRadiusKm) {
        this(restaurantId, name, status, minOrderValue, maxDeliveryRadiusKm, 0.0, 0.0);
    }

    public int getRestaurantId() { return restaurantId; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public double getMinOrderValue() { return minOrderValue; }
    public double getMaxDeliveryRadiusKm() { return maxDeliveryRadiusKm; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    public boolean isOpen() {
        return "OPEN".equalsIgnoreCase(status);
    }
}
