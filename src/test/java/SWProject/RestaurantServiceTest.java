package SWProject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantServiceTest {

    private RestaurantService restaurantService;
    private FakeRestaurantRepository fakeRepository;

    private static class FakeRestaurantRepository extends RestaurantRepository {
        @Override
        public List<Restaurant> findOpenRestaurants() {
            List<Restaurant> list = new ArrayList<>();
            list.add(new Restaurant(1, "Burger King", "OPEN", 15.0, 10.0));
            return list;
        }

        @Override
        public List<Restaurant> findAll() {
            List<Restaurant> list = new ArrayList<>();
            list.add(new Restaurant(1, "Burger King", "OPEN", 15.0, 10.0));
            list.add(new Restaurant(2, "Pizza Hut", "CLOSED", 20.0, 8.0));
            return list;
        }

        @Override
        public boolean updateStatus(int restaurantId, String newStatus) {
            return restaurantId == 1;
        }
    }

    @BeforeEach
    void setUp() {
        fakeRepository = new FakeRestaurantRepository();
        restaurantService = new RestaurantService(fakeRepository);
    }

    @Test
    @DisplayName("US1.2: Get open restaurants returns open ones only")
    void getOpenRestaurants_returnsOpenList() {
        List<Restaurant> list = restaurantService.getOpenRestaurants();
        assertEquals(1, list.size());
        assertTrue(list.get(0).isOpen());
    }

    @Test
    @DisplayName("US1.2: Get all restaurants returns both open and closed")
    void getAllRestaurants_returnsAll() {
        List<Restaurant> list = restaurantService.getAllRestaurants();
        assertEquals(2, list.size());
    }

    @Test
    @DisplayName("US1.2: Update restaurant status with valid input succeeds")
    void updateStatus_validInput_returnsTrue() {
        assertTrue(restaurantService.updateRestaurantStatus(1, "CLOSED"));
        assertTrue(restaurantService.updateRestaurantStatus(1, "open"));
    }

    @Test
    @DisplayName("US1.2: Update restaurant status with invalid input fails")
    void updateStatus_invalidInput_returnsFalse() {
        assertFalse(restaurantService.updateRestaurantStatus(1, "INVALID_STATUS"));
        assertFalse(restaurantService.updateRestaurantStatus(1, null));
    }
}
