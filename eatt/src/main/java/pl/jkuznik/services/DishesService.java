package pl.jkuznik.services;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.jkuznik.data.Dishes;
import pl.jkuznik.data.DishesRepository;

@Service
public class DishesService {

    private final DishesRepository repository;

    public DishesService(DishesRepository repository) {
        this.repository = repository;
    }

    public Optional<Dishes> get(Long id) {
        return repository.findById(id);
    }

    public Dishes update(Dishes entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Dishes> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Dishes> list(Pageable pageable, Specification<Dishes> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}