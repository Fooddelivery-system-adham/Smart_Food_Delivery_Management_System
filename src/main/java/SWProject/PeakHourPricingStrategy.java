package SWProject;

public class PeakHourPricingStrategy implements PricingStrategy {
    private static final double PEAK_MULTIPLIER = 1.20; // +20%

    @Override
    public double apply(double subtotal, double deliveryFee) {
        return (subtotal * PEAK_MULTIPLIER) + deliveryFee;
    }

    @Override
    public String getName() {
        return "PEAK_HOUR";
    }
}
