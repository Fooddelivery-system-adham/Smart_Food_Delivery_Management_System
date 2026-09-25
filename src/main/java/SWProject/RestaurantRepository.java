package SWProject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RestaurantRepository {

    // Fetch ALL restaurants for the Manager (both OPEN and CLOSED)
    public List<Restaurant> findAll() {
        List<Restaurant> restaurants = new ArrayList<>();
        String query = "SELECT restaurant_id, name, status FROM \"SWproject\".restaurants ORDER BY restaurant_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                restaurants.add(new Restaurant(
                    rs.getInt("restaurant_id"),
                    rs.getString("name"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching restaurants: " + e.getMessage());
        }

        return restaurants;
    }

    // Update restaurant status in the database
    public boolean updateStatus(int restaurantId, String newStatus) {
        String query = "UPDATE \"SWproject\".restaurants SET status = ? WHERE restaurant_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, restaurantId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Database error updating status: " + e.getMessage());
        }

        return false;
    }
}