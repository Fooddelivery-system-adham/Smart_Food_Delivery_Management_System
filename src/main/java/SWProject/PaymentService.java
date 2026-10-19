package SWProject;

public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public boolean processCashPayment(int orderId, double totalAmount, double cashProvided) {
        if (cashProvided < totalAmount) {
            System.out.println("❌ Payment Rejected: Insufficient cash provided. You need at least $" + totalAmount);
            return false;
        }
        
        boolean saved = paymentRepository.savePayment(orderId, totalAmount, "CASH", "COMPLETED");
        if (saved) {
            double change = cashProvided - totalAmount;
            System.out.println("✅ Payment Accepted (CASH). Change to return: $" + String.format("%.2f", change));
            return true;
        }
        return false;
    }
}