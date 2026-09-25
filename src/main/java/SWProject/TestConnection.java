
package SWProject;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {

    public static void main(String[] args) {

        try (Connection connection =
                DatabaseConnection.getConnection()) {

            System.out.println(
                "Connected successfully!"
            );

            System.out.println(
                "Database: " + connection.getCatalog()
            );

        } catch (SQLException e) {

            System.out.println(
                "Database connection failed!"
            );

            e.printStackTrace();
        }
    }
}