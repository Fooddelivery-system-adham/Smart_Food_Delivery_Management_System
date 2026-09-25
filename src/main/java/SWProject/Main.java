package SWProject;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        UserRepository userRepository = new UserRepository();
        AuthService authService = new AuthService(userRepository);
        RestaurantService restaurantService = new RestaurantService(new RestaurantRepository());
        MenuItemService menuItemService = new MenuItemService(new MenuItemRepository());

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n=== Food Delivery System ===");
                System.out.println("1. Log in");
                System.out.println("0. Exit");
                System.out.print("Choice: ");

                String choice = scanner.nextLine().trim();
                if ("0".equals(choice)) break;
                if ("1".equals(choice)) {
                    login(scanner, authService, restaurantService, menuItemService);
                } else {
                    System.out.println("Invalid choice.");
                }
            }
        }
        System.out.println("Goodbye!");
    }

    private static void login(Scanner scanner, AuthService auth,
                              RestaurantService restaurants, MenuItemService menu) {
        System.out.print("Username or Email: ");
        String id = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = auth.login(id, password);
        if (user == null) return;

        System.out.println("\nLogin Successful! Welcome " + user.getUsername());
        if (user.isCustomer()) customerMenu(scanner, restaurants, menu);
        else if (user.isManager()) managerMenu(scanner, restaurants, menu);
        else System.out.println("Unsupported role: " + user.getRole());
    }

    private static void customerMenu(Scanner scanner, RestaurantService restaurants,
                                     MenuItemService menu) {
        while (true) {
            List<Restaurant> list = restaurants.getOpenRestaurants();
            System.out.println("\n=== AVAILABLE RESTAURANTS ===");
            if (list.isEmpty()) System.out.println("No open restaurants available.");
            for (int i = 0; i < list.size(); i++)
                System.out.println((i + 1) + ". " + list.get(i).getName());
            System.out.println("0. Log out");
            System.out.print("Choose a restaurant: ");

            int choice = readInt(scanner);
            if (choice == 0) return;
            if (choice < 1 || choice > list.size()) {
                System.out.println("Invalid restaurant number.");
                continue;
            }

            Restaurant r = list.get(choice - 1);
            showCustomerMenuItems(scanner, r, menu);
        }
    }

    private static void showCustomerMenuItems(Scanner scanner, Restaurant r, MenuItemService menu) {
        while (true) {
            List<MenuItem> items = menu.getAvailableMenuItems(r.getRestaurantId());
            System.out.println("\n=== " + r.getName() + " MENU ===");
            if (items.isEmpty()) System.out.println("No available menu items.");
            for (int i = 0; i < items.size(); i++)
                System.out.println((i + 1) + ". " + items.get(i).getName()
                        + " - $" + items.get(i).getPrice());
            System.out.println("0. Back");
            System.out.print("Choice: ");
            if (readInt(scanner) == 0) return;
            System.out.println("Ordering is not part of Sprint 1. Enter 0 to go back.");
        }
    }

    private static void managerMenu(Scanner scanner, RestaurantService restaurants,
                                    MenuItemService menu) {
        while (true) {
            System.out.println("\n=== MANAGER MENU ===");
            System.out.println("1. View Restaurants & Menus");
            System.out.println("2. Change Restaurant Status");
            System.out.println("3. Change Menu Item Status");
            System.out.println("0. Log out");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": viewAll(scanner, restaurants, menu); break;
                case "2": changeRestaurant(scanner, restaurants); break;
                case "3": changeItem(scanner, menu); break;
                case "0": return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private static void viewAll(Scanner scanner, RestaurantService restaurants, MenuItemService menu) {
        List<Restaurant> list = restaurants.getAllRestaurants();
        System.out.println("\n--- All Restaurants ---");
        for (Restaurant r : list)
            System.out.println("[" + r.getRestaurantId() + "] " + r.getName()
                    + " (" + r.getStatus() + ")");
        System.out.print("Restaurant ID to view menu (0 = back): ");
        int id = readInt(scanner);
        if (id == 0) return;

        boolean exists = list.stream().anyMatch(r -> r.getRestaurantId() == id);
        if (!exists) {
            System.out.println("Restaurant not found.");
            return;
        }

        List<MenuItem> items = menu.getMenuItemsByRestaurant(id);
        System.out.println("\n--- Menu Items ---");
        if (items.isEmpty()) System.out.println("No menu items found.");
        for (MenuItem item : items)
            System.out.println("[" + item.getItemId() + "] " + item.getName()
                    + " | $" + item.getPrice() + " | " + item.getStatus());
    }

    private static void changeRestaurant(Scanner scanner, RestaurantService restaurants) {
        System.out.print("Restaurant ID: ");
        int id = readInt(scanner);
        if (id < 0) { System.out.println("Invalid ID."); return; }
        System.out.print("New status (OPEN/CLOSED): ");
        System.out.println(restaurants.updateRestaurantStatus(id, scanner.nextLine())
                ? "Restaurant status updated." : "Update failed.");
    }

    private static void changeItem(Scanner scanner, MenuItemService menu) {
        System.out.print("Menu Item ID: ");
        int id = readInt(scanner);
        if (id < 0) { System.out.println("Invalid ID."); return; }
        System.out.print("New status (AVAILABLE/UNAVAILABLE): ");
        System.out.println(menu.updateMenuItemStatus(id, scanner.nextLine())
                ? "Menu item status updated." : "Update failed.");
    }

    private static int readInt(Scanner scanner) {
        try { return Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { return -1; }
    }
}
