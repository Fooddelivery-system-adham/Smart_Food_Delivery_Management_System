package SWProject;

import java.util.List;

public class MenuItemService {
    private final MenuItemRepository repository;

    public MenuItemService(MenuItemRepository repository) {
        this.repository = repository;
    }

    public List<MenuItem> getMenuItemsByRestaurant(int restaurantId) {
        return repository.findByRestaurantId(restaurantId);
    }

    public List<MenuItem> getAvailableMenuItems(int restaurantId) {
        return repository.findAvailableByRestaurantId(restaurantId);
    }

    public boolean updateMenuItemStatus(int itemId, String status) {
        if (status == null) return false;
        String clean = status.trim().toUpperCase();
        if (!clean.equals("AVAILABLE") && !clean.equals("UNAVAILABLE")) {
            System.out.println("Invalid status! Use AVAILABLE or UNAVAILABLE.");
            return false;
        }
        return repository.updateStatus(itemId, clean);
    }
}
