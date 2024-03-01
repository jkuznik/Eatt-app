package pl.jkuznik.data.samplePerson;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import pl.jkuznik.data.AbstractEntity;

import java.time.LocalDate;

@Entity
public class SamplePerson extends AbstractEntity {

    private String userName;
    @Email
    private String userEmail;
    private String moRestaurantName;
    private String moMealName;
    private String moComment;
    private int moRating;
    private String meDescription;
    private String meAllergens;
    private String meNutritions;
    private LocalDate orderDate;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getMoRestaurantName() {
        return moRestaurantName;
    }

    public void setMoRestaurantName(String moRestaurantName) {
        this.moRestaurantName = moRestaurantName;
    }

    public String getMoMealName() {
        return moMealName;
    }

    public void setMoMealName(String moMealName) {
        this.moMealName = moMealName;
    }

    public String getMoComment() {
        return moComment;
    }

    public void setMoComment(String moComment) {
        this.moComment = moComment;
    }

    public int getMoRating() {
        return moRating;
    }

    public void setMoRating(int moRating) {
        this.moRating = moRating;
    }

    public String getMeDescription() {
        return meDescription;
    }

    public void setMeDescription(String meDescription) {
        this.meDescription = meDescription;
    }

    public String getMeAllergens() {
        return meAllergens;
    }

    public void setMeAllergens(String meAllergens) {
        this.meAllergens = meAllergens;
    }

    public String getMeNutritions() {
        return meNutritions;
    }

    public void setMeNutritions(String meNutritions) {
        this.meNutritions = meNutritions;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }
}
