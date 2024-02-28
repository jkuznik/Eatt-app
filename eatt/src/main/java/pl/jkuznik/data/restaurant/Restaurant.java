package pl.jkuznik.data.restaurant;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import pl.jkuznik.data.AbstractEntity;

import java.time.LocalDate;

@Entity
//@Table(name ="restaurant")    tworzenie nowej tabeli w bazie danych bezpośrendio z tego miejsca
public class Restaurant extends AbstractEntity {

    private String name;
    private boolean isActive;

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
}