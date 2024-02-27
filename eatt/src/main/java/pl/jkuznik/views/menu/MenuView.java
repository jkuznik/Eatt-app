package pl.jkuznik.views.menu;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import pl.jkuznik.data.Meal;
import pl.jkuznik.data.Restaurant;
import pl.jkuznik.services.MealService;
import pl.jkuznik.services.RestaurantService;
import pl.jkuznik.views.MainLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@PageTitle("Menu")
@Route(value = "menu", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class MenuView extends Composite<VerticalLayout> {

    private String restaurantName;
    private final RestaurantService restaurantService;
    private final MealService mealService;
    private RadioButtonGroup radioGroup = new RadioButtonGroup();
    private Accordion accordion = new Accordion();
    private Button order = new Button("Zamów");
    private Span description;
    private Span allergens;
    private Span nutritions;

    public MenuView(RestaurantService restaurantService, MealService mealService) {
        this.restaurantService = restaurantService;
        this.mealService = mealService;


        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        if (getRestaurant() == null)
            restaurantName = "Nie wybrano restauracji. Zgłoś to w administracji";
        else restaurantName = getRestaurant().getName();
        radioGroup.setLabel(restaurantName);
        radioGroup.setWidth("min-content");
        radioGroup.setItems(getMeals(getRestaurant()));
        radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        accordion.setWidth("100%");
        getContent().add(radioGroup);
        getContent().add(order);
        getContent().add(accordion);

        setAccordionSampleData(accordion, radioGroup);
        clickListener(order);

//        accordion.getElement().addEventListener( "opened", e -> {
//            setAccordionSampleData(accordion, radioGroup);
//            accordion.getElement().removeFromParent();
//        });
//        accordion.getElement().addEventListener("opened", e -> {
//            setAccordionSampleData(accordion, radioGroup);
//        });

        radioGroup.addValueChangeListener(event -> {
            accordion.remove(description);
            accordion.remove(allergens);
            accordion.remove(nutritions);
            setAccordionSampleData(accordion,radioGroup);
//            getContent().add(accordion);
        });
    }

    private void setAccordionSampleData(Accordion accordion, RadioButtonGroup radioGroup) {
        Span description;
        if (getRestaurant() == null) description = new Span("Wybierz restaurację");
        else description = new Span(getMeal(getRestaurant(), radioGroup).getDescription());
        VerticalLayout descriptionLayout = new VerticalLayout(description);
        descriptionLayout.setSpacing(true);
        descriptionLayout.setPadding(true);
        accordion.add("Opis potrawy - " + getMeal(getRestaurant(), radioGroup).getName(), descriptionLayout);
        Span allergens;
        if (getRestaurant() == null) allergens = new Span("Wybierz restaurację");
        else allergens = new Span(getMeal(getRestaurant(), radioGroup).getAllergens());
        VerticalLayout allergensLayout = new VerticalLayout();
        allergensLayout.setSpacing(true);
        allergensLayout.setPadding(true);
        allergensLayout.add(allergens);
        accordion.add("Alergeny", allergensLayout);
        Span nutritions;
        if (getRestaurant() == null) nutritions = new Span("Wybierz restaurację");
        else nutritions = new Span(getMeal(getRestaurant(), radioGroup).getNutritions());
        VerticalLayout nutritionsLayout = new VerticalLayout();
        nutritionsLayout.setSpacing(false);
        nutritionsLayout.setPadding(true);
        nutritionsLayout.add(nutritions);
        accordion.add("Wartości odżywcze", nutritionsLayout);

    }

    private Restaurant getRestaurant() { // NAPISAĆ TEST
        List<Restaurant> restaurants = restaurantService.list();
        Optional<Restaurant> restaurantOptional = restaurants.stream()
                .filter(Restaurant::isActive)
                .findFirst();
        return restaurantOptional.orElse(null);
    }

    private Meal getMeal(Restaurant restaurant, RadioButtonGroup radioGroup) {
        List<Meal> meals = mealService.list();
        if (restaurant == null) return meals.get(0);
        if (radioGroup.getValue() == null) {
            return meals.stream()
                    .filter(d -> d.getRestaurantId() == restaurant.getId())
                    .findFirst()
                    .orElse(meals.get(0));
        }

        return meals.stream()
                .filter(d -> {
                    return (d.getRestaurantId() == restaurant.getId() &&
                            radioGroup.getValue().toString().equals(d.getName()));
                })
                .findAny()
                .orElse(meals.get(0));
    }

    private List<String> getMeals(Restaurant restaurant) {
        List<Meal> meals = mealService.list();
        List<String> mealsNames = new ArrayList<>();
        if (restaurant == null) return mealsNames;
        List<Meal> mealList = meals.stream()
                .filter(d -> d.getRestaurantId() == restaurant.getId())
                .toList();

        for (Meal d : mealList) {
            mealsNames.add(d.getName());
        }
        return mealsNames;
    }

    private void clickListener(Button button) { // NAPISAĆ TEST
        button.addClickListener(e -> {
            try {
                if (radioGroup.getValue() != null) {
                    Notification n = Notification.show("Do następnego zamówienia wybrano " + radioGroup.getValue().toString());
                    n.setPosition(Notification.Position.MIDDLE);
                    n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    getContent().remove(button);
                } else Notification.show("Nie wybrano żadnej potrawy ");
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Notification.Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (IllegalStateException illegalStateException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

}
