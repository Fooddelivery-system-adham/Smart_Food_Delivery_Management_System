package SWProject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    private OrderService orderService;
    private FakeOrderRepository fakeOrderRepository;
    private FakeMenuItemRepository fakeMenuItemRepository;
    private FakeRestaurantRepository fakeRestaurantRepository;

    private static class FakeOrderRepository extends OrderRepository {
        @Override
        public boolean saveOrder(Order order) {
            order.setOrderId(100);
            return true;
        }

        @Override
        public boolean hasPendingOrder(int customerId) {
            return customerId == 1;
        }

        @Override
        public String findOrderStatus(int orderId, int customerId) {
            if (orderId == 1) return "PENDING";
            if (orderId == 2) return "PREPARING";
            return null;
        }

        @Override
        public boolean updateOrderStatus(int orderId, int customerId, String newStatus) {
            return true;
        }
    }

    private static class FakeMenuItemRepository extends MenuItemRepository {
        @Override
        public List<MenuItem> findAvailableByRestaurantId(int restaurantId) {
            List<MenuItem> items = new ArrayList<>();
            items.add(new MenuItem(1, restaurantId, "Burger", 25.0, "AVAILABLE"));
            return items;
        }
    }

    private static class FakeRestaurantRepository extends RestaurantRepository {
        @Override
        public Restaurant findById(int restaurantId) {
            return new Restaurant(restaurantId, "Burger King", "OPEN", 15.0, 10.0);
        }
    }

    @BeforeEach
    void setUp() {
        fakeOrderRepository = new FakeOrderRepository();
        fakeMenuItemRepository = new FakeMenuItemRepository();
        fakeRestaurantRepository = new FakeRestaurantRepository();
        orderService = new OrderService(fakeOrderRepository, fakeMenuItemRepository, fakeRestaurantRepository);
    }

    @Test
    @DisplayName("US2.1 & US2.3: Valid order is placed successfully")
    void placeOrder_validInputs_returnsNewOrder() {
        // Arrange
        List<OrderService.CartItem> cart = new ArrayList<>();
        cart.add(new OrderService.CartItem(1, 2));

        // Act
        Order order = orderService.placeOrder(1, 1, "Nablus Main St", 5.0, cart);

        // Assert
        assertNotNull(order);
        assertEquals(50.0, order.getSubtotal(), 0.001);
        assertEquals("PENDING", order.getStatus());
    }

    @Test
    @DisplayName("US2.2: Reject order with empty cart")
    void placeOrder_emptyCart_returnsNull() {
        // Act & Assert
        assertNull(orderService.placeOrder(1, 1, "Nablus Main St", 5.0, Collections.emptyList()));
    }

    @Test
    @DisplayName("US2.3: Reject order with invalid short address")
    void placeOrder_invalidAddress_returnsNull() {
        // Arrange
        List<OrderService.CartItem> cart = Collections.singletonList(new OrderService.CartItem(1, 1));

        // Act & Assert
        assertNull(orderService.placeOrder(1, 1, "abc", 5.0, cart));
    }

    @Test
    @DisplayName("US2.4: Cancel pending order returns CANCELLED")
    void cancelOrder_pendingOrder_returnsCancelled() {
        // Act
        OrderService.CancellationResult result = orderService.cancelOrder(1, 1);

        // Assert
        assertEquals(OrderService.CancellationResult.CANCELLED, result);
    }

    @Test
    @DisplayName("US2.4: Cancel non-pending order returns NOT_ALLOWED")
    void cancelOrder_preparingOrder_returnsNotAllowed() {
        // Act
        OrderService.CancellationResult result = orderService.cancelOrder(1, 2);

        // Assert
        assertEquals(OrderService.CancellationResult.NOT_ALLOWED, result);
    }
}