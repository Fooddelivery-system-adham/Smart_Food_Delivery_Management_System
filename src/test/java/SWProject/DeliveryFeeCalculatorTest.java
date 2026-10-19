package SWProject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DeliveryFeeCalculatorTest {

    @Test
    @DisplayName("US4.2: Should calculate correct fee for standard delivery distance")
    void calculate_standardDistance_returnsCorrectFee() {
        double distanceKm = 5.0; // Fee = 3.0 + (5.0 * 1.5) = 10.5
        double expectedFee = 10.5;

        double actualFee = DeliveryFeeCalculator.calculate(distanceKm);

        assertEquals(expectedFee, actualFee, 0.001);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0, -10.5})
    @DisplayName("US4.2: Should return 0.0 fee for zero or negative distances")
    void calculate_zeroOrNegativeDistance_returnsZero(double invalidDistance) {
        double expectedFee = 0.0;

        double actualFee = DeliveryFeeCalculator.calculate(invalidDistance);

     
        assertEquals(expectedFee, actualFee, 0.001);
    }
}