package SWProject;

import java.math.BigDecimal;

public class MenuItem {
    private final int itemId;
    private final int restaurantId;
    private final String name;
    private final BigDecimal price;
    private final String status;

    public MenuItem(int itemId, int restaurantId, String name, BigDecimal price, String status) {
        this.itemId = itemId;
        this.restaurantId = restaurantId;
        this.name = name;
        this.price = price;
        this.status = status;
    }

    public MenuItem(int itemId, String name, BigDecimal price) {
        this(itemId, 0, name, price, "AVAILABLE");
    }

    public int getItemId() { return itemId; }
    public int getId() { return itemId; }
    public int getRestaurantId() { return restaurantId; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public String getStatus() { return status; }
    public boolean isAvailable() { return "AVAILABLE".equalsIgnoreCase(status); }
}
