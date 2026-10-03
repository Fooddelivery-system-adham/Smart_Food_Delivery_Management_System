package SWProject;

public interface OrderObserver {
    void onOrderStatusChanged(int orderId, String oldStatus, String newStatus, String message);
}
