package SWProject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DriverAssignmentStrategyTest {

    private List<Driver> availableDrivers;
    private double restaurantLat;
    private double restaurantLng;

    @BeforeEach
    void setUp() {
        // Arrange common drivers and restaurant coordinates
        restaurantLat = 32.221111;
        restaurantLng = 35.254444;

        availableDrivers = new ArrayList<>();
        // Driver 1: Sami (Motorcycle) - 3 km away, speed 50 km/h -> ~3.6 minutes
        availableDrivers.add(new Driver(1, "Sami", "MOTORCYCLE", "AVAILABLE", 32.245000, 35.254444, 50.0));
        
        // Driver 2: Ahmad (Bicycle) - 1.5 km away, speed 10 km/h -> ~6.0 minutes
        availableDrivers.add(new Driver(2, "Ahmad", "BICYCLE", "AVAILABLE", 32.235000, 35.254444, 10.0));
    }

    @Test
    @DisplayName("US3.2: NearestDriverStrategy selects the geographically closest driver")
    void nearestDriverStrategy_selectsClosestDriver() {
        // Arrange
        DriverAssignmentStrategy strategy = new NearestDriverStrategy();

        // Act
        Driver selectedDriver = strategy.selectDriver(availableDrivers, restaurantLat, restaurantLng);

        // Assert
        assertEquals(2, selectedDriver.getDriverId()); // Ahmad is geographically closer
    }

    @Test
    @DisplayName("US3.2: FastestDriverStrategy selects driver with lowest estimated arrival time")
    void fastestDriverStrategy_selectsFastestDriver() {
        // Arrange
        DriverAssignmentStrategy strategy = new FastestDriverStrategy();

        // Act
        Driver selectedDriver = strategy.selectDriver(availableDrivers, restaurantLat, restaurantLng);

        // Assert
        assertEquals(1, selectedDriver.getDriverId()); // Sami arrives faster due to motorcycle speed
    }

    @Test
    @DisplayName("US3.2: Should return null when no available drivers exist")
    void strategy_emptyDriverList_returnsNull() {
        // Arrange
        DriverAssignmentStrategy strategy = new NearestDriverStrategy();

        // Act & Assert
        assertNull(strategy.selectDriver(Collections.emptyList(), restaurantLat, restaurantLng));
        assertNull(strategy.selectDriver(null, restaurantLat, restaurantLng));
    }
}