package pl.jkuznik.data.information;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InformationTest {
    @Test
    void getInformationText(){
        Information information = new Information();
        information.setText("test1");

        Assertions.assertEquals("test1", information.getText());
    }

    @Test
    void isInformationActive(){
        Information information = new Information();
        information.setActive(true);

        Assertions.assertTrue(information.isActive());
    }
}
