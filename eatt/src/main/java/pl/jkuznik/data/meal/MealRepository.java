package pl.jkuznik.data.meal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MealRepository
        extends
        JpaRepository<Meal, Long>,
        JpaSpecificationExecutor<Meal> {

}