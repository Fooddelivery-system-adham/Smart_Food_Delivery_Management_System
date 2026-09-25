package SWProject;

public class MenuItem {
    private final int itemId;
    private final int restaurantId;
    private final String name;
    private final double price;
    private final String status; 

    public MenuItem(int itemId, int restaurantId, String name, double price, String status) {
        this.itemId = itemId;
        this.restaurantId = restaurantId;
        this.name = name;
        this.price = price;
        this.status = status;
    }

    public int getItemId() { return itemId; }
    public int getRestaurantId() { return restaurantId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getStatus() { return status; }
}