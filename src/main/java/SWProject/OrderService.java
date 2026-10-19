package SWProject;

import java.util.ArrayList;
import java.util.List;

/**
 * Core business logic service responsible for managing food orders.
 * Handles order validation, GPS-based auto distance calculation, dynamic pricing strategies,
 * polymorphic order creation (Standard, Express, Scheduled), order cancellations, and order querying.
 *
 * @author Adham
 * @author Ahmad
 * @version 1.0
 */
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private PricingStrategy pricingStrategy = new NormalPricingStrategy();

    /**
     * Enumeration representing the possible outcomes of an order cancellation attempt (US2.4).
     *
     * @author Adham
     */
    public enum CancellationResult {
        /** Order was successfully cancelled. */
        CANCELLED,
        /** Order was already cancelled previously. */
        ALREADY_CANCELLED,
        /** Order cancellation is not allowed (e.g., status is beyond PENDING). */
        NOT_ALLOWED,
        /** Order was not found in the database. */
        NOT_FOUND,
        /** The provided order ID was invalid. */
        INVALID_ORDER_ID,
        /** A database error occurred during cancellation. */
        DATABASE_ERROR
    }

    /**
     * Constructs an OrderService with required repository dependencies.
     *
     * @param orderRepository      the repository managing order persistence
     * @param menuItemRepository  the repository managing menu item persistence
     * @param restaurantRepository the repository managing restaurant persistence
     */
    public OrderService(OrderRepository orderRepository, MenuItemRepository menuItemRepository,
                        RestaurantRepository restaurantRepository) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    /**
     * Sets the active dynamic pricing strategy for calculating order totals (US4.3).
     *
     * @param pricingStrategy the strategy to apply (e.g., Normal, Peak-Hour, Express, Promotional)
     */
    public void setPricingStrategy(PricingStrategy pricingStrategy) {
        if (pricingStrategy != null) {
            this.pricingStrategy = pricingStrategy;
        }
    }

    /**
     * Places a new food delivery order with validations, GPS-based auto distance calculation,
     * dynamic pricing, and polymorphic order type rules (US2.1, US2.2, US2.3, US4.2, US4.3, US5.1, US5.2).
     *
     * @param customer        the user placing the order (contains latitude and longitude coordinates)
     * @param restaurantId    the ID of the selected restaurant
     * @param deliveryAddress the delivery text address
     * @param cart            the list of selected cart items and quantities
     * @param orderType       the requested order type ("STANDARD", "EXPRESS", or "SCHEDULED")
     * @param scheduledTime   the requested delivery time for scheduled orders
     * @return the saved {@link Order} object if successfully placed, or {@code null} if rejected
     */
    public Order placeOrder(User customer, int restaurantId, String deliveryAddress, 
                            List<CartItem> cart, String orderType, String scheduledTime) {

        // US2.2: Reject empty orders
        if (cart == null || cart.isEmpty()) {
            System.out.println("Order Rejected: Cannot place an empty order.");
            return null;
        }

        // US2.3: Validate delivery address length
        if (deliveryAddress == null || deliveryAddress.trim().length() < 5) {
            System.out.println("Order Rejected: Invalid delivery address (must be at least 5 characters).");
            return null;
        }

        // Fetch restaurant
        Restaurant restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant == null || !restaurant.isOpen()) {    
            System.out.println("Order Rejected: Restaurant is closed or does not exist.");
            return null;
        }

        // GPS Auto-Distance Calculation
        double deliveryDistanceKm = GeoUtils.calculateDistanceKm(
                customer.getLatitude(), customer.getLongitude(),
                restaurant.getLatitude(), restaurant.getLongitude()
        );
        deliveryDistanceKm = Math.round(deliveryDistanceKm * 100.0) / 100.0;

        // US2.3: Reject if user is beyond the restaurant's maximum delivery radius
        if (deliveryDistanceKm > restaurant.getMaxDeliveryRadiusKm()) {
            System.out.println("Order Rejected: Restaurant is outside your delivery zone. (Max radius: "
                    + restaurant.getMaxDeliveryRadiusKm() + " km, Your distance: " + deliveryDistanceKm + " km)");
            return null;
        }

        List<MenuItem> availableItems = menuItemRepository.findAvailableByRestaurantId(restaurantId);
        List<OrderItem> orderItems = new ArrayList<>();
        double foodSubtotal = 0.0;

        for (CartItem cartItem : cart) {
            // US2.2: Reject invalid item quantities
            if (cartItem.getQuantity() <= 0) {
                System.out.println("Order Rejected: Invalid quantity (" + cartItem.getQuantity() + ").");
                return null;
            }

            // US2.2: Reject unavailable menu items
            MenuItem dbItem = availableItems.stream()
                    .filter(i -> i.getItemId() == cartItem.getItemId())
                    .findFirst()
                    .orElse(null);

            if (dbItem == null) {
                System.out.println("Order Rejected: Item ID " + cartItem.getItemId() + " is unavailable.");
                return null;
            }

            double itemSubtotal = dbItem.getPrice() * cartItem.getQuantity();
            foodSubtotal += itemSubtotal;
            orderItems.add(new OrderItem(dbItem.getItemId(), dbItem.getName(), cartItem.getQuantity(), dbItem.getPrice()));
        }

        // US2.3: Reject if subtotal is below restaurant's minimum order value
        if (foodSubtotal < restaurant.getMinOrderValue()) {
            System.out.println("Order Rejected: Food subtotal ($" + foodSubtotal
                    + ") is below minimum order value ($" + restaurant.getMinOrderValue() + ").");
            return null;
        }

        // US4.2: Calculate distance-based delivery fee
        double deliveryFee = DeliveryFeeCalculator.calculate(deliveryDistanceKm);

        // US4.3: Apply dynamic pricing strategy
        double pricedTotal = pricingStrategy.apply(foodSubtotal, deliveryFee);
        double beforeDiscount = foodSubtotal + deliveryFee;
        double discountAmount = Math.max(0, beforeDiscount - pricedTotal);
        double finalTotal = Math.round(pricedTotal * 100.0) / 100.0;

        // US5.1 & US5.2: Polymorphic Order Creation based on Order Type
        Order newOrder;
        String cleanType = (orderType != null) ? orderType.trim().toUpperCase() : "STANDARD";

        switch (cleanType) {
            case "EXPRESS":
                newOrder = new ExpressOrder(0, customer.getUserId(), restaurantId, "PENDING",
                        deliveryAddress.trim(), deliveryDistanceKm, foodSubtotal, deliveryFee, 
                        discountAmount, finalTotal, orderItems);
                break;
            case "SCHEDULED":
                newOrder = new ScheduledOrder(0, customer.getUserId(), restaurantId, "PENDING",
                        deliveryAddress.trim(), deliveryDistanceKm, foodSubtotal, deliveryFee, 
                        discountAmount, finalTotal, orderItems, scheduledTime);
                break;
            default:
                newOrder = new StandardOrder(0, customer.getUserId(), restaurantId, "PENDING",
                        deliveryAddress.trim(), deliveryDistanceKm, foodSubtotal, deliveryFee, 
                        discountAmount, finalTotal, orderItems);
                break;
        }

        // US5.2: Apply type-specific polymorphic rules (e.g. extra fee for Express, future time for Scheduled)
        newOrder.applyTypeRules();

        // Save order to database
        boolean success = orderRepository.saveOrder(newOrder);
        if (success) {
            return newOrder;
        } else {
            System.out.println("Order Rejected: Failed to save order to database.");
            return null;
        }
    }

    /**
     * Overloaded placeOrder method for backward compatibility (defaults to "STANDARD" order type and "ASAP").
     *
     * @param customer        the user placing the order
     * @param restaurantId    the ID of the restaurant
     * @param deliveryAddress the delivery text address
     * @param cart            the list of cart items
     * @return the created {@link Order} object if successful, or {@code null} if rejected
     */
    public Order placeOrder(User customer, int restaurantId, String deliveryAddress, List<CartItem> cart) {
        return placeOrder(customer, restaurantId, deliveryAddress, cart, "STANDARD", "ASAP");
    }

    /**
     * Cancels an existing pending order placed by a customer (US2.4).
     *
     * @param customerId the unique ID of the customer requesting cancellation
     * @param orderId    the unique ID of the order to cancel
     * @return a {@link CancellationResult} enum indicating the status of the cancellation request
     */
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

    /**
     * Checks if a specific customer currently has an active pending order.
     *
     * @param customerId the ID of the customer
     * @return {@code true} if the customer has at least one pending order; {@code false} otherwise
     */
    public boolean hasPendingOrder(int customerId) {
        return orderRepository.hasPendingOrder(customerId);
    }

    /**
     * Retrieves all pending orders currently stored in the system.
     *
     * @return a list of pending {@link Order} objects
     */
    public List<Order> getPendingOrders() {
        return orderRepository.findPendingOrders();
    }

    /**
     * Retrieves all active (non-delivered, non-cancelled) orders for manager view.
     *
     * @return a list of active {@link Order} objects for management
     */
    public List<Order> getActiveOrdersForManager() {
        return orderRepository.findActiveOrdersForManager();
    }

    /**
     * Retrieves all active orders belonging to a specific customer.
     *
     * @param customerId the ID of the customer
     * @return a list of active {@link Order} objects for the customer
     */
    public List<Order> getActiveOrdersByCustomer(int customerId) {
        return orderRepository.findActiveOrdersByCustomer(customerId);
    }

    /**
     * Data holder representing a customer's item selection choice from the console interface.
     *
     * @author Adham
     */
    public static class CartItem {
        private final int itemId;
        private final int quantity;

        /**
         * Constructs a CartItem instance.
         *
         * @param itemId   the unique identifier of the menu item
         * @param quantity the quantity of the menu item chosen
         */
        public CartItem(int itemId, int quantity) {
            this.itemId = itemId;
            this.quantity = quantity;
        }

        /**
         * Gets the item ID.
         *
         * @return the unique menu item ID
         */
        public int getItemId() { return itemId; }

        /**
         * Gets the selected item quantity.
         *
         * @return the item quantity
         */
        public int getQuantity() { return quantity; }
    }
}