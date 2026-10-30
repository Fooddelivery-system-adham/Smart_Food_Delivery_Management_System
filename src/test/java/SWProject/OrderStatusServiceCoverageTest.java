package SWProject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderStatusServiceCoverageTest {

    private OrderRepository mockOrderRepo;
    private NotificationService mockNotifService;
    private EmailService mockEmailService;
    private OrderStatusService orderStatusService;

    @BeforeEach
    void setUp() {
        mockOrderRepo = mock(OrderRepository.class);
        mockNotifService = mock(NotificationService.class);
        mockEmailService = mock(EmailService.class);

        orderStatusService = new OrderStatusService(mockOrderRepo, mockNotifService, mockEmailService);
    }

    @Test
    @DisplayName("Coverage: Test invalid order ID or null status")
    void testInvalidStatusInputs() {
        assertEquals(OrderStatusService.StatusChangeResult.INVALID_STATUS, orderStatusService.changeStatus(0, "CONFIRMED"));
        assertEquals(OrderStatusService.StatusChangeResult.INVALID_STATUS, orderStatusService.changeStatus(1, null));
    }

    @Test
    @DisplayName("Coverage: Test order not found")
    void testOrderNotFound() {
        when(mockOrderRepo.findOrderStatusById(99)).thenReturn(null);
        assertEquals(OrderStatusService.StatusChangeResult.NOT_FOUND, orderStatusService.changeStatus(99, "CONFIRMED"));
    }

    @Test
    @DisplayName("Coverage: Test no change when same status provided")
    void testNoChange() {
        when(mockOrderRepo.findOrderStatusById(1)).thenReturn("PENDING");
        assertEquals(OrderStatusService.StatusChangeResult.NO_CHANGE, orderStatusService.changeStatus(1, "PENDING"));
    }

    @Test
    @DisplayName("Coverage: Test delivered and cancelled orders are locked")
    void testLockedStatuses() {
        when(mockOrderRepo.findOrderStatusById(1)).thenReturn("DELIVERED");
        assertEquals(OrderStatusService.StatusChangeResult.DELIVERED_LOCKED, orderStatusService.changeStatus(1, "CONFIRMED"));

        when(mockOrderRepo.findOrderStatusById(2)).thenReturn("CANCELLED");
        assertEquals(OrderStatusService.StatusChangeResult.DELIVERED_LOCKED, orderStatusService.changeStatus(2, "CONFIRMED"));
    }

    @Test
    @DisplayName("Coverage: Test invalid lifecycle transitions (e.g. PENDING -> DELIVERED)")
    void testInvalidTransition() {
        when(mockOrderRepo.findOrderStatusById(1)).thenReturn("PENDING");
        assertEquals(OrderStatusService.StatusChangeResult.INVALID_TRANSITION, orderStatusService.changeStatus(1, "DELIVERED"));
    }

    @Test
    @DisplayName("Coverage: Test database update error")
    void testDatabaseError() {
        when(mockOrderRepo.findOrderStatusById(1)).thenReturn("PENDING");
        when(mockOrderRepo.updateOrderStatusById(1, "PENDING", "CONFIRMED")).thenReturn(false);

        assertEquals(OrderStatusService.StatusChangeResult.DATABASE_ERROR, orderStatusService.changeStatus(1, "CONFIRMED"));
    }

    @Test
    @DisplayName("Coverage: Successful transition to DELIVERED triggers completion email")
    void testDeliveredTriggersEmail() {
        when(mockOrderRepo.findOrderStatusById(1)).thenReturn("OUT_FOR_DELIVERY");
        when(mockOrderRepo.updateOrderStatusById(1, "OUT_FOR_DELIVERY", "DELIVERED")).thenReturn(true);
        when(mockOrderRepo.findCustomerEmailByOrderId(1)).thenReturn("customer@test.com");

        assertEquals(OrderStatusService.StatusChangeResult.UPDATED, orderStatusService.changeStatus(1, "DELIVERED"));
        verify(mockEmailService, times(1)).sendOrderCompletedEmail("customer@test.com", 1);
    }
}