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
import pl.jkuznik.data.meal.Meal;
import pl.jkuznik.data.myOrder.MyOrder;
import pl.jkuznik.data.restaurant.Restaurant;
import pl.jkuznik.data.user.User;
import pl.jkuznik.security.AuthenticatedUser;
import pl.jkuznik.services.MealService;
import pl.jkuznik.services.MyOrderService;
import pl.jkuznik.services.RestaurantService;
import pl.jkuznik.views.MainLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.stream.Collectors;

@PageTitle("Menu")
@Route(value = "menu", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class MenuView extends Composite<VerticalLayout> { // poprawić tę klasę, button listenery zmienić na metody, poprawić pojawianie się działającego przycisku zmien zamowienie, dodać osobną tabelę na zrealizowane zamówienia
    private final AuthenticatedUser authenticatedUser;
    private final RestaurantService restaurantService;
    private final MealService mealService;
    private final MyOrderService myOrderService;
    private String restaurantName;
    private RadioButtonGroup radioGroup = new RadioButtonGroup();
    private Accordion accordion = new Accordion();
    private Button order = new Button("Zamów");
    private Button change = new Button("Zmień zamówienie");
//    private Span description;
//    private Span allergens;
//    private Span nutritions;

    public MenuView(AuthenticatedUser authenticatedUser, RestaurantService restaurantService, MealService mealService, MyOrderService myOrderService) {
        this.authenticatedUser = authenticatedUser;
        this.restaurantService = restaurantService;
        this.mealService = mealService;
        this.myOrderService = myOrderService;
        Optional<User> loggedUser = authenticatedUser.get();
        User user = loggedUser.get();
        List<MyOrder> myOrders1 = getActualMyOrders();

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        if (getRestaurant() == null)
            restaurantName = "Nie wybrano restauracji. Zgłoś to w administracji";
        else restaurantName = getRestaurant().getName();
        Optional<MyOrder> any = myOrders1.stream()
                .filter(myOrder -> myOrder.getUserId() == user.getId())
                .filter(MyOrder::isActive)
                .findAny();

        radioGroup.setLabel(restaurantName);
        radioGroup.setWidth("min-content");
        radioGroup.setItems(getMeals(getRestaurant()));
        radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        accordion.setWidth("100%");
        getContent().add(radioGroup);
        if (any.isEmpty()){
            getContent().add(order);
            order.addClickListener(e -> {
                try {
                    if (radioGroup.getValue() != null) {

                        myOrders1.forEach( myOrder -> {
                            if ((myOrder.getUserId()==user.getId()) && myOrder.isActive() ) myOrder.setActive(false);
                        });

                        MyOrder newOrder = new MyOrder();
                        newOrder.setRestaurantName(radioGroup.getLabel());
                        newOrder.setMealName(radioGroup.getValue().toString());
                        newOrder.setUserId(user.getId());
                        newOrder.setComment("Dodaj komentarz");
                        newOrder.setRating(0);
                        newOrder.setActive(true);

                        myOrders1.add(newOrder);

                        myOrderService.update(newOrder);
                        myOrderService.updateAll(myOrders1);

                        Notification n = Notification.show("Witaj " + user.getName() + ". Na jutro zamówiłeś  " + radioGroup.getValue().toString());
                        n.setPosition(Notification.Position.MIDDLE);
                        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                        getContent().remove(order);
                        getContent().add(change);

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
        else {
            getContent().add(change);
            change.addClickListener(e -> {
                try {
                    if (radioGroup.getValue() != null) {

                        List<MyOrder> myOrders2 = getActualMyOrders();
                        long index = myOrders2.stream()
                                .filter(myOrder -> myOrder.getUserId() == user.getId())
                                .filter(MyOrder::isActive)
                                .mapToLong(MyOrder::getId)
                                .findFirst()
                                .getAsLong();

                        myOrderService.delete(index);
                        List<MyOrder> myOrders3 = getActualMyOrders();

                        MyOrder newOrder = new MyOrder();
                        newOrder.setRestaurantName(radioGroup.getLabel());
                        newOrder.setMealName(radioGroup.getValue().toString());
                        newOrder.setUserId(user.getId());
                        newOrder.setComment("Dodaj komentarz");
                        newOrder.setRating(0);
                        newOrder.setActive(true);

                        myOrders3.add(newOrder);

                        myOrderService.update(newOrder);
                        myOrderService.updateAll(myOrders3);

                        Notification n = Notification.show("Witaj " + user.getName() + ". Na jutro zamówiłeś  " + radioGroup.getValue().toString());
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

        getContent().add(accordion);

        setAccordionSampleData(accordion, radioGroup);

//        accordion.getElement().addEventListener( "opened", e -> {
//            setAccordionSampleData(accordion, radioGroup);
//            accordion.getElement().removeFromParent();
//        });
//        accordion.getElement().addEventListener("opened", e -> {
//            setAccordionSampleData(accordion, radioGroup);
//        });

//        radioGroup.addValueChangeListener(event -> {
//            accordion.remove(description);
//            accordion.remove(allergens);
//            accordion.remove(nutritions);
//            setAccordionSampleData(accordion,radioGroup);
////            getContent().add(accordion);
//        });
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

                    Optional<User> loggedUser = authenticatedUser.get();
                    User user = loggedUser.get();

                    List<MyOrder> myOrders = myOrderService.list();
                    long max = myOrders.stream()
                            .mapToLong(MyOrder::getId)
                            .max()
                            .orElse(0); //tutaj obsłużyć w przypadku braku rekordów

                    myOrders.forEach( myOrder -> {
                                if ((myOrder.getUserId()==user.getId()) && myOrder.isActive() ) myOrder.setActive(false);
                            });

//                    MyOrder newOrder = new MyOrder(radioGroup.getLabel(), radioGroup.getValue().toString(), user.getName(), "Dodaj komentarz", 0, true);
                    MyOrder newOrder = new MyOrder();
                    newOrder.setRestaurantName(radioGroup.getLabel());
                    newOrder.setMealName(radioGroup.getValue().toString());
//                    newOrder.setApplicationUserName(user.getName());
                    newOrder.setUserId(user.getId());
                    newOrder.setComment("Dodaj komentarz");
                    newOrder.setRating(0);
                    newOrder.setActive(true);

                    myOrders.add(newOrder);

                    myOrderService.update(newOrder);
                    myOrderService.updateAll(myOrders);


//                    newOrder.setRestaurantName(radioGroup.getLabel());
//                    newOrder.setMealName(radioGroup.getValue().toString());
//                    newOrder.


                    Notification n = Notification.show("Witaj " + user.getName() + ". Na jutro zamówiłeś  " + radioGroup.getValue().toString());
                    n.setPosition(Notification.Position.MIDDLE);
                    n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    getContent().remove(button);
                    getContent().add(change);

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
    private List<MyOrder> getActualMyOrders(){
        return myOrderService.list();
    }

}
