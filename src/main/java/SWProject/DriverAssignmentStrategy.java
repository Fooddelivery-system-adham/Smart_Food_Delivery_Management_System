package SWProject;

import java.util.List;

public interface DriverAssignmentStrategy {
    // Selects the best available driver according to the active strategy
    Driver selectDriver(List<Driver> availableDrivers, double restaurantLat, double restaurantLng);
}