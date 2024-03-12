package pl.jkuznik.data.restaurant;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RestaurantTest {
    @Test
    void getName(){
        Restaurant restaurant = new Restaurant();
        restaurant.setName("test1");

        Assertions.assertEquals("test1", restaurant.getName());
    }
    @Test
    void isActive(){
        Restaurant restaurant = new Restaurant();
        restaurant.setActive(true);

        Assertions.assertTrue(restaurant.isActive());
    }
    @Test
    void isEnabled(){
        Restaurant restaurant = new Restaurant();
        restaurant.setEnabled(true);

        Assertions.assertTrue(restaurant.isEnabled());
    }
}
