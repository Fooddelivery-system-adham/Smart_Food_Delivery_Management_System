package SWProject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private AuthService authService;
    private FakeUserRepository fakeUserRepository;

    private static class FakeUserRepository extends UserRepository {
        @Override
        public User findByUsernameOrEmail(String identifier) {
            if ("adhamhalemi@gmail.com".equalsIgnoreCase(identifier) || "adham".equalsIgnoreCase(identifier)) {
                return new User(1, "adham", "adhamhalemi@gmail.com", "123456", "CUSTOMER");
            }
            return null;
        }
    }

    @BeforeEach
    void setUp() {
        fakeUserRepository = new FakeUserRepository();
        authService = new AuthService(fakeUserRepository);
    }

    @Test
    @DisplayName("US1.1: Login with valid email and password succeeds")
    void login_validEmailCredentials_returnsUser() {
        // Arrange & Act
        User user = authService.login("adhamhalemi@gmail.com", "123456");

        // Assert
        assertNotNull(user);
        assertEquals("adham", user.getUsername());
    }

    @Test
    @DisplayName("US1.1: Login with valid username succeeds")
    void login_validUsernameCredentials_returnsUser() {
        // Arrange & Act
        User user = authService.login("adham", "123456");

        // Assert
        assertNotNull(user);
    }

    @Test
    @DisplayName("US1.1: Empty username/email or password is rejected")
    void login_emptyCredentials_returnsNull() {
        assertNull(authService.login("", "123456"));
        assertNull(authService.login("adham", ""));
        assertNull(authService.login(null, "123456"));
    }

    @Test
    @DisplayName("US1.1: Invalid email format is rejected")
    void login_invalidEmailFormat_returnsNull() {
        assertNull(authService.login("invalid-email@", "123456"));
    }

    @Test
    @DisplayName("US1.1: Incorrect password returns null")
    void login_wrongPassword_returnsNull() {
        assertNull(authService.login("adham", "wrong_pass"));
    }

    @Test
    @DisplayName("US1.1: Non-existing user returns null")
    void login_unknownUser_returnsNull() {
        assertNull(authService.login("unknown_user", "123456"));
    }
}