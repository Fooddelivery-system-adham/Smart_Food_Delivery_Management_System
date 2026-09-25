package SWProject;

import java.util.List;

public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public boolean updateRestaurantStatus(int restaurantId, String newStatus) {
        if (newStatus == null) return false;
        
        String cleanStatus = newStatus.trim().toUpperCase();
        if (!cleanStatus.equals("OPEN") && !cleanStatus.equals("CLOSED")) {
            System.out.println("Invalid status! Must be OPEN or CLOSED.");
            return false;
        }

        return restaurantRepository.updateStatus(restaurantId, cleanStatus);
    }
}