package SWProject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    private PaymentRepository paymentRepository;
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentRepository = mock(PaymentRepository.class);
        paymentService = new PaymentService(paymentRepository);
    }

    @Test
    @DisplayName("US4.4: Cash payment succeeds when cash is sufficient")
    void testProcessCashPaymentSuccess() {
        int orderId = 101;
        double totalAmount = 25.0;
        double cashProvided = 30.0;

        // Important: Mockito returns false by default for boolean methods
        when(paymentRepository.savePayment(orderId, totalAmount, "CASH", "COMPLETED"))
                .thenReturn(true);

        boolean isPaid = paymentService.processCashPayment(orderId, totalAmount, cashProvided);

        assertTrue(isPaid);
        verify(paymentRepository, times(1))
                .savePayment(orderId, totalAmount, "CASH", "COMPLETED");
    }

    @Test
    @DisplayName("US4.4: Cash payment fails when provided cash is less than total amount")
    void testProcessCashPaymentInsufficientFunds() {
        int orderId = 101;
        double totalAmount = 25.0;
        double cashProvided = 20.0;

        boolean isPaid = paymentService.processCashPayment(orderId, totalAmount, cashProvided);

        assertFalse(isPaid);
        verify(paymentRepository, never())
                .savePayment(anyInt(), anyDouble(), anyString(), anyString());
    }
}