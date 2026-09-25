package SWProject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuItemRepository {
    private static final String SCHEMA = "\"SWproject\"";

    public List<MenuItem> findByRestaurantId(int restaurantId) {
        return findByRestaurantId(restaurantId, false);
    }

    public List<MenuItem> findAvailableByRestaurantId(int restaurantId) {
        return findByRestaurantId(restaurantId, true);
    }

    private List<MenuItem> findByRestaurantId(int restaurantId, boolean availableOnly) {
        List<MenuItem> items = new ArrayList<>();
        String sql = "SELECT item_id, restaurant_id, name, price, status FROM " + SCHEMA
                + ".menu_items WHERE restaurant_id = ?"
                + (availableOnly ? " AND status = 'AVAILABLE'" : "")
                + " ORDER BY item_id";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, restaurantId);
            try (ResultSet rs = s.executeQuery()) {
                while (rs.next()) {
                    items.add(new MenuItem(
                            rs.getInt("item_id"),
                            rs.getInt("restaurant_id"),
                            rs.getString("name"),
                            rs.getBigDecimal("price"),
                            rs.getString("status")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching menu items: " + e.getMessage());
        }
        return items;
    }

    public boolean updateStatus(int itemId, String newStatus) {
        String sql = "UPDATE " + SCHEMA + ".menu_items SET status = ? WHERE item_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, newStatus);
            s.setInt(2, itemId);
            return s.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database error updating menu item: " + e.getMessage());
            return false;
        }
    }
}
