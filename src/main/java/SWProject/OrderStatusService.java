package SWProject;

public class OrderStatusService {

    public enum StatusChangeResult {
        UPDATED,
        NOT_FOUND,
        INVALID_STATUS,
        NO_CHANGE,
        DATABASE_ERROR
    }

    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    public OrderStatusService(OrderRepository orderRepository, NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
    }

    // US3.3 & US3.4: every successful order-status change generates a notification.
    public StatusChangeResult changeStatus(int orderId, String newStatus) {
        if (orderId <= 0 || newStatus == null) {
            return StatusChangeResult.INVALID_STATUS;
        }

        String cleanStatus = newStatus.trim().toUpperCase();
        if (!isSupportedStatus(cleanStatus)) {
            return StatusChangeResult.INVALID_STATUS;
        }

        String oldStatus = orderRepository.findOrderStatusById(orderId);
        if (oldStatus == null) {
            return StatusChangeResult.NOT_FOUND;
        }

        if (oldStatus.equalsIgnoreCase(cleanStatus)) {
            return StatusChangeResult.NO_CHANGE;
        }

        boolean updated = orderRepository.updateOrderStatusById(orderId, oldStatus, cleanStatus);
        if (!updated) {
            return StatusChangeResult.DATABASE_ERROR;
        }

        notificationService.notifyStatusChange(
                orderId,
                oldStatus,
                cleanStatus,
                buildMessage(cleanStatus)
        );

        return StatusChangeResult.UPDATED;
    }

    private boolean isSupportedStatus(String status) {
        return "PREPARING".equals(status)
                || "READY".equals(status)
                || "OUT_FOR_DELIVERY".equals(status)
                || "DELIVERED".equals(status);
    }

    private String buildMessage(String newStatus) {
        switch (newStatus) {
            case "PREPARING":
                return "Your order is now being prepared.";
            case "READY":
                return "Your order is ready for delivery.";
            case "OUT_FOR_DELIVERY":
                return "Your order is out for delivery.";
            case "DELIVERED":
                return "Your order has been delivered.";
            default:
                return "Your order status has changed to " + newStatus + ".";
        }
    }
}
