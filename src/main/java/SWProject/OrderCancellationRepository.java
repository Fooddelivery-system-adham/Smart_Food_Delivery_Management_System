package SWProject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Persistence operations used only by US2.4 - Cancel an Order.
 * Existing OrderRepository is intentionally left unchanged.
 */
public class OrderCancellationRepository {

    /**
     * Returns the current status of an order only when it belongs to the given customer.
     * Returns null when the order does not exist for that customer.
     */
    public String findStatusForCustomer(int customerId, int orderId) {
        String sql = "SELECT status FROM \"SWproject\".orders "
                   + "WHERE order_id = ? AND customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            stmt.setInt(2, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error reading order status: " + e.getMessage());
        }

        return null;
    }

    /**
     * Cancels only a PENDING order owned by the given customer.
     * The status condition also protects against a status change between read and update.
     */
    public boolean cancelPendingOrder(int customerId, int orderId) {
        String sql = "UPDATE \"SWproject\".orders "
                   + "SET status = 'CANCELLED' "
                   + "WHERE order_id = ? AND customer_id = ? AND status = 'PENDING'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            stmt.setInt(2, customerId);

            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Database error cancelling order: " + e.getMessage());
            return false;
        }
    }
}
