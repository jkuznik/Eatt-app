package pl.jkuznik.data.myOrder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MyOrderService {

    private final MyOrderRepository repository;

    public MyOrderService(MyOrderRepository repository) {
        this.repository = repository;
    }

    public Optional<MyOrder> get(Long id) {
        return repository.findById(id);
    }

    public MyOrder update(MyOrder entity) {
        return repository.save(entity);
    }
    public void updateAll(List<MyOrder> myOrders) {
        repository.saveAll(myOrders);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
    public List<MyOrder> list() {
        return repository.findAll();
    }
    public Page<MyOrder> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<MyOrder> list(Pageable pageable, Specification<MyOrder> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}