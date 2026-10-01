package SWProject;

import java.sql.*;

public class OrderRepository {
    public boolean hasPendingOrder(int customerId) {
        String query = "SELECT COUNT(*) as count FROM \"SWproject\".orders WHERE customer_id = ? AND status = 'PENDING'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error checking pending orders: " + e.getMessage());
        }

        return false;
    }
    // Saves order + order items inside a single SQL Transaction
    public boolean saveOrder(Order order) {
        String insertOrderSql = "INSERT INTO \"SWproject\".orders "
                + "(customer_id, restaurant_id, order_type, status, delivery_address, total) "
                + "VALUES (?, ?, ?, ?, ?, ?) RETURNING order_id";

        String insertItemSql = "INSERT INTO \"SWproject\".order_items "
                + "(order_id, item_id, quantity, unit_price) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Start Transaction

            int generatedOrderId = -1;

            // 1. Insert into orders table
            try (PreparedStatement stmt = conn.prepareStatement(insertOrderSql)) {
                stmt.setInt(1, order.getCustomerId());
                stmt.setInt(2, order.getRestaurantId());
                stmt.setString(3, order.getOrderType());
                stmt.setString(4, order.getStatus());
                stmt.setString(5, order.getDeliveryAddress());
                stmt.setDouble(6, order.getTotal());

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        generatedOrderId = rs.getInt(1);
                        order.setOrderId(generatedOrderId);
                    }
                }
            }

            if (generatedOrderId == -1) {
                conn.rollback();
                return false;
            }

            // 2. Insert line items into order_items table
            try (PreparedStatement itemStmt = conn.prepareStatement(insertItemSql)) {
                for (OrderItem item : order.getItems()) {
                    itemStmt.setInt(1, generatedOrderId);
                    itemStmt.setInt(2, item.getItemId());
                    itemStmt.setInt(3, item.getQuantity());
                    itemStmt.setDouble(4, item.getUnitPrice());
                    itemStmt.addBatch();
                }
                itemStmt.executeBatch();
            }

            conn.commit(); // Commit Transaction
            return true;

        } catch (SQLException e) {
            System.err.println("Database error placing order: " + e.getMessage());
        }

        return false;
    }

    // US2.4: Find the current status of an order for a specific customer
    public String findOrderStatus(int orderId, int customerId) {
        String query = "SELECT status FROM \"SWproject\".orders WHERE order_id = ? AND customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, orderId);
            stmt.setInt(2, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error finding order status: " + e.getMessage());
        }

        return null; // Return null if order does not exist or belongs to another user
    }

    // US2.4: Update order status in PostgreSQL
    public boolean updateOrderStatus(int orderId, int customerId, String newStatus) {
        String query = "UPDATE \"SWproject\".orders SET status = ? WHERE order_id = ? AND customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, orderId);
            stmt.setInt(3, customerId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database error updating order status: " + e.getMessage());
        }

        return false;
    }

}