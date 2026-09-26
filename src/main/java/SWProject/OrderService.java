package SWProject;

import java.util.ArrayList;
import java.util.List;

public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;

    public OrderService(OrderRepository orderRepository, MenuItemRepository menuItemRepository) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public Order placeOrder(int customerId, int restaurantId, String deliveryAddress, List<CartItem> cart) {
        // US2.2: Reject empty orders
        if (cart == null || cart.isEmpty()) {
            System.out.println("❌ Order Rejected: Cannot place an empty order.");
            return null;
        }

        // US2.2: Reject invalid delivery address
        if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
            System.out.println("❌ Order Rejected: Delivery address cannot be empty.");
            return null;
        }

        // Fetch available items for this restaurant from DB
        List<MenuItem> availableItems = menuItemRepository.findAvailableByRestaurantId(restaurantId);
        List<OrderItem> orderItems = new ArrayList<>();
        double calculatedTotal = 0.0;

        for (CartItem cartItem : cart) {
            // US2.2: Reject invalid quantities
            if (cartItem.getQuantity() <= 0) {
                System.out.println("❌ Order Rejected: Invalid quantity (" + cartItem.getQuantity() 
                        + ") for item ID: " + cartItem.getItemId());
                return null;
            }

            // US2.2: Reject unavailable items or items not from this restaurant
            MenuItem dbItem = availableItems.stream()
                    .filter(i -> i.getItemId() == cartItem.getItemId())
                    .findFirst()
                    .orElse(null);

            if (dbItem == null) {
                System.out.println("❌ Order Rejected: Item ID " + cartItem.getItemId() 
                        + " is unavailable or does not belong to this restaurant.");
                return null;
            }

            // US2.1: Correct order total calculation
            double itemSubtotal = dbItem.getPrice() * cartItem.getQuantity();
            calculatedTotal += itemSubtotal;

            orderItems.add(new OrderItem(dbItem.getItemId(), dbItem.getName(), cartItem.getQuantity(), dbItem.getPrice()));
        }

        // US2.1: Order status changes to Pending
        Order newOrder = new Order(
                0, customerId, restaurantId, "STANDARD",
                "PENDING", deliveryAddress.trim(), calculatedTotal, orderItems
        );

        // US2.1: Order record created in database
        boolean success = orderRepository.saveOrder(newOrder);
        if (success) {
            return newOrder;
        } else {
            System.out.println("❌ Order Rejected: Failed to save order to database.");
            return null;
        }
    }

    // Simple helper class representing item choice from console
    public static class CartItem {
        private final int itemId;
        private final int quantity;

        public CartItem(int itemId, int quantity) {
            this.itemId = itemId;
            this.quantity = quantity;
        }

        public int getItemId() { return itemId; }
        public int getQuantity() { return quantity; }
    }
}