package pl.jkuznik.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

public class RandomSenteceTest {

    @Test
    public void getRandomSentence(){
        RandomSentence randomSentence = new RandomSentence();

        assertEquals("Smacznego!", randomSentence.getSentences(1));
        assertEquals(anyString(), randomSentence.getSentences());
    }
}
