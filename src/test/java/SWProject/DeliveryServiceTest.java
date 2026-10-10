package SWProject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryServiceTest {

    private DeliveryService deliveryService;
    private FakeDriverRepository fakeDriverRepository;
    private FakeOrderRepository fakeOrderRepository;
    private FakeRestaurantRepository fakeRestaurantRepository;
    private MockNotificationService mockNotificationService;

    // Fake Driver Repository
    private static class FakeDriverRepository extends DriverRepository {
        private boolean driversAvailable = true;

        public void setDriversAvailable(boolean available) { 
            this.driversAvailable = available; 
        }

        @Override
        public List<Driver> findAvailableDrivers() {
            if (!driversAvailable) return new ArrayList<>();
            List<Driver> list = new ArrayList<>();
            list.add(new Driver(1, "Sami", "MOTORCYCLE", "AVAILABLE", 32.225000, 35.258000, 45.0));
            return list;
        }

        @Override
        public boolean updateDriverStatus(int driverId, String newStatus) {
            return true;
        }
    }

    // Fake Order Repository - Submitting the correct assignment method
    private static class FakeOrderRepository extends OrderRepository {
        private boolean assignmentSuccess = true;

        public void setAssignmentSuccess(boolean success) {
            this.assignmentSuccess = success;
        }

        @Override
        public boolean assignAvailableDriverToOrder(int orderId, int restaurantId, int driverId) {
            return assignmentSuccess && orderId == 1 && restaurantId == 1;
        }
    }

    // Fake Restaurant Repository
    private static class FakeRestaurantRepository extends RestaurantRepository {
        private boolean restaurantExists = true;

        public void setRestaurantExists(boolean exists) {
            this.restaurantExists = exists;
        }

        @Override
        public Restaurant findById(int restaurantId) {
            if (!restaurantExists) return null;
            return new Restaurant(restaurantId, "Burger King", "OPEN", 15.0, 10.0);
        }
    }

    // Custom Null Strategy for testing edge case
    private static class NullDriverStrategy implements DriverAssignmentStrategy {
        @Override
        public Driver selectDriver(List<Driver> drivers, double restaurantLat, double restaurantLon) {
            return null; // Simulates no strategy match
        }
    }

    @BeforeEach
    void setUp() {
        fakeDriverRepository = new FakeDriverRepository();
        fakeOrderRepository = new FakeOrderRepository();
        fakeRestaurantRepository = new FakeRestaurantRepository();
        mockNotificationService = new MockNotificationService();

        deliveryService = new DeliveryService(
                fakeDriverRepository, fakeOrderRepository, fakeRestaurantRepository,
                mockNotificationService, new NearestDriverStrategy()
        );
    }

    @Test
    @DisplayName("US3.1: Assign driver to order succeeds when driver is available")
    void assignDriverToOrder_success() {
        assertTrue(deliveryService.assignDriverToOrder(1, 1));
        assertEquals(1, mockNotificationService.getNotificationCount());
    }

    @Test
    @DisplayName("US3.1: Assign driver fails when no drivers available")
    void assignDriverToOrder_noDriversAvailable_returnsFalse() {
        fakeDriverRepository.setDriversAvailable(false);
        assertFalse(deliveryService.assignDriverToOrder(1, 1));
    }

    @Test
    @DisplayName("US3.2: Switch assignment strategy dynamically")
    void setStrategy_updatesStrategy() {
        deliveryService.setStrategy(new FastestDriverStrategy());
        assertTrue(deliveryService.assignDriverToOrder(1, 1));
    }

    @Test
    @DisplayName("Validation Error: Invalid Order ID or Restaurant ID returns false")
    void assignDriverToOrder_invalidIds_returnsFalse() {
        assertFalse(deliveryService.assignDriverToOrder(0, 1));
        assertFalse(deliveryService.assignDriverToOrder(1, -1));
        assertFalse(deliveryService.assignDriverToOrder(-1, 0));
    }

    @Test
    @DisplayName("Validation Error: Non-existent restaurant returns false")
    void assignDriverToOrder_restaurantNotFound_returnsFalse() {
        fakeRestaurantRepository.setRestaurantExists(false);
        assertFalse(deliveryService.assignDriverToOrder(1, 1));
    }

    @Test
    @DisplayName("Strategy Error: When Strategy returns null driver returns false")
    void assignDriverToOrder_strategyReturnsNull_returnsFalse() {
        deliveryService.setStrategy(new NullDriverStrategy());
        assertFalse(deliveryService.assignDriverToOrder(1, 1));
    }

    @Test
    @DisplayName("Database Error: When DB assignment fails returns false")
    void assignDriverToOrder_dbAssignmentFails_returnsFalse() {
        fakeOrderRepository.setAssignmentSuccess(false);
        assertFalse(deliveryService.assignDriverToOrder(1, 1));
    }

    @Test
    @DisplayName("Coverage test: Setting strategy to null retains existing strategy")
    void setStrategy_nullStrategy_doesNotOverwrite() {
        deliveryService.setStrategy(null);
        // Strategy remains NearestDriverStrategy and assignment still succeeds
        assertTrue(deliveryService.assignDriverToOrder(1, 1));
    }
}