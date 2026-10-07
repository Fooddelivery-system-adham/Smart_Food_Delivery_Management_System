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
    public boolean assignDriverToOrder(int orderId, int driverId) {
        String query = "UPDATE \"SWproject\".orders SET driver_id = ?, status = 'CONFIRMED' WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, driverId);
            stmt.setInt(2, orderId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database error assigning driver to order: " + e.getMessage());
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

    // US3.1: Atomically claim an AVAILABLE driver and assign it to a matching PENDING order.
    public boolean assignAvailableDriverToOrder(int orderId, int restaurantId, int driverId) {
        String claimDriverSql = "UPDATE \"SWproject\".drivers SET status = 'BUSY' "
                + "WHERE driver_id = ? AND status = 'AVAILABLE'";

        String assignOrderSql = "UPDATE \"SWproject\".orders "
                + "SET driver_id = ?, status = 'CONFIRMED' "
                + "WHERE order_id = ? AND restaurant_id = ? "
                + "AND driver_id IS NULL AND status = 'PENDING'";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                try (PreparedStatement driverStmt = conn.prepareStatement(claimDriverSql)) {
                    driverStmt.setInt(1, driverId);
                    if (driverStmt.executeUpdate() != 1) {
                        conn.rollback();
                        return false;
                    }
                }

                try (PreparedStatement orderStmt = conn.prepareStatement(assignOrderSql)) {
                    orderStmt.setInt(1, driverId);
                    orderStmt.setInt(2, orderId);
                    orderStmt.setInt(3, restaurantId);

                    if (orderStmt.executeUpdate() != 1) {
                        conn.rollback();
                        return false;
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Database error assigning available driver: " + e.getMessage());
            return false;
        }
    }

    // US3.3: Read status by order ID so a status-change notification can include old/new status.
    public String findOrderStatusById(int orderId) {
        String query = "SELECT status FROM \"SWproject\".orders WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error finding order status: " + e.getMessage());
        }

        return null;
    }

    // US3.3/US3.4: Optimistic status update; succeeds only if status did not change meanwhile.
    public boolean updateOrderStatusById(int orderId, String expectedOldStatus, String newStatus) {
        String query = "UPDATE \"SWproject\".orders SET status = ? "
                + "WHERE order_id = ? AND status = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, orderId);
            stmt.setString(3, expectedOldStatus);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Database error updating order status: " + e.getMessage());
            return false;
        }
    }

}