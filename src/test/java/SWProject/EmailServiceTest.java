package SWProject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EmailServiceTest {

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailService();
    }

    @Test
    @DisplayName("Test null and empty recipient email handling")
    void testNullAndEmptyEmails() {
        assertFalse(emailService.sendOrderConfirmation(null, null));
        assertFalse(emailService.sendOrderConfirmation("   ", null));
        assertFalse(emailService.sendOrderCompletedEmail(null, 101));
        assertFalse(emailService.sendOrderCompletedEmail("", 101));
    }

    @Test
    @DisplayName("Test sendOrderConfirmation with valid order details")
    void testSendOrderConfirmationSuccess() {
        Order order = new StandardOrder(
                101, 1, 10, "CONFIRMED", "Ramallah St", 3.5, 30.0, 4.0, 2.0, 32.0, new ArrayList<>()
        );
        
        boolean result = emailService.sendOrderConfirmation("test@example.com", order);
        assertTrue(result, "Email sending should return true (either sent or fallback logged)");
    }

    @Test
    @DisplayName("Test sendOrderCompletedEmail")
    void testSendOrderCompletedEmail() {
        boolean result = emailService.sendOrderCompletedEmail("customer@example.com", 101);
        assertTrue(result);
    }
}