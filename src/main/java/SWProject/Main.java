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

        // Sprint 3 & 4 setup
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
            managerMenu(scanner, restaurants, menu, deliveryService, orderStatusService, orderService);
        } else {
            System.out.println("Unsupported role: " + user.getRole());
        }
    }

    // ================= CUSTOMER MENU =================
    private static void customerMenu(Scanner scanner, User user, RestaurantService restaurants,
                                     MenuItemService menu, OrderService orderService,
                                     NotificationService notificationService) {
        while (true) {
            System.out.println("\n=== CUSTOMER MENU ===");
            System.out.println("1. View Available Restaurants");
            System.out.println("2. Track My Active Orders");

            boolean hasPending = orderService.hasPendingOrder(user.getUserId());
            if (hasPending) {
                System.out.println("3. Cancel a Pending Order");
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
                List<Order> myOrders = orderService.getActiveOrdersByCustomer(user.getUserId());
                if (myOrders.isEmpty()) {
                    System.out.println("\nYou do not have any active orders right now.");
                } else {
                    System.out.println("\n--- MY ACTIVE ORDERS ---");
                    for (Order o : myOrders) {
                        System.out.println("Order #" + o.getOrderId()
                                + " | Status: [" + o.getStatus() + "]"
                                + " | Total: $" + o.getTotal()
                                + " | Address: " + o.getDeliveryAddress());
                    }
                }
            } else if (choice == 3) {
                if (!hasPending) {
                    System.out.println("No pending orders available to cancel.");
                    continue;
                }
                handleCancelOrder(scanner, user, orderService, notificationService);
            } else {
                System.out.println("Invalid choice.");
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

                // Palestinian Cities Address Validation
                String[] palestinianCities = {
                    "ramallah", "nablus", "hebron", "jenin", "jerusalem",
                    "bethlehem", "tulkarm", "qalqilya", "jericho", "gaza",
                    "tubas", "salfit"
                };

                String address = "";
                boolean validAddress = false;

                while (!validAddress) {
                    System.out.print("Enter Delivery Address (must start with a Palestinian city, e.g. Nablus, Ramallah): ");
                    address = scanner.nextLine().trim();

                    String lowerAddress = address.toLowerCase();
                    for (String city : palestinianCities) {
                        if (lowerAddress.startsWith(city)) {
                            validAddress = true;
                            break;
                        }
                    }

                    if (!validAddress) {
                        System.out.println("ERROR: We only deliver inside Palestine. Please start your address with a valid Palestinian city name.");
                    }
                }

                System.out.print("Enter Delivery Distance in km: ");
                double distance;
                try {
                    distance = Double.parseDouble(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid distance entered. Please enter a number.");
                    continue;
                }

                // US4.3: Dynamic Pricing Selection based on Manager Configuration
                System.out.println("\nSelect Pricing Strategy:");

                if (PricingConfig.isPeakHourEnabled()) {
                    System.out.println("1. Peak-Hour Pricing (+20% on food)   [AVAILABLE]");
                    System.out.println("2. Normal Pricing                    [Not available now]");
                    System.out.println("3. Express Delivery Pricing (+$7)    [Not available now]");
                    System.out.println("4. Promotional Pricing (10% off)     [Not available now]");
                    System.out.print("Choice: ");

                    int pricingChoice = readInt(scanner);
                    if (pricingChoice != 1) {
                        System.out.println("Only Peak-Hour pricing is active right now (Manager enabled Peak-Hour mode).");
                    }
                    orderService.setPricingStrategy(new PeakHourPricingStrategy());
                    System.out.println("Applied: Peak-Hour Pricing Strategy.");

                } else if (PricingConfig.isPromotionalEnabled()) {
                    System.out.println("1. Normal Pricing                    [AVAILABLE]");
                    System.out.println("2. Express Delivery Pricing (+$7)    [AVAILABLE]");
                    System.out.println("3. Promotional Pricing (10% off)     [AVAILABLE]");
                    System.out.println("4. Peak-Hour Pricing (+20% on food)  [Not available now]");
                    System.out.print("Choice: ");

                    int pricingChoice = readInt(scanner);
                    if (pricingChoice == 2) {
                        orderService.setPricingStrategy(new ExpressDeliveryPricingStrategy());
                        System.out.println("Applied: Express Delivery Pricing Strategy.");
                    } else if (pricingChoice == 3) {
                        orderService.setPricingStrategy(new PromotionalPricingStrategy());
                        System.out.println("Applied: Promotional Pricing Strategy.");
                    } else if (pricingChoice == 4) {
                        System.out.println("Peak-Hour mode is currently disabled by Manager.");
                        orderService.setPricingStrategy(new NormalPricingStrategy());
                        System.out.println("Applied: Normal Pricing Strategy.");
                    } else {
                        orderService.setPricingStrategy(new NormalPricingStrategy());
                        System.out.println("Applied: Normal Pricing Strategy.");
                    }

                } else {
                    System.out.println("1. Normal Pricing                    [AVAILABLE]");
                    System.out.println("2. Express Delivery Pricing (+$7)    [AVAILABLE]");
                    System.out.println("3. Peak-Hour Pricing (+20% on food)  [Not available now]");
                    System.out.println("4. Promotional Pricing (10% off)     [Not available now]");
                    System.out.print("Choice: ");

                    int pricingChoice = readInt(scanner);
                    if (pricingChoice == 2) {
                        orderService.setPricingStrategy(new ExpressDeliveryPricingStrategy());
                        System.out.println("Applied: Express Delivery Pricing Strategy.");
                    } else if (pricingChoice == 3 || pricingChoice == 4) {
                        System.out.println("This pricing option is not active now. Ask Manager to enable it.");
                        orderService.setPricingStrategy(new NormalPricingStrategy());
                        System.out.println("Applied: Normal Pricing Strategy.");
                    } else {
                        orderService.setPricingStrategy(new NormalPricingStrategy());
                        System.out.println("Applied: Normal Pricing Strategy.");
                    }
                }

                // Place the order
                Order order = orderService.placeOrder(
                        user.getUserId(),
                        r.getRestaurantId(),
                        address,
                        distance,
                        cart
                );

                if (order != null) {
                    // Detailed Receipt Output for US4.2 & US4.3
                    System.out.println("\n==========================================");
                    System.out.println("🎉 ORDER PLACED SUCCESSFULLY!");
                    System.out.println("Order ID:     #" + order.getOrderId());
                    System.out.println("Status:       " + order.getStatus());
                    System.out.println("Address:      " + order.getDeliveryAddress());
                    System.out.println("Distance:     " + order.getDeliveryDistanceKm() + " km");
                    System.out.println("------------------------------------------");
                    System.out.println("Food Subtotal: $" + order.getSubtotal());
                    System.out.println("Delivery Fee:  $" + order.getDeliveryFee());
                    System.out.println("Discount:      -$" + order.getDiscountAmount());
                    System.out.println("------------------------------------------");
                    System.out.println("FINAL TOTAL:   $" + order.getTotal());
                    System.out.println("==========================================");

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

    // ================= MANAGER MENU =================
    private static void managerMenu(Scanner scanner, RestaurantService restaurants,
                                    MenuItemService menu, DeliveryService deliveryService,
                                    OrderStatusService orderStatusService, OrderService orderService) {
        while (true) {
            System.out.println("\n=== MANAGER MENU ===");
            System.out.println("1. View Restaurants & Menus");
            System.out.println("2. Change Restaurant Status");
            System.out.println("3. Change Menu Item Status");
            System.out.println("4. Assign Driver to Order (Smart Selection)");
            System.out.println("5. Change Order Status (Lifecycle Transitions)");
            System.out.println("6. Configure Pricing Mode (Peak / Promo)");
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
                    handleAssignDriver(scanner, deliveryService, orderService);
                    break;
                case "5":
                    handleOrderStatusChange(scanner, orderStatusService, orderService);
                    break;
                case "6":
                    handlePricingConfig(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // ================= MANAGER PRICING CONTROL =================
    private static void handlePricingConfig(Scanner scanner) {
        while (true) {
            System.out.println("\n=== PRICING CONFIGURATION ===");
            System.out.println("Current Mode: " + PricingConfig.getCurrentModeLabel());
            System.out.println("1. Enable Peak-Hour Pricing (+20% on food)");
            System.out.println("2. Disable Peak-Hour Pricing");
            System.out.println("3. Enable Promotional Pricing (10% off food)");
            System.out.println("4. Disable Promotional Pricing");
            System.out.println("0. Back");
            System.out.print("Choice: ");

            int choice = readInt(scanner);
            if (choice == 0) {
                return;
            }

            if (choice == 1) {
                PricingConfig.setPeakHourEnabled(true);
                System.out.println("Peak-Hour mode ENABLED. Customers will now use Peak-Hour pricing.");
            } else if (choice == 2) {
                PricingConfig.setPeakHourEnabled(false);
                System.out.println("Peak-Hour mode DISABLED.");
            } else if (choice == 3) {
                if (PricingConfig.isPeakHourEnabled()) {
                    System.out.println("Cannot enable Promo while Peak-Hour is active. Disable Peak-Hour first.");
                } else {
                    PricingConfig.setPromotionalEnabled(true);
                    System.out.println("Promotional mode ENABLED. Customers can now select Promotional pricing.");
                }
            } else if (choice == 4) {
                PricingConfig.setPromotionalEnabled(false);
                System.out.println("Promotional mode DISABLED.");
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    // ================= ASSIGN DRIVER =================
    private static void handleAssignDriver(Scanner scanner, DeliveryService deliveryService, OrderService orderService) {
        List<Order> pendingOrders = orderService.getPendingOrders();
        if (pendingOrders.isEmpty()) {
            System.out.println("\nThere are no PENDING orders waiting for a driver.");
            return;
        }

        System.out.println("\n--- PENDING ORDERS WAITING FOR DRIVER ---");
        for (Order o : pendingOrders) {
            System.out.println("Order #" + o.getOrderId()
                    + " | Restaurant ID: " + o.getRestaurantId()
                    + " | Address: " + o.getDeliveryAddress());
        }

        System.out.print("\nEnter Order ID to assign driver (or 0 to cancel): ");
        int orderId = readInt(scanner);
        if (orderId <= 0) {
            return;
        }

        Order selectedOrder = pendingOrders.stream()
                .filter(o -> o.getOrderId() == orderId)
                .findFirst()
                .orElse(null);

        if (selectedOrder == null) {
            System.out.println("Invalid Order ID. Please choose an ID from the list above.");
            return;
        }

        int restaurantId = selectedOrder.getRestaurantId();

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

    // ================= ORDER STATUS CHANGE =================
    private static void handleOrderStatusChange(Scanner scanner, OrderStatusService orderStatusService, OrderService orderService) {
        List<Order> activeOrders = orderService.getActiveOrdersForManager();
        if (activeOrders.isEmpty()) {
            System.out.println("\nThere are no active orders to update.");
            return;
        }

        System.out.println("\n--- ACTIVE ORDERS ---");
        for (Order o : activeOrders) {
            System.out.println("Order #" + o.getOrderId() + " | Current Status: [" + o.getStatus() + "]");
        }

        System.out.print("\nEnter Order ID to update (or 0 to cancel): ");
        int orderId = readInt(scanner);
        if (orderId <= 0) {
            return;
        }

        System.out.println("\nSelect new status:");
        System.out.println("1. CONFIRMED");
        System.out.println("2. PREPARING");
        System.out.println("3. READY");
        System.out.println("4. OUT_FOR_DELIVERY");
        System.out.println("5. DELIVERED");
        System.out.print("Choice: ");

        int choice = readInt(scanner);
        String newStatus;
        switch (choice) {
            case 1: newStatus = "CONFIRMED"; break;
            case 2: newStatus = "PREPARING"; break;
            case 3: newStatus = "READY"; break;
            case 4: newStatus = "OUT_FOR_DELIVERY"; break;
            case 5: newStatus = "DELIVERED"; break;
            default:
                System.out.println("Invalid status choice.");
                return;
        }

        OrderStatusService.StatusChangeResult result = orderStatusService.changeStatus(orderId, newStatus);
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
            case INVALID_TRANSITION:
                System.out.println("❌ INVALID STATE TRANSITION: Cannot jump directly to this status!");
                break;
            case DELIVERED_LOCKED:
                System.out.println("🔒 ORDER LOCKED: Delivered or Cancelled orders cannot be modified!");
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