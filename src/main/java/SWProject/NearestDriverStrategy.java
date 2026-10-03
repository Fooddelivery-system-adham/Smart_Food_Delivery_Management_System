package SWProject;

import java.util.List;

public class NearestDriverStrategy implements DriverAssignmentStrategy {

    @Override
    public Driver selectDriver(List<Driver> availableDrivers, double restaurantLat, double restaurantLng) {
        if (availableDrivers == null || availableDrivers.isEmpty()) {
            return null;
        }

        Driver nearestDriver = null;
        double minDistance = Double.MAX_VALUE;

        for (Driver driver : availableDrivers) {
            double distance = calculateDistance(restaurantLat, restaurantLng, driver.getLatitude(), driver.getLongitude());
            if (distance < minDistance) {
                minDistance = distance;
                nearestDriver = driver;
            }
        }

        return nearestDriver;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Approximate distance calculation using Euclidean distance formula
        double latDiff = lat1 - lat2;
        double lonDiff = lon1 - lon2;
        return Math.sqrt(latDiff * latDiff + lonDiff * lonDiff) * 111.0; // ~111 km per degree
    }
}
