package SWProject;

import java.util.List;

public class MenuItemService {

    private final MenuItemRepository menuItemRepository;

    public MenuItemService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuItem> getMenuItemsByRestaurant(int restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId);
    }

    public boolean updateMenuItemStatus(int itemId, String newStatus) {
        if (newStatus == null) return false;

        String clean = newStatus.trim().toUpperCase();
        if (!clean.equals("AVAILABLE") && !clean.equals("UNAVAILABLE")) {
            System.out.println("Invalid status! Use AVAILABLE or UNAVAILABLE.");
            return false;
        }

        return menuItemRepository.updateStatus(itemId, clean);
    }
}