package SWProject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DomainModelTest {

    @Test
    @DisplayName("User getters and role checks")
    void userModel_testGettersAndRoles() {
        User customer = new User(1, "ahmad", "ahmad@mail.com", "123456", "CUSTOMER");
        User manager = new User(2, "manager", "manager@mail.com", "123456", "MANAGER");

        assertEquals(1, customer.getUserId());
        assertEquals("ahmad", customer.getUsername());
        assertEquals("ahmad@mail.com", customer.getEmail());
        assertEquals("123456", customer.getPassword());
        assertEquals("CUSTOMER", customer.getRole());

        assertTrue(customer.isCustomer());
        assertFalse(customer.isManager());
        assertTrue(manager.isManager());
        assertFalse(manager.isCustomer());
    }

    @Test
    @DisplayName("Restaurant getters and status checks")
    void restaurantModel_testGetters() {
        Restaurant r = new Restaurant(1, "KFC", "OPEN", 20.0, 8.0);

        assertEquals(1, r.getRestaurantId());
        assertEquals("KFC", r.getName());
        assertEquals("OPEN", r.getStatus());
        assertEquals(20.0, r.getMinOrderValue(), 0.001);
        assertEquals(8.0, r.getMaxDeliveryRadiusKm(), 0.001);
        assertTrue(r.isOpen());
    }

    @Test
    @DisplayName("MenuItem getters and availability checks")
    void menuItemModel_testGetters() {
        MenuItem item = new MenuItem(10, 1, "Zinger", 25.0, "AVAILABLE");

        assertEquals(10, item.getItemId());
        assertEquals(1, item.getRestaurantId());
        assertEquals("Zinger", item.getName());
        assertEquals(25.0, item.getPrice(), 0.001);
        assertEquals("AVAILABLE", item.getStatus());
        assertTrue(item.isAvailable());
    }

    @Test
    @DisplayName("Order and OrderItem getters and setters")
    void orderModel_testGettersAndSetters() {
        OrderItem item = new OrderItem(1, "Whopper", 2, 25.0);
        assertEquals(1, item.getItemId());
        assertEquals("Whopper", item.getItemName());
        assertEquals(2, item.getQuantity());
        assertEquals(25.0, item.getUnitPrice(), 0.001);
        assertEquals(50.0, item.getSubtotal(), 0.001);

        Order order = new Order(100, 1, 2, "STANDARD", "PENDING", "Nablus", 5.0, 50.0, 10.5, 0.0, 60.5, new ArrayList<>());
        order.setOrderId(200);
        order.setDriverId(5);
        order.setStatus("CONFIRMED");

        assertEquals(200, order.getOrderId());
        assertEquals(1, order.getCustomerId());
        assertEquals(2, order.getRestaurantId());
        assertEquals(Integer.valueOf(5), order.getDriverId());
        assertEquals("STANDARD", order.getOrderType());
        assertEquals("CONFIRMED", order.getStatus());
        assertEquals("Nablus", order.getDeliveryAddress());
        assertEquals(5.0, order.getDeliveryDistanceKm(), 0.001);
        assertEquals(50.0, order.getSubtotal(), 0.001);
        assertEquals(10.5, order.getDeliveryFee(), 0.001);
        assertEquals(0.0, order.getDiscountAmount(), 0.001);
        assertEquals(60.5, order.getTotal(), 0.001);
        assertNotNull(order.getItems());
    }

    @Test
    @DisplayName("Driver model getters and setters")
    void driverModel_testGettersAndSetters() {
        Driver driver = new Driver(1, "Sami", "MOTORCYCLE", "AVAILABLE", 32.22, 35.25, 40.0);
        driver.setStatus("BUSY");

        assertEquals(1, driver.getDriverId());
        assertEquals("Sami", driver.getName());
        assertEquals("MOTORCYCLE", driver.getDriverType());
        assertEquals("BUSY", driver.getStatus());
        assertEquals(32.22, driver.getLatitude(), 0.001);
        assertEquals(35.25, driver.getLongitude(), 0.001);
        assertEquals(40.0, driver.getAverageSpeedKmh(), 0.001);
        assertFalse(driver.isAvailable());
    }
}