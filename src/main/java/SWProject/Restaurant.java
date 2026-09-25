package SWProject;

public class Restaurant {
    private final int restaurantId;
    private final String name;
    private final String status;

    public Restaurant(int restaurantId, String name, String status) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.status = status;
    }

    public int getRestaurantId() { return restaurantId; }
    public int getId() { return restaurantId; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public boolean isOpen() { return "OPEN".equalsIgnoreCase(status); }
}
