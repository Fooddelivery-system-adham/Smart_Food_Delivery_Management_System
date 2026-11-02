package SWProject;

public class User {
    private final int userId;
    private final String username;
    private final String email;
    private final String password;
    private final String role;
    private final double latitude;
    private final double longitude;

    public User(int userId, String username, String email, String password, String role, double latitude, double longitude) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public User(int userId, String username, String email, String password, String role) {
        this(userId, username, email, password, role, 0.0, 0.0);
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    public boolean isCustomer() { return "CUSTOMER".equalsIgnoreCase(role); }
    public boolean isManager() { return "MANAGER".equalsIgnoreCase(role); }
}