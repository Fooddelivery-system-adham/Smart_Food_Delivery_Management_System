
package SWProject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RestaurantRepository {

    public List<Restaurant> getOpenRestaurants()
            throws SQLException {

        List<Restaurant> restaurants = new ArrayList<>();

        String sql =
                "SELECT restaurant_id, name, status " +
                "FROM restaurants " +
                "WHERE status = ? " +
                "ORDER BY restaurant_id";

        try (Connection connection =
                     DatabaseConnection.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, "OPEN");

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    int id =
                            resultSet.getInt("restaurant_id");

                    String name =
                            resultSet.getString("name");

                    String status =
                            resultSet.getString("status");

                    Restaurant restaurant =
                            new Restaurant(id, name, status);

                    restaurants.add(restaurant);
                }
            }
        }

        return restaurants;
    }
    public List<MenuItem> getAvailableMenu(int restaurantId)
            throws SQLException {

        List<MenuItem> items = new ArrayList<>();

        String sql =
                "SELECT item_id, name, price " +
                "FROM menu_items " +
                "WHERE restaurant_id = ? " +
                "AND status = 'AVAILABLE' " +
                "ORDER BY item_id";

        try (Connection connection =
                     DatabaseConnection.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, restaurantId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    MenuItem item = new MenuItem(
                            resultSet.getInt("item_id"),
                            resultSet.getString("name"),
                            resultSet.getBigDecimal("price")
                    );

                    items.add(item);
                }
            }
        }

        return items;
    }
}