package SWProject;

import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private final List<OrderObserver> observers = new ArrayList<>();

    public void addObserver(OrderObserver observer) {
        observers.add(observer);
    }

    public void notifyStatusChange(int orderId, String oldStatus, String newStatus, String customMessage) {
        for (OrderObserver observer : observers) {
            observer.onOrderStatusChanged(orderId, oldStatus, newStatus, customMessage);
        }
    }
}
