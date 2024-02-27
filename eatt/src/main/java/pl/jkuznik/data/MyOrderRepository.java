package pl.jkuznik.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MyOrderRepository
        extends
        JpaRepository<MyOrder, Long>,
        JpaSpecificationExecutor<MyOrder> {

}