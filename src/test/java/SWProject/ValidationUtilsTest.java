package SWProject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationUtilsTest {

    @Test
    @DisplayName("Valid email formats should pass validation")
    void isValidEmail_validEmails_returnsTrue() {
        assertTrue(ValidationUtils.isValidEmail("customer@mail.com"));
        assertTrue(ValidationUtils.isValidEmail("user.name@domain.co.uk"));
    }

    @Test
    @DisplayName("Invalid email formats should fail validation")
    void isValidEmail_invalidEmails_returnsFalse() {
        assertFalse(ValidationUtils.isValidEmail("invalid-email"));
        assertFalse(ValidationUtils.isValidEmail("@domain.com"));
        assertFalse(ValidationUtils.isValidEmail(null));
    }

    @Test
    @DisplayName("Password must be at least 6 characters without spaces")
    void isValidPassword_validationRules() {
        assertTrue(ValidationUtils.isValidPassword("123456"));
        assertFalse(ValidationUtils.isValidPassword("12345")); // too short
        assertFalse(ValidationUtils.isValidPassword("123 456")); // contains space
        assertFalse(ValidationUtils.isValidPassword(null));
    }

    @Test
    @DisplayName("Username must be alphanumeric and at least 3 characters")
    void isValidUsername_validationRules() {
        assertTrue(ValidationUtils.isValidUsername("adham_halemi"));
        assertFalse(ValidationUtils.isValidUsername("ad")); // too short
        assertFalse(ValidationUtils.isValidUsername("user@name")); // special char @ not allowed
        assertFalse(ValidationUtils.isValidUsername(null));
    }
}