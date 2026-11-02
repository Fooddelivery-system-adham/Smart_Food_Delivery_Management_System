package SWProject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PricingStrategyTest {

    @Test
    @DisplayName("US4.3: Normal Pricing should sum food subtotal and delivery fee")
    void normalPricing_appliesCorrectTotal() {
        PricingStrategy strategy = new NormalPricingStrategy();
        double subtotal = 50.0;
        double deliveryFee = 10.0;
        double expectedTotal = 60.0;

        double actualTotal = strategy.apply(subtotal, deliveryFee);

        assertEquals(expectedTotal, actualTotal, 0.001);
        assertEquals("NORMAL", strategy.getName());
    }

    @Test
    @DisplayName("US4.3: Peak-Hour Pricing should add 20% surcharge on food subtotal")
    void peakHourPricing_appliesSurchargeOnFood() {
        PricingStrategy strategy = new PeakHourPricingStrategy();
        double subtotal = 50.0; // 50 * 1.20 = 60
        double deliveryFee = 10.0;
        double expectedTotal = 70.0; // 60 + 10 = 70

        double actualTotal = strategy.apply(subtotal, deliveryFee);

        assertEquals(expectedTotal, actualTotal, 0.001);
        assertEquals("PEAK_HOUR", strategy.getName());
    }

    @Test
    @DisplayName("US4.3: Express Delivery Pricing should add fixed $7 express surcharge")
    void expressDeliveryPricing_addsExpressFee() {
        PricingStrategy strategy = new ExpressDeliveryPricingStrategy();
        double subtotal = 50.0;
        double deliveryFee = 10.0;
        double expectedTotal = 67.0; // 50 + 10 + 7 = 67

        double actualTotal = strategy.apply(subtotal, deliveryFee);

        assertEquals(expectedTotal, actualTotal, 0.001);
        assertEquals("EXPRESS", strategy.getName());
    }

    @Test
    @DisplayName("US4.3: Promotional Pricing should apply 10% discount on food subtotal")
    void promotionalPricing_appliesDiscountOnFood() {
        PricingStrategy strategy = new PromotionalPricingStrategy();
        double subtotal = 50.0; // 50 * 0.90 = 45
        double deliveryFee = 10.0;
        double expectedTotal = 55.0; // 45 + 10 = 55

        double actualTotal = strategy.apply(subtotal, deliveryFee);

        assertEquals(expectedTotal, actualTotal, 0.001);
        assertEquals("PROMOTIONAL", strategy.getName());
    }
}