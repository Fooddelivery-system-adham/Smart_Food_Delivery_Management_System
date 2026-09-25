package SWProject;

import java.util.List;

public class RestaurantService {
    private final RestaurantRepository repository;

    public RestaurantService(RestaurantRepository repository) {
        this.repository = repository;
    }

    public List<Restaurant> getOpenRestaurants() { return repository.getOpenRestaurants(); }
    public List<Restaurant> getAllRestaurants() { return repository.findAll(); }

    public boolean updateRestaurantStatus(int restaurantId, String status) {
        if (status == null) return false;
        String clean = status.trim().toUpperCase();
        if (!clean.equals("OPEN") && !clean.equals("CLOSED")) {
            System.out.println("Invalid status! Must be OPEN or CLOSED.");
            return false;
        }
        return repository.updateStatus(restaurantId, clean);
    }
}
