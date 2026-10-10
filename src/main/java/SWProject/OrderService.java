package SWProject;

import java.util.ArrayList;
import java.util.List;

public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    // Result enum for cancellation US2.4
    public enum CancellationResult {
        CANCELLED,
        ALREADY_CANCELLED,
        NOT_ALLOWED,
        NOT_FOUND,
        INVALID_ORDER_ID,
        DATABASE_ERROR
    }

    public OrderService(OrderRepository orderRepository, MenuItemRepository menuItemRepository,
                        RestaurantRepository restaurantRepository) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    // US2.1, US2.2, US2.3: Place a new order with validations
    public Order placeOrder(int customerId, int restaurantId, String deliveryAddress,
                            double deliveryDistanceKm, List<CartItem> cart) {

        // US2.2: Reject empty orders
        if (cart == null || cart.isEmpty()) {
            System.out.println("Order Rejected: Cannot place an empty order.");
            return null;
        }

        // US2.3: Validate delivery address format
        if (deliveryAddress == null || deliveryAddress.trim().length() < 5) {
            System.out.println("Order Rejected: Invalid delivery address (must be at least 5 characters).");
            return null;
        }

        // Fetch restaurant details
        Restaurant restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant == null || !restaurant.isOpen()) {
            System.out.println("Order Rejected: Restaurant is closed or does not exist.");
            return null;
        }

        // US2.3: Reject if outside delivery zone
        if (deliveryDistanceKm <= 0 || deliveryDistanceKm > restaurant.getMaxDeliveryRadiusKm()) {
            System.out.println("Order Rejected: Restaurant is outside your delivery zone. (Max radius: "
                    + restaurant.getMaxDeliveryRadiusKm() + " km, Your distance: " + deliveryDistanceKm + " km)");
            return null;
        }

        List<MenuItem> availableItems = menuItemRepository.findAvailableByRestaurantId(restaurantId);
        List<OrderItem> orderItems = new ArrayList<>();
        double calculatedTotal = 0.0;

        for (CartItem cartItem : cart) {
            // US2.2: Reject invalid quantities
            if (cartItem.getQuantity() <= 0) {
                System.out.println("Order Rejected: Invalid quantity (" + cartItem.getQuantity()
                        + ") for item ID: " + cartItem.getItemId());
                return null;
            }

            // US2.2: Reject unavailable items
            MenuItem dbItem = availableItems.stream()
                    .filter(i -> i.getItemId() == cartItem.getItemId())
                    .findFirst()
                    .orElse(null);

            if (dbItem == null) {
                System.out.println("Order Rejected: Item ID " + cartItem.getItemId()
                        + " is unavailable or does not belong to this restaurant.");
                return null;
            }

            double itemSubtotal = dbItem.getPrice() * cartItem.getQuantity();
            calculatedTotal += itemSubtotal;

            orderItems.add(new OrderItem(dbItem.getItemId(), dbItem.getName(), cartItem.getQuantity(), dbItem.getPrice()));
        }

        // US2.3: Reject if total is below minimum order value
        if (calculatedTotal < restaurant.getMinOrderValue()) {
            System.out.println("Order Rejected: Total ($" + calculatedTotal
                    + ") is below minimum order value ($" + restaurant.getMinOrderValue() + ").");
            return null;
        }

        // US2.1: Order status changes to PENDING
        Order newOrder = new Order(
                0, customerId, restaurantId, "STANDARD",
                "PENDING", deliveryAddress.trim(), calculatedTotal, orderItems
        );

        boolean success = orderRepository.saveOrder(newOrder);
        if (success) {
            return newOrder;
        } else {
            System.out.println("Order Rejected: Failed to save order to database.");
            return null;
        }
    }

    // US2.4: Cancel an existing order
    public CancellationResult cancelOrder(int customerId, int orderId) {
        if (orderId <= 0) {
            return CancellationResult.INVALID_ORDER_ID;
        }

        String currentStatus = orderRepository.findOrderStatus(orderId, customerId);

        if (currentStatus == null) {
            return CancellationResult.NOT_FOUND;
        }

        if ("CANCELLED".equalsIgnoreCase(currentStatus)) {
            return CancellationResult.ALREADY_CANCELLED;
        }

        if (!"PENDING".equalsIgnoreCase(currentStatus)) {
            return CancellationResult.NOT_ALLOWED;
        }

        boolean updated = orderRepository.updateOrderStatus(orderId, customerId, "CANCELLED");
        if (updated) {
            return CancellationResult.CANCELLED;
        }

        return CancellationResult.DATABASE_ERROR;
    }

    // Helper method to check if customer has a pending order
    public boolean hasPendingOrder(int customerId) {
        return orderRepository.hasPendingOrder(customerId);
    }

    // --- UX IMPROVEMENT METHODS FOR DISPLAYING ORDERS LISTS ---

    // For Manager: Fetch pending orders that need driver assignment
    public List<Order> getPendingOrders() {
        return orderRepository.findPendingOrders();
    }

    // For Manager: Fetch active orders for status changes
    public List<Order> getActiveOrdersForManager() {
        return orderRepository.findActiveOrdersForManager();
    }

    // For Customer: Fetch active orders for live tracking
    public List<Order> getActiveOrdersByCustomer(int customerId) {
        return orderRepository.findActiveOrdersByCustomer(customerId);
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