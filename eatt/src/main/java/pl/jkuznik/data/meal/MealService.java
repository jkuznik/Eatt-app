package pl.jkuznik.data.meal;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.jkuznik.data.meal.Meal;
import pl.jkuznik.data.meal.MealRepository;
import pl.jkuznik.data.myOrder.MyOrder;

@Service
public class MealService {

    private final MealRepository repository;

    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Optional<Meal> get(Long id) {
        return repository.findById(id);
    }

    public Meal update(Meal entity) {
        return repository.save(entity);
    }
    public void updateAll(List<Meal> meals) {
        repository.saveAll(meals);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
    public List<Meal> list() {
        return repository.findAll();
    }

    public Page<Meal> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Meal> list(Pageable pageable, Specification<Meal> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}