package pl.jkuznik.data.information;

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
public class InformationServiceTest {
    @Mock
    private InformationRepository informationRepository;
    @InjectMocks
    private InformationService informationService;
    @Test
    void getRecordById(){
        Information information = new Information();
        BDDMockito.given(informationRepository.findById(5L)).willReturn(Optional.of(information));
        Assertions.assertThat(informationService.get(5L)).isEqualTo(Optional.of(information));
    }
    @Test
    void updateOneRecord(){
        Information information = new Information();
//        BDDMockito.given(informationRepository.save(information)).getMock();
        BDDMockito.given(informationRepository.save(information)).willReturn(information);

        informationService.update(information);
        Mockito.verify(informationRepository, Mockito.times(1)).save(information);
    }
    @Test
    void updateListOfRecords(){
        Information information1 = new Information();
        Information information2 = new Information();
        Information information3 = new Information();
        List<Information> informations = List.of(information1, information2, information3);

        BDDMockito.given(informationRepository.saveAll(informations)).willReturn(informations);

        informationService.updateAll(informations);
        informationService.updateAll(informations);
        informationService.updateAll(informations);

        Mockito.verify(informationRepository, Mockito.times(3)).saveAll(informations);
    }

    @Test
    void deleteRecordById(){
        BDDMockito.doNothing().when(informationRepository).deleteById(5L);
        informationService.delete(5L);
        Mockito.verify(informationRepository, Mockito.times(1)).deleteById(5L);
    }
    @Test
    void getInformationsListWhenDataBaseIsEmpty(){
        List<Information> informations = new ArrayList<>();
        BDDMockito.given(informationRepository.findAll()).willReturn(informations);

        Assertions.assertThat(informationService.list().isEmpty());
    }

    @Test
    void countDataBaseInformationsListSize() {
        BDDMockito.given(informationRepository.count()).willReturn(10L);
        Assertions.assertThat(informationService.count()).isEqualTo(10);
    }
}
