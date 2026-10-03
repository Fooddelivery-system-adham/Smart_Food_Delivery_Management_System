package SWProject;

import java.util.List;

public class DeliveryService {

    private final DriverRepository driverRepository;
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final NotificationService notificationService;
    private DriverAssignmentStrategy assignmentStrategy;

    public DeliveryService(DriverRepository driverRepository, OrderRepository orderRepository,
                           RestaurantRepository restaurantRepository, NotificationService notificationService,
                           DriverAssignmentStrategy defaultStrategy) {
        this.driverRepository = driverRepository;
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.notificationService = notificationService;
        this.assignmentStrategy = defaultStrategy;
    }

    public void setStrategy(DriverAssignmentStrategy strategy) {
        this.assignmentStrategy = strategy;
    }

    // US3.1 & US3.2: Assign driver using Strategy Pattern
    public boolean assignDriverToOrder(int orderId, int restaurantId) {
        // 1. Get ONLY available drivers (Acceptance US3.1)
        List<Driver> availableDrivers = driverRepository.findAvailableDrivers();
        if (availableDrivers.isEmpty()) {
            System.out.println("Driver Assignment Rejected: No drivers are currently available.");
            return false;
        }

        // Fetch restaurant location
        Restaurant restaurant = restaurantRepository.findById(restaurantId);
        double restLat = (restaurant != null) ? 32.221111 : 0.0;
        double restLng = (restaurant != null) ? 35.254444 : 0.0;

        // 2. Select driver using current strategy (US3.2)
        Driver selectedDriver = assignmentStrategy.selectDriver(availableDrivers, restLat, restLng);
        if (selectedDriver == null) {
            System.out.println("Driver Assignment Rejected: Strategy could not find an eligible driver.");
            return false;
        }

        // 3. Record driver ID in order and update driver status to BUSY (US3.1)
        boolean orderUpdated = orderRepository.assignDriverToOrder(orderId, selectedDriver.getDriverId());
        if (orderUpdated) {
            driverRepository.updateDriverStatus(selectedDriver.getDriverId(), "BUSY");
            selectedDriver.setStatus("BUSY");

            // 4. Trigger Notification via Observer Pattern (US3.3)
            notificationService.notifyStatusChange(
                orderId, "PENDING", "CONFIRMED",
                "Driver " + selectedDriver.getName() + " (" + selectedDriver.getDriverType() + ") assigned to your order!"
            );
            return true;
        }

        return false;
    }
}