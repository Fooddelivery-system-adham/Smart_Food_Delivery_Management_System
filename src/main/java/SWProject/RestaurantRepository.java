package SWProject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantRepository {
    private static final String SCHEMA = "\"SWproject\"";

    public List<Restaurant> getOpenRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        String sql = "SELECT restaurant_id, name, status FROM " + SCHEMA
                + ".restaurants WHERE status = 'OPEN' ORDER BY restaurant_id";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql);
             ResultSet rs = s.executeQuery()) {
            while (rs.next()) {
                restaurants.add(map(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching open restaurants: " + e.getMessage());
        }
        return restaurants;
    }

    public List<Restaurant> findAll() {
        List<Restaurant> restaurants = new ArrayList<>();
        String sql = "SELECT restaurant_id, name, status FROM " + SCHEMA
                + ".restaurants ORDER BY restaurant_id";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql);
             ResultSet rs = s.executeQuery()) {
            while (rs.next()) restaurants.add(map(rs));
        } catch (SQLException e) {
            System.err.println("Database error fetching restaurants: " + e.getMessage());
        }
        return restaurants;
    }

    public boolean updateStatus(int restaurantId, String newStatus) {
        String sql = "UPDATE " + SCHEMA + ".restaurants SET status = ? WHERE restaurant_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, newStatus);
            s.setInt(2, restaurantId);
            return s.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database error updating restaurant status: " + e.getMessage());
            return false;
        }
    }

    private Restaurant map(ResultSet rs) throws SQLException {
        return new Restaurant(rs.getInt("restaurant_id"),
                rs.getString("name"), rs.getString("status"));
    }
}
