package SWProject;

public class CustomerConsoleNotifier implements OrderObserver {

    @Override
    public void onOrderStatusChanged(int orderId, String oldStatus, String newStatus, String message) {
        System.out.println("NOTIFICATION [Order #" + orderId + "]: Status changed from "
                + oldStatus + " -> " + newStatus + " | " + message);
    }
}
