package SWProject;

/**
 * Represents a single menu item that belongs to a restaurant.
 */
public class MenuItem {

    private final int itemId;
    private final int restaurantId;
    private final String name;
    private final double price;
    private final String status; // AVAILABLE or UNAVAILABLE

    public MenuItem(int itemId, int restaurantId, String name, double price, String status) {
        this.itemId = itemId;
        this.restaurantId = restaurantId;
        this.name = name;
        this.price = price;
        this.status = status;
    }

    public int getItemId() {
        return itemId;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    // Helper method to check if the item can be ordered
    public boolean isAvailable() {
        return "AVAILABLE".equalsIgnoreCase(status);
    }

    // Useful for printing the item quickly in the console
    @Override
    public String toString() {
        return "[" + itemId + "] " + name + " | $" + price + " | " + status;
    }
}