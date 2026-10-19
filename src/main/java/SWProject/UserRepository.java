package SWProject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    public User findByUsernameOrEmail(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            return null;
        }

        String query = "SELECT user_id, username, email, password, role, latitude, longitude "
                     + "FROM \"SWproject\".users WHERE username = ? OR email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            String cleanId = identifier.trim();
            stmt.setString(1, cleanId);
            stmt.setString(2, cleanId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude") 
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: Could not retrieve user info.");
            System.err.println("Details: " + e.getMessage());
        }

        return null;
    }
}