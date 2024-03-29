package pl.jkuznik.data.sentence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SentenceService {
    private final SentenceRepository repository;

    public SentenceService(SentenceRepository repository) {
        this.repository = repository;
    }

    public Optional<Sentence> get(Long id) {
        return repository.findById(id);
    }

    public Sentence update(Sentence entity) {
        return repository.save(entity);
    }
    public void updateAll(List<Sentence> sentences) {
        repository.saveAll(sentences);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
    public List<Sentence> list() {
        return repository.findAll();
    }
    public Page<Sentence> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Sentence> list(Pageable pageable, Specification<Sentence> filter) {
        return repository.findAll(filter, pageable);
    }
    public int count() {
        return (int) repository.count();
    }
}