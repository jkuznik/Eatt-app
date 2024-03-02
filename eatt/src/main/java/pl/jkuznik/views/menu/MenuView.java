package pl.jkuznik.views.menu;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import pl.jkuznik.data.meal.Meal;
import pl.jkuznik.data.myOrder.MyOrder;
import pl.jkuznik.data.restaurant.Restaurant;
import pl.jkuznik.data.user.User;
import pl.jkuznik.security.AuthenticatedUser;
import pl.jkuznik.data.meal.MealService;
import pl.jkuznik.data.myOrder.MyOrderService;
import pl.jkuznik.data.restaurant.RestaurantService;
import pl.jkuznik.views.MainLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@PageTitle("Menu")
@Route(value = "menu", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class MenuView extends Composite<VerticalLayout> { // poprawić tę klasę, button listenery zmienić na metody, poprawić pojawianie się działającego przycisku zmien zamowienie, dodać osobną tabelę na zrealizowane zamówienia
    private final AuthenticatedUser authenticatedUser;
    private final RestaurantService restaurantService;
    private final MealService mealService;
    private final MyOrderService myOrderService;
    private final Button order = new Button("Zamów");
    private final Button change = new Button("Zmień zamówienie");
    private final Button absent = new Button("Odwołaj zamówienie");

    private RadioButtonGroup radioGroup = new RadioButtonGroup();
        private VerticalLayout descriptionLayout = new VerticalLayout();
    private VerticalLayout allergensLayout = new VerticalLayout();
    private VerticalLayout nutritionsLayout = new VerticalLayout();
    private String restaurantName;
    public MenuView(AuthenticatedUser authenticatedUser, RestaurantService restaurantService, MealService mealService, MyOrderService myOrderService) {
        this.authenticatedUser = authenticatedUser;
        this.restaurantService = restaurantService;
        this.mealService = mealService;
        this.myOrderService = myOrderService;
        Accordion accordion = new Accordion();

        if (getRestaurant() == null) restaurantName = "Nie wybrano restauracji. Zgłoś to w administracji";
        else restaurantName = getRestaurant().getName();

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        radioGroup.setLabel(restaurantName);
        radioGroup.setWidth("min-content");
        radioGroup.setItems(getMeals(getRestaurant()));
        radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);

        List<MyOrder> actualMyOrders = myOrderService.list();
        User loggedUser = getLoggedUser();
        Optional<MyOrder> any = actualMyOrders.stream()
                .filter(myOrder -> myOrder.getUserName() == loggedUser.getName())
                .filter(MyOrder::isActive)
                .findAny();
        getContent().add(radioGroup);
        getContent().add(accordion);
        if (any.isEmpty()) {
            order.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            getContent().add(order);
        }
        else {
            getContent().add(change);
            absent.addThemeVariants(ButtonVariant.LUMO_ERROR);
            getContent().add(absent);
        }
        setAccoriondSampleData(accordion, radioGroup);

        orderClickListener(order);
        changeClickListener(change);
        absentClickListener(absent);
    radioGroup.addValueChangeListener(event -> setAccordionSampleDataAndRefresh(accordion, radioGroup, order, change, absent, descriptionLayout, allergensLayout, nutritionsLayout));
    }
    private void setAccoriondSampleData(Accordion accordion, RadioButtonGroup radioGroup){
        Span description;
        Span allergens;
        Span nutritions;

        if (getRestaurant() == null) description = new Span("Wybierz restaurację");
        else description = new Span(getMeal(getRestaurant(), radioGroup).getDescription());
        descriptionLayout.setSpacing(true);
        descriptionLayout.setPadding(true);
        descriptionLayout.add(description);
        accordion.add("Opis potrawy - " + getMeal(getRestaurant(), radioGroup).getName(), descriptionLayout);
        if (getRestaurant() == null) allergens = new Span("Wybierz restaurację");
        else allergens = new Span(getMeal(getRestaurant(), radioGroup).getAllergens());
        allergensLayout.setSpacing(true);
        allergensLayout.setPadding(true);
        allergensLayout.add(allergens);
        accordion.add("Alergeny", allergensLayout);
        if (getRestaurant() == null) nutritions = new Span("Wybierz restaurację");
        else nutritions = new Span(getMeal(getRestaurant(), radioGroup).getNutritions());
        nutritionsLayout.setSpacing(false);
        nutritionsLayout.setPadding(true);
        nutritionsLayout.add(nutritions);
        accordion.add("Wartości odżywcze", nutritionsLayout);

    }

    private void setAccordionSampleDataAndRefresh(Accordion accordion, RadioButtonGroup radioGroup, Button button1, Button button2, Button button3, VerticalLayout verticalLayout1, VerticalLayout verticalLayout2, VerticalLayout verticalLayout3) {
        accordion.remove(verticalLayout1);
        accordion.remove(verticalLayout2);
        accordion.remove(verticalLayout3);
        getContent().remove(accordion);
        getContent().remove(radioGroup);


        List<MyOrder> actualMyOrders = myOrderService.list();
        User loggedUser = getLoggedUser();
        Optional<MyOrder> any = actualMyOrders.stream()
                .filter(myOrder -> myOrder.getUserName() == loggedUser.getName())
                .filter(MyOrder::isActive)
                .findAny();

        if (any.isEmpty()) {
            order.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            getContent().add(button1);
        }
        else {
            getContent().add(button2);
            absent.addThemeVariants(ButtonVariant.LUMO_ERROR);
            getContent().add(button3);
        }
        Span description;
        Span allergens;
        Span nutritions;
        if (getRestaurant() == null) description = new Span("Wybierz restaurację");
        else description = new Span(getMeal(getRestaurant(), radioGroup).getDescription());
        descriptionLayout = new VerticalLayout();
        descriptionLayout.setSpacing(true);
        descriptionLayout.setPadding(true);
        descriptionLayout.add(description);
        accordion.add("Opis potrawy - " + getMeal(getRestaurant(), radioGroup).getName(), descriptionLayout);
        if (getRestaurant() == null) allergens = new Span("Wybierz restaurację");
        else allergens = new Span(getMeal(getRestaurant(), radioGroup).getAllergens());
        allergensLayout = new VerticalLayout();
        allergensLayout.setSpacing(true);
        allergensLayout.setPadding(true);
        allergensLayout.add(allergens);
        accordion.add("Alergeny", allergensLayout);
        if (getRestaurant() == null) nutritions = new Span("Wybierz restaurację");
        else nutritions = new Span(getMeal(getRestaurant(), radioGroup).getNutritions());
        nutritionsLayout = new VerticalLayout();
        nutritionsLayout.setSpacing(false);
        nutritionsLayout.setPadding(true);
        nutritionsLayout.add(nutritions);
        accordion.add("Wartości odżywcze", nutritionsLayout);
        getContent().add(radioGroup);
        getContent().add(accordion);
        if (any.isEmpty()) {
            button1.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            getContent().add(button1);
        }
        else {
            getContent().add(button2);
            absent.addThemeVariants(ButtonVariant.LUMO_ERROR);
            getContent().add(button3);
        }
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
    private void orderClickListener(Button button) {
        button.addClickListener(e -> {
            try {
                if (radioGroup.getValue() != null) {
                    List<MyOrder> actualMyOrders = myOrderService.list();
                    User loggedUser = getLoggedUser();
                    actualMyOrders.forEach( myOrder -> {
                        if ((myOrder.getUserName() == loggedUser.getName()) && myOrder.isActive() ) myOrder.setActive(false);
                    });
                    MyOrder newOrder = new MyOrder();
                    newOrder.setRestaurantName(radioGroup.getLabel());
                    newOrder.setMealName(radioGroup.getValue().toString());
                    newOrder.setUserName(loggedUser.getName());
                    newOrder.setComment("Dodaj komentarz");
                    newOrder.setRating(0);
                    newOrder.setActive(true);

                    actualMyOrders.add(newOrder);

                    myOrderService.update(newOrder);
                    myOrderService.updateAll(actualMyOrders);

                    Notification n = Notification.show("Witaj " + loggedUser.getName() + ". Zamówiono " + radioGroup.getValue().toString() + ". Życzymy smacznego!");
                    n.setPosition(Notification.Position.MIDDLE);
                    n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    getContent().remove(order);
                    getContent().add(change);
                    absent.addThemeVariants(ButtonVariant.LUMO_ERROR);
                    getContent().add(absent);

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
    private void changeClickListener(Button button) {
        button.addClickListener(e -> {
            try {
                if (radioGroup.getValue() != null) {
                    List<MyOrder> myOrders = myOrderService.list();
                    User loggedUser = getLoggedUser();

                    long index = myOrders.stream()
                            .filter(myOrder -> myOrder.getUserName() == loggedUser.getName())
                            .filter(MyOrder::isActive)
                            .mapToLong(MyOrder::getId)
                            .findFirst()
                            .getAsLong();

                    myOrderService.delete(index);
                    List<MyOrder> myOrders1 = myOrderService.list();

                    MyOrder newOrder = new MyOrder();
                    newOrder.setRestaurantName(radioGroup.getLabel());
                    newOrder.setMealName(radioGroup.getValue().toString());
                    newOrder.setUserName(loggedUser.getName());
                    newOrder.setComment("Dodaj komentarz");
                    newOrder.setRating(0);
                    newOrder.setActive(true);

                    myOrders1.add(newOrder);

                    myOrderService.update(newOrder);
                    myOrderService.updateAll(myOrders1);

                    Notification n = Notification.show("Witaj " + loggedUser.getName() + ". Zamówiono  " + radioGroup.getValue().toString() + ". Życzymy smacznego!");
                    n.setPosition(Notification.Position.MIDDLE);
                    n.addThemeVariants(NotificationVariant.LUMO_ERROR);

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
    private void absentClickListener(Button button) {
        button.addClickListener(e -> {
            try {
                    List<MyOrder> actualMyOrders = myOrderService.list();
                    User loggedUser = getLoggedUser();
                    String cancelOrder;

                    long index = actualMyOrders.stream()
                            .filter(myOrder -> myOrder.getUserName() == loggedUser.getName() && myOrder.isActive())
                            .mapToLong(MyOrder::getId)
                            .findFirst()
                            .getAsLong();

                    cancelOrder = myOrderService.get(index).orElse(null).getMealName();
                    myOrderService.delete(index);
                    List<MyOrder> myOrders = myOrderService.list();

                    myOrderService.updateAll(myOrders);

                    getContent().remove(absent);
                    getContent().remove(change);
                    order.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                    getContent().add(order);

                    Notification n = Notification.show("Uwaga! " + loggedUser.getName() + ". Zamówienie " + cancelOrder + " zostało odwołane");
                    n.setPosition(Notification.Position.MIDDLE);
                    n.addThemeVariants(NotificationVariant.LUMO_ERROR);

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
    private User getLoggedUser(){
        Optional<User> loggedUser = authenticatedUser.get();
        return loggedUser.get();
    }

}
