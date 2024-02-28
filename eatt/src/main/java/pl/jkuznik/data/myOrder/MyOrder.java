package pl.jkuznik.data.myOrder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import pl.jkuznik.data.AbstractEntity;
import pl.jkuznik.data.user.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import pl.jkuznik.data.AbstractEntity;

import java.time.LocalDate;

@Entity
@Table(name ="my_order")
public class MyOrder extends AbstractEntity {

    private String restaurantName;
    private String mealName;
    private Long userId;

//    private String applicationUserName;
    private String comment;
    private int rating;
    private boolean isActive;

    public MyOrder() {
    }

    public MyOrder(String restaurantName, String mealName, /*String applicationUserName,*/Long userId, String comment, int rating, boolean isActive) {
        this.restaurantName = restaurantName;
        this.mealName = mealName;
//        this.applicationUserName = applicationUserName;
        this.userId = userId;
        this.comment = comment;
        this.rating = rating;
        this.isActive = isActive;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long applicationUserId) {
        this.userId = applicationUserId;
    }

    //    public String getApplicationUserName() {
//        return applicationUserName;
//    }
//
//    public void setApplicationUserName(String applicationUserName) {
//        this.applicationUserName = applicationUserName;
//    }

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

    public void setActive(boolean active) {
        isActive = active;
    }
}