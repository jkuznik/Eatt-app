package pl.jkuznik.data.restaurant;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.jkuznik.data.restaurant.Restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {
    
    @Mock
    private RestaurantRepository restaurantRepository;
    
    @InjectMocks
    private RestaurantService restaurantService;

    @Test
    void getRecordById(){
        Restaurant restaurant = new Restaurant();
        BDDMockito.given(restaurantRepository.findById(5L)).willReturn(Optional.of(restaurant));
        Assertions.assertThat(restaurantService.get(5L)).isEqualTo(Optional.of(restaurant));
    }
    @Test
    void updateOneRecord(){
        Restaurant restaurant = new Restaurant();
//        BDDMockito.given(restaurantRepository.save(restaurant)).getMock();
        BDDMockito.given(restaurantRepository.save(restaurant)).willReturn(restaurant);

        restaurantService.update(restaurant);
        Mockito.verify(restaurantRepository, Mockito.times(1)).save(restaurant);
    }
    @Test
    void updateListOfRecords(){
        Restaurant restaurant1 = new Restaurant();
        Restaurant restaurant2 = new Restaurant();
        Restaurant restaurant3 = new Restaurant();
        List<Restaurant> restaurants = List.of(restaurant1, restaurant2, restaurant3);

        BDDMockito.given(restaurantRepository.saveAll(restaurants)).willReturn(restaurants);

        restaurantService.updateAll(restaurants);
        restaurantService.updateAll(restaurants);
        restaurantService.updateAll(restaurants);

        Mockito.verify(restaurantRepository, Mockito.times(3)).saveAll(restaurants);
    }

    @Test
    void deleteRecordById(){
        BDDMockito.doNothing().when(restaurantRepository).deleteById(5L);
        restaurantService.delete(5L);
        Mockito.verify(restaurantRepository, Mockito.times(1)).deleteById(5L);
    }
    @Test
    void getRestaurantsListWhenDataBaseIsEmpty(){
        List<Restaurant> restaurants = new ArrayList<>();
        BDDMockito.given(restaurantRepository.findAll()).willReturn(restaurants);

        Assertions.assertThat(restaurantService.list().isEmpty());
    }

    @Test
    void countDataBaseRestaurantsListSize() {
        BDDMockito.given(restaurantRepository.count()).willReturn(10L);
        Assertions.assertThat(restaurantService.count()).isEqualTo(10);
    }
}
