package pl.jkuznik.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.jkuznik.data.information.Information;
import pl.jkuznik.data.information.InformationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class InformationService {
    private final InformationRepository repository;

    public InformationService(InformationRepository repository) {
        this.repository = repository;
    }

    public Optional<Information> get(Long id) {
        return repository.findById(id);
    }

    public Information update(Information entity) {
        return repository.save(entity);
    }
    public void updateAll(List<Information> informations) {
        repository.saveAll(informations);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
    public List<Information> list() {
        return repository.findAll();
    }
    public Page<Information> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Information> list(Pageable pageable, Specification<Information> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }
}
