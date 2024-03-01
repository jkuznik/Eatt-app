package pl.jkuznik.data.restaurant;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.jkuznik.data.restaurant.Restaurant;
import pl.jkuznik.data.restaurant.RestaurantRepository;

@Service
public class RestaurantService {

    private final RestaurantRepository repository;

    public RestaurantService(RestaurantRepository repository) {
        this.repository = repository;
    }

    public Optional<Restaurant> get(Long id) {
        return repository.findById(id);
    }

    public Restaurant update(Restaurant entity) {
        return repository.save(entity);
    }
    public void updateAll(List<Restaurant> restaurants) {
        repository.saveAll(restaurants);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
    public List<Restaurant> list() {
        return repository.findAll();
    }
    public Page<Restaurant> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Restaurant> list(Pageable pageable, Specification<Restaurant> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}