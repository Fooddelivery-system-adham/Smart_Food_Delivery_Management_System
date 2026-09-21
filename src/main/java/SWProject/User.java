package SWProject;

import java.util.regex.Pattern;

public class User {
    private final int userId;
    private final String username;
    private final String email;
    private final String password;
    private final String role;

    public User(int userId, String username, String email, String password, String role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    public boolean isCustomer() {
        return "CUSTOMER".equalsIgnoreCase(this.role);
    }

    public boolean isManager() {
        return "MANAGER".equalsIgnoreCase(this.role);
    }
}
class ValidationUtils {

    // A standard way (Regex) to check if an email is written correctly
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    /**
     * Checks if the given text is a valid email format.
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Checks if the password is safe enough:
     * - Must be at least 6 characters long.
     * - Cannot contain spaces.
     */
    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        String trimmed = password.trim();
        return trimmed.length() >= 6 && !password.contains(" ");
    }

    /**
     * Checks if the username is valid (only letters, numbers, and underscore).
     * Also, it must be at least 3 characters long.
     */
    public static boolean isValidUsername(String username) {
        if (username == null) return false;
        String trimmed = username.trim();
        return trimmed.length() >= 3 && trimmed.matches("^[a-zA-Z0-9_]+$");
    }
}