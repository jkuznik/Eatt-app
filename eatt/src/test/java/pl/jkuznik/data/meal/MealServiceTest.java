package pl.jkuznik.data.meal;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MealServiceTest {
    @Mock
    private MealRepository mealRepository;
    @InjectMocks
    private MealService mealService;
    @Test
    void getRecordById(){
        Meal meal = new Meal();
        BDDMockito.given(mealRepository.findById(5L)).willReturn(Optional.of(meal));
        Assertions.assertThat(mealService.get(5L)).isEqualTo(Optional.of(meal));
    }
    @Test
    void updateOneRecord(){
        Meal meal = new Meal();
//        BDDMockito.given(mealRepository.save(meal)).getMock();
        BDDMockito.given(mealRepository.save(meal)).willReturn(meal);

        mealService.update(meal);
        Mockito.verify(mealRepository, Mockito.times(1)).save(meal);
    }
    @Test
    void updateListOfRecords(){
        Meal meal1 = new Meal();
        Meal meal2 = new Meal();
        Meal meal3 = new Meal();
        List<Meal> meals = List.of(meal1, meal2, meal3);

        BDDMockito.given(mealRepository.saveAll(meals)).willReturn(meals);

        mealService.updateAll(meals);
        mealService.updateAll(meals);
        mealService.updateAll(meals);

        Mockito.verify(mealRepository, Mockito.times(3)).saveAll(meals);
    }

    @Test
    void deleteRecordById(){
        BDDMockito.doNothing().when(mealRepository).deleteById(5L);
        mealService.delete(5L);
        Mockito.verify(mealRepository, Mockito.times(1)).deleteById(5L);
    }
    @Test
    void getMealsListWhenDataBaseIsEmpty(){
        List<Meal> meals = new ArrayList<>();
        BDDMockito.given(mealRepository.findAll()).willReturn(meals);

        Assertions.assertThat(mealService.list().isEmpty());
    }

    @Test
    void countDataBaseMealsListSize() {
        BDDMockito.given(mealRepository.count()).willReturn(10L);
        Assertions.assertThat(mealService.count()).isEqualTo(10);
    }
}
