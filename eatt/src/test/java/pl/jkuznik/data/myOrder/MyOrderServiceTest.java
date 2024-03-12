package pl.jkuznik.data.myOrder;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MyOrderServiceTest {
    @Mock
    private MyOrderRepository myOrderRepository;
    @InjectMocks
    private MyOrderService myOrderService;
    @Test
    void getRecordById(){
        MyOrder myOrder = new MyOrder();
        BDDMockito.given(myOrderRepository.findById(5L)).willReturn(Optional.of(myOrder));
        Assertions.assertThat(myOrderService.get(5L)).isEqualTo(Optional.of(myOrder));
    }
    @Test
    void updateOneRecord(){
        MyOrder myOrder = new MyOrder();
//        BDDMockito.given(myOrderRepository.save(myOrder)).getMock();
        BDDMockito.given(myOrderRepository.save(myOrder)).willReturn(myOrder);

        myOrderService.update(myOrder);
        Mockito.verify(myOrderRepository, Mockito.times(1)).save(myOrder);
    }
    @Test
    void updateListOfRecords(){
        MyOrder myOrder1 = new MyOrder();
        MyOrder myOrder2 = new MyOrder();
        MyOrder myOrder3 = new MyOrder();
        List<MyOrder> myOrders = List.of(myOrder1, myOrder2, myOrder3);

        BDDMockito.given(myOrderRepository.saveAll(myOrders)).willReturn(myOrders);

        myOrderService.updateAll(myOrders);
        myOrderService.updateAll(myOrders);
        myOrderService.updateAll(myOrders);

        Mockito.verify(myOrderRepository, Mockito.times(3)).saveAll(myOrders);
    }

    @Test
    void deleteRecordById(){
        BDDMockito.doNothing().when(myOrderRepository).deleteById(5L);
        myOrderService.delete(5L);
        Mockito.verify(myOrderRepository, Mockito.times(1)).deleteById(5L);
    }
    @Test
    void getMyOrdersListWhenDataBaseIsEmpty(){
        List<MyOrder> myOrders = new ArrayList<>();
        BDDMockito.given(myOrderRepository.findAll()).willReturn(myOrders);

        Assertions.assertThat(myOrderService.list().isEmpty());
    }

    @Test
    void countDataBaseMyOrdersListSize() {
        BDDMockito.given(myOrderRepository.count()).willReturn(10L);
        Assertions.assertThat(myOrderService.count()).isEqualTo(10);
    }
}
