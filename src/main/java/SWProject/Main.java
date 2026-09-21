package SWProject;
import java.util.Scanner;
public class Main {

    public static void main(String[] args) {
        UserRepository userRepository = new UserRepository();
        AuthService authService = new AuthService(userRepository);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Food Delivery System ===");
            System.out.println("1. Log in");
            System.out.println("2. Exit");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                handleLogin(scanner, authService);
            } else if (choice.equals("2")) {
                System.out.println("Goodbye!");
                break;
            } else {
                System.out.println("Invalid choice! Please enter 1 or 2.");
            }
        }

        scanner.close();
    }

    private static void handleLogin(Scanner scanner, AuthService authService) {
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
                handleManagerMenu(scanner, user);
            }
        }
    }

    private static void handleCustomerMenu(Scanner scanner, User user) {
    }

    private static void handleManagerMenu(Scanner scanner, User user) {
    }
}