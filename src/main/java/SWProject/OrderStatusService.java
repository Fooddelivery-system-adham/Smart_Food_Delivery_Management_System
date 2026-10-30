package SWProject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Service handling lifecycle status transitions for orders and sending status notifications.
 *
 * @author Smart Food Delivery Team
 */
public class OrderStatusService {

    public enum StatusChangeResult {
        UPDATED,
        NOT_FOUND,
        INVALID_STATUS,
        INVALID_TRANSITION,
        DELIVERED_LOCKED,
        NO_CHANGE,
        DATABASE_ERROR
    }

    private final OrderRepository orderRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final Map<String, Set<String>> allowedTransitions = new HashMap<>();

    /**
     * Standard constructor using default EmailService instance.
     */
    public OrderStatusService(OrderRepository orderRepository, NotificationService notificationService) {
        this(orderRepository, notificationService, new EmailService());
    }

    /**
     * Overloaded constructor for dependency injection (useful for Mockito testing).
     */
    public OrderStatusService(OrderRepository orderRepository, NotificationService notificationService, EmailService emailService) {
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
        this.emailService = (emailService != null) ? emailService : new EmailService();
        initTransitions();
    }

    // US4.1: define valid order lifecycle transitions
    private void initTransitions() {
        allowedTransitions.put("PENDING", setOf("CONFIRMED", "CANCELLED"));
        allowedTransitions.put("CONFIRMED", setOf("PREPARING"));
        allowedTransitions.put("PREPARING", setOf("READY"));
        allowedTransitions.put("READY", setOf("OUT_FOR_DELIVERY"));
        allowedTransitions.put("OUT_FOR_DELIVERY", setOf("DELIVERED"));
        allowedTransitions.put("DELIVERED", setOf()); // locked
        allowedTransitions.put("CANCELLED", setOf()); // locked
    }

    private Set<String> setOf(String... values) {
        Set<String> set = new HashSet<>();
        for (String v : values) {
            set.add(v);
        }
        return set;
    }

    public StatusChangeResult changeStatus(int orderId, String newStatus) {
        if (orderId <= 0 || newStatus == null) {
            return StatusChangeResult.INVALID_STATUS;
        }

        String cleanStatus = newStatus.trim().toUpperCase();
        String oldStatus = orderRepository.findOrderStatusById(orderId);

        if (oldStatus == null) {
            return StatusChangeResult.NOT_FOUND;
        }

        if (oldStatus.equalsIgnoreCase(cleanStatus)) {
            return StatusChangeResult.NO_CHANGE;
        }

        // US4.1: delivered orders cannot be modified
        if ("DELIVERED".equalsIgnoreCase(oldStatus) || "CANCELLED".equalsIgnoreCase(oldStatus)) {
            return StatusChangeResult.DELIVERED_LOCKED;
        }

        // US4.1: reject invalid transitions
        Set<String> nextAllowed = allowedTransitions.getOrDefault(oldStatus.toUpperCase(), new HashSet<>());
        if (!nextAllowed.contains(cleanStatus)) {
            return StatusChangeResult.INVALID_TRANSITION;
        }

        boolean updated = orderRepository.updateOrderStatusById(orderId, oldStatus, cleanStatus);
        if (!updated) {
            return StatusChangeResult.DATABASE_ERROR;
        }

        // 📧 1. إرسال إيميل الاستلام النهائي إذا أصبحت الحالة DELIVERED
        if ("DELIVERED".equalsIgnoreCase(cleanStatus)) {
            String customerEmail = orderRepository.findCustomerEmailByOrderId(orderId);
            if (customerEmail != null && !customerEmail.trim().isEmpty()) {
                emailService.sendOrderCompletedEmail(customerEmail, orderId);
            }
        } // ⬅️ أصلحنا القوس المفقود هنا

        // US3.3/US3.4: notify on every successful status change
        notificationService.notifyStatusChange(
                orderId,
                oldStatus,
                cleanStatus,
                buildMessage(cleanStatus)
        );

        return StatusChangeResult.UPDATED;
    }

    private String buildMessage(String newStatus) {
        switch (newStatus) {
            case "CONFIRMED":
                return "Your order has been confirmed.";
            case "PREPARING":
                return "Your order is now being prepared.";
            case "READY":
                return "Your order is ready for delivery.";
            case "OUT_FOR_DELIVERY":
                return "Your order is out for delivery.";
            case "DELIVERED":
                return "Your order has been delivered.";
            case "CANCELLED":
                return "Your order has been cancelled.";
            default:
                return "Your order status has changed to " + newStatus + ".";
        }
    }
}