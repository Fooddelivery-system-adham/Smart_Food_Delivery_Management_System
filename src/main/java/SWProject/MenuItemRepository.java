package SWProject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuItemRepository {

    // Get ALL menu items for one restaurant (available + unavailable)
    public List<MenuItem> findByRestaurantId(int restaurantId) {
        List<MenuItem> items = new ArrayList<>();
        String query = "SELECT item_id, restaurant_id, name, price, status "
                     + "FROM \"SWproject\".menu_items WHERE restaurant_id = ? ORDER BY item_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, restaurantId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(new MenuItem(
                        rs.getInt("item_id"),
                        rs.getInt("restaurant_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching menu items: " + e.getMessage());
        }

        return items;
    }

    // Change menu item status
    public boolean updateStatus(int itemId, String newStatus) {
        String query = "UPDATE \"SWproject\".menu_items SET status = ? WHERE item_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, itemId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database error updating menu item: " + e.getMessage());
        }

        return false;
    }
}