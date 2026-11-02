package SWProject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GeoUtilsTest {

    @Test
    @DisplayName("Test GPS distance calculation between two points")
    void testCalculateDistanceKm() {
        // Amman Downtown (31.9515, 35.9239) to Abdali (31.9632, 35.9092)
        double distance = GeoUtils.calculateDistanceKm(31.9515, 35.9239, 31.9632, 35.9092);

        assertTrue(distance > 1.0 && distance < 2.5, "Distance should be around 1.8 km");
    }

    @Test
    @DisplayName("Test zero distance for identical coordinates")
    void testZeroDistance() {
        double distance = GeoUtils.calculateDistanceKm(31.95, 35.91, 31.95, 35.91);
        assertEquals(0.0, distance, 0.001);
    }
}