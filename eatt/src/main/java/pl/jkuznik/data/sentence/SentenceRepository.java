package pl.jkuznik.data.sentence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SentenceRepository extends
        JpaRepository<Sentence, Long>,
        JpaSpecificationExecutor<Sentence> {
}
