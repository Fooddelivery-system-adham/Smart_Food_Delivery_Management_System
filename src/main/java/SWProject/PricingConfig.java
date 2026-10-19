package SWProject;

/**
 * Shared pricing configuration controlled by the Manager.
 * This decides which pricing strategies are available to customers.
 */
public class PricingConfig {

    private static boolean peakHourEnabled = false;
    private static boolean promotionalEnabled = false;

    private PricingConfig() {
        // utility class
    }

    public static boolean isPeakHourEnabled() {
        return peakHourEnabled;
    }

    public static boolean isPromotionalEnabled() {
        return promotionalEnabled;
    }

    public static void setPeakHourEnabled(boolean enabled) {
        peakHourEnabled = enabled;
        // Peak-hour overrides promo mode
        if (enabled) {
            promotionalEnabled = false;
        }
    }

    public static void setPromotionalEnabled(boolean enabled) {
        // Promo cannot be enabled while peak-hour is active
        if (peakHourEnabled && enabled) {
            return;
        }
        promotionalEnabled = enabled;
    }

    public static String getCurrentModeLabel() {
        if (peakHourEnabled) {
            return "PEAK-HOUR MODE (forced for all customers)";
        }
        if (promotionalEnabled) {
            return "PROMOTIONAL MODE (Normal + Express + Promo available)";
        }
        return "NORMAL MODE (Normal + Express available)";
    }
}
