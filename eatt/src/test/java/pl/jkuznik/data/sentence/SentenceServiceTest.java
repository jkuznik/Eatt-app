package pl.jkuznik.data.sentence;

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
class SentenceServiceTest {

    @Mock
    private SentenceRepository sentenceRepository;

    @InjectMocks
    private SentenceService sentenceService;

    @Test
    void getRecordById(){
        Sentence sentence = new Sentence();
        BDDMockito.given(sentenceRepository.findById(5L)).willReturn(Optional.of(sentence));
        Assertions.assertThat(sentenceService.get(5L)).isEqualTo(Optional.of(sentence));
    }
    @Test
    void updateOneRecord(){
        Sentence sentence = new Sentence();
//        BDDMockito.given(sentenceRepository.save(sentence)).getMock();
        BDDMockito.given(sentenceRepository.save(sentence)).willReturn(sentence);

        sentenceService.update(sentence);
        Mockito.verify(sentenceRepository, Mockito.times(1)).save(sentence);
    }
    @Test
    void updateListOfRecords(){
        Sentence sentence1 = new Sentence();
        Sentence sentence2 = new Sentence();
        Sentence sentence3 = new Sentence();
        List<Sentence> sentences = List.of(sentence1, sentence2, sentence3);

        BDDMockito.given(sentenceRepository.saveAll(sentences)).willReturn(sentences);

        sentenceService.updateAll(sentences);
        sentenceService.updateAll(sentences);
        sentenceService.updateAll(sentences);

        Mockito.verify(sentenceRepository, Mockito.times(3)).saveAll(sentences);
    }

    @Test
    void deleteRecordById(){
        BDDMockito.doNothing().when(sentenceRepository).deleteById(5L);
        sentenceService.delete(5L);
        Mockito.verify(sentenceRepository, Mockito.times(1)).deleteById(5L);
    }
    @Test
    void getSentencesListWhenDataBaseIsEmpty(){
        List<Sentence> sentences = new ArrayList<>();
        BDDMockito.given(sentenceRepository.findAll()).willReturn(sentences);

        Assertions.assertThat(sentenceService.list().isEmpty());
    }

    @Test
    void countDataBaseSentencesListSize() {
        BDDMockito.given(sentenceRepository.count()).willReturn(10L);
        Assertions.assertThat(sentenceService.count()).isEqualTo(10);
    }
}