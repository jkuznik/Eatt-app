package pl.jkuznik.views.easyAccesToTaste;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.jkuznik.data.sentence.RandomSentence;

public class EasyAccesToTaseViewTest {
    @Test
    public void tryNotNullSentenceWhenSencencesListIsEmpty(){
        RandomSentence randomSentence = new RandomSentence();
        Assertions.assertNotNull(randomSentence.getSentence());
    }
}
