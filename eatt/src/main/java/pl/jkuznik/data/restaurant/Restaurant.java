package pl.jkuznik.data.restaurant;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import pl.jkuznik.data.AbstractEntity;

import java.time.LocalDate;

@Entity
@Table(name ="restaurant")
public class Restaurant extends AbstractEntity {

    private String name;
    private boolean isActive;
    private boolean isEnabled;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}