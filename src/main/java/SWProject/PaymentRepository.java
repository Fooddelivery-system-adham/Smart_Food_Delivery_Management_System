package SWProject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PaymentRepository {

    public boolean savePayment(int orderId, double amount, String method, String status) {
        String query = "INSERT INTO \"SWproject\".payments (order_id, amount, method, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, orderId);
            stmt.setDouble(2, amount);
            stmt.setString(3, method);
            stmt.setString(4, status);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database error saving payment: " + e.getMessage());
        }
        return false;
    }
}