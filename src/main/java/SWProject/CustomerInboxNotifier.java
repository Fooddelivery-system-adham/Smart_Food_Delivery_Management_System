package SWProject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerInboxNotifier implements OrderObserver {
    private final Map<Integer, List<String>> inbox = new HashMap<>();

    @Override
    public void onOrderStatusChanged(int orderId, String oldStatus, String newStatus, String message) {
        inbox.putIfAbsent(orderId, new ArrayList<>());
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String notification = "[" + time + "] " + oldStatus + " ➔ " + newStatus + " | " + message;
        inbox.get(orderId).add(notification);
    }

    public List<String> getNotificationsForOrder(int orderId) {
        return inbox.getOrDefault(orderId, new ArrayList<>());
    }
}