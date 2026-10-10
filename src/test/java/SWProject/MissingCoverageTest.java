package SWProject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MissingCoverageTest {

    @Test
    @DisplayName("Coverage: NotificationService & CustomerConsoleNotifier")
    void testNotifications() {
        // This covers NotificationService loop and CustomerConsoleNotifier print statement
        NotificationService service = new NotificationService();
        CustomerConsoleNotifier consoleNotifier = new CustomerConsoleNotifier();
        service.addObserver(consoleNotifier);
        
        // Ensure no exceptions are thrown when printing to console
        assertDoesNotThrow(() -> 
            service.notifyStatusChange(101, "PENDING", "CONFIRMED", "Test Notification")
        );
    }

    @Test
    @DisplayName("Coverage: MockNotificationService full coverage")
    void testMockNotificationService() {
        MockNotificationService mock = new MockNotificationService();
        mock.notifyStatusChange(50, "READY", "OUT_FOR_DELIVERY", "Driver is on the way");
        
        assertEquals(1, mock.getNotificationCount());
        
        MockNotificationService.NotificationRecord record = mock.getNotifications().get(0);
        assertEquals(50, record.getOrderId());
        assertEquals("READY", record.getOldStatus());
        assertEquals("OUT_FOR_DELIVERY", record.getNewStatus());
        assertEquals("Driver is on the way", record.getMessage());
        
        mock.clear();
        assertEquals(0, mock.getNotificationCount());
    }

    @Test
    @DisplayName("Coverage: MenuItem missing getters and toString")
    void testMenuItemExtra() {
        MenuItem item = new MenuItem(99, 5, "Pizza", 15.0, "UNAVAILABLE");
        assertFalse(item.isAvailable());
        assertTrue(item.toString().contains("Pizza"));
        assertTrue(item.toString().contains("15.0"));
    }

    @Test
    @DisplayName("Coverage: OrderService UX helper methods")
    void testOrderServiceHelpers() {
        // Fake repo specifically for testing the helper lists
        OrderRepository fakeRepo = new OrderRepository() {
            @Override public boolean hasPendingOrder(int customerId) { return true; }
            @Override public java.util.List<Order> findPendingOrders() { return new ArrayList<>(); }
            @Override public java.util.List<Order> findActiveOrdersForManager() { return new ArrayList<>(); }
            @Override public java.util.List<Order> findActiveOrdersByCustomer(int customerId) { return new ArrayList<>(); }
        };
        
        OrderService service = new OrderService(fakeRepo, null, null);
        
        assertTrue(service.hasPendingOrder(1));
        assertNotNull(service.getPendingOrders());
        assertNotNull(service.getActiveOrdersForManager());
        assertNotNull(service.getActiveOrdersByCustomer(1));
    }
    
    @Test
    @DisplayName("Coverage: DeliveryFeeCalculator negative distance")
    void testDeliveryFeeCalculatorNegative() {
        // Hits the `if (distanceKm <= 0)` line
        assertEquals(0.0, DeliveryFeeCalculator.calculate(-10.0));
        assertEquals(0.0, DeliveryFeeCalculator.calculate(0.0));
    }
}