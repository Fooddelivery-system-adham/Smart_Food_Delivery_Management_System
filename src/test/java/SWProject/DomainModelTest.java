package SWProject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
    @DisplayName("Order model getters, setters and polymorphism")
    void orderModel_testGettersAndSetters() {
        OrderItem item = new OrderItem(1, "Whopper", 2, 25.0);
        assertEquals(1, item.getItemId());
        assertEquals("Whopper", item.getItemName());
        assertEquals(2, item.getQuantity());
        assertEquals(25.0, item.getUnitPrice(), 0.001);
        assertEquals(50.0, item.getSubtotal(), 0.001);

        List<OrderItem> items = new ArrayList<>();
        items.add(item);
        // Use StandardOrder (concrete subclass) instead of abstract Order
        Order order = new StandardOrder(
                100, 1, 2, "PENDING", "Nablus", 5.0,
                50.0, 10.5, 0.0, 60.5, items
        );
        order.applyTypeRules();

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
        assertEquals(60.5, order.getTotal(), 0.001); // no extra fee for STANDARD
        assertNotNull(order.getItems());

        // Extra polymorphism checks (US5.1 / US5.2)
        ExpressOrder express = new ExpressOrder(
                101, 1, 2, "PENDING", "Ramallah", 3.0,
                40.0, 5.0, 0.0, 45.0, items
        );
        express.applyTypeRules();
        assertEquals("EXPRESS", express.getOrderType());
        assertEquals(50.0, express.getTotal(), 0.001); // 45 + 5 express fee

        ScheduledOrder scheduled = new ScheduledOrder(
                102, 1, 2, "PENDING", "Hebron", 4.0,
                30.0, 6.0, 0.0, 36.0, items, "18:30"
        );
        scheduled.applyTypeRules();
        assertEquals("SCHEDULED", scheduled.getOrderType());
        assertEquals("18:30", scheduled.getScheduledTime());
        assertEquals(36.0, scheduled.getTotal(), 0.001); // no extra fee
    }

    @Test
    @DisplayName("Driver model getters, setters and polymorphism rules")
    void driverModel_testGettersAndSetters() {
        Driver driver = new MotorcycleDriver(1, "Sami", "AVAILABLE", 32.22, 35.25, 40.0);
        driver.setStatus("BUSY");

        assertEquals(1, driver.getDriverId());
        assertEquals("Sami", driver.getName());
        assertEquals("MOTORCYCLE", driver.getDriverType());
        assertEquals("BUSY", driver.getStatus());
        assertEquals(32.22, driver.getLatitude(), 0.001);
        assertEquals(35.25, driver.getLongitude(), 0.001);
        assertEquals(40.0, driver.getAverageSpeedKmh(), 0.001);
        assertFalse(driver.isAvailable());

        assertTrue(driver.canDeliver(10.0)); 
        assertFalse(driver.canDeliver(20.0));
    }
}