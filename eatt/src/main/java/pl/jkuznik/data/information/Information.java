package pl.jkuznik.data.information;

import jakarta.persistence.*;
import pl.jkuznik.data.AbstractEntity;

@Entity
@Table(name="information")
public class Information extends AbstractEntity {
    private String text;
    private boolean isActive;
    public Information() {}
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        isActive = active;
    }
}
