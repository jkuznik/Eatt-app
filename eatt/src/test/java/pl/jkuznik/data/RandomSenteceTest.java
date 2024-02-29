package pl.jkuznik.data;

import org.junit.jupiter.api.Test;
import pl.jkuznik.data.sentence.RandomSentence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

public class RandomSenteceTest {

    @Test
    public void getSentence(){
        RandomSentence randomSentence = new RandomSentence();

//        assertEquals(("Smacznego!" || "Na zdrowie!"), randomSentence.getSentences(1));
        assertEquals(anyString(), randomSentence.getSentence());
    }
}
