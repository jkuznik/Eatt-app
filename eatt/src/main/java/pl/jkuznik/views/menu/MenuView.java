package pl.jkuznik.views.menu;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.PermitAll;
import pl.jkuznik.data.AbstractEntity;
import pl.jkuznik.data.Dishes;
import pl.jkuznik.data.Restaurant;
import pl.jkuznik.data.RestaurantRepository;
import pl.jkuznik.services.DishesService;
import pl.jkuznik.services.RestaurantService;
import pl.jkuznik.views.MainLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@PageTitle("Menu")
@Route(value = "menu", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class MenuView extends Composite<VerticalLayout> {

    private String restaurantName;
    private final RestaurantService restaurantService;
    private final DishesService dishesService;
    private RadioButtonGroup radioGroup = new RadioButtonGroup();
    private Accordion accordion = new Accordion();
    private Tab tab = new Tab(accordion);
    private Tab tab2 = new Tab();
    public MenuView(RestaurantService restaurantService, DishesService dishesService) {
        this.restaurantService = restaurantService;
        this.dishesService = dishesService;


        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        if (getRestaurant() == null) restaurantName = "Nie wybrano żadnej restauracji. Zgłoś żeś głodny w administracji";
        else restaurantName = getRestaurant().getName();
        radioGroup.setLabel(restaurantName);
        radioGroup.setWidth("min-content");
        radioGroup.setItems(getDishesList(getRestaurant()));
        radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        accordion.setWidth("100%");
        getContent().add(radioGroup);
        getContent().add(accordion);
        setAccordionSampleData(accordion, radioGroup);

        radioGroup.addValueChangeListener(event ->{
            getContent().remove(accordion);
        });

        radioGroup.addValueChangeListener(event -> {
//            accordion.removeFromParent();
//            getContent().remove(accordion);
//            accordion.remove(accordion);
            setAccordionSampleData(accordion, radioGroup);
            getContent().add(accordion);
        });
    }

    private void setAccordionSampleData(Accordion accordion, RadioButtonGroup radioGroup) {
        Span description = new Span(getDishes(getRestaurant(), radioGroup).getDescription());
        VerticalLayout descriptionLayout = new VerticalLayout(description);
        descriptionLayout.setSpacing(false);
        descriptionLayout.setPadding(false);
        accordion.add("Opis potrawy - " + getDishes(getRestaurant(), radioGroup).getName() , descriptionLayout);
        Span allergens = new Span(getDishes(getRestaurant(), radioGroup).getAllergens());
        VerticalLayout allergensLayout = new VerticalLayout();
        allergensLayout.setSpacing(false);
        allergensLayout.setPadding(false);
        allergensLayout.add(allergens);
        accordion.add("Alergeny", allergensLayout);
        Span nutritions = new Span(getDishes(getRestaurant(), radioGroup).getNutritions());
        VerticalLayout nutritionsLayout = new VerticalLayout();
        nutritionsLayout.setSpacing(false);
        nutritionsLayout.setPadding(false);
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

    private Dishes getDishes(Restaurant restaurant, RadioButtonGroup radioGroup) {

        List<Dishes> dishes = dishesService.list();
        if (radioGroup.getValue()==null) {
            return dishes.stream()
                    .filter(d -> d.getRestaurantId() == restaurant.getId())
                    .findFirst()
                    .orElse(dishes.get(0));
        }

        return dishes.stream()
                .filter(d -> {
                    return (d.getRestaurantId() == restaurant.getId() &&
                            radioGroup.getValue().toString().equals(d.getName()));
                })
                .findAny()
                .orElse(dishes.get(0));
    }
    private List<String> getDishesList(Restaurant restaurant) {
        List<Dishes> dishes = dishesService.list();
        List<String> dishesNames = new ArrayList<>();
        List<Dishes> dishesList = dishes.stream()
                .filter(d -> d.getRestaurantId() == restaurant.getId())
                .toList();

        for (Dishes d: dishesList) {
            dishesNames.add(d.getName());
        }
        return dishesNames;
    }

}
