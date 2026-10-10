package SWProject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PricingConfigTest {

    @BeforeEach
    void resetConfig() {
        PricingConfig.setPeakHourEnabled(false);
        PricingConfig.setPromotionalEnabled(false);
    }

    @Test
    @DisplayName("US4.3: Toggle Peak-Hour mode")
    void togglePeakHourMode() {
        PricingConfig.setPeakHourEnabled(true);
        assertTrue(PricingConfig.isPeakHourEnabled());
        assertFalse(PricingConfig.isPromotionalEnabled());
        assertTrue(PricingConfig.getCurrentModeLabel().contains("PEAK-HOUR"));
    }

    @Test
    @DisplayName("US4.3: Toggle Promotional mode")
    void togglePromotionalMode() {
        PricingConfig.setPromotionalEnabled(true);
        assertTrue(PricingConfig.isPromotionalEnabled());
        assertTrue(PricingConfig.getCurrentModeLabel().contains("PROMOTIONAL"));
    }

    @Test
    @DisplayName("US4.3: Peak-Hour overrides Promotional mode")
    void peakHourOverridesPromotionalMode() {
        PricingConfig.setPromotionalEnabled(true);
        PricingConfig.setPeakHourEnabled(true);

        assertTrue(PricingConfig.isPeakHourEnabled());
        assertFalse(PricingConfig.isPromotionalEnabled());
    }
}
