package SWProject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MenuItemServiceTest {

    private MenuItemService menuItemService;
    private FakeMenuItemRepository fakeRepository;

    private static class FakeMenuItemRepository extends MenuItemRepository {
        @Override
        public List<MenuItem> findAvailableByRestaurantId(int restaurantId) {
            List<MenuItem> list = new ArrayList<>();
            list.add(new MenuItem(1, restaurantId, "Burger", 25.0, "AVAILABLE"));
            return list;
        }

        @Override
        public List<MenuItem> findByRestaurantId(int restaurantId) {
            List<MenuItem> list = new ArrayList<>();
            list.add(new MenuItem(1, restaurantId, "Burger", 25.0, "AVAILABLE"));
            list.add(new MenuItem(2, restaurantId, "Fries", 8.0, "UNAVAILABLE"));
            return list;
        }

        @Override
        public boolean updateStatus(int itemId, String newStatus) {
            return itemId == 1;
        }
    }

    @BeforeEach
    void setUp() {
        fakeRepository = new FakeMenuItemRepository();
        menuItemService = new MenuItemService(fakeRepository);
    }

    @Test
    @DisplayName("US1.3: Get available menu items returns available ones only")
    void getAvailableMenuItems_returnsAvailable() {
        List<MenuItem> items = menuItemService.getAvailableMenuItems(1);
        assertEquals(1, items.size());
        assertTrue(items.get(0).isAvailable());
    }

    @Test
    @DisplayName("US1.3: Update menu item status validation")
    void updateMenuItemStatus_validations() {
        assertTrue(menuItemService.updateMenuItemStatus(1, "UNAVAILABLE"));
        assertFalse(menuItemService.updateMenuItemStatus(1, "INVALID"));
        assertFalse(menuItemService.updateMenuItemStatus(1, null));
    }
}
