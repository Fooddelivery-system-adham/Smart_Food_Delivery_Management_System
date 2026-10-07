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
        OrderService orderService = new OrderService(orderRepository, menuItemRepository, restaurantRepository);

        // Sprint 3: Delivery and Notification setup
        DriverRepository driverRepository = new DriverRepository();
        NotificationService notificationService = new NotificationService();
        notificationService.addObserver(new CustomerConsoleNotifier());

        DeliveryService deliveryService = new DeliveryService(
                driverRepository,
                orderRepository,
                restaurantRepository,
                notificationService,
                new NearestDriverStrategy()
        );

        OrderStatusService orderStatusService = new OrderStatusService(
                orderRepository, notificationService
        );

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
                    login(scanner, authService, restaurantService, menuItemService,
                          orderService, deliveryService, notificationService, orderStatusService);
                } else {
                    System.out.println("Invalid choice. Please enter 1 or 0.");
                }
            }
        }
        System.out.println("Goodbye!");
    }

    private static void login(Scanner scanner, AuthService auth,
                              RestaurantService restaurants, MenuItemService menu,
                              OrderService orderService, DeliveryService deliveryService,
                              NotificationService notificationService,
                              OrderStatusService orderStatusService) {
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
            customerMenu(scanner, user, restaurants, menu, orderService, notificationService);
        } else if (user.isManager()) {
            managerMenu(scanner, restaurants, menu, deliveryService, orderStatusService);
        } else {
            System.out.println("Unsupported role: " + user.getRole());
        }
    }

    // ================= CUSTOMER MENU (Sprint 1, 2, 3) =================
    private static void customerMenu(Scanner scanner, User user, RestaurantService restaurants,
                                     MenuItemService menu, OrderService orderService,
                                     NotificationService notificationService) {
        while (true) {
            System.out.println("\n=== CUSTOMER MENU ===");
            System.out.println("1. View Available Restaurants");

            boolean hasPending = orderService.hasPendingOrder(user.getUserId());
            if (hasPending) {
                System.out.println("2. Cancel an Existing Order");
            }

            System.out.println("0. Log out");
            System.out.print("Choice: ");

            int choice = readInt(scanner);
            if (choice == 0) {
                System.out.println("Logging out...");
                return;
            }

            if (choice == 1) {
                showAvailableRestaurants(scanner, user, restaurants, menu, orderService, notificationService);
            } else if (choice == 2) {
                if (!hasPending) {
                    System.out.println("No pending orders available to cancel.");
                    continue;
                }
                handleCancelOrder(scanner, user, orderService, notificationService);
            } else {
                System.out.println("Invalid choice. Please enter 1, 2 (if available), or 0.");
            }
        }
    }

    private static void showAvailableRestaurants(Scanner scanner, User user, RestaurantService restaurants,
                                                  MenuItemService menu, OrderService orderService,
                                                  NotificationService notificationService) {
        while (true) {
            List<Restaurant> list = restaurants.getOpenRestaurants();
            System.out.println("\n=== AVAILABLE RESTAURANTS ===");

            if (list.isEmpty()) {
                System.out.println("No open restaurants available.");
                return;
            } else {
                for (int i = 0; i < list.size(); i++) {
                    Restaurant r = list.get(i);
                    System.out.println((i + 1) + ". " + r.getName()
                            + " (Min: $" + r.getMinOrderValue()
                            + " | Max radius: " + r.getMaxDeliveryRadiusKm() + " km)");
                }
            }

            System.out.println("0. Back to Main Menu");
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
            handleCustomerOrdering(scanner, user, selectedRestaurant, menu, orderService, notificationService);
        }
    }

    private static void handleCustomerOrdering(Scanner scanner, User user, Restaurant r,
                                               MenuItemService menu, OrderService orderService,
                                               NotificationService notificationService) {
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

                    // US3.3: Notify customer when order is placed
                    notificationService.notifyStatusChange(
                            order.getOrderId(),
                            "NONE",
                            "PENDING",
                            "Your order has been placed and is waiting for confirmation."
                    );

                    return;
                } else {
                    System.out.println("Checkout failed due to delivery policy violation.");
                }

            } else if (choice == 3) {
                cart.clear();
                System.out.println("Cart cleared successfully!");
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    // ================= US2.4 - CANCEL ORDER =================
    private static void handleCancelOrder(Scanner scanner, User user, OrderService orderService,
                                          NotificationService notificationService) {
        System.out.print("Enter Order ID to cancel: ");
        int orderId = readInt(scanner);

        OrderService.CancellationResult result = orderService.cancelOrder(user.getUserId(), orderId);

        switch (result) {
            case CANCELLED:
                System.out.println("Order cancelled successfully. Status is now CANCELLED.");
                // US3.3: Notify customer when order is cancelled
                notificationService.notifyStatusChange(
                        orderId,
                        "PENDING",
                        "CANCELLED",
                        "Your order has been cancelled."
                );
                break;
            case ALREADY_CANCELLED:
                System.out.println("This order is already CANCELLED.");
                break;
            case NOT_ALLOWED:
                System.out.println("Cancellation rejected: only PENDING orders can be cancelled.");
                break;
            case NOT_FOUND:
                System.out.println("Order not found, or it does not belong to this customer.");
                break;
            case INVALID_ORDER_ID:
                System.out.println("Invalid Order ID.");
                break;
            default:
                System.out.println("Cancellation failed because of a database error.");
        }
    }

    // ================= MANAGER MENU (Sprint 1, 2, 3) =================
    private static void managerMenu(Scanner scanner, RestaurantService restaurants,
                                    MenuItemService menu, DeliveryService deliveryService,
                                    OrderStatusService orderStatusService) {
        while (true) {
            System.out.println("\n=== MANAGER MENU ===");
            System.out.println("1. View Restaurants & Menus");
            System.out.println("2. Change Restaurant Status");
            System.out.println("3. Change Menu Item Status");
            System.out.println("4. Assign Driver to Order (Smart Selection)");
            System.out.println("5. Change Order Status (Notifications)");
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
                case "4":
                    handleAssignDriver(scanner, deliveryService);
                    break;
                case "5":
                    handleOrderStatusChange(scanner, orderStatusService);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // ================= US3.1 & US3.2 - ASSIGN DRIVER =================
    private static void handleAssignDriver(Scanner scanner, DeliveryService deliveryService) {
        System.out.print("Enter Order ID to assign driver: ");
        int orderId = readInt(scanner);
        if (orderId < 0) {
            System.out.println("Invalid Order ID.");
            return;
        }

        System.out.print("Enter Restaurant ID: ");
        int restaurantId = readInt(scanner);
        if (restaurantId < 0) {
            System.out.println("Invalid Restaurant ID.");
            return;
        }

        System.out.println("\nSelect Driver Assignment Strategy:");
        System.out.println("1. Nearest Driver (closest to restaurant)");
        System.out.println("2. Fastest Driver (lowest estimated arrival time)");
        System.out.print("Choice: ");

        int strategyChoice = readInt(scanner);
        if (strategyChoice == 2) {
            deliveryService.setStrategy(new FastestDriverStrategy());
            System.out.println("Using Fastest Driver Strategy.");
        } else {
            deliveryService.setStrategy(new NearestDriverStrategy());
            System.out.println("Using Nearest Driver Strategy.");
        }

        boolean assigned = deliveryService.assignDriverToOrder(orderId, restaurantId);
        if (assigned) {
            System.out.println("Driver assigned successfully!");
        } else {
            System.out.println("Driver assignment failed. No available driver found.");
        }
    }

    // ================= US3.3 & US3.4 - STATUS NOTIFICATIONS =================
    private static void handleOrderStatusChange(Scanner scanner, OrderStatusService orderStatusService) {
        System.out.print("Enter Order ID: ");
        int orderId = readInt(scanner);
        if (orderId <= 0) {
            System.out.println("Invalid Order ID.");
            return;
        }

        System.out.println("Select new status:");
        System.out.println("1. PREPARING");
        System.out.println("2. READY");
        System.out.println("3. OUT_FOR_DELIVERY");
        System.out.println("4. DELIVERED");
        System.out.print("Choice: ");

        int choice = readInt(scanner);
        String newStatus;
        switch (choice) {
            case 1:
                newStatus = "PREPARING";
                break;
            case 2:
                newStatus = "READY";
                break;
            case 3:
                newStatus = "OUT_FOR_DELIVERY";
                break;
            case 4:
                newStatus = "DELIVERED";
                break;
            default:
                System.out.println("Invalid status choice.");
                return;
        }

        OrderStatusService.StatusChangeResult result =
                orderStatusService.changeStatus(orderId, newStatus);

        switch (result) {
            case UPDATED:
                System.out.println("Order status updated successfully.");
                break;
            case NOT_FOUND:
                System.out.println("Order not found.");
                break;
            case NO_CHANGE:
                System.out.println("Order already has that status.");
                break;
            case INVALID_STATUS:
                System.out.println("Invalid order status.");
                break;
            default:
                System.out.println("Order status update failed.");
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