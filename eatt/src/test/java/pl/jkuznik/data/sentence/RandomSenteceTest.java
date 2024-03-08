package pl.jkuznik.data.sentence;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class RandomSenteceTest {

    private RandomSentence randomSentence = new RandomSentence();

//    @Mock
//    private SentenceRepository sentenceRepository;

    @InjectMocks
    private SentencesService sentencesService;
    @Test
    public void getSentence(){  // Empty
        assertEquals("Brak sentencji w bazie danych", randomSentence.getSentence());
    }
    @Test
    public void getSentence2(){  // Filled

        List<String> list = List.of("test1", "test2", "test3");
        list.forEach(randomSentence::addSentence);

        String random = randomSentence.getSentence();

//        Assertions.assertThat(list).contains(random);
        assertTrue(list.contains(random));
    }
    @Test
    public void clear() {

        List<Sentence> sentences = sentencesService.list();
        for (Sentence s : sentences) {
            randomSentence.addSentence(s.getText());
        }
        
        randomSentence.clearSentences();

        int size = randomSentence.size();

        assertEquals(0, size);
    }
    @Test
    public void addSentence(){
        List<Sentence> sentences = sentencesService.list();
        for (Sentence s : sentences) {
            randomSentence.addSentence(s.getText());
        }
        int beforeAdd = randomSentence.size();
        randomSentence.addSentence("test");
        int afterAdd = randomSentence.size();

        assertEquals(beforeAdd+1, afterAdd);
    }

}
