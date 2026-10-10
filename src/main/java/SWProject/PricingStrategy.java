package SWProject;

public interface PricingStrategy {
    // Returns the final amount after applying this pricing strategy
    double apply(double subtotal, double deliveryFee);
    String getName();
}