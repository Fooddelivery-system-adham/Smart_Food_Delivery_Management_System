package SWProject;

public class ExpressDeliveryPricingStrategy implements PricingStrategy {
    private static final double EXPRESS_FEE = 7.0;

    @Override
    public double apply(double subtotal, double deliveryFee) {
        return subtotal + deliveryFee + EXPRESS_FEE;
    }

    @Override
    public String getName() {
        return "EXPRESS";
    }
}
