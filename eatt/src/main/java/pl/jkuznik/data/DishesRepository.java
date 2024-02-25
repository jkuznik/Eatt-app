package pl.jkuznik.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DishesRepository
        extends
        JpaRepository<Dishes, Long>,
        JpaSpecificationExecutor<Dishes> {

}