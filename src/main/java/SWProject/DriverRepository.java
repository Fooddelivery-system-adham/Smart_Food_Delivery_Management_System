package SWProject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverRepository {

    // US3.1: Fetch ONLY available drivers
    public List<Driver> findAvailableDrivers() {
        List<Driver> drivers = new ArrayList<>();
        String query = "SELECT driver_id, name, driver_type, status, latitude, longitude, average_speed_kmh "
                     + "FROM \"SWproject\".drivers WHERE status = 'AVAILABLE'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                drivers.add(new Driver(
                    rs.getInt("driver_id"),
                    rs.getString("name"),
                    rs.getString("driver_type"),
                    rs.getString("status"),
                    rs.getDouble("latitude"),
                    rs.getDouble("longitude"),
                    rs.getDouble("average_speed_kmh")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching drivers: " + e.getMessage());
        }

        return drivers;
    }

    // US3.1: Update driver status to BUSY/AVAILABLE in database
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
}
