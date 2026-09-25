package SWProject;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        UserRepository userRepository = new UserRepository();
        AuthService authService = new AuthService(userRepository);

        RestaurantRepository restaurantRepository = new RestaurantRepository();
        RestaurantService restaurantService = new RestaurantService(restaurantRepository);

        MenuItemRepository menuItemRepository = new MenuItemRepository();
        MenuItemService menuItemService = new MenuItemService(menuItemRepository);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Food Delivery System ===");
            System.out.println("1. Log in");
            System.out.println("2. Exit");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                handleLogin(scanner, authService, restaurantService, menuItemService);
            } else if (choice.equals("2")) {
                System.out.println("Goodbye!");
                break;
            } else {
                System.out.println("Invalid choice! Please enter 1 or 2.");
            }
        }

        scanner.close();
    }

    private static void handleLogin(Scanner scanner, AuthService authService,
                                    RestaurantService restaurantService,
                                    MenuItemService menuItemService) {
        System.out.print("Enter Username or Email: ");
        String identifier = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        User user = authService.login(identifier, password);

        if (user != null) {
            System.out.println("\nLogin Successful!");
            System.out.println("Welcome: " + user.getUsername());

            if (user.isCustomer()) {
                handleCustomerMenu(scanner, user);
            } else if (user.isManager()) {
                handleManagerMenu(scanner, user, restaurantService, menuItemService);
            }
        }
    }

    private static void handleCustomerMenu(Scanner scanner, User user) {
        // empty for now
    }

    // ==== UPDATED MANAGER MENU ====
    private static void handleManagerMenu(Scanner scanner, User user,
                                          RestaurantService restaurantService,
                                          MenuItemService menuItemService) {
        boolean inManagerMenu = true;

        while (inManagerMenu && user.isManager()) {
            System.out.println("\n=== Manager Menu ===");
            System.out.println("1. View Restaurants & Menus");
            System.out.println("2. Change Restaurant Status (OPEN / CLOSED)");
            System.out.println("3. Change Menu Item Status (AVAILABLE / UNAVAILABLE)");
            System.out.println("4. Log out");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();

            // ===== Option 1: Clean List & Specific Menu =====
            if (choice.equals("1")) {
                List<Restaurant> restaurants = restaurantService.getAllRestaurants();

                if (restaurants.isEmpty()) {
                    System.out.println("No restaurants found.");
                } else {
                    System.out.println("\n--- All Restaurants ---");
                    for (Restaurant r : restaurants) {
                        System.out.println("[" + r.getRestaurantId() + "] " 
                                + r.getName() + " (Status: " + r.getStatus() + ")");
                    }

                    System.out.print("\nEnter a Restaurant ID to view its menu (or 0 to go back): ");
                    String idText = scanner.nextLine().trim();

                    if (idText.equals("0")) {
                        continue; // skips the rest and loops back to Manager Menu
                    }

                    try {
                        int selectedRestId = Integer.parseInt(idText);
                        
                        // Check if the ID actually exists in the list we just fetched
                        boolean exists = restaurants.stream().anyMatch(r -> r.getRestaurantId() == selectedRestId);
                        
                        if (!exists) {
                            System.out.println("Restaurant not found. Returning to menu...");
                        } else {
                            // Fetch and show only the menu for the selected restaurant
                            List<MenuItem> items = menuItemService.getMenuItemsByRestaurant(selectedRestId);
                            System.out.println("\n--- Menu Items ---");
                            if (items.isEmpty()) {
                                System.out.println("No menu items available for this restaurant.");
                            } else {
                                for (MenuItem item : items) {
                                    System.out.println("  ID: " + item.getItemId()
                                            + " | " + item.getName()
                                            + " | Price: $" + item.getPrice()
                                            + " | Status: " + item.getStatus());
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Must be a number. Returning to menu...");
                    }
                }

            // ===== Option 2: Change restaurant status =====
            } else if (choice.equals("2")) {
                System.out.print("Enter Restaurant ID: ");
                String idText = scanner.nextLine().trim();
                int restaurantId;
                try {
                    restaurantId = Integer.parseInt(idText);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid ID. Must be a number.");
                    continue;
                }

                System.out.print("Enter new status (OPEN / CLOSED): ");
                String newStatus = scanner.nextLine().trim();

                boolean ok = restaurantService.updateRestaurantStatus(restaurantId, newStatus);
                if (ok) {
                    System.out.println("Restaurant status updated successfully.");
                } else {
                    System.out.println("Update failed. Check the Restaurant ID.");
                }

            // ===== Option 3: Change menu item status =====
            } else if (choice.equals("3")) {
                System.out.print("Enter Menu Item ID: ");
                String idText = scanner.nextLine().trim();
                int itemId;
                try {
                    itemId = Integer.parseInt(idText);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid ID. Must be a number.");
                    continue;
                }

                System.out.print("Enter new status (AVAILABLE / UNAVAILABLE): ");
                String newStatus = scanner.nextLine().trim();

                boolean ok = menuItemService.updateMenuItemStatus(itemId, newStatus);
                if (ok) {
                    System.out.println("Menu item status updated successfully.");
                } else {
                    System.out.println("Update failed. Check the Item ID.");
                }

            // ===== Option 4: Exit =====
            } else if (choice.equals("4")) {
                System.out.println("Logging out from Manager Menu...");
                inManagerMenu = false;
            } else {
                System.out.println("Invalid choice! Please enter 1, 2, 3 or 4.");
            }
        }
    }
}