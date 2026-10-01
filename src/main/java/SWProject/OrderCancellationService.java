package SWProject;

/**
 * Business rules for US2.4 - Cancel an Order.
 */
public class OrderCancellationService {

    public enum CancellationResult {
        CANCELLED,
        NOT_FOUND,
        ALREADY_CANCELLED,
        NOT_ALLOWED,
        INVALID_ORDER_ID,
        FAILED
    }

    private final OrderCancellationRepository repository;

    public OrderCancellationService(OrderCancellationRepository repository) {
        this.repository = repository;
    }

    public CancellationResult cancelOrder(int customerId, int orderId) {
        if (orderId <= 0) {
            return CancellationResult.INVALID_ORDER_ID;
        }

        String currentStatus = repository.findStatusForCustomer(customerId, orderId);

        if (currentStatus == null) {
            return CancellationResult.NOT_FOUND;
        }

        if ("CANCELLED".equalsIgnoreCase(currentStatus)) {
            return CancellationResult.ALREADY_CANCELLED;
        }

        // Sprint 2 acceptance: pending orders can be cancelled.
        // Any order that has moved beyond PENDING is protected from cancellation.
        if (!"PENDING".equalsIgnoreCase(currentStatus)) {
            return CancellationResult.NOT_ALLOWED;
        }

        return repository.cancelPendingOrder(customerId, orderId)
                ? CancellationResult.CANCELLED
                : CancellationResult.FAILED;
    }
}
