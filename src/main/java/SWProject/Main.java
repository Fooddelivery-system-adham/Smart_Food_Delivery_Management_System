package SWProject;

import java.util.ArrayList;
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

        OrderRepository orderRepository = new OrderRepository();
        // US2.3: OrderService now also needs RestaurantRepository for delivery policies
        OrderService orderService = new OrderService(orderRepository, menuItemRepository, restaurantRepository);

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n=== Food Delivery System ===");
                System.out.println("1. Log in");
                System.out.println("0. Exit");
                System.out.print("Choice: ");

                String choice = scanner.nextLine().trim();

                if ("0".equals(choice)) {
                    break;
                }

                if ("1".equals(choice)) {
                    login(scanner, authService, restaurantService, menuItemService, orderService);
                } else {
                    System.out.println("Invalid choice. Please enter 1 or 0.");
                }
            }
        }
        System.out.println("Goodbye!");
    }

    private static void login(Scanner scanner, AuthService auth,
                              RestaurantService restaurants, MenuItemService menu,
                              OrderService orderService) {
        System.out.print("Username or Email: ");
        String id = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = auth.login(id, password);
        if (user == null) {
            return;
        }

        System.out.println("\nLogin Successful! Welcome " + user.getUsername());

        if (user.isCustomer()) {
            customerMenu(scanner, user, restaurants, menu, orderService);
        } else if (user.isManager()) {
            managerMenu(scanner, restaurants, menu);
        } else {
            System.out.println("Unsupported role: " + user.getRole());
        }
    }

    // ================= CUSTOMER MENU (Sprint 1 & Sprint 2) =================
    private static void customerMenu(Scanner scanner, User user, RestaurantService restaurants,
                                     MenuItemService menu, OrderService orderService) {
        while (true) {
            List<Restaurant> list = restaurants.getOpenRestaurants();
            System.out.println("\n=== AVAILABLE RESTAURANTS ===");

            if (list.isEmpty()) {
                System.out.println("No open restaurants available.");
            } else {
                for (int i = 0; i < list.size(); i++) {
                    Restaurant r = list.get(i);
                    System.out.println((i + 1) + ". " + r.getName()
                            + " (Min: $" + r.getMinOrderValue()
                            + " | Max radius: " + r.getMaxDeliveryRadiusKm() + " km)");
                }
            }

            System.out.println("0. Log out");
            System.out.print("Choose a restaurant: ");

            int choice = readInt(scanner);
            if (choice == 0) {
                return;
            }

            if (choice < 1 || choice > list.size()) {
                System.out.println("Invalid restaurant number.");
                continue;
            }

            Restaurant selectedRestaurant = list.get(choice - 1);
            handleCustomerOrdering(scanner, user, selectedRestaurant, menu, orderService);
        }
    }

    private static void handleCustomerOrdering(Scanner scanner, User user, Restaurant r,
                                               MenuItemService menu, OrderService orderService) {
        List<OrderService.CartItem> cart = new ArrayList<>();

        while (true) {
            List<MenuItem> items = menu.getAvailableMenuItems(r.getRestaurantId());
            System.out.println("\n=== " + r.getName() + " MENU ===");
            System.out.println("Min Order Value: $" + r.getMinOrderValue()
                    + " | Max Radius: " + r.getMaxDeliveryRadiusKm() + " km");

            if (items.isEmpty()) {
                System.out.println("No available menu items.");
                return;
            } else {
                for (MenuItem item : items) {
                    System.out.println("[" + item.getItemId() + "] " + item.getName()
                            + " - $" + item.getPrice());
                }
            }

            // Display current cart items if cart is not empty
            if (!cart.isEmpty()) {
                System.out.println("\nYour Current Cart (" + cart.size() + " item/s):");
                for (OrderService.CartItem c : cart) {
                    System.out.println("   - Item ID: " + c.getItemId() + " | Qty: " + c.getQuantity());
                }
            }

            System.out.println("\nOptions:");
            System.out.println("1. Add item to cart");
            System.out.println("2. Checkout & Place Order");
            System.out.println("3. Clear Cart");
            System.out.println("0. Cancel & Back");
            System.out.print("Choice: ");

            int choice = readInt(scanner);
            if (choice == 0) {
                return;
            }

            if (choice == 1) {
                System.out.print("Enter Item ID: ");
                int itemId = readInt(scanner);

                // Verify that the item ID exists in this specific restaurant
                boolean exists = items.stream().anyMatch(i -> i.getItemId() == itemId);
                if (!exists) {
                    System.out.println("Invalid Item ID! Please select an ID from the menu above.");
                    continue;
                }

                System.out.print("Enter Quantity: ");
                int qty = readInt(scanner);

                if (qty <= 0) {
                    System.out.println("Invalid quantity! Quantity must be 1 or more.");
                    continue;
                }

                cart.add(new OrderService.CartItem(itemId, qty));
                System.out.println("Item added to cart!");

            } else if (choice == 2) {
                if (cart.isEmpty()) {
                    System.out.println("Cart is empty! Add items first.");
                    continue;
                }

                System.out.print("Enter Delivery Address: ");
                String address = scanner.nextLine().trim();

                System.out.print("Enter Delivery Distance in km: ");
                double distance;
                try {
                    distance = Double.parseDouble(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid distance entered. Please enter a number.");
                    continue;
                }

                // US2.3: placeOrder now receives distance for delivery zone check
                Order order = orderService.placeOrder(
                        user.getUserId(),
                        r.getRestaurantId(),
                        address,
                        distance,
                        cart
                );

                if (order != null) {
                    System.out.println("\nOrder Placed Successfully!");
                    System.out.println("Order ID: " + order.getOrderId());
                    System.out.println("Status:   " + order.getStatus());
                    System.out.println("Address:  " + order.getDeliveryAddress());
                    System.out.println("Total:    $" + order.getTotal());
                    return; // Order completed, return to restaurants list
                } else {
                    System.out.println("Checkout failed due to delivery policy violation.");
                    // Keep cart so user can fix quantity/address/distance and try again
                }

            } else if (choice == 3) {
                cart.clear();
                System.out.println("Cart cleared successfully!");
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    // ================= MANAGER MENU =================
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
                case "1":
                    viewAll(scanner, restaurants, menu);
                    break;
                case "2":
                    changeRestaurant(scanner, restaurants);
                    break;
                case "3":
                    changeItem(scanner, menu);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void viewAll(Scanner scanner, RestaurantService restaurants, MenuItemService menu) {
        List<Restaurant> list = restaurants.getAllRestaurants();
        System.out.println("\n--- All Restaurants ---");

        for (Restaurant r : list) {
            System.out.println("[" + r.getRestaurantId() + "] " + r.getName()
                    + " (" + r.getStatus() + ")"
                    + " | Min: $" + r.getMinOrderValue()
                    + " | Max radius: " + r.getMaxDeliveryRadiusKm() + " km");
        }

        System.out.print("Restaurant ID to view menu (0 = back): ");
        int id = readInt(scanner);
        if (id == 0) {
            return;
        }

        boolean exists = list.stream().anyMatch(r -> r.getRestaurantId() == id);
        if (!exists) {
            System.out.println("Restaurant not found.");
            return;
        }

        List<MenuItem> items = menu.getMenuItemsByRestaurant(id);
        System.out.println("\n--- Menu Items ---");
        if (items.isEmpty()) {
            System.out.println("No menu items found.");
        } else {
            for (MenuItem item : items) {
                System.out.println("[" + item.getItemId() + "] " + item.getName()
                        + " | $" + item.getPrice() + " | " + item.getStatus());
            }
        }
    }

    private static void changeRestaurant(Scanner scanner, RestaurantService restaurants) {
        System.out.print("Restaurant ID: ");
        int id = readInt(scanner);
        if (id < 0) {
            System.out.println("Invalid ID.");
            return;
        }

        System.out.print("New status (OPEN/CLOSED): ");
        boolean updated = restaurants.updateRestaurantStatus(id, scanner.nextLine());
        System.out.println(updated ? "Restaurant status updated." : "Update failed.");
    }

    private static void changeItem(Scanner scanner, MenuItemService menu) {
        System.out.print("Menu Item ID: ");
        int id = readInt(scanner);
        if (id < 0) {
            System.out.println("Invalid ID.");
            return;
        }

        System.out.print("New status (AVAILABLE/UNAVAILABLE): ");
        boolean updated = menu.updateMenuItemStatus(id, scanner.nextLine());
        System.out.println(updated ? "Menu item status updated." : "Update failed.");
    }

    private static int readInt(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}