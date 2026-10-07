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
        if (strategy != null) {
            this.assignmentStrategy = strategy;
        }
    }

    // US3.1 & US3.2: Assign an available driver using the selected Strategy.
    public boolean assignDriverToOrder(int orderId, int restaurantId) {
        if (orderId <= 0 || restaurantId <= 0) {
            System.out.println("Driver Assignment Rejected: Invalid order or restaurant ID.");
            return false;
        }

        Restaurant restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant == null) {
            System.out.println("Driver Assignment Rejected: Restaurant does not exist.");
            return false;
        }

        List<Driver> availableDrivers = driverRepository.findAvailableDrivers();
        if (availableDrivers.isEmpty()) {
            System.out.println("Driver Assignment Rejected: No eligible drivers are currently available.");
            return false;
        }

        Driver selectedDriver = assignmentStrategy.selectDriver(
                availableDrivers,
                restaurant.getLatitude(),
                restaurant.getLongitude());

        if (selectedDriver == null) {
            System.out.println("Driver Assignment Rejected: Strategy could not find an eligible driver.");
            return false;
        }

        // One database transaction makes these two acceptance criteria atomic:
        // 1) driver becomes BUSY, 2) order records the driver and becomes CONFIRMED.
        boolean assigned = orderRepository.assignAvailableDriverToOrder(
                orderId, restaurantId, selectedDriver.getDriverId());

        if (!assigned) {
            System.out.println("Driver Assignment Rejected: Order is not PENDING, already has a driver, "
                    + "restaurant does not match, or the selected driver is no longer AVAILABLE.");
            return false;
        }

        selectedDriver.setStatus("BUSY");

        notificationService.notifyStatusChange(
                orderId,
                "PENDING",
                "CONFIRMED",
                "Driver " + selectedDriver.getName() + " (" + selectedDriver.getDriverType()
                        + ") assigned to your order."
        );

        return true;
    }
}
