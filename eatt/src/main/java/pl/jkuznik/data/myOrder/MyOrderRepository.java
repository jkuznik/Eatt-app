package pl.jkuznik.data.myOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pl.jkuznik.data.myOrder.MyOrder;

public interface MyOrderRepository
        extends
        JpaRepository<MyOrder, Long>,
        JpaSpecificationExecutor<MyOrder> {

}