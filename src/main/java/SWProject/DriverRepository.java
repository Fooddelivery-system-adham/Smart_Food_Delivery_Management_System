package SWProject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverRepository {

    // US3.1 & US3.2: only eligible AVAILABLE drivers are returned.
    public List<Driver> findAvailableDrivers() {
        List<Driver> drivers = new ArrayList<>();
        String query = "SELECT driver_id, name, driver_type, status, latitude, longitude, average_speed_kmh "
                     + "FROM \"SWproject\".drivers "
                     + "WHERE status = 'AVAILABLE' "
                     + "AND latitude IS NOT NULL AND longitude IS NOT NULL "
                     + "AND average_speed_kmh IS NOT NULL AND average_speed_kmh > 0 "
                     + "ORDER BY driver_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("driver_id");
                String name = rs.getString("name");
                String type = rs.getString("driver_type");
                String status = rs.getString("status");
                double lat = rs.getDouble("latitude");
                double lon = rs.getDouble("longitude");
                double speed = rs.getDouble("average_speed_kmh");

                Driver driver;
                switch (type.toUpperCase()) {
                    case "BICYCLE":
                        driver = new BicycleDriver(id, name, status, lat, lon, speed);
                        break;
                    case "CAR":
                        driver = new CarDriver(id, name, status, lat, lon, speed);
                        break;
                    case "ELECTRIC_VEHICLE":
                        driver = new ElectricVehicleDriver(id, name, status, lat, lon, speed);
                        break;
                    default:  
                        driver = new MotorcycleDriver(id, name, status, lat, lon, speed);
                        break;
                }
                drivers.add(driver);
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching drivers: " + e.getMessage());
        }

        return drivers;
    }
    // Existing helper retained for compatibility.
    public boolean updateDriverStatus(int driverId, String newStatus) {
        String query = "UPDATE \"SWproject\".drivers SET status = ? WHERE driver_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, driverId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database error updating driver status: " + e.getMessage());
        }

        return false;
    }
    // Add this method inside DriverRepository.java
    public List<Driver> getAllDrivers() {
        List<Driver> drivers = new ArrayList<>();
        String sql = "SELECT * FROM \"SWproject\".drivers";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("driver_id");
                String name = rs.getString("name");
                String type = rs.getString("driver_type");
                String status = rs.getString("status");
                double lat = rs.getDouble("latitude");
                double lon = rs.getDouble("longitude");
                double speed = rs.getDouble("average_speed_kmh");

                Driver driver;
                switch (type.toUpperCase()) {
                    case "BICYCLE":
                        driver = new BicycleDriver(id, name, status, lat, lon, speed);
                        break;
                    case "MOTORCYCLE":
                        driver = new MotorcycleDriver(id, name, status, lat, lon, speed);
                        break;
                    case "ELECTRICVEHICLE":
                        driver = new ElectricVehicleDriver(id, name, status, lat, lon, speed);
                        break;
                    case "CAR":
                    default:
                        driver = new CarDriver(id, name, status, lat, lon, speed);
                        break;
                }
                drivers.add(driver);
            }
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
        }
        return drivers;
    }
}
