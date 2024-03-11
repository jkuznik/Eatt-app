package pl.jkuznik.data.sentence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
public class SentenceTest {

    @Test
    void getTextWhenRecordIsNotDeclared(){
        Sentence sentence = new Sentence();
        String test = sentence.getText();

        Assertions.assertNull(test);
    }
    @Test
    void getTextWhenRecordHasValues(){
        Sentence sentence = new Sentence();
        sentence.setText("test1");

        Assertions.assertEquals("test1",sentence.getText());
    }
}
