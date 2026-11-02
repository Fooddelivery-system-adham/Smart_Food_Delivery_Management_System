package SWProject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class PolymorphicOrderTest {

    @Test
    @DisplayName("US5.1: Express Order should add extra $5.00 fee")
    void testExpressOrderRules() {
        ExpressOrder expressOrder = new ExpressOrder(
                1,
                10,
                100,
                "PENDING",
                "Amman St",
                4.0,
                20.0,
                3.0,
                0.0,
                23.0,
                new ArrayList<>()
        );

        expressOrder.applyTypeRules();

        assertEquals(5.00, expressOrder.getAdditionalTypeFee());
        assertEquals(28.00, expressOrder.getTotal(), 0.001);
        assertEquals("ASAP (PRIORITY)", expressOrder.getScheduledTime());
    }

    @Test
    @DisplayName("US5.2: Scheduled Order should set custom time without extra fee")
    void testScheduledOrderRules() {
    }
}