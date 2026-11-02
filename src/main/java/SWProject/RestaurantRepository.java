package SWProject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RestaurantRepository {

    private static final String BASE_COLUMNS =
            "restaurant_id, name, status, min_order_value, max_delivery_radius_km, latitude, longitude ";

    public List<Restaurant> findOpenRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        String query = "SELECT " + BASE_COLUMNS
                     + "FROM \"SWproject\".restaurants WHERE status = 'OPEN' ORDER BY restaurant_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                restaurants.add(mapRestaurant(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching open restaurants: " + e.getMessage());
        }

        return restaurants;
    }

    public List<Restaurant> findAll() {
        List<Restaurant> restaurants = new ArrayList<>();
        String query = "SELECT " + BASE_COLUMNS
                     + "FROM \"SWproject\".restaurants ORDER BY restaurant_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                restaurants.add(mapRestaurant(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching all restaurants: " + e.getMessage());
        }

        return restaurants;
    }

    public Restaurant findById(int restaurantId) {
        String query = "SELECT " + BASE_COLUMNS
                     + "FROM \"SWproject\".restaurants WHERE restaurant_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, restaurantId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRestaurant(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error finding restaurant by ID: " + e.getMessage());
        }

        return null;
    }

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

    private Restaurant mapRestaurant(ResultSet rs) throws SQLException {
        return new Restaurant(
                rs.getInt("restaurant_id"),
                rs.getString("name"),
                rs.getString("status"),
                rs.getDouble("min_order_value"),
                rs.getDouble("max_delivery_radius_km"),
                rs.getDouble("latitude"),
                rs.getDouble("longitude")
        );
    }
}
