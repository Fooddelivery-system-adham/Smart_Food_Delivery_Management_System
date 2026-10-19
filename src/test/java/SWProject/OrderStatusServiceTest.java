package SWProject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderStatusServiceTest {

    private OrderStatusService orderStatusService;
    private MockNotificationService mockNotificationService;
    private FakeOrderRepository fakeOrderRepository;

    // Inner Fake Repository class to isolate unit tests from real PostgreSQL database
    private static class FakeOrderRepository extends OrderRepository {
        private String currentStatus = "PENDING";

        public void setStatus(String status) { this.currentStatus = status; }

        @Override
        public String findOrderStatusById(int orderId) {
            if (orderId == 999) return null; // Non-existent order
            return currentStatus;
        }

        @Override
        public boolean updateOrderStatusById(int orderId, String expectedOldStatus, String newStatus) {
            if (currentStatus.equalsIgnoreCase(expectedOldStatus)) {
                this.currentStatus = newStatus;
                return true;
            }
            return false;
        }
    }

    @BeforeEach
    void setUp() {
        fakeOrderRepository = new FakeOrderRepository();
        mockNotificationService = new MockNotificationService();
        orderStatusService = new OrderStatusService(fakeOrderRepository, mockNotificationService);
    }

    @Test
    @DisplayName("US4.1: Valid state transition PENDING -> CONFIRMED is accepted")
    void changeStatus_validTransition_updatesStatusAndNotifies() {
        fakeOrderRepository.setStatus("PENDING");

        OrderStatusService.StatusChangeResult result = orderStatusService.changeStatus(1, "CONFIRMED");

        assertEquals(OrderStatusService.StatusChangeResult.UPDATED, result);
        assertEquals(1, mockNotificationService.getNotificationCount());
        assertEquals("CONFIRMED", mockNotificationService.getNotifications().get(0).getNewStatus());
    }

    @Test
    @DisplayName("US4.1: Invalid state transition PENDING -> DELIVERED is rejected")
    void changeStatus_invalidTransition_returnsInvalidTransition() {
        fakeOrderRepository.setStatus("PENDING");

        OrderStatusService.StatusChangeResult result = orderStatusService.changeStatus(1, "DELIVERED");

        assertEquals(OrderStatusService.StatusChangeResult.INVALID_TRANSITION, result);
        assertEquals(0, mockNotificationService.getNotificationCount()); // No notification sent
    }

    @Test
    @DisplayName("US4.1: Delivered orders cannot be modified (Locked state)")
    void changeStatus_deliveredOrder_returnsDeliveredLocked() {
        fakeOrderRepository.setStatus("DELIVERED");

        OrderStatusService.StatusChangeResult result = orderStatusService.changeStatus(1, "PREPARING");

        assertEquals(OrderStatusService.StatusChangeResult.DELIVERED_LOCKED, result);
    }

    @Test
    @DisplayName("US4.1: Changing status to the same current status returns NO_CHANGE")
    void changeStatus_sameStatus_returnsNoChange() {
        fakeOrderRepository.setStatus("PREPARING");

        OrderStatusService.StatusChangeResult result = orderStatusService.changeStatus(1, "PREPARING");
        assertEquals(OrderStatusService.StatusChangeResult.NO_CHANGE, result);
    }

    @Test
    @DisplayName("US4.1: Non-existing order ID returns NOT_FOUND")
    void changeStatus_nonExistingOrder_returnsNotFound() {
        OrderStatusService.StatusChangeResult result = orderStatusService.changeStatus(999, "CONFIRMED");

        assertEquals(OrderStatusService.StatusChangeResult.NOT_FOUND, result);
    }
}