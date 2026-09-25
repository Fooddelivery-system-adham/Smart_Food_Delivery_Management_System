
package SWProject;

import java.util.Scanner;

public class RestaurantApp {

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {

            RestaurantConsole console =
                    new RestaurantConsole(scanner);

            console.showRestaurants();
        }
    }
}