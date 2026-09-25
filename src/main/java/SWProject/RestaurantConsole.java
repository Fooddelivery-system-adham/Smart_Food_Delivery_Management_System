
package SWProject;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class RestaurantConsole {

    private final RestaurantRepository repository =
            new RestaurantRepository();

    private final Scanner scanner;

    public RestaurantConsole(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showRestaurants() {

        while (true) {

            try {
                List<Restaurant> restaurants =
                        repository.getOpenRestaurants();

                System.out.println();
                System.out.println(
                        "===== AVAILABLE RESTAURANTS =====");

                if (restaurants.isEmpty()) {
                    System.out.println(
                            "No open restaurants available.");
                }

                for (int i = 0; i < restaurants.size(); i++) {
                    System.out.println(
                            (i + 1) + ". " +
                            restaurants.get(i).getName());
                }

                System.out.println("0. Exit");
                System.out.print("Choose a restaurant: ");

                int choice = readNumber();

                if (choice == 0) {
                    System.out.println("Goodbye!");
                    return;
                }

                if (choice < 1 || choice > restaurants.size()) {
                    System.out.println(
                            "Invalid restaurant number.");
                    continue;
                }

                Restaurant selected =
                        restaurants.get(choice - 1);

                showMenu(selected);

            } catch (SQLException e) {
                System.out.println(
                        "Database error: " + e.getMessage());
                return;
            }
        }
    }

    private void showMenu(Restaurant restaurant)
            throws SQLException {

        List<MenuItem> items =
                repository.getAvailableMenu(restaurant.getId());

        while (true) {

            System.out.println();
            System.out.println(
                    "===== " + restaurant.getName() +
                    " MENU =====");

            if (items.isEmpty()) {
                System.out.println(
                        "No available menu items.");
            }

            for (int i = 0; i < items.size(); i++) {

                MenuItem item = items.get(i);

                System.out.println(
                        (i + 1) + ". " +
                        item.getName() +
                        " - " + item.getPrice());
            }

            System.out.println("0. Back to restaurants");
            System.out.print("Choose: ");

            int choice = readNumber();

            if (choice == 0) {
                return;
            }

            System.out.println(
                    "To return, please enter 0.");
        }
    }

    private int readNumber() {

        String input = scanner.nextLine().trim();

        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}