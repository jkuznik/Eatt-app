package pl.jkuznik.data.sentence;

import jakarta.persistence.*;
import pl.jkuznik.data.AbstractEntity;

@Entity
@Table(name ="sentence")
public class Sentence extends AbstractEntity {

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}