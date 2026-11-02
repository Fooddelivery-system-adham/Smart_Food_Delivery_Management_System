package SWProject;

public class AuthService {

    private final UserRepository userRepository;

    // We pass the repository here so this service doesn't need to know about the database directly
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String identifier, String password) {
        // Edge Case 1: Checking if inputs are completely empty
        if (identifier == null || identifier.trim().isEmpty()) {
            System.out.println("Validation Error: Username or Email cannot be empty.");
            return null;
        }
        if (password == null || password.trim().isEmpty()) {
            System.out.println("Validation Error: Password cannot be empty.");
            return null;
        }

        String trimmedId = identifier.trim();

        // Edge Case 2: Checking if the format is correct (is it a broken email or a bad username?)
        if (trimmedId.contains("@")) {
            if (!ValidationUtils.isValidEmail(trimmedId)) {
                System.out.println("Format Error: The email format you entered is invalid.");
                return null;
            }
        } else {
            if (!ValidationUtils.isValidUsername(trimmedId)) {
                System.out.println("Format Error: Username must be at least 3 characters and contain only letters, numbers, or underscore.");
                return null;
            }
        }

        // Edge Case 3: Checking password rules before we even bother asking the database
        if (!ValidationUtils.isValidPassword(password)) {
            System.out.println("Format Error: Password must be at least 6 characters and cannot contain spaces.");
            return null;
        }

        // Now we ask the database for the user
        User user = userRepository.findByUsernameOrEmail(trimmedId);

        // Edge Case 4: The username or email doesn't exist in the database
        if (user == null) {
            System.out.println("Auth Error: No account found with this username/email.");
            return null;
        }

        // Edge Case 5: The password from the user doesn't match the one from the database
        if (!user.getPassword().equals(password)) {
            System.out.println("Auth Error: Incorrect password.");
            return null;
        }

        return user; // If we reached here, everything is perfectly correct
    }
}