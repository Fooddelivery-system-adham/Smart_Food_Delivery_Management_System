package SWProject;

import java.util.List;

public class FastestDriverStrategy implements DriverAssignmentStrategy {

    @Override
    public Driver selectDriver(List<Driver> availableDrivers, double restaurantLat, double restaurantLng) {
        if (availableDrivers == null || availableDrivers.isEmpty()) {
            return null;
        }

        Driver fastestDriver = null;
        double minTimeHours = Double.MAX_VALUE;

        for (Driver driver : availableDrivers) {
            double distanceKm = Math.sqrt(Math.pow(restaurantLat - driver.getLatitude(), 2)
                    + Math.pow(restaurantLng - driver.getLongitude(), 2)) * 111.0;

            double speed = driver.getAverageSpeedKmh() > 0 ? driver.getAverageSpeedKmh() : 30.0;
            double estimatedTimeHours = distanceKm / speed;

            if (estimatedTimeHours < minTimeHours) {
                minTimeHours = estimatedTimeHours;
                fastestDriver = driver;
            }
        }

        return fastestDriver;
    }
}