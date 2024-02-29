package pl.jkuznik.data.information;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InformationRepository extends
        JpaRepository<Information, Long>,
        JpaSpecificationExecutor<Information> {
}
