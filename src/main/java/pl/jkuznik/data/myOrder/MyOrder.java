package pl.jkuznik.data.myOrder;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import pl.jkuznik.data.AbstractEntity;

import java.time.LocalDateTime;

@Entity
@Table(name ="my_order")
public class MyOrder extends AbstractEntity {
    private LocalDateTime date;
    private String restaurantName;
    private String mealName;
    private String userName;
    private String userEmail;
    private String notes;
    private String comment;
    private int rating;
    private boolean isActive;

    public MyOrder() {}
    public LocalDateTime getDate() {
        return date;
    }
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    public String getRestaurantName() {
        return restaurantName;
    }
    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
    public String getMealName() {
        return mealName;
    }
    public void setMealName(String mealName) {
        this.mealName = mealName;
    }
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
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
    public boolean isActive() {
        return isActive;
    }
    public boolean getIsActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        isActive = active;
    }
    public void setIsActive(boolean active) {
        isActive = active;
    }
}