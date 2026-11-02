package SWProject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class OrderServiceEmailTest {

    private OrderRepository mockOrderRepo;
    private MenuItemRepository mockMenuRepo;
    private RestaurantRepository mockRestRepo;
    private EmailService mockEmailService;
    private OrderService orderService;

    @BeforeEach
    public void init() {
        mockOrderRepo = mock(OrderRepository.class);
        mockMenuRepo = mock(MenuItemRepository.class);
        mockRestRepo = mock(RestaurantRepository.class);
        mockEmailService = mock(EmailService.class);

        when(mockEmailService.sendOrderConfirmation(anyString(), any(Order.class))).thenReturn(true);
        when(mockEmailService.sendOrderCompletedEmail(anyString(), anyInt())).thenReturn(true);

        orderService = new OrderService(mockOrderRepo, mockMenuRepo, mockRestRepo, mockEmailService);
    }

    @Test
    @DisplayName("US-Email: Invoice email should be sent to customer's DB email after payment")
    void testPlaceOrderTriggersInvoiceEmail() {
        // 1. Arrange
        User customer = new User(1, "ahmad_close", "ahmad@test.com", "123", "CUSTOMER", 31.7810, 35.2250);
        Restaurant restaurant = new Restaurant(10, "Burger King", "OPEN", 15.0, 10.0, 31.7800, 35.2200);
        MenuItem item = new MenuItem(1, 10, "Whopper", 20.0, "AVAILABLE");

        when(mockRestRepo.findById(10)).thenReturn(restaurant);
        when(mockMenuRepo.findAvailableByRestaurantId(10)).thenReturn(List.of(item));
        when(mockOrderRepo.saveOrder(any(Order.class))).thenReturn(true);

        List<OrderService.CartItem> cart = List.of(new OrderService.CartItem(1, 1));

        // 2. Act: Place Order
        Order placedOrder = orderService.placeOrder(customer, 10, "Ramallah Main St", cart, "STANDARD", "ASAP");
        assertNotNull(placedOrder);

        // 3. Act: Simulate successful payment triggering email
        boolean emailSent = orderService.sendOrderInvoiceEmail(customer, placedOrder);

        // 4. Assert
        assertTrue(emailSent);
        verify(mockEmailService, times(1)).sendOrderConfirmation(eq("ahmad@test.com"), any(Order.class));
    }

    @Test
    @DisplayName("US-Email: Delivered status should trigger order completed email")
    void testOrderDeliveredTriggersEmail() {
        String customerEmail = "omar@test.com";
        int orderId = 105;

        boolean result = mockEmailService.sendOrderCompletedEmail(customerEmail, orderId);

        assertTrue(result);
        verify(mockEmailService, times(1)).sendOrderCompletedEmail("omar@test.com", 105);
    }
}