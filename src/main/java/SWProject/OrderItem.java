package SWProject;

public class OrderItem {
    private final int itemId;
    private final String itemName;
    private final int quantity;
    private final double unitPrice;

    public OrderItem(int itemId, String itemName, int quantity, double unitPrice) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }

    public double getSubtotal() {
        return quantity * unitPrice;
    }
}