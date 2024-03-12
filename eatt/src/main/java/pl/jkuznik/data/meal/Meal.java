package pl.jkuznik.data.meal;

import jakarta.persistence.*;
import pl.jkuznik.data.AbstractEntity;

@Entity
@Table(name="meal")
public class Meal extends AbstractEntity {
    private String name;
    private boolean isMealActive;
    private String description;
    private String allergens;
    private String nutritions;
    private String restaurantName;
    private boolean isRestaurantActive;
    public Meal(){    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isMealActive() {
        return isMealActive;
    }
    public void setMealActive(boolean mealActive) {
        isMealActive = mealActive;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getAllergens() {
        return allergens;
    }
    public void setAllergens(String allergens) {
        this.allergens = allergens;
    }
    public String getNutritions() {
        return nutritions;
    }
    public void setNutritions(String nutritions) {
        this.nutritions = nutritions;
    }
    public String getRestaurantName() {
        return restaurantName;
    }
    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
    public boolean isRestaurantActive() {
        return isRestaurantActive;
    }
    public void setRestaurantActive(boolean restaurantActive) {
        isRestaurantActive = restaurantActive;
    }
}