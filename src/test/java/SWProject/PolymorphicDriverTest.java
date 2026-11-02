package SWProject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PolymorphicDriverTest {

    @Test
    @DisplayName("US5.3 & US5.4: Test driver distance limits according to vehicle type")
    void testDriverDistanceLimits() {
        Driver bike = new BicycleDriver(1, "Ahmad", "AVAILABLE", 31.95, 35.91, 15.0);
        Driver moto = new MotorcycleDriver(2, "Omar", "AVAILABLE", 31.95, 35.91, 40.0);
        Driver ev = new ElectricVehicleDriver(3, "Khaled", "AVAILABLE", 31.95, 35.91, 35.0);
        Driver car = new CarDriver(4, "Zaid", "AVAILABLE", 31.95, 35.91, 50.0);

        // Bicycle <= 5.0 km
        assertTrue(bike.canDeliver(4.5));
        assertFalse(bike.canDeliver(5.5));

        // Motorcycle <= 15.0 km
        assertTrue(moto.canDeliver(12.0));
        assertFalse(moto.canDeliver(16.0));

        // EV <= 30.0 km
        assertTrue(ev.canDeliver(28.0));
        assertFalse(ev.canDeliver(31.0));

        // Car <= 50.0 km
        assertTrue(car.canDeliver(45.0));
        assertFalse(car.canDeliver(55.0));
    }
}