package SWProject;

public class PromotionalPricingStrategy implements PricingStrategy {
    private static final double DISCOUNT_RATE = 0.10; // 10% off food only

    @Override
    public double apply(double subtotal, double deliveryFee) {
        double discountedFood = subtotal * (1.0 - DISCOUNT_RATE);
        return discountedFood + deliveryFee;
    }

    @Override
    public String getName() {
        return "PROMOTIONAL";
    }
}