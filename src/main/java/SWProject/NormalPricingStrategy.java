package SWProject;

public class NormalPricingStrategy implements PricingStrategy {
    @Override
    public double apply(double subtotal, double deliveryFee) {
        return subtotal + deliveryFee;
    }

    @Override
    public String getName() {
        return "NORMAL";
    }
}
