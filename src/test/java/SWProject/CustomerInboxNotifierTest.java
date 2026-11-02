package SWProject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerInboxNotifierTest {

    @Test
    @DisplayName("Test CustomerInboxNotifier records status notifications")
    void testInboxNotificationTracking() {
        CustomerInboxNotifier notifier = new CustomerInboxNotifier();

        StandardOrder order = new StandardOrder(
                50,
                10,
                100,
                "PENDING",
                "Street 1",
                2.0,
                15.0,
                2.0,
                0.0,
                17.0,
                new ArrayList<>()
        );

        notifier.onOrderStatusChanged(order.getOrderId(), "STANDARD", "PENDING", "PREPARING");
        notifier.onOrderStatusChanged(order.getOrderId(), "STANDARD", "PREPARING", "DELIVERED");

        List<String> notifications = notifier.getNotificationsForOrder(50);
        
        assertNotNull(notifications, "Notifications list should not be null");
        assertEquals(2, notifications.size(), "Should have recorded 2 notifications");
        assertTrue(notifications.get(0).contains("PREPARING"));
        assertTrue(notifications.get(1).contains("DELIVERED"));
    }
}