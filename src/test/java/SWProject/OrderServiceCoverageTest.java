package SWProject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceCoverageTest {

    private OrderRepository mockOrderRepo;
    private MenuItemRepository mockMenuRepo;
    private RestaurantRepository mockRestRepo;
    private EmailService mockEmailService;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        mockOrderRepo = mock(OrderRepository.class);
        mockMenuRepo = mock(MenuItemRepository.class);
        mockRestRepo = mock(RestaurantRepository.class);
        mockEmailService = mock(EmailService.class);

        orderService = new OrderService(mockOrderRepo, mockMenuRepo, mockRestRepo, mockEmailService);
    }

    @Test
    @DisplayName("Coverage: Test placeOrder with invalid item quantity <= 0")
    void testInvalidItemQuantity() {
        User customer = new User(1, "ahmad", "ahmad@test.com", "123", "CUSTOMER", 31.78, 35.22);
        Restaurant restaurant = new Restaurant(10, "Burger King", "OPEN", 10.0, 10.0, 31.78, 35.22);

        when(mockRestRepo.findById(10)).thenReturn(restaurant);
        List<OrderService.CartItem> cart = List.of(new OrderService.CartItem(1, 0)); // Qty = 0

        assertNull(orderService.placeOrder(customer, 10, "Ramallah St", cart));
    }

    @Test
    @DisplayName("Coverage: Test placeOrder with unavailable menu item")
    void testUnavailableItem() {
        User customer = new User(1, "ahmad", "ahmad@test.com", "123", "CUSTOMER", 31.78, 35.22);
        Restaurant restaurant = new Restaurant(10, "Burger King", "OPEN", 10.0, 10.0, 31.78, 35.22);

        when(mockRestRepo.findById(10)).thenReturn(restaurant);
        when(mockMenuRepo.findAvailableByRestaurantId(10)).thenReturn(new ArrayList<>()); // No items available

        List<OrderService.CartItem> cart = List.of(new OrderService.CartItem(99, 2));

        assertNull(orderService.placeOrder(customer, 10, "Ramallah St", cart));
    }

    @Test
    @DisplayName("Coverage: Test all cancelOrder outcomes")
    void testCancelOrderResults() {
        // Invalid order ID
        assertEquals(OrderService.CancellationResult.INVALID_ORDER_ID, orderService.cancelOrder(1, 0));

        // Order Not Found
        when(mockOrderRepo.findOrderStatus(10, 1)).thenReturn(null);
        assertEquals(OrderService.CancellationResult.NOT_FOUND, orderService.cancelOrder(1, 10));

        // Already Cancelled
        when(mockOrderRepo.findOrderStatus(11, 1)).thenReturn("CANCELLED");
        assertEquals(OrderService.CancellationResult.ALREADY_CANCELLED, orderService.cancelOrder(1, 11));

        // Not Allowed (e.g. PREPARING)
        when(mockOrderRepo.findOrderStatus(12, 1)).thenReturn("PREPARING");
        assertEquals(OrderService.CancellationResult.NOT_ALLOWED, orderService.cancelOrder(1, 12));

        // Successful Cancel
        when(mockOrderRepo.findOrderStatus(13, 1)).thenReturn("PENDING");
        when(mockOrderRepo.updateOrderStatus(13, 1, "CANCELLED")).thenReturn(true);
        assertEquals(OrderService.CancellationResult.CANCELLED, orderService.cancelOrder(1, 13));

        // Database Error
        when(mockOrderRepo.findOrderStatus(14, 1)).thenReturn("PENDING");
        when(mockOrderRepo.updateOrderStatus(14, 1, "CANCELLED")).thenReturn(false);
        assertEquals(OrderService.CancellationResult.DATABASE_ERROR, orderService.cancelOrder(1, 14));
    }

    @Test
    @DisplayName("Coverage: Test getter helper methods")
    void testHelperMethods() {
        when(mockOrderRepo.hasPendingOrder(1)).thenReturn(true);
        assertTrue(orderService.hasPendingOrder(1));

        when(mockOrderRepo.findPendingOrders()).thenReturn(new ArrayList<>());
        assertNotNull(orderService.getPendingOrders());

        when(mockOrderRepo.findActiveOrdersForManager()).thenReturn(new ArrayList<>());
        assertNotNull(orderService.getActiveOrdersForManager());

        when(mockOrderRepo.findActiveOrdersByCustomer(1)).thenReturn(new ArrayList<>());
        assertNotNull(orderService.getActiveOrdersByCustomer(1));
    }

    @Test
    @DisplayName("Coverage: Test sendOrderInvoiceEmail null checks")
    void testSendOrderInvoiceEmailNulls() {
        assertFalse(orderService.sendOrderInvoiceEmail(null, null));
        
        User userNoEmail = new User(1, "test", "", "123", "CUSTOMER", 0, 0);
        assertFalse(orderService.sendOrderInvoiceEmail(userNoEmail, null));
    }
}