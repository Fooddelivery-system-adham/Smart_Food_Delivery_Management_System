package SWProject;

import java.sql.*;

public class OrderRepository {

    // Saves order + order items inside a single SQL Transaction
    public boolean saveOrder(Order order) {
        String insertOrderSql = "INSERT INTO \"SWproject\".orders "
                + "(customer_id, restaurant_id, order_type, status, delivery_address, total) "
                + "VALUES (?, ?, ?, ?, ?, ?) RETURNING order_id";

        String insertItemSql = "INSERT INTO \"SWproject\".order_items "
                + "(order_id, item_id, quantity, unit_price) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); 

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

            conn.commit(); 
            return true;

        } catch (SQLException e) {
            System.err.println("Database error placing order: " + e.getMessage());
        }

        return false;
    }
}