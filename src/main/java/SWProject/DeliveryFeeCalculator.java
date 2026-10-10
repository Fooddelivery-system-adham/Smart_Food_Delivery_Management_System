package SWProject;

public class DeliveryFeeCalculator {
    private static final double BASE_FEE = 3.0;
    private static final double RATE_PER_KM = 1.5;

    // US4.2: delivery fee based on distance
    public static double calculate(double distanceKm) {
        if (distanceKm <= 0) {
            return 0.0;
        }
        return BASE_FEE + (distanceKm * RATE_PER_KM);
    }
}