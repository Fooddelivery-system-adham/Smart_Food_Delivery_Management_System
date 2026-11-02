package SWProject;

import java.util.List;

public class NearestDriverStrategy implements DriverAssignmentStrategy {

    @Override
    public Driver selectDriver(List<Driver> availableDrivers, double restaurantLat, double restaurantLng) {
        if (availableDrivers == null || availableDrivers.isEmpty()) {
            return null;
        }

        Driver nearestDriver = null;
        double minDistanceKm = Double.MAX_VALUE;

        for (Driver driver : availableDrivers) {
            if (!isEligible(driver)) {
                continue;
            }

            double distanceKm = haversineKm(
                    restaurantLat, restaurantLng,
                    driver.getLatitude(), driver.getLongitude());

            if (distanceKm < minDistanceKm) {
                minDistanceKm = distanceKm;
                nearestDriver = driver;
            }
        }

        return nearestDriver;
    }

    private boolean isEligible(Driver driver) {
        return driver != null
                && driver.isAvailable()
                && Double.isFinite(driver.getLatitude())
                && Double.isFinite(driver.getLongitude());
    }

    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        final double earthRadiusKm = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusKm * c;
    }
}
