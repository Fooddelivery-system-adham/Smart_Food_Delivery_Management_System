package SWProject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Test double for Sprint 3 notification tests.
 * It records notifications instead of sending/printing them.
 */
public class MockNotificationService extends NotificationService {

    public static class NotificationRecord {
        private final int orderId;
        private final String oldStatus;
        private final String newStatus;
        private final String message;

        public NotificationRecord(int orderId, String oldStatus, String newStatus, String message) {
            this.orderId = orderId;
            this.oldStatus = oldStatus;
            this.newStatus = newStatus;
            this.message = message;
        }

        public int getOrderId() { return orderId; }
        public String getOldStatus() { return oldStatus; }
        public String getNewStatus() { return newStatus; }
        public String getMessage() { return message; }
    }

    private final List<NotificationRecord> notifications = new ArrayList<>();

    @Override
    public void notifyStatusChange(int orderId, String oldStatus, String newStatus, String customMessage) {
        notifications.add(new NotificationRecord(orderId, oldStatus, newStatus, customMessage));
    }

    public List<NotificationRecord> getNotifications() {
        return Collections.unmodifiableList(notifications);
    }

    public int getNotificationCount() {
        return notifications.size();
    }

    public void clear() {
        notifications.clear();
    }
}
