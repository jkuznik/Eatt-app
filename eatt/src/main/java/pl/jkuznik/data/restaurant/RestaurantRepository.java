package pl.jkuznik.data.restaurant;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pl.jkuznik.data.restaurant.Restaurant;

public interface RestaurantRepository
        extends
        JpaRepository<Restaurant, Long>,
        JpaSpecificationExecutor<Restaurant> {

}