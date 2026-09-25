package SWProject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RestaurantRepository {

    // US1.2: Fetch OPEN restaurants only (For Customer)
    public List<Restaurant> findOpenRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        String query = "SELECT restaurant_id, name, status FROM \"SWproject\".restaurants "
                     + "WHERE status = 'OPEN' ORDER BY restaurant_id";

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
            System.err.println("Database error fetching open restaurants: " + e.getMessage());
        }

        return restaurants;
    }

    // Fetch ALL restaurants (For Manager)
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
            System.err.println("Database error fetching all restaurants: " + e.getMessage());
        }

        return restaurants;
    }

    // Update status
    public boolean updateStatus(int restaurantId, String newStatus) {
        String query = "UPDATE \"SWproject\".restaurants SET status = ? WHERE restaurant_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, restaurantId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database error updating restaurant status: " + e.getMessage());
        }

        return false;
    }
}